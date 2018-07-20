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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.form.data_types.BaseInputDataType.BaseType;
import org.sagebionetworks.research.domain.interfaces.HashCodeHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CollectionInputDataType extends InputDataType {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({CollectionType.SINGLE_CHOICE, CollectionType.MULTIPLE_CHOICE, CollectionType.MULTIPLE_COMPONENT})
    public @interface CollectionType {
        String SINGLE_CHOICE = "singleChoice";
        String MULTIPLE_CHOICE = "multipleChoice";
        String MULTIPLE_COMPONENT = "multipleComponent";

        ImmutableSet<String> ALL = ImmutableSet.of(SINGLE_CHOICE, MULTIPLE_CHOICE, MULTIPLE_COMPONENT);
    }

    public static final String DELIMINATOR = ".";

    public static final Creator<CollectionInputDataType> CREATOR = new Creator<CollectionInputDataType>() {
        @Override
        public CollectionInputDataType createFromParcel(Parcel source) {
            return new CollectionInputDataType(source);
        }

        @Override
        public CollectionInputDataType[] newArray(int size) {
            return new CollectionInputDataType[size];
        }
    };

    @Nullable
    @BaseType
    private final String baseType;

    @NonNull
    @CollectionType
    private final String collectionType;

    /**
     * Default initializer for gson.
     */
    public CollectionInputDataType() {
        super();
        this.collectionType = null;
        this.baseType = null;
    }

    public CollectionInputDataType(@CollectionType @NonNull final String collectionType,
            @BaseType @Nullable final String baseType) {
        super();
        this.collectionType = collectionType;
        this.baseType = baseType;
    }

    public CollectionInputDataType(@CollectionType @NonNull final String collectionType) {
        super();
        this.collectionType = collectionType;
        this.baseType = null;
    }

    protected CollectionInputDataType(Parcel in) {
        this.collectionType = in.readString();
        this.baseType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.collectionType);
        dest.writeString(this.baseType);
    }

    @Nullable
    @BaseType
    public String getBaseType() {
        return this.baseType;
    }

    @NonNull
    @CollectionType
    public String getCollectionType() {
        return this.collectionType;
    }

    @Override
    public String toString() {
        return this.collectionType + DELIMINATOR + this.baseType;
    }

    @Override
    protected boolean equalsHelper(Object o) {
        CollectionInputDataType collectionType = (CollectionInputDataType) o;
        return Objects.equal(this.getCollectionType(), collectionType.getCollectionType()) &&
                Objects.equal(this.getBaseType(), collectionType.getBaseType());
    }

    @Override
    protected HashCodeHelper hashCodeHelper() {
        return super.hashCodeHelper()
                .addFields(this.collectionType, this.baseType);
    }
}
