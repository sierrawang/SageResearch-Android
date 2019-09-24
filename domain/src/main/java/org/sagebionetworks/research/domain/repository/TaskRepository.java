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

package org.sagebionetworks.research.domain.repository;

import android.content.res.Resources.NotFoundException;
import androidx.annotation.CheckResult;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfoView;

import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface TaskRepository {
    /**
     * Gets the task with the given task identifier.
     *
     * @param taskIdentifier
     *         The name of the task to get.
     * @return The task with the given taskIdentifier.
     */
    @NonNull
    Single<Task> getTask(String taskIdentifier);

    /**
     * Gets the task info with the given identifier.
     *
     * @param taskIdentifier
     *         The name of the task info to get.
     * @return The task info with the given identifier.
     */
    @NonNull
    Single<TaskInfoView> getTaskInfo(String taskIdentifier);

    /**
     * Gets the task result with the given UUID.
     *
     * @param taskRunUUID
     *         The UUID of the result to get.
     * @return the task result with the given UUID.
     */
    @NonNull
    Maybe<TaskResult> getTaskResult(UUID taskRunUUID);

    /**
     * Finds and returns the drawable resource with the given identifier, or throws a NotFoundException()
     *
     * @param name
     *         - the identifier of the drawable to find.
     * @return the drawable resource with the given identifier.
     * @throws NotFoundException
     *         if the given resource identifier cannot be resolved as a drawable.
     */
    @DrawableRes
    int resolveDrawableFromString(@NonNull String name) throws NotFoundException;

    @NonNull
    @CheckResult
    Completable setTaskResult(TaskResult taskResult);
}
