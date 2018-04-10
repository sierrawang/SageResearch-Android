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

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.sagebionetworks.research.sdk.Schema;
import org.threeten.bp.Duration;

/**
 * Created by liujoshua on 10/2/2017.
 */
public class TaskBase implements Task {
    @NonNull
    private final String taskIdentifier;

    @Nullable
    private final Schema schema;

    @Nullable
    private final int title;

    @Nullable
    private final int detail;

    @Nullable
    private final int copyright;

    @Nullable
    private final Duration estimatedDuration;

    @DrawableRes
    private final int icon;

    public TaskBase(@NonNull String taskIdentifier, Schema schema, int title, int detail,
        int copyright, Duration estimatedDuration, int icon) {
        this.taskIdentifier = taskIdentifier;
        this.schema = schema;
        this.title = title;
        this.detail = detail;
        this.copyright = copyright;
        this.estimatedDuration = estimatedDuration;
        this.icon = icon;
    }

    @Override
    @NonNull
    public String getIdentifier() {

        return taskIdentifier;
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return schema;
    }

    @Override
    public int getTitle() {
        return 0;
    }

    @Override
    public int getDetail() {
        return 0;
    }

    @Override
    public int getCopyright() {
        return 0;
    }

    @Override
    @Nullable
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    @Override
    @DrawableRes
    public int getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskBase taskBase = (TaskBase) o;
        return icon == taskBase.icon &&
            Objects.equal(taskIdentifier, taskBase.taskIdentifier) &&
            Objects.equal(schema, taskBase.schema) &&
            Objects.equal(title, taskBase.title) &&
            Objects.equal(detail, taskBase.detail) &&
            Objects.equal(copyright, taskBase.copyright) &&
            Objects.equal(estimatedDuration, taskBase.estimatedDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskIdentifier, schema, title, detail, copyright,
            estimatedDuration, icon);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("taskIdentifier", taskIdentifier)
            .add("schema", schema)
            .add("title", title)
            .add("detail", detail)
            .add("copyright", copyright)
            .add("estimatedDuration", estimatedDuration)
            .add("icon", icon)
            .toString();
    }
}
