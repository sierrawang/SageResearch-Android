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

package org.sagebionetworks.research.domain.form.data_types;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.sagebionetworks.research.domain.form.data_types.BaseInputDataType.BaseType;
import org.sagebionetworks.research.domain.form.data_types.CollectionInputDataType.CollectionType;
import org.sagebionetworks.research.domain.interfaces.ObjectHelper;

import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class InputDataType extends ObjectHelper implements Serializable {
    @Nullable
    public static JsonDeserializer<? extends InputDataType> getJsonDeserializer() {
        return new JsonDeserializer<InputDataType>() {
            @Override
            public InputDataType deserialize(final JsonElement json, final Type typeOfT,
                    final JsonDeserializationContext context)
                    throws JsonParseException {
                String string = json.getAsString();
                if (string != null) {
                    String[] piecesArray = string.split("\\" + CollectionInputDataType.DELIMINATOR, -1);
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
}