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

package org.sagebionetworks.research.domain.interfaces;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to help with the creation of a hashCode without having to worry about doing any
 * math. User's simply add all the fields they want to be hashed and then call hash().
 */
public class HashCodeHelper {
    // This set represents the fields of the object that the HashCodeHelper is producing the hash code for.
    private Set<Object> fields;

    /**
     * Creates a new HashCodeHelper with no fields.
     */
    public HashCodeHelper() {
        this.fields = new HashSet<>();
    }

    /**
     * Adds all the given fields to the fields that this HashCodeHelper is hashing.
     * @param objects The objects to add as fields to this HashCodeHelper.
     * @return This HashCodeHelper.
     */
    public HashCodeHelper addFields(Object... objects) {
        this.fields.addAll(Arrays.asList(objects));
        return this;
    }

    /**
     * The hashCode for this hashCodeHelper.
     * @return The hashCode for this hashCodeHelper.
     */
    public int hash() {
        // We convert the set to an array to ensure the hashCode method is getting the
        // correct form.
        return Objects.hashCode(this.fields.toArray());
    }

}
