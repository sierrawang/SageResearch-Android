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

package org.sagebionetworks.research.presentation.recorder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.domain.async.DeviceMotionRecorderConfiguration;
import org.sagebionetworks.research.presentation.mapper.DaggerSensorComponent;
import org.sagebionetworks.research.presentation.mapper.SensorComponent;

import java.util.HashSet;
import java.util.Set;

@AutoValue
public abstract class DeviceMotionRecorderConfigPresentationBase implements DeviceMotionRecorderConfigPresentation {
    public static SensorComponent SENSOR_COMPONENT = DaggerSensorComponent.builder().build();

    // TODO rkolmos 06/27/2018 change this name
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract DeviceMotionRecorderConfigPresentationBase build();

        public abstract Builder setIdentifier(@NonNull String identifier);

        public abstract Builder setStartStepIdentifier(@Nullable String startStepIdentifier);

        public abstract Builder setStopStepIdentifier(@Nullable String stopStepIdentifier);

        public abstract Builder setFrequency(double frequency);

        public abstract Builder setRecorderTypes(@NonNull Set<Integer> recorderTypes);
    }

    public static Builder builder() {
        return new AutoValue_DeviceMotionRecorderConfigPresentationBase.Builder();
    }

    public static DeviceMotionRecorderConfigPresentationBase fromDeviceMotionRecorderConfig(DeviceMotionRecorderConfiguration
            config) {
        double frequency = config.getFrequency() != null ? config.getFrequency() : 0; // 0 is the default value.
        Set<Integer> recorderTypes = new HashSet<>();
        for (String sensor : config.getRecorderTypes()) {
            Integer sensorType = SENSOR_COMPONENT.getSensorMap().get(sensor);
            if (sensorType != null) {
                recorderTypes.add(sensorType);
            }
        }

        return DeviceMotionRecorderConfigPresentationBase.builder()
                .setIdentifier(config.getIdentifier())
                .setStartStepIdentifier(config.getStartStepIdentifier())
                .setStopStepIdentifier(config.getStopStepIdentifier())
                .setFrequency(frequency)
                .setRecorderTypes(recorderTypes)
                .build();
    }
}
