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

package org.sagebionetworks.research.presentation.recorder.reactive;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.presentation.recorder.RecorderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;

/**
 * A ReactiveRecorder is a recorder which records based on a stream of events, and supports maintaining a FileResult
 * via a ReactiveDataLogger, a Summary via a SummarySubscriber, and a current state, via a CurrentStateSubscriber.
 * <p>
 * This class is intended to make making recorders from anything that can be turned into a stream of events easier,
 * and more convenient.
 *
 * @param <E>
 *         The type of event from the stream this recorder records based on.
 */
public abstract class ReactiveRecorder<E, R extends Result> extends RecorderBase<R> {
    private final Logger LOGGER = LoggerFactory.getLogger(ReactiveRecorder.class);

    private CompositeDisposable compositeDisposable;

    // this allows us to stop connectableFlowableData
    private Disposable connectableFlowableConnectionDisposable;

    private ConnectableFlowable<E> eventConnectableFlowable;

    public static <T, V, R extends Result, S extends Result> ReactiveRecorder<V, S> transform(
            ReactiveRecorder<T, R> originalRecorder,
            FlowableTransformer<T, V> flowableTransformer,
            Function<Maybe<R>, Maybe<S>> resultTransform) {
        return new ReactiveRecorder<V, S>(originalRecorder.identifier,
                originalRecorder.eventConnectableFlowable.compose(flowableTransformer).publish()) {

            @NonNull
            @Override
            public Maybe<S> getResult() {
                return resultTransform.apply(originalRecorder.getResult());
            }
        };
    }

    public static <T, V, R extends Result> ReactiveRecorder<V, R> transform(
            ReactiveRecorder<T, R> originalRecorder,
            FlowableTransformer<T, V> flowableTransformer) {
        return transform(originalRecorder, flowableTransformer, Functions.identity());
    }

    public static <T, R extends Result, S extends Result> ReactiveRecorder<T, S> transform(
            ReactiveRecorder<T, R> originalRecorder,
            Function<Maybe<R>, Maybe<S>> resultTransform) {
        return transform(originalRecorder, upstream -> upstream, resultTransform);
    }

    public ReactiveRecorder(@NonNull final String identifier, ConnectableFlowable<E> eventConnectableFlowable) {
        super(identifier);
        this.eventConnectableFlowable = eventConnectableFlowable
                .doFinally(this::doFinally)
                .publish();
        this.compositeDisposable = new CompositeDisposable();
    }

    @NonNull
    public Flowable<E> getEventConnectableFlowable() {
        return this.eventConnectableFlowable;
    }

    @Override
    @CallSuper
    public void startRecorder() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting recorder {}" + identifier);
        }
        eventConnectableFlowable.connect(d -> {
            compositeDisposable.add(d);
            connectableFlowableConnectionDisposable = d;
        });
    }

    @Override
    @CallSuper
    public void stopRecorder() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Stopping recorder {}" + identifier);
        }
        connectableFlowableConnectionDisposable.dispose();
    }

    @VisibleForTesting
    void doFinally() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Do finally recorder {}" + identifier);
        }
        compositeDisposable.dispose();
    }
}
