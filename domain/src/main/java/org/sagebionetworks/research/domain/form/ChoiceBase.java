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

package org.sagebionetworks.research.domain.form;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.domain.interfaces.ObjectHelper;
import org.sagebionetworks.research.domain.result.Result;

import java.lang.reflect.Type;


public class ChoiceBase<E> extends ObjectHelper implements Choice<E> {
    public static final JsonDeserializer<ChoiceBase<?>> JSON_DESERIALIZER =
            new JsonDeserializer<ChoiceBase<?>>() {
        @Override
        public ChoiceBase deserialize(final JsonElement json, final Type typeOfT,
        final JsonDeserializationContext context)
                        throws JsonParseException {
            if (json.isJsonObject()) {
                // Suppose we are deserializing a Choice<T> for some type T. We create a TypeToken<ChoiceBase<T>>
                // then we deserialize using this.
                TypeToken token = createChoiceBaseTypeToken(typeOfT);
                ChoiceBase<?> result = context.deserialize(json, token.getType());
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

    @NonNull
    @SerializedName("value")
    private final E answerValue;
    @NonNull
    private final String text;
    @Nullable
    private final String detail;
    @Nullable
    @SerializedName("icon")
    private final String iconName;
    private final boolean isExclusive;

    /**
     * Default initializer for gson.
     */
    public ChoiceBase() {
        this.answerValue = null;
        this.text = null;
        this.detail = null;
        this.iconName = null;
        this.isExclusive = false;
    }

    public ChoiceBase(@NonNull final E answerValue, @NonNull final String text, @Nullable final String detail,
            @Nullable final String iconName, final boolean isExclusive) {
        this.answerValue = answerValue;
        this.text = text;
        this.detail = detail;
        this.iconName = iconName;
        this.isExclusive = isExclusive;
    }

    public ChoiceBase(@NonNull final E answerValue) {
        this.answerValue = answerValue;
        this.text = answerValue.toString();
        this.detail = null;
        this.iconName = null;
        this.isExclusive = false;
    }

    @NonNull
    @Override
    public E getAnswerValue() {
        return this.answerValue;
    }

    @NonNull
    @Override
    public String getText() {
        return this.text;
    }

    @Nullable
    @Override
    public String getDetail() {
        return this.detail;
    }

    @Nullable
    @Override
    public String getIconName() {
        return this.iconName;
    }

    @Override
    public boolean isExclusive() {
        return this.isExclusive;
    }

    @Override
    public boolean isEqualToResult(final Result result) {
        // TODO: rkolmos 05/01/2018 implement this method.
        return false;
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("answerValue", this.getAnswerValue())
                .add("text", this.getText())
                .add("detail", this.getDetail())
                .add("iconName", this.getIconName())
                .add("isExclusive", this.isExclusive());
    }

    @Override
    protected boolean equalsHelper(Object o) {
        ChoiceBase<?> choice = (ChoiceBase<?>) o;
        return Objects.equal(this.getAnswerValue(), choice.getAnswerValue()) &&
                Objects.equal(this.getText(), choice.getText()) &&
                Objects.equal(this.getDetail(), choice.getDetail()) &&
                Objects.equal(this.isExclusive(), choice.isExclusive()) &&
                Objects.equal(this.getIconName(), choice.getIconName());
    }

    // region Deserialization


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

    public static JsonDeserializer<ChoiceBase<?>> getJsonDeserializer() {
        return new JsonDeserializer<ChoiceBase<?>>() {
            @Override
            public ChoiceBase deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                if (json.isJsonObject()) {
                    // Suppose we are deserializing a Choice<T> for some type T. We create a TypeToken<ChoiceBase<T>>
                    // then we deserialize using this.
                    TypeToken token = createChoiceBaseTypeToken(typeOfT);
                    ChoiceBase<?> result = context.deserialize(json, token.getType());
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
}
