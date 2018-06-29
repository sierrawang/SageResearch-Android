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

package org.sagebionetworks.research.domain.inject;

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughDeserializer;

import com.google.gson.JsonDeserializer;

import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.async.DeviceMotionRecorderConfiguration;
import org.sagebionetworks.research.domain.async.DeviceMotionRecorderConfigurationBase;
import org.sagebionetworks.research.domain.async.DistanceRecorderConfiguration;
import org.sagebionetworks.research.domain.async.DistanceRecorderConfigurationBase;

import java.util.Map;
import java.util.Map.Entry;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

@Module(includes = GsonModule.class)
public class AsyncActionModule {
    @MapKey
    public @interface AsyncActionClassKey {
        Class<? extends AsyncActionConfiguration> value();
    }

    @Provides
    @IntoMap
    @AsyncActionClassKey(DeviceMotionRecorderConfiguration.class)
    static String provideDeviceMotionRecorderConfigTypeKey() {
        return DeviceMotionRecorderConfigurationBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @AsyncActionClassKey(DistanceRecorderConfiguration.class)
    static String provideDistanceRecorderConfigTypeKey() {
        return DistanceRecorderConfigurationBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ClassKey(DeviceMotionRecorderConfiguration.class)
    static JsonDeserializer<?> provideDeviceMotionRecorderConfigDeserializer() {
        return createPassThroughDeserializer(DeviceMotionRecorderConfigurationBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(DistanceRecorderConfiguration.class)
    static JsonDeserializer<?> provideDeviceMotionRecorderConfigDeserizlier() {
        return createPassThroughDeserializer(DistanceRecorderConfigurationBase.class);
    }

    @Provides
    @IntoSet
    static RuntimeTypeAdapterFactory provideType(Map<Class<? extends AsyncActionConfiguration>, String> classKeys) {
        RuntimeTypeAdapterFactory<AsyncActionConfiguration> factory = RuntimeTypeAdapterFactory.of(AsyncActionConfiguration.class);

        for (Entry<Class<? extends AsyncActionConfiguration>, String> entry : classKeys.entrySet()) {
            factory.registerSubtype(entry.getKey(), entry.getValue());
        }

        return factory;
    }
}
