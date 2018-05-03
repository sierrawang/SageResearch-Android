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

import com.dampcake.gson.immutable.ImmutableAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;

import org.aaronhe.threetengson.ThreeTenGsonAdapter;
import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.step.json.DomainAutoValueTypeAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import javax.inject.Singleton;

@Module
public abstract class GsonModule {
    @MapKey
    public @interface ClassKey {
        Class<?> value();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GsonModule.class);

    /**
     * Redirects deserialization of one type to a subtype. This can be useful when registering a type adapter which
     * specifies a concrete class for an interface, e.g. gsonBuilder.registerTypeAdapter(MyInterface.class,
     * createPassThroughDeserializer(MyInterfaceImpl.class));
     *
     * @param subtype
     *         subtype to use for deserialization
     * @param <T>
     *         type targeted for deserialization
     * @return deserializer which defers/delegates/redirects to subtype
     */
    public static <T> JsonDeserializer<T> createPassthroughDeserializer(final Class<? extends T> subtype) {
        return new JsonDeserializer<T>() {
            @Override
            public T deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                return context.deserialize(json, subtype);
            }
        };
    }

    @Multibinds
    abstract Map<Class<?>, JsonDeserializer> jsonDeserializerMap();

    @Multibinds
    abstract Set<JsonDeserializer> provideJsonDeserializers();

    @Multibinds
    abstract Set<RuntimeTypeAdapterFactory> provideRuntimeTypeAdapterFactories();

    @Provides
    @IntoSet
    static TypeAdapterFactory provideAutoValueTypeAdapter() {
        return DomainAutoValueTypeAdapterFactory.create();
    }

    @Provides
    @Singleton
    static Gson provideGson(Map<Class<?>, JsonDeserializer> jsonDeserializerMap,
            Set<TypeAdapterFactory> typeAdapterFactories,
            Set<RuntimeTypeAdapterFactory> runtimeTypeAdapterFactories) {
        GsonBuilder builder = new GsonBuilder();

        for (Map.Entry<Class<?>, JsonDeserializer> entry : jsonDeserializerMap.entrySet()) {
            LOGGER.debug("Registering JsonDeserializer for: {}", entry.getKey());
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        for (TypeAdapterFactory typeAdapterFactory : typeAdapterFactories) {
            LOGGER.debug("Registering TypeAdapterFactory: {}", typeAdapterFactory.getClass());
            builder.registerTypeAdapterFactory(typeAdapterFactory);
        }
        for (RuntimeTypeAdapterFactory runtimeTypeAdapterFactory : runtimeTypeAdapterFactories) {
            LOGGER.debug("Registering RuntimeTypeAdapterFactory: {}", runtimeTypeAdapterFactory.getClass());
            builder.registerTypeAdapterFactory(runtimeTypeAdapterFactory);
        }

        ThreeTenGsonAdapter.registerAll(builder);

        return builder.create();
    }

    @Provides
    @IntoSet
    static TypeAdapterFactory provideGuavaImmutableTypeAdapter() {
        return ImmutableAdapterFactory.forGuava();
    }
}
