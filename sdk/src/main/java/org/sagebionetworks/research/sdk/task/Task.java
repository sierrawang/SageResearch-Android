/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebionetworks.research.sdk.task;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.sdk.Duration;
import org.sagebionetworks.research.sdk.Schema;


interface Task {
    class Progress {
        private final int progress;
        private final int total;
        private final boolean isEstimated;

        public Progress(int progress, int total, boolean isEstimated) {
            this.progress = progress;
            this.total = total;
            this.isEstimated = isEstimated;
        }

        public int getProgress() {
            return progress;
        }

        public int getTotal() {
            return total;
        }

        public boolean isEstimated() {
            return isEstimated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Progress progress1 = (Progress) o;

            if (progress != progress1.progress) return false;
            if (total != progress1.total) return false;
            return isEstimated == progress1.isEstimated;

        }

        @Override
        public int hashCode() {
            int result = progress;
            result = 31 * result + total;
            result = 31 * result + (isEstimated ? 1 : 0);
            return result;
        }
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
