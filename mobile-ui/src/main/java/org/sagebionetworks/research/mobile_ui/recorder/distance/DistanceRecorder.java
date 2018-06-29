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
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.mobile_ui.recorder.data.DataLogger;
import org.sagebionetworks.research.mobile_ui.recorder.ReactiveRecorder;
import org.sagebionetworks.research.mobile_ui.recorder.distance.DistanceRecorder.CurrentDistanceInfo;
import org.sagebionetworks.research.mobile_ui.recorder.distance.DistanceRecorder.DistanceInfo;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Records the user's location and distance travelled via a Stream of Location's that the user is measured at.
 */
public abstract class DistanceRecorder extends ReactiveRecorder<DistanceInfo, CurrentDistanceInfo, Location> {
    protected final Context context;

    public DistanceRecorder(RecorderConfiguration config, Context context, @Nullable final DataLogger dataLogger) {
        super(config.getIdentifier(), config.getStartStepIdentifier(), config.getStopStepIdentifier(),
                dataLogger, new DistanceSummarySubscriber(), new DistanceStateSubscriber());
        this.context = context;
    }

    @Override
    @NonNull
    public Flowable<Location> intializeEventFlowable() {
        return LocationSensor.getLocation(this.context);
    }

    /**
     * Represents the of the measurements of a DistanceRecorder. Provides access to
     * the total distance traveled thus far, the first location the user was at, and the most recent location the
     * user has been at.
     */
    public static class DistanceInfo {
        private float totalDistance;
        private long totalTime;
        private float lastDistanceChange;
        private Location firstLocation;
        private Location lastLocation;

        public DistanceInfo() {
            this.lastLocation = null;
            this.firstLocation = null;
            this.totalDistance = 0;
            this.totalTime = 0;
            this.lastDistanceChange = 0;
        }

        /**
         * Updates this DistanceState provided the user has just been measured at the given location.
         * @param nextLocation The location the user has just been measured at.
         */
        public void update(Location nextLocation) {
            if (this.lastLocation != null) {
                this.lastDistanceChange = this.lastLocation.distanceTo(nextLocation);
                this.totalDistance += this.lastDistanceChange;
            } else {
                this.firstLocation = nextLocation;
            }

            this.lastLocation = nextLocation;
        }

        /**
         * Returns the total distance the user has traveled since the recorder was started in meters.
         * @return the total distance the user has traveled since the recorder was started in meters.
         */
        public float getTotalDistance() {
            return this.totalDistance;
        }

        /**
         * Returns the total time between when the user was measured at the first location and when they were
         * measured at the last location, in microseconds.
         * @return the total time between when the user was measured at the first location and when they were
         * measured at the last location, in microseconds.
         */
        public long getTotalTime() {
            return this.lastLocation != null && this.firstLocation != null ?
                    this.lastLocation.getTime() - this.firstLocation.getTime() : 0;
        }

        /**
         * Returns the average speed of the user during the recorders run in meters per second.
         * @return the average speed of the user during the recorders run in meters per second.
         */
        public double getAverageSpeed() {
            return this.totalDistance / (this.getTotalTime() * 1e-6);
        }

        /**
         * Returns the first location the user was recorded at by the recorder.
         * @return the first location the user was recorded at by the recorder.
         */
        public Location getFirstLocation() {
            return this.firstLocation;
        }

        /**
         * Returns the location the user finished their recording at.
         * @return the location the user finished their recording at.
         */
        public Location getLastLocation() {
            return this.lastLocation;
        }
    }

    /**
     * Adds information that is only relevant to the CurrentState of the recorder to DistanceInfo.
     */
    public static class CurrentDistanceInfo extends DistanceInfo {
        private double currentSpeed;

        public CurrentDistanceInfo() {
            super();
            this.currentSpeed = 0;
        }

        /**
         * Returns the most recently measured location of the user.
         * @return the most recently measured location of the user.
         */
        public Location getCurrentLocation() {
            // When the recorder is running the last is the most recent location the user was measured at.
            return this.getLastLocation();
        }

        @Override
        public void update(Location nextLocation) {
            if (this.getLastLocation() != null) {
                long timeChangeMicros = nextLocation.getTime() - this.getLastLocation().getTime();
                float distanceChangeMeters = this.getLastLocation().distanceTo(nextLocation);
                this.currentSpeed = distanceChangeMeters / (timeChangeMicros * 1e-6);
            }

            super.update(nextLocation);
        }

        /**
         * Returns the most recently measured speed of the user.
         * @return the most recently measured speed of the user.
         */
        public double getCurrentSpeed() {
            return this.currentSpeed;
        }
    }

    /**
     * Subscribes to the same Location stream as the DistanceRecorder and maintains a Summary of the recorder's
     * recordings that will complete when the recorder is finished. This class should be used when the recorder's
     * findings are only needed after the recorder is done recording.
     */
    protected static class DistanceSummarySubscriber implements SummarySubscriber<DistanceInfo, Location>,
            SingleOnSubscribe<DistanceInfo> {
        protected Set<SingleEmitter<DistanceInfo>> observers;
        protected DistanceInfo result;

        public DistanceSummarySubscriber() {
            this.observers = new HashSet<>();
            this.result = new DistanceInfo();
        }

        @Override
        public Single<DistanceInfo> getSummary() {
            return Single.create(this);
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
            for (SingleEmitter<DistanceInfo> emitter : this.observers) {
                emitter.onError(t);
            }
        }

        @Override
        public void onComplete() {
            for (SingleEmitter<DistanceInfo> emitter : this.observers) {
                emitter.onSuccess(this.result);
            }
        }

        @Override
        public void subscribe(final SingleEmitter<DistanceInfo> emitter) throws Exception {
            this.observers.add(emitter);
        }
    }

    /**
     * Subscribes to the same Location stream as the DistanceRecorder and maintains and provides access to the current
     * state of the recorder. This class should be used when the recorders readings are needed in real time.
     */
    protected static class DistanceStateSubscriber implements CurrentStateSubscriber<CurrentDistanceInfo, Location> {
        protected CurrentDistanceInfo result;

        public DistanceStateSubscriber() {
            this.result = new CurrentDistanceInfo();
        }

        @Override
        public CurrentDistanceInfo getCurrentState() {
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
}
