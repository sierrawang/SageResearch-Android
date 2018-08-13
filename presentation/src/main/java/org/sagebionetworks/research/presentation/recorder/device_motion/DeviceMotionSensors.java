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

package org.sagebionetworks.research.presentation.recorder.device_motion;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * This class is a Wrapper around ReactiveSensors that allows subscribing to a set of sensors with a single call.
 */
public class DeviceMotionSensors implements FlowableOnSubscribe<ReactiveSensorEvent> {
    protected Disposable disposable;

    @NonNull
    protected Set<FlowableEmitter<ReactiveSensorEvent>> observers;

    @NonNull
    protected ReactiveSensors reactiveSensors;

    protected int sensorDelay;

    @NonNull
    protected Set<Integer> sensorTypes;

    public DeviceMotionSensors(@NonNull Context context, @NonNull Set<Integer> sensorTypes, int sensorDelay) {
        this.reactiveSensors = new ReactiveSensors(context);
        List<Flowable<ReactiveSensorEvent>> allFlowables = new ArrayList<>();
        for (Integer sensorType : sensorTypes) {
            allFlowables.add(this.reactiveSensors.observeSensor(sensorType, sensorDelay));
        }

        this.disposable = Flowable.merge(allFlowables)
                .doOnNext(event -> {
                    for (FlowableEmitter<ReactiveSensorEvent> emitter : observers) {
                        emitter.onNext(event);
                    }
                }).subscribe();
        this.sensorTypes = sensorTypes;
        this.sensorDelay = sensorDelay;
        this.observers = new HashSet<>();
    }

    public void cancel() {
        for (FlowableEmitter<ReactiveSensorEvent> emitter : observers) {
            emitter.onError(new Throwable("Recording cancelled"));
        }

        this.disposable.dispose();
    }

    public void complete() {
        for (FlowableEmitter<ReactiveSensorEvent> emitter : observers) {
            emitter.onComplete();
        }

        this.disposable.dispose();
    }

    @NonNull
    public Flowable<ReactiveSensorEvent> getFlowable() {
        return Flowable.create(this, BackpressureStrategy.BUFFER);
    }

    @Override
    public void subscribe(final FlowableEmitter<ReactiveSensorEvent> emitter) throws Exception {
        this.observers.add(emitter);
    }
}