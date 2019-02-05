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

import android.os.Parcel;
import android.support.annotation.StringDef;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.form.InputUIHint;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class BaseInputDataType extends InputDataType {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({BaseType.BOOLEAN, BaseType.DATE, BaseType.DECIMAL, BaseType.DURATION, BaseType.FRACTION,
            BaseType.INTEGER, BaseType.STRING, BaseType.YEAR})
    public @interface BaseType {
        String BOOLEAN = "boolean";
        String DATE = "date";
        String DECIMAL = "decimal";
        String DURATION = "duration";
        String FRACTION = "fraction";
        String INTEGER = "integer";
        String STRING = "string";
        String YEAR = "year";

        ImmutableSet<String> ALL = ImmutableSet.of(BOOLEAN, DATE, DECIMAL, DURATION, FRACTION, INTEGER, STRING, YEAR);
    }

    @BaseType
    private final String baseType;

    /**
     * Default initializer for gson.
     */
    public BaseInputDataType() {
        super();
        this.baseType = null;
    }

    public BaseInputDataType(@BaseType String baseType) {
        super();
        this.baseType = baseType;
    }

    protected BaseInputDataType(Parcel in) {
        this.baseType = in.readString();
    }

    @Override
    public HashSet<String> listSelectionHints() {
        if (BaseType.BOOLEAN.equals(baseType)) {
            return new HashSet<>(Arrays.asList(
                    InputUIHint.LIST, InputUIHint.CHECKBOX, InputUIHint.RADIO_BUTTON));
        }
        return new HashSet<>();
    }

    @Override
    public String getAnswerResultType() {
        return baseType;
    }

    @Override
    public LinkedHashSet<String> validStandardUIHints() {
        // TODO: mdephillips 12/4/18 see comments above all returns to eventually match iOS
        switch (baseType) {
            case BaseType.BOOLEAN:
                // return [.list, .picker, .checkbox, .radioButton, .toggle]
                return new LinkedHashSet<>(Arrays.asList(
                        InputUIHint.LIST, InputUIHint.CHECKBOX, InputUIHint.RADIO_BUTTON));
            case BaseType.DATE:
            case BaseType.DURATION:
                // return [.picker, .textfield]
                return new LinkedHashSet<>();
            case BaseType.DECIMAL:
            case BaseType.INTEGER:
            case BaseType.YEAR:
            case BaseType.FRACTION:
                // return [.textfield, .slider, .picker]
                return new LinkedHashSet<>();
            case BaseType.STRING:
                // return [.textfield, .multipleLine]
                return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        return this.baseType;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        BaseInputDataType baseType = (BaseInputDataType) o;
        return Objects.equal(this.getBaseType(), baseType.getBaseType());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.baseType);
    }

    @BaseType
    String getBaseType() {
        return this.baseType;
    }
}
