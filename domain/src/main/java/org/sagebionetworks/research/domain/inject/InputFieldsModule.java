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
import com.google.gson.JsonDeserializer;

import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.form.data_types.BaseInputDataType.BaseType;
import org.sagebionetworks.research.domain.form.data_types.CollectionInputDataType;
import org.sagebionetworks.research.domain.form.data_types.CollectionInputDataType.CollectionType;
import org.sagebionetworks.research.domain.form.data_types.InputDataType;
import org.sagebionetworks.research.domain.form.implementations.ChoiceBase;
import org.sagebionetworks.research.domain.form.implementations.ChoiceInputField;
import org.sagebionetworks.research.domain.form.implementations.InputFieldBase;
import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.form.interfaces.InputField;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

@Module(includes = {GsonModule.class})
public class InputFieldsModule {
    @Provides
    @IntoMap
    @ClassKey(BaseType.class)
    static Map<String, Class<? extends Comparable>> provideBaseTypeClassMap() {
        Map<String, Class<? extends Comparable>> classMap = new HashMap<>();
        classMap.put(BaseType.BOOLEAN, Boolean.class);
        classMap.put(BaseType.STRING, String.class);
        classMap.put(BaseType.INTEGER, Integer.class);
        classMap.put(BaseType.DECIMAL, Double.class);
        classMap.put(BaseType.DURATION, Double.class);
        return classMap;
    }

    static Map<String, Class<InputField<? extends Comparable>>> provideInputFieldMap() {
        Map<String, Class<InputField<? extends Comparable>>> classMap = new HashMap<>();
        return classMap;
    }

    @Provides
    @IntoMap
    @ClassKey(Choice.class)
    static JsonDeserializer<?> provideChoiceDeserializer() {
        return ChoiceBase.getJsonDeserializer();
    }

    @Provides
    @IntoMap
    @ClassKey(InputDataType.class)
    static JsonDeserializer<?> provideInputDataTypeDeserializer() {
        return InputDataType.getJsonDeserializer();
    }

    /**
     * @return Gson RuntimeTypeAdapterFactory for InputField.class.
     */
    @Provides
    @IntoSet
    static RuntimeTypeAdapterFactory provideType(Map<Class<?>, Map<String, Class<? extends Comparable>>> classMap) {
        RuntimeTypeAdapterFactory<InputField> typeAdapterFactory = RuntimeTypeAdapterFactory.of(InputField.class,
                InputField.KEY_TYPE);
        Map<String, Class<? extends Comparable>> baseClassMap = classMap.get(BaseType.class);
        Map<String, Type> choiceInputFieldTypes = new HashMap<>();
        for (String collectionType : CollectionType.ALL) {
            // A collection type with no base type corresponds to a raw ChoiceInputField.
            choiceInputFieldTypes.put(collectionType, ChoiceInputField.class);
            for (String baseType : BaseType.ALL) {
                // We either use the registered class for the base type, or just use a raw ChoiceInputField.
                Type type = baseClassMap.get(baseType);
                if (type != null) {
                    type = createChoiceInputFieldTypeToken(TypeToken.of(baseClassMap.get(baseType))).getType();
                } else {
                    type = ChoiceInputField.class;
                }

                choiceInputFieldTypes.put(new CollectionInputDataType(collectionType, baseType).toString(), type);
            }
        }

        for (Entry<String, Type> entry : choiceInputFieldTypes.entrySet()) {
            typeAdapterFactory.registerSubtype(entry.getValue(), entry.getKey());
        }

        typeAdapterFactory.registerDefaultType(InputFieldBase.class);
        return typeAdapterFactory;
    }

    /**
     * Returns a TypeToken<ChoiceInputField<genericToken.getType()>>
     *
     * @param genericToken
     *         The TypeToken for the class to be the generic type argument to ChoiceInputField.
     * @param <T>
     *         The Type of the generic type argument to ChoiceInputField.
     * @return a TypeToken<ChoiceInputField<genericToken.getType()>>
     */
    private static <T extends Comparable<T>> TypeToken<ChoiceInputField<T>> createChoiceInputFieldTypeToken(TypeToken<T> genericToken) {
        return new TypeToken<ChoiceInputField<T>>() {
        }.where(new TypeParameter<T>() {
        }, genericToken);
    }
}
