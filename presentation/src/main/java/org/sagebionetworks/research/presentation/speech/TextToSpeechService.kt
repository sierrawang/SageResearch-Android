/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.presentation.speech

import android.Manifest.permission
import android.annotation.TargetApi
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.support.annotation.RequiresPermission
import dagger.android.AndroidInjection
import dagger.android.DaggerService
import org.sagebionetworks.research.presentation.speech.TextToSpeechService.TextToSpeechState.SpeakingState.ERROR
import org.sagebionetworks.research.presentation.speech.TextToSpeechService.TextToSpeechState.SpeakingState.IDLE
import org.sagebionetworks.research.presentation.speech.TextToSpeechService.TextToSpeechState.SpeakingState.QUEUED
import org.sagebionetworks.research.presentation.speech.TextToSpeechService.TextToSpeechState.SpeakingState.SPEAKING
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import java.util.HashMap
import java.util.Locale

/**
 * Service that allows clients to setup speeches to play at a given time interval. For example a client
 * may want to have various instructions play at different offset's into an active step. Supports keywords
 * "start", "end", and "halfway" which allow the inputs in a form that is easier for humans to process.
 */
class TextToSpeechService : DaggerService(), OnInitListener {

    /**
     * Stores whether the TextToSpeechService is IDLE, SPEAKING, or has encountered an ERROR, as well as
     * the currently being spoken text in the case the service is SPEAKING. If the service isn't SPEAKING
     * the currentText should be null.
     */
    data class TextToSpeechState(val speakingState: SpeakingState, val currentText: String?) {

        enum class SpeakingState {
            SPEAKING, IDLE, QUEUED, ERROR
        }
    }

    /**
     * The Binder for the TextToSpeechService which allows the client to get a reference to this service.
     */
    inner class Binder : android.os.Binder() {

        fun getService(): TextToSpeechService {
            return this@TextToSpeechService
        }
    }

    private data class FutureSpeechData(val speechMap: MutableMap<Long, Pair<String, Boolean>>,
            val duration: Duration,
            val countdown: LiveData<Long>) {

        fun hasQueuedSpeech(): Boolean {
            // if speechMap != null duration != null and countdown != null
            val currentOffset = duration.seconds - (countdown.value ?: 0L)
            return speechMap.keys.any { offset -> offset > currentOffset }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TextToSpeechService::class.java)
    }

    private val serviceBinder: Binder = Binder()
    // values set when after a step is set by the client
    private var textToSpeech: TextToSpeech? = null
    private var futureSpeechData: FutureSpeechData? = null
    private var textToSpeakOnInit: String? = null
    // TODO rkolmos 10/19/2018 make this a dagger binding so users can add their own keys.
    private val specialKeyMap: MutableMap<String, (Duration) -> Long> = mutableMapOf()

    init {
        specialKeyMap["start"] = { _ -> 0 }
        // halfway rounding down to the nearest second is good enough.
        specialKeyMap["halfway"] = { duration -> duration.seconds / 2 }
        specialKeyMap["end"] = { duration -> duration.seconds }
    }

    // Queuing behavior is passed to all calls to TextToSpeech.speak(), default value is QUEUE_ADD
    var queueingBehavior = TextToSpeech.QUEUE_ADD
    // The duration of the sound played by playSound() and the vibration triggered by vibrate() in ms, default value
    // is 500.
    var defaultVibrateAndSoundDurationMillis: Long = 500
    // The amount of time in seconds that speeches will still be spoken for if the service is initialized after
    // the speeches exact time. (ie, if speechGracePeriod = 5 and the service is initialized 4 seconds after the step
    // formally starts, instructions with the key "start" will still be spoken.
    var speechGracePeriod: Long = 5
    // The public getter for state should have type live data so client's don't modify it.
    val state: LiveData<TextToSpeechState>
        get() = _state
    // The private state value is mutable so the service can update it's state as needed
    private val _state = MutableLiveData<TextToSpeechState>()
    private val bindCount = MutableLiveData<Int>()
    private val cleanupMediator = MediatorLiveData<Pair<Int, TextToSpeechState>>()
    private val cleanupObserver: Observer<Pair<Int, TextToSpeechState>>

    init {
        bindCount.postValue(0)
        _state.postValue(TextToSpeechState(IDLE, null))
        // We observe the state so that if every client unbinds and the service stops speaking, the service
        // stops itself. In the initial state the service is IDLE, and has 0 bound clients.
        cleanupMediator.postValue(Pair(0, TextToSpeechState(IDLE, null)))
        cleanupMediator.addSource(bindCount) { count ->
            // these checks must be null safe to prevent the service from crashing
            cleanupMediator.postValue(
                    Pair(count ?: 0,
                            cleanupMediator.value?.second ?: TextToSpeechState(IDLE, null)))
        }
        cleanupMediator.addSource(_state) { s ->
            cleanupMediator.postValue(Pair(cleanupMediator.value?.first ?: 0,
                    s ?: TextToSpeechState(IDLE, null)))
        }
        cleanupObserver = Observer { pair ->
            if (pair != null && pair.first == 0 && pair.second.speakingState != SPEAKING
                    && pair.second.speakingState != QUEUED) {
                // If there are no bound clients, and speech isn't either currently happening, or queued to happen
                // we should stop this service.
                stopSelf()
            }
        }

        cleanupMediator.observeForever(cleanupObserver)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        val bindCountValue = (bindCount.value ?: 0) + 1
        bindCount.postValue(bindCountValue)
        LOGGER.debug("onBind() called bindCount = $bindCount")
        return serviceBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val bindCountValue = (bindCount.value ?: 1) - 1
        bindCount.postValue(bindCountValue)
        LOGGER.debug("onUnbind() called bindCount = $bindCount")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        cleanupMediator.removeObserver(cleanupObserver)
        clear()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val languageAvailable = textToSpeech?.isLanguageAvailable(Locale.getDefault())
            // >= 0 means LANG_AVAILABLE, LANG_COUNTRY_AVAILABLE, or LANG_COUNTRY_VAR_AVAILABLE
            if (languageAvailable != null && languageAvailable >= 0) {
                textToSpeech?.let { tts ->
                    tts.language = Locale.getDefault()
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onDone(text: String?) {
                            if (!tts.isSpeaking) {
                                val data = futureSpeechData
                                if (data == null || !data.hasQueuedSpeech()) {
                                    // The TTS has just finished so we can shut it down and set the state to idle
                                    _state.postValue(TextToSpeechState(IDLE, null))
                                    tts.shutdown()
                                } else {
                                    // More speech is queued
                                    _state.postValue(TextToSpeechState(QUEUED, null))
                                }
                            }
                        }

                        override fun onError(text: String?) {
                            _state.postValue(TextToSpeechState(ERROR, null))
                        }

                        override fun onStart(text: String?) {
                            _state.postValue(TextToSpeechState(SPEAKING, text))
                        }
                    })
                }

                textToSpeakOnInit?.let { speakText(it) }
            } else {
                LOGGER.warn("Language not available for TTS")
                textToSpeech?.shutdown()
                textToSpeech = null
            }
        } else {
            LOGGER.warn("Failed to initialize TTS with error code $status")
            textToSpeech = null
        }
    }

    /**
     * Register the given speech map to be spoken at the times determined by the given countdown and duration.
     * @param duration the duration of the current step
     * @param countdown the countdown LiveData for the current step
     * @param speechMap the map where keys are offsets into the countdown and values are instructions that should be
     * spoken to the user.
     */
    fun registerSpeechesOnCountdown(duration: Duration, countdown: LiveData<Long>, speechMap: Map<String, String>) {
        LOGGER.debug(
                "register speeches on countdown called,\nduration: $duration\ncountdown: $countdown\nspeechMap: $speechMap")
        textToSpeech = TextToSpeech(this, this)
        val canonicalSpeechMap = getCanonicalSpeechMap(speechMap, duration).toMutableMap()
        val currentCount = countdown.value
        if (currentCount != null) {
            // filter out all the speeches that shouldn't be spoken right now, and sort the ones that should in
            // increasing order of offset.
            val skippedSpeeches = canonicalSpeechMap.filter { entry ->
                val currentOffset = duration.seconds - currentCount
                entry.key < currentOffset && currentOffset - entry.key < speechGracePeriod
            }.toSortedMap()

            for (speech in skippedSpeeches) {
                if (!speech.value.second) {
                    LOGGER.debug(
                            "Spoken Instruction: {}, should have been spoken before the service was initialized," +
                                    " and was still within the grace period.")
                    speakText(speech.value.first)
                    canonicalSpeechMap[speech.key] = speech.value.copy(second = true)
                }
            }
        }

        futureSpeechData = FutureSpeechData(canonicalSpeechMap, duration, countdown)
        futureSpeechData?.countdown?.observeForever(::onCountdownChanged)
    }

    /**
     * Stops speaking and removes all registered speeches.
     */
    fun clear() {
        LOGGER.debug("clear() called")
        textToSpeech?.let { tts ->
            // Stop speaking and shut down the current text to speech.
            if (tts.isSpeaking) {
                tts.stop()
            }

            tts.shutdown()
            textToSpeech = null
        }

        futureSpeechData?.countdown?.removeObserver(::onCountdownChanged)
        futureSpeechData = null
    }

    /**
     * Adds the given text to the TextToSpeech's output in a manner consistent with the current queueingBehavior
     * @param text the text to speak to the user.
     */
    fun speakText(text: String) {
        LOGGER.debug("speakText() called with text \"$text\"")
        textToSpeech?.let { tts ->
            // Setting this will guarantee the text gets spoken in the case that tts isn't set up yet
            textToSpeakOnInit = text
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                speakGreater21(text, tts)
            } else {
                speakUnder20(text, tts)
            }
        }
    }

    /**
     * Plays a tone to the user. This method doesn't wait for the text to speech to be idle before playing the tone.
     */
    fun playSound(duration: Int = defaultVibrateAndSoundDurationMillis.toInt()) {
        LOGGER.debug("playSound() called with duration = $duration")
        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 50) // 50 = half volume
        // Play a low and high tone for 500 ms at full volume
        toneG.startTone(ToneGenerator.TONE_CDMA_LOW_L, duration)
        toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_L, duration)
    }

    @RequiresPermission(permission.VIBRATE)
    fun vibrate(duration: Long = defaultVibrateAndSoundDurationMillis) {
        LOGGER.debug("vibrate() called with duration = $duration")
        (getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(duration)
    }

    /**
     * Returns a canonical version of the speech map provided with all special keys converted to their value in
     * seconds. A special key is some String key in the speech map that maps to a specific offset. Supported
     * special keys are,
     *      START_KEY -> 0
     *      HALFWAY_KEY -> duration.seconds / 2
     *      END_KEY -> duration.seconds
     * @param speechMap The map of String offsets to spoken instructions, which may contain special case keys
     * @param duration The duration of the current step, used to compute the value of special case keys
     */
    private fun getCanonicalSpeechMap(speechMap: Map<String, String>, duration: Duration)
            : Map<Long, Pair<String, Boolean>> {
        val result: MutableMap<Long, Pair<String, Boolean>> = mutableMapOf()
        for (entry in speechMap.entries) {
            val startOffset: Long? =
                    when {
                        specialKeyMap.containsKey(entry.key) -> specialKeyMap[entry.key]!!(duration)
                        else -> entry.key.toLongOrNull()
                    }
            when {
                startOffset == null ->
                    LOGGER.warn("failed to parse start time for $entry, omitting this spoken instruction")
                startOffset > duration.seconds -> {
                    LOGGER.debug("start offset for $entry, exceeds duration $duration, treating instruction as an end.")
                    result[duration.seconds] = Pair(entry.value, false)
                }
                else -> result[startOffset] = Pair(entry.value, false)
            }
        }

        LOGGER.debug("getCanonicalSpeechMap() called, \nduration=$duration\ninput: $speechMap\n\noutput:$result")
        return result
    }

    /**
     * Uses the given TextToSpeech to speak the given text assuming the phone's
     * api is pre build code 20.
     * @param text the text to speak to the user.
     * @param tts the TextToSpeech to use to speak the text.
     */
    @SuppressWarnings("deprecation")
    private fun speakUnder20(text: String, tts: TextToSpeech) {
        val map: HashMap<String, String> = HashMap()
        map[Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        tts.speak(text, queueingBehavior, map)
    }

    /**
     * Uses the given TextToSpeech to speak the given text assuming the phone's
     * api is build code 21 or greater.
     * @param text the text to speak to the user.
     * @param tts the TextToSpeech to use to speak the text.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    private fun speakGreater21(text: String, tts: TextToSpeech) {
        val utteranceId = hashCode().toString()
        tts.speak(text, queueingBehavior, null, utteranceId)
    }

    /**
     * Called when the countdown for the provided step changes.
     * @param count the current value of the countdown.
     */
    private fun onCountdownChanged(count: Long?) {
        val data = futureSpeechData
        if (data != null && count != null) {
            val elapsedTime = data.duration.seconds - count
            val pair = data.speechMap[elapsedTime]
            if (pair != null && !pair.second) {
                speakText(pair.first)
                data.speechMap[elapsedTime] = pair.copy(second = true)
            }
        }
    }
}