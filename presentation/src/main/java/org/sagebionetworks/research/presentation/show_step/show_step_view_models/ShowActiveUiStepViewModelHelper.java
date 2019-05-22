package org.sagebionetworks.research.presentation.show_step.show_step_view_models;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Nullable;
import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.step.ui.active.Command;
import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel;
import org.sagebionetworks.research.presentation.speech.TextToSpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.internal.operators.flowable.FlowableFromObservable;

public class ShowActiveUiStepViewModelHelper <S extends ActiveUIStepView> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ShowActiveUiStepViewModelHelper.class);

    private class Connection implements ServiceConnection {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
            isBound = true;
            textToSpeechService = ((TextToSpeechService.Binder)iBinder).getService();
            textToSpeechService.registerSpeechesOnCountdown(
                    mStepView.getDuration(), getCountdown(),
                    formattedSpokenInstructions(), mStepView.getCommands());
            onSpeechServiceConnected();
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            isBound = false;
            textToSpeechService = null;
        }
    }

    protected void onSpeechServiceConnected() {
        if (!mStepView.getCommands().contains(Command.TRANSITION_AUTOMATICALLY)) {
            //For steps that don't start their countdown automatically we need to start text to speech once the service is bound.
            textToSpeechService.forceSpeakStartCommands();
        }
    }

    private Connection connection;
    private boolean isBound;
    protected TextToSpeechService textToSpeechService;
    @android.support.annotation.Nullable
    private Observer<TextToSpeechService.TextToSpeechState> textToSpeechStateObserver;

    protected LiveData<Long> tempLiveData;
    protected MutableLiveData<Long> countdown;

    protected @Nullable
    Observer<Long> countDownObserver;
    protected @NonNull Long currentDuration = 0L;
    protected @NonNull Long currentStartingValue = 0L;

    private Application mApplication;
    private PerformTaskViewModel mPerformTaskViewModel;
    private S mStepView;

    private final Instant startTime;

    public ShowActiveUiStepViewModelHelper(@NonNull Application application, PerformTaskViewModel performTaskViewModel, final S stepView) {
        mApplication = application;
        mPerformTaskViewModel = performTaskViewModel;
        mStepView = stepView;
        this.countdown = new MutableLiveData<>();

        connection = new Connection();
        if (!isSpokenInstructionsEmpty()) {
            bindTextToSpeechService();
        }
        this.startTime = Instant.now();
    }

    private Application getApplication() {
        return mApplication;
    }

    /**
     * @return the instant when the step view started
     */
    public Instant getStartTime() {
        return startTime;
    }

    public void goForward() {
        addStepResultAfterCountdown();
        if (!isSpokenInstructionsEmpty() && isBound) {
            if (textToSpeechStateObserver == null) {
                LOGGER.info("TTS service running, before we go forward, we must check if the state is IDLE");
                textToSpeechStateObserver = state -> {
                    if (state != null) {
                        LOGGER.info("Text to speech state changed to " + state.getSpeakingState());
                        if (!TextToSpeechService.TextToSpeechState.SpeakingState.SPEAKING.equals(state.getSpeakingState()) &&
                                !TextToSpeechService.TextToSpeechState.SpeakingState.QUEUED.equals(state.getSpeakingState())) {
                            LOGGER.info("TTS is IDLE, we can move to the next step.");
                            textToSpeechService.getState().removeObserver(textToSpeechStateObserver);
                            mPerformTaskViewModel.goForward();
                        }
                    }
                };
            } else {
                LOGGER.info("Removing previous TTS state observer before adding another one");
                textToSpeechService.getState().removeObserver(textToSpeechStateObserver);
            }
            LOGGER.info("Adding TTS state observer before before moving forward");
            textToSpeechService.getState().observeForever(textToSpeechStateObserver);
        } else {
            LOGGER.info("No TTS service running, move to next step");
            mPerformTaskViewModel.goForward();
        }
    }

    /**
     * Add the basic step result to mark the step as completed, can be overridden for custom results.
     */
    protected void addStepResultAfterCountdown() {
        // Because we use SkipToActionView, we must explicitly set a base result to move forward normally
        // Usually, this is done automatically in showStepViewModel.handleAction(),
        // But, if we already have NavigationResult, it won't create the default one to navigate normally.
        mPerformTaskViewModel.addStepResult(
                new ResultBase(mStepView.getIdentifier(), getStartTime(), Instant.now()));
    }

    /**
     * Sub-classes can override to do custom formatting.
     * If this functions returns a stepView.getSpokenInstructions() with a different map keySet count,
     * then isSpokenInstructionsEmpty() must be overwritten to check against the map returned from this function.
     * @return a spoken instructions map that has its values correctly formatted for any cases where,
     *         "%s" or "%d" need filled in with a dynamic value.
     */
    protected Map<String, String> formattedSpokenInstructions() {
        return mStepView.getSpokenInstructions();
    }

    /**
     * @return true if there are no instructions to speak for this step, false if there are.
     */
    protected boolean isSpokenInstructionsEmpty() {
        return mStepView.getSpokenInstructions().isEmpty();
    }

    protected void bindTextToSpeechService() {
        if (getApplication() == null) {
            return; // NPE guard
        }
        Intent intent = new Intent(getApplication(), TextToSpeechService.class);
        getApplication().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    protected void cleanup() {
        if (textToSpeechService != null && textToSpeechStateObserver != null) {
            textToSpeechService.getState().removeObserver(textToSpeechStateObserver);
        }
        if (isBound) {
            if (getApplication() != null) {
                getApplication().unbindService(connection);
            }
        }
    }

    /**
     * This function starts the countdown from a value equal to the step view's provided duration,
     * and counts down at second intervals.
     * To get a countdown update every second, observe countdown LiveData.
     */
    public void startCountdown() {
        removeAnyPreviousObservers();
        currentStartingValue = 0L;
        currentDuration = mStepView.getDuration().getSeconds();
        resumeCountdown();
    }

    /**
     * @return true if countdown is currently running, false if not running or paused.
     */
    public boolean isCountdownRunning() {
        return countDownObserver != null;
    }

    /**
     * @return true if the countdown is currently paused, false otherwise.
     */
    public boolean isCountdownPaused() {
        return !isCountdownRunning() && (
                currentStartingValue != 0 && // hasn't started yet
                        !currentStartingValue.equals(currentDuration)); // is done
    }

    /**
     * This function pauses the countdown at its current countdown value.
     */
    public void pauseCountdown() {
        removeAnyPreviousObservers();
    }

    /**
     * This resumes the countdown from whatever its countdown value was when pauseCountdown() was called.
     * If pauseCountdown() was never called, nothing is done.
     */
    public void resumeCountdown() {
        if (isCountdownRunning()) {
            return;  // Guard against pauseCountdown() never being called.
        }
        this.tempLiveData = LiveDataReactiveStreams.fromPublisher(
                new FlowableFromObservable<>(
                        Observable.<Long>intervalRange(currentStartingValue, currentDuration + 1,
                                0, 1, TimeUnit.SECONDS)
                                .map(i -> currentDuration - i)));
        countDownObserver = this::updateCountdown;
        this.tempLiveData.observeForever(countDownObserver);
    }

    /**
     * Called every second through the FlowableFromObservable.
     * @param countDown current value of the count down
     */
    protected void updateCountdown(Long countDown) {
        currentStartingValue = currentDuration - countDown;
        this.countdown.setValue(countDown);
        if (countDown == 0 && mStepView.getCommands().contains(Command.TRANSITION_AUTOMATICALLY)) {
            goForward();
        }
    }

    /**
     * This stops the countdown by removing the countdown observer.
     */
    protected void removeAnyPreviousObservers() {
        if (countDownObserver != null) {
            this.tempLiveData.removeObserver(countDownObserver);
            countDownObserver = null;
        }
    }

    public LiveData<Long> getCountdown() {
        return this.countdown;
    }





}
