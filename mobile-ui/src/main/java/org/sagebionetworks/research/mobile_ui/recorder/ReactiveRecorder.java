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

package org.sagebionetworks.research.mobile_ui.recorder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.reactivestreams.Subscriber;
import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.sagebionetworks.research.mobile_ui.recorder.data.DataLogger;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * A ReactiveRecorder is a recorder which records based on a stream of events, and supports maintaining a FileResult
 * via a DataLogger, a Summary via a SummarySubscriber, and a current state, via a CurrentStateSubscriber.
 *
 * This class is intended to make making recorders from anything that can be turned into a stream of events easier,
 * and more convenient.
 *
 * @param <S> The type of summary that this recorder produces.
 * @param <C> The type of current state that this recorder produces.
 * @param <E> The type of event from the stream this recorder records based on.
 */
public abstract class ReactiveRecorder<S, C, E> extends RecorderBase {
    @Nullable
    protected final SummarySubscriber<S, E> summarySubscriber;
    @Nullable
    protected final CurrentStateSubscriber<C, E> currentStateSubscriber;
    @Nullable
    protected final DataLogger dataLogger;
    @NonNull
    protected Flowable<E> eventFlowable;

    public ReactiveRecorder(@NonNull final String identifier,
            @Nullable final String startStepIdentifier,
            @Nullable final String stopStepIdentifier,
            @Nullable final DataLogger dataLogger,
            @Nullable final SummarySubscriber<S, E> summarySubscriber,
            @Nullable final CurrentStateSubscriber<C, E> currentStateSubscriber) {
        super(identifier, startStepIdentifier, stopStepIdentifier);
        this.summarySubscriber = summarySubscriber;
        this.currentStateSubscriber = currentStateSubscriber;
        this.dataLogger = dataLogger;
        this.eventFlowable = this.intializeEventFlowable();

    }

    @Override
    public void start() {
        super.start();
        this.eventFlowable = this.eventFlowable.subscribeOn(Schedulers.computation());
        if (this.summarySubscriber != null) {
            this.eventFlowable = this.eventFlowable.doOnEach(this.summarySubscriber);
        }

        if (this.currentStateSubscriber != null) {
            this.eventFlowable = this.eventFlowable.doOnEach(this.currentStateSubscriber);
        }

        if (this.dataLogger != null) {
            this.eventFlowable.map(this::getDataString)
                    .observeOn(Schedulers.io())
                    .subscribe(this.dataLogger);
        }
    }

    @NonNull
    public Flowable<E> getEventFlowable() {
        return this.eventFlowable;
    }

    @Override
    public void cancel() {
        super.cancel();
        if (this.dataLogger != null) {
            this.dataLogger.onError(new Throwable("Recorder Canceled"));
        }
    }

    /**
     * Returns a single that when complete will contain the FileResult with the full data this recorder recorded. If
     * there is no data recorder for this recorder this method will return null.
     * @return a single that when complete will contain the FileResult with the full data this recorder recorded.
     */
    @Nullable
    public Single<FileResult> getFileResult() {
        return this.dataLogger != null ? Single.create(this.dataLogger) : null;
    }

    /**
     * Returns a single that when complete will contain a summary of the data this recorder recorded. If there is
     * no SummarySubscriber for this recorder this method will return null.
     * @return a single that when complete will contain a summary of the data this recorder recorded.
     */
    @Nullable
    public Single<S> getSummary() {
        return this.summarySubscriber != null ? this.summarySubscriber.getSummary() : null;
    }

    /**
     * Returns a representation of the current state of this recorder. IF there is no CurrentStateSubscriber this
     * method will return null.
     * @return a representation of the current state of this recorder.
     */
    @Nullable
    public C getCurrentState() {
        return this.currentStateSubscriber != null ? this.currentStateSubscriber.getCurrentState() : null;
    }

    /**
     * Initializes the Flowable that the events will come from for this recorder.
     * @return the Flowable that the events will come from for this recorder.
     */
    @NonNull
    protected abstract Flowable<E> intializeEventFlowable();

    /**
     * Converts an event into a String that can be recorded by the DataLogger into a file.
     * @param event The event to convert into a string.
     * @return The string conversion of the given event.
     */
    @NonNull
    protected abstract String getDataString(@NonNull E event);

    /**
     * Subscribes to the same set of ReactiveSensorEvents provided to the DeviceMotionRecorder, and maintains a
     * summary result that becomes available after the recorder has finished. This is useful when wanting to display
     * a summary result to the UI later in the task without having to wait for or parse the FileResult.
     * @param <S> The type of the summary result.
     * @param <E> The type of the event that the subscriber subscribes to.
     */
    public interface SummarySubscriber<S, E> extends Subscriber<E> {
        /**
         * Returns a Single that when completed provides the summary result for this recorder. It is expected that
         * the single will complete at the same time as the recorder completes.
         * @return a Single that when completed provides the summary result for this recorder.
         */
        Single<S> getSummary();
    }

    /**
     * Subscribes to the same set of ReactiveSensorEvents provided to the DeviceMotionRecorder, and maintains a
     * representation of the current state of this recorder. This is useful when wanting to display  information
     * about the recorders current readings while the recorder is running (e.g. if the user were to measure their
     * heart rate and the UI wanted to display this in real time.
     * @param <C> The type of the representation of the recorders current state.
     * @param <E> The type of the event that the subscriber subscribes to.
     */
    public interface CurrentStateSubscriber<C, E> extends Subscriber<E> {
        /**
         * Returns a representation of the current state of this recorder.
         * @return a representation the current state of this recorder.
         */
        C getCurrentState();
    }
}
