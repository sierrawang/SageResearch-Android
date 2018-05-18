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

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.result.AnswerResultType;
import org.sagebionetworks.research.domain.result.ResultType;
import org.sagebionetworks.research.domain.result.data.AnswerResultData;
import org.sagebionetworks.research.domain.result.implementations.AnswerResultBase;
import org.sagebionetworks.research.domain.result.implementations.CollectionResultBase;
import org.sagebionetworks.research.domain.result.implementations.ErrorResultBase;
import org.sagebionetworks.research.domain.result.implementations.FileResultBase;
import org.sagebionetworks.research.domain.result.implementations.ResultBase;
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase;
import org.sagebionetworks.research.domain.result.interfaces.AnswerResult;
import org.sagebionetworks.research.domain.result.interfaces.CollectionResult;
import org.sagebionetworks.research.domain.result.interfaces.ErrorResult;
import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.threeten.bp.Instant;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughDeserializer;
import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughSerializer;

@Module(includes = {GsonModule.class})
public class ResultModule {
    @MapKey
    public @interface ResultClassKey {
        Class<? extends Result> value();
    }

    // region Result Type Keys
    @Provides
    @IntoMap
    @ResultClassKey(AnswerResult.class)
    static String provideAnswerResultTypeKey() {
        return AnswerResultBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ResultClassKey(CollectionResult.class)
    static String provideCollectionResultTypeKey() {
        return CollectionResultBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ResultClassKey(ErrorResult.class)
    static String provideErrorResultTypeKey() {
        return ErrorResultBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ResultClassKey(FileResult.class)
    static String provideFileResultTypeKey() {
        return FileResultBase.TYPE_KEY;
    }

    @Provides
    @IntoMap
    @ResultClassKey(TaskResult.class)
    static String provideTaskResultTypeKey() {
        return TaskResultBase.TYPE_KEY;
    }
    // endregion

    // region Result Deserializers
    @Provides
    @IntoMap
    @ClassKey(AnswerResult.class)
    static JsonDeserializer<?> provideAnswerResultDeserializer() {
        return createPassThroughDeserializer(AnswerResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(CollectionResult.class)
    static JsonDeserializer<?> provideCollectionResultDeserializer() {
        return createPassThroughDeserializer(CollectionResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(ErrorResult.class)
    static JsonDeserializer<?> provideErrorResultDeserializer() {
        return createPassThroughDeserializer(ErrorResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(FileResult.class)
    static JsonDeserializer<?> provideFileResultDeserializer() {
        return createPassThroughDeserializer(FileResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(TaskResult.class)
    static JsonDeserializer<?> provideTaskResultDeserializer() {
        return createPassThroughDeserializer(TaskResultBase.class);
    }
    // endregion

    // region Result Serializers
    @Provides
    @IntoMap
    @ClassKey(AnswerResult.class)
    static JsonSerializer<?> provideAnswerResultSerializer() {
        return createPassThroughSerializer(AnswerResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(CollectionResult.class)
    static JsonSerializer<?> provideCollectionResultSerializer() {
        return createPassThroughSerializer(CollectionResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(ErrorResult.class)
    static JsonSerializer<?> provideErrorResultSerializer() {
        return createPassThroughSerializer(ErrorResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(FileResult.class)
    static JsonSerializer<?> provideFileResultSerializer() {
        return createPassThroughSerializer(FileResultBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(TaskResult.class)
    static JsonSerializer<?> provideTaskResultSerializer() {
        return createPassThroughSerializer(TaskResultBase.class);
    }
    // endregion

    @Provides
    @IntoMap
    @ClassKey(Instant.class)
    static JsonDeserializer<?> provideInstantDeserializer() {
        return new JsonDeserializer<Instant>() {
            @Override
            public Instant deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                    String str = json.getAsString();
                    return Instant.parse(str);
                }

                throw new JsonParseException("Instants should be represented as their toString representation");
            }
        };
    }

    /**
     * @return The RuntimeTypeAdapterFactor for Result.class
     */
    @Provides
    @IntoSet
    static RuntimeTypeAdapterFactory provideType(Map<Class<? extends Result>, String> resultMap) {
        RuntimeTypeAdapterFactory<Result> factory = RuntimeTypeAdapterFactory.of(Result.class);
        for (Entry<Class<? extends Result>, String> resultEntry : resultMap.entrySet()) {
            factory.registerSubtype(resultEntry.getKey(), resultEntry.getValue());
        }

        factory.registerDefaultType(ResultBase.class);
        return factory;
    }

    @Provides
    @IntoMap
    @ClassKey(AnswerResultType.class)
    static Map<String, Class<?>> provideAnswerResultTypeClassMapping() {
        Map<String, Class<?>> classMap = new HashMap<>();
        classMap.put(AnswerResultType.DECIMAL, Double.class);
        classMap.put(AnswerResultType.BOOLEAN, Boolean.class);
        classMap.put(AnswerResultType.INTEGER, Integer.class);
        classMap.put(AnswerResultType.STRING, String.class);
        return classMap;
    }

    @Provides
    @IntoSet
    static RuntimeTypeAdapterFactory provideAnswerResultDataFactory(Map<Class<?>, Map<String, Class<?>>> classMap) {
        RuntimeTypeAdapterFactory<AnswerResultData> factory = RuntimeTypeAdapterFactory.of(AnswerResultData.class,
                "answerResultType");
        Map<String, Class<?>> answerClassMap = classMap.get(AnswerResultType.class);
        for (String resultType : AnswerResultType.ALL) {
            Type genericType = null;
            if (answerClassMap.containsKey(resultType)) {
                genericType = answerClassMap.get(resultType);
            } else {
                genericType = Object.class;
            }

            factory.registerSubtype(createAnswerResultData(TypeToken.of(genericType)).getType(), resultType);
        }

        factory.registerDefaultType(createAnswerResultData(TypeToken.of(Object.class)).getType());
        return factory;
    }

    private static <T> TypeToken<AnswerResultData<T>> createAnswerResultData(TypeToken<T> genericType) {
        return new TypeToken<AnswerResultData<T>>() {}.where(new TypeParameter<T>() {}, genericType);
    }
}
