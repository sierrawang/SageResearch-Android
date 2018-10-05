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

package org.sagebionetworks.research.domain.form.implementations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.domain.form.interfaces.Choice;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;
import org.sagebionetworks.research.domain.interfaces.ObjectHelper;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * ChoiceBase is the concrete implementation of a choice in a form.
 *
 * @param <E>
 *         The type of answer that the choice represents.
 */
public class ChoiceBase<E> extends ObjectHelper implements Choice<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChoiceBase.class);

    @NonNull
    @SerializedName("value")
    private final E answerValue;

    @Nullable
    private final String detail;

    @SerializedName("isExclusive")
    private final boolean exclusive;

    @Nullable
    @SerializedName("icon")
    private final String iconName;

    @NonNull
    private final String text;

    public static JsonDeserializer<ChoiceBase> getJsonDeserializer() {
        return new JsonDeserializer<ChoiceBase>() {
            @Override
            public ChoiceBase deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                if (json.isJsonObject()) {
                    // This behavior is a workaround for calling context.deserialize(json, ChoiceBase<T>). We
                    // are writing this out long form to avoid the infinite recursion that occurs when a
                    // JsonDeserializer<T> calls context.deserialize(json, T).
                    JsonObject object = json.getAsJsonObject();
                    Type innerType = getInnerType(typeOfT);
                    JsonElement valueElement = object.get("value");
                    Object answerValue = valueElement != null ? context.deserialize(valueElement, innerType) : null;
                    JsonElement textElement = object.get("text");
                    String text = (String)
                            (textElement != null ? textElement.getAsString() : null);
                    JsonElement detailElement = object.get("detail");
                    String detail = (String)
                            (detailElement != null ? detailElement.getAsString() : null);
                    JsonElement iconElement = object.get("icon");
                    String iconName = (String)
                            (iconElement != null ? iconElement.getAsString() : null);
                    JsonElement isExclusiveElement = object.get("isExclusive");
                    boolean isExclusive = isExclusiveElement != null && isExclusiveElement.getAsBoolean();
                    return new ChoiceBase<>(answerValue, text, detail, iconName, isExclusive);
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

    /**
     * Default initializer for gson.
     */
    public ChoiceBase() {
        super();
        this.answerValue = null;
        this.text = null;
        this.detail = null;
        this.iconName = null;
        this.exclusive = false;
    }

    public ChoiceBase(@NonNull final E answerValue, @NonNull final String text, @Nullable final String detail,
            @Nullable final String iconName, final boolean exclusive) {
        super();
        this.answerValue = answerValue;
        this.text = text;
        this.detail = detail;
        this.iconName = iconName;
        this.exclusive = exclusive;
    }

    public ChoiceBase(@NonNull final E answerValue) {
        super();
        this.answerValue = answerValue;
        this.text = answerValue.toString();
        this.detail = null;
        this.iconName = null;
        this.exclusive = false;
    }

    @NonNull
    @Override
    public E getAnswerValue() {
        return this.answerValue;
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

    @NonNull
    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public boolean isEqualToResult(final Result result) {
        // TODO: rkolmos 05/01/2018 implement this method.
        return false;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
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

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.exclusive, this.iconName, this.detail, this.text, this.answerValue);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("answerValue", this.getAnswerValue())
                .add("text", this.getText())
                .add("detail", this.getDetail())
                .add("iconName", this.getIconName())
                .add("exclusive", this.isExclusive());
    }

    /**
     * Given a Type corresponding to some Choice<T> returns a TypeToken corresponding to ChoiceBase<T>.
     *
     * @param typeOfT
     *         The type corresponding to some Choice<T> to produce a ChoiceBase<T> for.
     * @return A TypeToken<ChoiceBase<T>> with the same generic parameter as the given Type which should be a
     *         Choice<T> for some Type T.
     */
    private static Type getInnerType(Type typeOfT) {
        TypeToken tToken = TypeToken.of(typeOfT);
        try {
            TypeToken token = tToken.resolveType(Choice.class.getDeclaredMethod("getAnswerValue")
                    .getGenericReturnType());
            return token.getType();
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Error retrieving inner type of AnswerValue", e);
        }

        return null;
    }
}
