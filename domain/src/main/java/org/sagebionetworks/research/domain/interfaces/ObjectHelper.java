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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public abstract class ObjectHelper {
    private volatile HashCodeHelper hashCodeHelper;

    @Override
    public int hashCode() {
        if (hashCodeHelper == null) {
            synchronized (this) {
                if (hashCodeHelper == null) {
                    hashCodeHelper = hashCodeHelper();
                }
            }
        }

        return hashCodeHelper.hash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return this.equalsHelper(o);
    }

    @Override
    public String toString() {
        return this.toStringHelper().toString();
    }

    /**
     * Returns true if this objects fields are equal to the fields of the given object, false otherwise. Expected that
     * subclasses will override to add their own fields. Requires: this.getClass() == o.getClass()
     *
     * @param o The object to check for equality with.
     * @return true if this object's fields are equal to the fields of the given object.
     */
    protected abstract boolean equalsHelper(Object o);

    /**
     * Instantiates and returns a HashCodeHelper that can be used to produce the hashCode for this object.
     * Expected that subclasses will override to add their own fields to the hash code, as with equals and toString helpers.
     *
     * @return The HashCodeHelper that can be used to produce the hashCode for this object.
     */
    protected HashCodeHelper hashCodeHelper() {
        return hashCodeHelper;
    }

    /**
     * Returns a toStringHelper that can be used to produce the toString for this object. Expected that subclasses
     * will override to add their own fields.
     *
     * @return The ToStringHelper that can be used to produce the toString for this object.
     */
    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this);
    }
}
