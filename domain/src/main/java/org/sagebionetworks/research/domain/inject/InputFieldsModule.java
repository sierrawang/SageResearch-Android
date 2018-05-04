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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.sagebionetworks.research.domain.form.Choice;
import org.sagebionetworks.research.domain.form.ChoiceBase;
import org.sagebionetworks.research.domain.form.ChoiceInputField;
import org.sagebionetworks.research.domain.form.DataTypes.BaseInputDataType;
import org.sagebionetworks.research.domain.form.DataTypes.BaseInputDataType.BaseType;
import org.sagebionetworks.research.domain.form.DataTypes.CollectionInputDataType;
import org.sagebionetworks.research.domain.form.DataTypes.CollectionInputDataType.CollectionType;
import org.sagebionetworks.research.domain.form.DataTypes.InputDataType;
import org.sagebionetworks.research.domain.form.InputField;
import org.sagebionetworks.research.domain.form.InputFieldBase;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;

import java.lang.reflect.Type;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

@Module(includes = {GsonModule.class})
public class InputFieldsModule {
    // region InputField
    /**
     * @return The JsonDeserializer to use for deserializing InputField.class
     */
    @Provides
    @IntoMap
    @ClassKey(InputField.class)
    static JsonDeserializer provideInputFieldDeserializer() {
        return new JsonDeserializer<InputField>() {
            @Override
            public InputField deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                if (json.isJsonObject()) {
                    JsonObject object = json.getAsJsonObject();
                    JsonElement dataTypeElement = object.get("dataType");
                    InputDataType dataType = context.deserialize(dataTypeElement, InputDataType.class);
                    if (dataType != null) {
                        return deserializeInputFieldWithDataType(json, dataType, context);
                    }
                }

                throw new JsonParseException("Unknown InputField format");
            }
        };
    }

    /**
     * Deserializes and returns the InputField represented by the given JsonElement, assuming it has the type represented
     * by the given InputDataType, using the given JsonDeserializationContext.
     * @param json The JsonElement representing the InputField to be deserialized.
     * @param dataType The InputDataType that the resulting InputField should have.
     * @param context The JsonDeserializationContext to use for deserailization.
     * @return The InputField represented by the given JsonElement.
     */
    private static InputField deserializeInputFieldWithDataType(JsonElement json, InputDataType dataType,
            JsonDeserializationContext context) {
        if (dataType instanceof CollectionInputDataType) {
            // We are deserializing a Collection which means we have should create a ChoiceInputField.
            CollectionInputDataType collectionType = (CollectionInputDataType) dataType;
            // We use the baseType to get the expected answerValue type.
            Class innerClass = BaseInputDataType.CLASSES.get(collectionType.getBaseType());
            // Next we use attempt to parse the result as a ChoiceInputField with no generic parameter.
            InputField result = null;
            if (innerClass != null) {
                // If we have an expected inner class we create a ChoiceInputField<innerClass> token
                // and use this type to deserialize.
                Type expectedType = createChoiceInputFieldTypeToken(TypeToken.of(innerClass)).getType();
                return context.deserialize(json, expectedType);
            }

            // Otherwise we just allow gson to attempt to figure out the type.
            return context.deserialize(json, ChoiceInputField.class);
        } else if (dataType instanceof BaseInputDataType) {
            return context.deserialize(json, InputFieldBase.class);
        } else {
            throw new JsonParseException("dataType: " + dataType + " is invalid");
        }
    }
    // endregion

    // region Choice
    /**
     * Allows a Choice to be deserialized in the default gson way, or as just a String.
     * The latter results in both the answerValue and text of the choice being set to the
     * given string.
     * @return The JsonDeserializer to use for deserializing Choice.class
     */
    @Provides
    @IntoMap
    @ClassKey(Choice.class)
    static JsonDeserializer provideChoiceDeserializer() {
        return new JsonDeserializer<Choice<?>>() {
            @Override
            public Choice deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                if (json.isJsonObject()) {
                    // Suppose we are deserializing a Choice<T> for some type T. We create a TypeToken<ChoiceBase<T>>
                    // then we deserialize using this.
                    TypeToken token = createChoiceBaseTypeToken(typeOfT);
                    Choice<?> result = context.deserialize(json, token.getType());
                    if (result != null) {
                       return result;
                    }
                } else if (json.isJsonPrimitive()) {
                    // If the json is just a primitive value we assume that it is just a string and create a
                    // ChoiceBase from it.
                    String answerValue = context.deserialize(json, String.class);
                    return new ChoiceBase<>(answerValue);
                }

                throw new JsonParseException("Unknown format for Choice");
            }
        };
    }
    // endregion

    // region TypeToken
    /**
     * Given a Type corresponding to some Choice<T> returns a TypeToken corresponding to
     * ChoiceBase<T>.
     * @param typeOfT The type corresponding to some Choice<T> to produce a ChoiceBase<T> for.
     * @return A TypeToken<ChoiceBase<T>> with the same generic parameter as the given Type which should
     * be a Choice<T> for some Type T.
     */
    private static TypeToken<ChoiceBase<?>> createChoiceBaseTypeToken(Type typeOfT) {
        TypeToken tToken = TypeToken.of(typeOfT);
        try {
            TypeToken token = tToken.resolveType(Choice.class.getMethod("getAnswerValue").getGenericReturnType());
            return createChoiceBaseTypeTokenHelper(token);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns a TypeToken<ChoiceBase<genericToken.getType()>>
     * @param genericToken The TypeToken for the class to be the generic type argument to ChoiceBase.
     * @param <T> The Type of the generic type argument to ChoiceBase.
     * @return a TypeToken<ChoiceBase<genericToken.getType()>>
     */
    private static <T> TypeToken<ChoiceBase<T>> createChoiceBaseTypeTokenHelper(TypeToken<T> genericToken) {
        return new TypeToken<ChoiceBase<T>>() {}.where(new TypeParameter<T>() {}, genericToken);
    }

    /**
     * Returns a TypeToken<ChoiceInputField<genericToken.getType()>>
     * @param genericToken The TypeToken for the class to be the generic type argument to ChoiceInputField.
     * @param <T> The Type of the generic type argument to ChoiceInputField.
     * @return a TypeToken<ChoiceInputField<genericToken.getType()>>
     */
    private static <T> TypeToken<ChoiceInputField<T>> createChoiceInputFieldTypeToken(TypeToken<T> genericToken) {
        return new TypeToken<ChoiceInputField<T>>() {}.where(new TypeParameter<T>() {}, genericToken);
    }
    // endregion

    // region InputDataType
    /**
     * @return The JsonDeserializer to use for deserializing InputDataType.class.
     */
    @Provides
    @IntoMap
    @ClassKey(InputDataType.class)
    static JsonDeserializer provideInputDataTypeDeserializer() {
        return new JsonDeserializer<InputDataType>() {
            @Override
            public InputDataType deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                String string = json.getAsString();
                if (string != null) {
                    String[] piecesArray = string.split("\\" + CollectionInputDataType.DELIMINATOR);
                    if (piecesArray.length == 1 && BaseType.ALL.contains(piecesArray[0])) {
                        return new BaseInputDataType(piecesArray[0]);
                    } else if (piecesArray.length == 2 && CollectionType.ALL.contains(piecesArray[0]) &&
                                    BaseType.ALL.contains(piecesArray[1])) {
                        return new CollectionInputDataType(piecesArray[0], piecesArray[1]);
                    } else if (piecesArray.length == 1 && CollectionType.ALL.contains(piecesArray[0])) {
                        return new CollectionInputDataType(piecesArray[0]);
                    }
                }

                throw new JsonParseException("JSON value " + json.toString() + " doesn't represent an InputDataType");
            }
        };
    }
    // endregion
}
