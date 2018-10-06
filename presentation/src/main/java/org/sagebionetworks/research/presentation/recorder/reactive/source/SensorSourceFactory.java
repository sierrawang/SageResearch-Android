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

package org.sagebionetworks.research.presentation.recorder.reactive.source;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

import static com.google.common.base.Preconditions.checkNotNull;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorEvent;
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import java.util.Set;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * This class is a Wrapper around ReactiveSensors that allows subscribing to a set of sensors with a single call.
 */
public class SensorSourceFactory {
    @NonNull
    protected Set<FlowableEmitter<ReactiveSensorEvent>> observers;

    @NonNull
    protected ReactiveSensors reactiveSensors;

    @NonNull
    protected Set<Integer> sensorTypes;

    public static class SensorConfig {
        public final int sensorType;

        public final int samplingPeriodInUs;

        public final BackpressureStrategy backpressureStrategy;

        private SensorConfig(final int sensorType, final int samplingPeriodInUs,
                final BackpressureStrategy backpressureStrategy) {
            this.sensorType = sensorType;
            this.samplingPeriodInUs = samplingPeriodInUs;
            this.backpressureStrategy = backpressureStrategy;
        }

        public static class SensorConfigBuilder {
            private BackpressureStrategy backpressureStrategy = BackpressureStrategy.LATEST;

            private int samplingPeriodInUs;

            private final int sensorType;

            public SensorConfigBuilder(int sensorType) {
                this.sensorType = sensorType;
                this.samplingPeriodInUs = SENSOR_DELAY_NORMAL;
            }

            public SensorConfig build() {
                return new SensorConfig(sensorType, samplingPeriodInUs, backpressureStrategy);
            }

            public SensorConfigBuilder setBackpressureStrategy(
                    @NonNull final BackpressureStrategy backpressureStrategy) {
                this.backpressureStrategy = checkNotNull(backpressureStrategy);
                return this;
            }

            public SensorConfigBuilder setSamplingPeriodInUs(final int samplingPeriodInUs) {
                this.samplingPeriodInUs = samplingPeriodInUs;
                return this;
            }
        }
    }

    public static class SensorAccuracyChangeEvent {
        public final Sensor sensor;

        public final int accuracy;

        public SensorAccuracyChangeEvent(@NonNull ReactiveSensorEvent reactiveSensorEvent) {
            checkNotNull(reactiveSensorEvent);

            this.sensor = reactiveSensorEvent.getSensor();
            this.accuracy = reactiveSensorEvent.getAccuracy();
        }

        public SensorAccuracyChangeEvent(@NonNull final Sensor sensor, final int accuracy) {
            this.sensor = checkNotNull(sensor);
            this.accuracy = accuracy;
        }
    }

    @Inject
    public SensorSourceFactory(@NonNull ReactiveSensors reactiveSensors) {
        this.reactiveSensors = checkNotNull(reactiveSensors);
    }

    @NonNull
    @VisibleForTesting
    Flowable<ReactiveSensorEvent> getReactiveSensorEvents(SensorConfig config) {
        if (!reactiveSensors.hasSensor(config.sensorType)) {
            // TODO: handle missing sensors
            return Flowable.empty();
        }
        return reactiveSensors
                .observeSensor(config.sensorType, config.samplingPeriodInUs, null, config.backpressureStrategy)
                .subscribeOn(Schedulers.computation());
    }

    @NonNull
    public Flowable<SensorEvent> getSensorEvents(SensorConfig config) {
        return getReactiveSensorEvents(config).filter(ReactiveSensorFilter.filterSensorChanged())
                .map(ReactiveSensorEvent::getSensorEvent);
    }

    @NonNull
    public Flowable<SensorAccuracyChangeEvent> getSensorAccuracyChangeEvents(SensorConfig config) {
        return getReactiveSensorEvents(config).filter(ReactiveSensorFilter.filterAccuracyChanged())
                .map(SensorAccuracyChangeEvent::new);
    }
}