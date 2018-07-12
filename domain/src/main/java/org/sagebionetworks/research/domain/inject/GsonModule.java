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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dampcake.gson.immutable.ImmutableAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;

import org.aaronhe.threetengson.ThreeTenGsonAdapter;
import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.step.json.DomainAutoValueTypeAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Singleton;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;

@Module
public abstract class GsonModule {
    /**
     * Annotation marker for registering a custom deserializer for a class.
     */
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
    public static <T> JsonDeserializer<T> createPassThroughDeserializer(final Class<T> subtype) {
        return new JsonDeserializer<T>() {
            @Override
            public T deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
                return context.deserialize(json, subtype);
            }
        };
    }

    public static <T> JsonSerializer<T> createPassThroughSerializer(final Class<T> subtype) {
        return new JsonSerializer<T>() {
            @Override
            public JsonElement serialize(final T src, final Type typeOfSrc, final JsonSerializationContext context) {
                return context.serialize(src, subtype);
            }
        };
    }

    @Multibinds
    abstract Map<DependencyInjectionType, Map<Class<?>, JsonDeserializer<?>>>
    provideJsonDeserializerMap();

    @Multibinds
    abstract Map<DependencyInjectionType, Map<Class<?>, JsonSerializer<?>>>
    provideJsonSerializerMap();

    @Multibinds
    abstract Set<RuntimeTypeAdapterFactory> provideRuntimeTypeAdapterFactories();

    @Provides
    @IntoSet
    static TypeAdapterFactory provideAutoValueTypeAdapter() {
        return DomainAutoValueTypeAdapterFactory.create();
    }

    @Provides
    @Singleton
    static Gson provideGson(Map<DependencyInjectionType, Map<Class<?>, JsonDeserializer<?>>> jsonDeserializerMap,
            Map<DependencyInjectionType, Map<Class<?>, JsonSerializer<?>>> jsonSerializerMap,
            Set<TypeAdapterFactory> typeAdapterFactories,
            Set<RuntimeTypeAdapterFactory> runtimeTypeAdapterFactories) {
        GsonBuilder builder = new GsonBuilder();
        // Registers Deserializers
        Map<Class<?>, JsonDeserializer<?>> defaultJsonDeserializerMap = jsonDeserializerMap.get(DependencyInjectionType.DEFAULT);
        Map<Class<?>, JsonDeserializer<?>> overrideJsonDeserializerMap = jsonDeserializerMap.get(DependencyInjectionType.OVERRIDE);
        GsonModule.registerTypeAdapters(defaultJsonDeserializerMap, overrideJsonDeserializerMap, builder);

        // Register Serializers
        Map<Class<?>, JsonSerializer<?>> defaultJsonSerializerMap = jsonSerializerMap.get(DependencyInjectionType.DEFAULT);
        Map<Class<?>, JsonSerializer<?>> overrideJsonSerializerMap = jsonSerializerMap.get(DependencyInjectionType.OVERRIDE);
        GsonModule.registerTypeAdapters(defaultJsonSerializerMap, overrideJsonSerializerMap, builder);

        // Register TypeAdapterFactories
        for (TypeAdapterFactory typeAdapterFactory : typeAdapterFactories) {
            LOGGER.debug("Registering TypeAdapterFactory: {}", typeAdapterFactory.getClass());
            builder.registerTypeAdapterFactory(typeAdapterFactory);
        }

        // Registers RuntimeTypeAdapterFactories
        for (RuntimeTypeAdapterFactory runtimeTypeAdapterFactory : runtimeTypeAdapterFactories) {
            LOGGER.debug("Registering RuntimeTypeAdapterFactory: {}", runtimeTypeAdapterFactory.getClass());
            builder.registerTypeAdapterFactory(runtimeTypeAdapterFactory);
        }

        ThreeTenGsonAdapter.registerAll(builder);

        return builder.create();
    }

    /**
     * Registers the json type adapters from both maps. If a given class key exists in both maps only
     * the type adapter from the override map will be registered.
     * @param defaultTypeAdapterMap The default type adapters.
     * @param overrideTypeAdapterMap The override type adapters which if present take precedent over the default ones.
     * @param builder The gson builder to register the type adapters in.
     */
    static void registerTypeAdapters(@Nullable Map<Class<?>, ? extends Object> defaultTypeAdapterMap,
                                     @Nullable Map<Class<?>, ? extends Object> overrideTypeAdapterMap,
                                     @NonNull GsonBuilder builder) {
        if (overrideTypeAdapterMap != null) {
            for (Entry<Class<?>, ? extends Object> entry : overrideTypeAdapterMap.entrySet()) {
                LOGGER.debug("Registering Override TypeAdapter ({}) for: {}", entry.getValue(), entry.getKey());
                builder.registerTypeAdapter(entry.getKey(), entry.getValue());
            }
        }

        if (defaultTypeAdapterMap != null) {
            for (Entry<Class<?>, ? extends Object> entry : defaultTypeAdapterMap.entrySet()) {
                LOGGER.debug("Registering Default TypeAdapter ({}) for: {}", entry.getValue(), entry.getKey());
                // We only register the default if there isn't an override for this class.
                if (overrideTypeAdapterMap == null || !overrideTypeAdapterMap.containsKey(entry.getKey())) {
                    builder.registerTypeAdapter(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    @Provides
    @IntoSet
    static TypeAdapterFactory provideGuavaImmutableTypeAdapter() {
        return ImmutableAdapterFactory.forGuava();
    }
}
