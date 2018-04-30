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

package org.sagebionetworks.research.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.TaskResult;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfo;
import org.sagebionetworks.research.domain.task.TaskInfoBase;
import org.sagebionetworks.research.domain.task.navigation.TaskBase;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import javax.inject.Inject;

public class ResourceTaskRepository implements TaskRepository {
    private final Context context;

    private final Gson gson;

    @Inject
    public ResourceTaskRepository(Context context, Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    @NonNull
    @Override
    public Single<Task> getTask(final String taskIdentifier) {
        AssetManager assetManager = context.getAssets();

        try {
            byte[] bytes = ByteStreams
                    .toByteArray(assetManager.open("task/" + taskIdentifier + ".json"));
            String json = new String(bytes);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Single.fromCallable(() ->
                gson.fromJson(
                        new InputStreamReader(assetManager.open("task/" + taskIdentifier + ".json")),
                        TaskBase.class));
    }

    @NonNull
    @Override
    public Single<TaskInfo> getTaskInfo(final String taskIdentifier) {
        AssetManager assetManager = context.getAssets();

        return Single.fromCallable(() ->
                gson.fromJson(
                        new InputStreamReader(assetManager.open("task/info/" + taskIdentifier + ".json")),
                        TaskInfoBase.class));
    }

    @NonNull
    @Override
    public Maybe<TaskResult> getTaskResult(final UUID taskRunUUID) {
        return Maybe.error(new UnsupportedOperationException("Not implemented"));
    }

    @NonNull
    @Override
    public Completable setTaskResult(final TaskResult taskResult) {
        return Completable.error(new UnsupportedOperationException("Not implemented"));
    }
}
