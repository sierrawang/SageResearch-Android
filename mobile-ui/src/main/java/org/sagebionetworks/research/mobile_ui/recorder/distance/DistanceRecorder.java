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

import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.mobile_ui.recorder.ReactiveRecorder;
import org.sagebionetworks.research.mobile_ui.recorder.data.DataLogger;

import io.reactivex.Flowable;

/**
 * Records the user's location and distance travelled via a Stream of Location's that the user is measured at.
 */
public abstract class DistanceRecorder extends ReactiveRecorder<Location> {
    protected final Context context;
    protected final Flowable<Path> pathFlowable;

    public DistanceRecorder(RecorderConfiguration config, Context context, @Nullable final DataLogger dataLogger) {
        super(config.getIdentifier(), config.getStartStepIdentifier(), config.getStopStepIdentifier(), dataLogger);
        this.context = context;
        this.pathFlowable = this.getEventFlowable().scan(Path.ZERO, new PathAccumulator());
    }

    /**
     * Returns a Flowable<Path> that can be used to obtain information about the user's path while the recorder
     * is running.
     * @return a Flowable<Path> that can be used to obtain information about the user's path while the recorder
     * is running.
     */
    public Flowable<Path> getPathFlowable() {
        return this.pathFlowable;
    }

    @Override
    @NonNull
    public Flowable<Location> intializeEventFlowable() {
        return LocationSensor.getLocation(this.context);
    }
}
