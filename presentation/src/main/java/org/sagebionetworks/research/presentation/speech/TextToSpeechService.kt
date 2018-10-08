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

import android.Manifest
import android.annotation.TargetApi
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.speech.tts.TextToSpeech
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
import javax.inject.Inject

class TextToSpeechService : DaggerService(), TextToSpeech.OnInitListener {
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

    private data class FutureSpeechData(val speechMap: Map<Long, String>, val duration: Duration,
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
    private var bindCount: Int = 0
    // values set when after a step is set by the client
    private var textToSpeech: TextToSpeech? = null
    private var futureSpeechData: FutureSpeechData? = null
    private var textToSpeakOnInit: String? = null
    @Inject lateinit var specialKeyMap: Map<String, (Duration) -> Long>
    // Queuing behavior is passed to all calls to TextToSpeech.speak(), default value is QUEUE_ADD
    var queueingBehavior = TextToSpeech.QUEUE_ADD
    // The duration of the sound played by playSound() and the vibration triggered by vibrate() in ms, default value
    // is 500.
    var defaultVibrateAndSoundDurationMillis: Long = 500
    // The public getter for state should have type live data so client's don't modify it.
    val state: LiveData<TextToSpeechState>
        get() = _state
    // The private state value is mutable so the service can update it's state as needed
    private val _state = MutableLiveData<TextToSpeechState>()
    private val stateObserver: Observer<TextToSpeechState>

    init {
        _state.value = TextToSpeechState(IDLE, null)
        // We observe the state so that if every client unbinds and the service stops speaking, the service
        // stops itself.
        stateObserver = Observer { state ->
            if (state != null) {
                if (bindCount == 0 && state.speakingState != SPEAKING && state.speakingState != QUEUED) {
                    stopSelf()
                }
            }
        }
        _state.observeForever(stateObserver)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        bindCount++
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onBind() called bindCount = $bindCount")
        }

        return serviceBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        bindCount--
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("onUnbind() called bindCount = $bindCount")
        }

        if (bindCount == 0 && _state.value!!.speakingState != SPEAKING) {
            stopSelf()
        }

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _state.removeObserver(stateObserver)
        futureSpeechData?.countdown?.removeObserver(::onCountdownChanged)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val languageAvailable = textToSpeech!!.isLanguageAvailable(Locale.getDefault())
            // >= 0 means LANG_AVAILABLE, LANG_COUNTRY_AVAILABLE, or LANG_COUNTRY_VAR_AVAILABLE
            if (languageAvailable >= 0) {
                textToSpeech!!.language = Locale.getDefault()
                textToSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onDone(text: String?) {
                        val tts = textToSpeech
                        if (tts != null && !tts.isSpeaking) {
                            val data = futureSpeechData
                            if (data == null || !data.hasQueuedSpeech()) {
                                _state.value = TextToSpeechState(IDLE, null)
                            } else {
                                _state.value = TextToSpeechState(QUEUED, null)
                            }
                        }
                    }

                    override fun onError(text: String?) {
                        _state.value = TextToSpeechState(ERROR, null)
                    }

                    override fun onStart(text: String?) {
                        _state.value = TextToSpeechState(SPEAKING, text)
                    }
                })
                if (textToSpeakOnInit != null) {
                    speakText(textToSpeakOnInit!!)
                }
            } else {
                textToSpeech = null
            }
        } else {
            LOGGER.debug("Failed to initialize TTS with error code $status")
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
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("register speeches on countdown called,\nduration: $duration\ncountdown: $countdown\n" +
                    "speechMap: $speechMap")
        }

        textToSpeech = TextToSpeech(this, this)
        futureSpeechData = FutureSpeechData(getCanonicalSpeechMap(speechMap, duration), duration, countdown)
        futureSpeechData!!.countdown.observeForever(::onCountdownChanged)
    }

    /**
     * Stops speaking and removes all registered speeches.
     */
    fun clear() {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("clear() called")
        }

        val tts = textToSpeech
        // Stop speaking and shut down the current text to speech.
        if (tts != null) {
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
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("speakText() called with text = $text")
        }

        val tts = textToSpeech
        // Setting this will guarantee the text gets spoken in the case that tts isn't set up yet
        textToSpeakOnInit = text
        if (tts != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("playSound() called with duration = $duration")
        }

        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 50) // 50 = half volume
        // Play a low and high tone for 500 ms at full volume
        toneG.startTone(ToneGenerator.TONE_CDMA_LOW_L, duration)
        toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_L, duration)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun vibrate(duration: Long = defaultVibrateAndSoundDurationMillis) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("vibrate() called with duration = $duration")
        }

        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(duration)
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
    private fun getCanonicalSpeechMap(speechMap: Map<String, String>, duration: Duration): Map<Long, String> {
        val result: MutableMap<Long, String> = mutableMapOf()
        for (entry in speechMap.entries) {
            val startOffset: Long? =
                    when {
                        specialKeyMap.containsKey(entry.key) -> specialKeyMap[entry.key]!!(duration)
                        else -> entry.key.toLongOrNull()
                    }
            when {
                startOffset == null -> LOGGER.warn("failed to parse start time for $entry," +
                        " omitting this spoken instruction")
                startOffset > duration.seconds -> {
                    if (LOGGER.isDebugEnabled) {
                        LOGGER.debug("start offset for $entry, exceeds duration $duration, " +
                                "treating instruction as an end.")
                    }

                    result[duration.seconds] = entry.value
                }
                else -> result[startOffset] = entry.value
            }
        }

        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("getCanonicalSpeechMap() called, \nduration=$duration\ninput: $speechMap\n" +
                    "\noutput:$result")
        }

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
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "MessageId"
        tts.speak(text, queueingBehavior, map)
    }

    /**
     * Uses the given TextToSpeech to speak the given text assuming the phone's
     * api is build code 21 or greater.
     * @param text the text to speak to the user.
     * @param tts the TextToSpeech to use to speak the text.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
            val text = data.speechMap[elapsedTime]
            if (text != null) {
                speakText(text)
            }
        }
    }
}