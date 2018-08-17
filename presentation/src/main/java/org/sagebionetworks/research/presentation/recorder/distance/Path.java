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

package org.sagebionetworks.research.presentation.recorder.distance;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * A Path is a lightweight representation of the data from a Distance recorder. It provides access to the the first
 * and last locations the user was measured at. The total distance that was travelled, and the duration of the
 * trip.
 */
@AutoValue
public abstract class Path {
    public static final Path ZERO = builder().setDistance(0).setDuration(0).setFirstLocation(null).setLastLocation(null)
            .build();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Path build();

        public abstract Builder setDistance(float distance);

        public abstract Builder setFirstLocation(@Nullable Location firstLocation);

        public abstract Builder setLastLocation(@Nullable Location lastLocation);

        public abstract Builder setDuration(long duration);
    }

    public static Builder builder() {
        return new AutoValue_Path.Builder();
    }

    public abstract float getDistance();

    @Nullable
    public abstract Location getFirstLocation();

    @Nullable
    public abstract Location getLastLocation();

    public abstract long getDuration();

    /**
     * Returns a new Path that is the result of adding the given location to this Path.
     * @param location The location tot add to this path.
     * @return a new Path that is the result of adding the given location to this Path.
     */
    public Path addLocation(@NonNull Location location) {
        long lastLocationTime = this.getLastLocation() != null ? this.getLastLocation().getTime() : 0;
        long duration = this.getDuration() + (location.getTime() - lastLocationTime);
        float distance = this.getDistance() + (this.getLastLocation().distanceTo(location));
        return Path.builder().setDuration(duration).setDistance(distance).setFirstLocation(this.getFirstLocation())
                .setLastLocation(location).build();
    }}
