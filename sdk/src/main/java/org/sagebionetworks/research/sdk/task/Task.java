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

package org.sagebionetworks.research.sdk.task;

import org.sagebionetworks.research.sdk.Schema;

import java.time.Duration;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Task extends Parcelable {
    @NonNull
    String getTaskIdentifier();

    @Nullable
    Schema getSchema();

    @Nullable
    String getTitle();

    @Nullable
    String getDetail();

    @Nullable
    String getCopyright();

    @Nullable
    Duration getEstimatedDuration();

    @DrawableRes
    int getIcon();

    interface Progress extends Parcelable {
        public int getProgress();

        public int getTotal();

        public boolean isEstimated();
    }

    class Info {
        @NonNull
        private final String taskIdentifier;
        @Nullable
        private final Schema schema;
        @Nullable
        private final String title;
        @Nullable
        private final String detail;
        @Nullable
        private final String copyright;
        @Nullable
        private final Duration estimatedDuration;
        @DrawableRes
        private final int icon;

        public Info(@NonNull String taskIdentifier, Schema schema, String title, String detail, String copyright, Duration estimatedDuration, int icon) {
            this.taskIdentifier = taskIdentifier;
            this.schema = schema;
            this.title = title;
            this.detail = detail;
            this.copyright = copyright;
            this.estimatedDuration = estimatedDuration;
            this.icon = icon;
        }

        @NonNull
        public String getTaskIdentifier() {

            return taskIdentifier;
        }

        @Nullable
        public Schema getSchema() {
            return schema;
        }

        @Nullable
        public String getTitle() {
            return title;
        }

        @Nullable
        public String getDetail() {
            return detail;
        }

        @Nullable
        public String getCopyright() {
            return copyright;
        }

        @Nullable
        public Duration getEstimatedDuration() {
            return estimatedDuration;
        }

        @DrawableRes
        public int getIcon() {
            return icon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Info info = (Info) o;

            if (icon != info.icon) return false;
            if (!taskIdentifier.equals(info.taskIdentifier)) return false;
            if (schema != null ? !schema.equals(info.schema) : info.schema != null) return false;
            if (title != null ? !title.equals(info.title) : info.title != null) return false;
            if (detail != null ? !detail.equals(info.detail) : info.detail != null) return false;
            if (copyright != null ? !copyright.equals(info.copyright) : info.copyright != null)
                return false;
            return estimatedDuration != null ? estimatedDuration.equals(info.estimatedDuration) : info.estimatedDuration == null;
        }

        @Override
        public int hashCode() {
            int result = taskIdentifier.hashCode();
            result = 31 * result + (schema != null ? schema.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (detail != null ? detail.hashCode() : 0);
            result = 31 * result + (copyright != null ? copyright.hashCode() : 0);
            result = 31 * result + (estimatedDuration != null ? estimatedDuration.hashCode() : 0);
            result = 31 * result + icon;
            return result;
        }
    }
}
