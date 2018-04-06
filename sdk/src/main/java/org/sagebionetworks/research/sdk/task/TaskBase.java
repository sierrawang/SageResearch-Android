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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.sdk.AsyncAction;
import org.sagebionetworks.research.sdk.result.TaskResult;
import org.sagebionetworks.research.sdk.step.Step;

import java.util.ArrayList;
import java.util.List;


public abstract class TaskBase implements Task {

    @NonNull
    private final String identifier;
    @Nullable
    private final Info info;
    @NonNull
    private final List<AsyncAction> asyncActions;

    public TaskBase(@NonNull String identifier, Info info) {
        this.identifier = identifier;
        this.info = info;
        asyncActions = new ArrayList<>();
    }

    @Nullable
    public abstract Step getStep(@NonNull String identifier);

    @Nullable
    public abstract Step getStepBefore(@NonNull Step step, @Nullable TaskResult taskResult);

    @Nullable
    public abstract Step getStepAfter(@NonNull Step step, @Nullable TaskResult taskResult);

    @NonNull
    public abstract Progress getProgress(@NonNull Step step, @Nullable TaskResult taskResult);

    public abstract void validate();

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @Nullable
    public Info getInfo() {
        return info;
    }

    @NonNull
    public List<AsyncAction> getAsyncActions() {
        return asyncActions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskBase task = (TaskBase) o;

        if (!identifier.equals(task.identifier)) return false;
        if (info != null ? !info.equals(task.info) : task.info != null) return false;
        return asyncActions.equals(task.asyncActions);

    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + asyncActions.hashCode();
        return result;
    }
}

