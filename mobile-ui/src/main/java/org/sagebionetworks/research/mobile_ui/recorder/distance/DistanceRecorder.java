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

package org.sagebionetworks.research.mobile_ui.recorder.distance;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.reactivestreams.Subscription;
import org.sagebionetworks.research.domain.recorder.RecorderConfig;
import org.sagebionetworks.research.mobile_ui.recorder.DataRecorder;
import org.sagebionetworks.research.mobile_ui.recorder.ReactiveRecorder;
import org.sagebionetworks.research.mobile_ui.recorder.distance.DistanceRecorder.DistanceState;
import org.sagebionetworks.research.mobile_ui.recorder.distance.DistanceRecorder.DistanceSummary;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public abstract class DistanceRecorder extends ReactiveRecorder<DistanceSummary, DistanceState, Location> {
    public static class DistanceState {
        private float currentTotalDistance;

        private Location firstLocation;

        private float lastDistanceChange;

        private Location lastLocation;

        public DistanceState() {
            this.lastLocation = null;
            this.firstLocation = null;
            this.currentTotalDistance = 0;
            this.lastDistanceChange = 0;
        }

        public Location getFirstLocation() {
            return this.firstLocation;
        }

        public float getLastDistanceChange() {
            return this.lastDistanceChange;
        }

        public float getTotalDistance() {
            return this.currentTotalDistance;
        }

        public void update(Location nextLocation) {
            if (this.lastLocation != null) {
                this.lastDistanceChange = this.lastLocation.distanceTo(nextLocation);
                this.currentTotalDistance += this.lastDistanceChange;
            } else {
                this.firstLocation = nextLocation;
            }

            this.lastLocation = nextLocation;
        }
    }

    public static class DistanceSummary {
        private Location lastLocation;

        private float totalDistance;

        public DistanceSummary() {
            this.lastLocation = null;
            this.totalDistance = 0;
        }

        public void update(Location nextLocation) {
            if (this.lastLocation != null) {
                this.totalDistance += this.lastLocation.distanceTo(nextLocation);
            }

            this.lastLocation = nextLocation;
        }
    }

    protected static class DistanceSummarySubscriber implements SummarySubscriber<DistanceSummary, Location>,
            SingleOnSubscribe<DistanceSummary> {
        protected Set<SingleEmitter<DistanceSummary>> observers;

        protected DistanceSummary result;

        public DistanceSummarySubscriber() {
            this.observers = new HashSet<>();
            this.result = new DistanceSummary();
        }

        @Override
        public Single<DistanceSummary> getSummary() {
            return Single.create(this);
        }

        @Override
        public void onSubscribe(final Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void subscribe(final SingleEmitter<DistanceSummary> emitter) throws Exception {
            this.observers.add(emitter);
        }        @Override
        public void onNext(final Location location) {
            this.result.update(location);
        }

        @Override
        public void onError(final Throwable t) {
            for (SingleEmitter<DistanceSummary> emitter : this.observers) {
                emitter.onError(t);
            }
        }

        @Override
        public void onComplete() {
            for (SingleEmitter<DistanceSummary> emitter : this.observers) {
                emitter.onSuccess(this.result);
            }
        }


    }

    protected static class DistanceStateSubscriber implements CurrentStateSubscriber<DistanceState, Location> {
        protected DistanceState result;

        public DistanceStateSubscriber() {
            this.result = new DistanceState();
        }

        @Override
        public DistanceState getCurrentState() {
            return this.result;
        }

        @Override
        public void onSubscribe(final Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(final Location location) {
            this.result.update(location);
        }

        @Override
        public void onError(final Throwable t) {
            this.result = null;
        }

        @Override
        public void onComplete() {
            // After the recorder completes the result of the state is null.
            this.result = null;
        }
    }

    protected final Context context;

    public DistanceRecorder(RecorderConfig config, Context context, @Nullable final DataRecorder dataRecorder) {
        super(config.getIdentifier(), config.getStartStepIdentifier(), config.getStopStepIdentifier(),
                dataRecorder, new DistanceSummarySubscriber(), new DistanceStateSubscriber());
        this.context = context;
    }

    @Override
    @NonNull
    public Flowable<Location> intializeEventFlowable() {
        return LocationSensor.getLocation(this.context);
    }
}
