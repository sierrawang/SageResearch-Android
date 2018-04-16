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

package org.sagebionetworks.research.domain.task;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.sagebionetworks.research.domain.Schema;
import org.threeten.bp.Duration;


/**
 * Created by liujoshua on 10/2/2017.
 */

public interface Task {
    @NonNull
    String getIdentifier();

    @Nullable
    Schema getSchema();

    @StringRes
    int getTitle();

    @StringRes
    int getDetail();

    @StringRes
    int getCopyright();

    @Nullable
    Duration getEstimatedDuration();

    @DrawableRes
    int getIcon();

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
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Progress progress1 = (Progress) o;
            return progress == progress1.progress &&
                total == progress1.total &&
                isEstimated == progress1.isEstimated;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(progress, total, isEstimated);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("progress", progress)
                .add("total", total)
                .add("isEstimated", isEstimated)
                .toString();
        }
    }
}
