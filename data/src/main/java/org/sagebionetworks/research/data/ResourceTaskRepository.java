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

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.TaskResult;
import org.sagebionetworks.research.domain.step.SectionStep;
import org.sagebionetworks.research.domain.step.SectionStepBase;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.TransformerStep;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import javax.inject.Inject;

public class ResourceTaskRepository implements TaskRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceTaskRepository.class);

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
        return Single.fromCallable(() -> {
            Task task = gson.fromJson(this.getJsonTaskAsset(taskIdentifier), Task.class);
            ImmutableList<Step> taskSteps = task.getSteps();
            List<Step> steps = new ArrayList<>();
            for (Step step : taskSteps) {
                steps.add(resolveTransformers(step));
            }

            return task.copyWithSteps(steps);
        });
    }

    /**
     * Returns the given step with all of the transformers that are substeps of it, recursively
     * replaced with the result of getting their resource and creating a SectionStep from it.
     * @param step The step to replace all the transformer substeps of.
     * @return The givne step with all the transformer substeps replaced with the result of turning their resources
     * into section steps.
     * @throws IOException If any of the transformer steps has a resource that cannot be opened.
     */
    private Step resolveTransformers(Step step) throws IOException {
        if (step instanceof TransformerStep) {
            TransformerStep transformer = (TransformerStep) step;
            // For now the transformer only supports SectionSteps.
            SectionStep result = gson.fromJson(
                    this.getJsonTransformerAsset(transformer.getResourceName()), SectionStep.class);
            result = result.copyWithIdentifier(transformer.getIdentifier());
            return resolveTransformers(result);
        } else if (step instanceof SectionStep) {
            SectionStep section = (SectionStep) step;
            ImmutableList<Step> steps = section.getSteps();
            ImmutableList.Builder<Step> builder = new ImmutableList.Builder<>();
            for (Step innerStep : steps) {
                builder.add(resolveTransformers(innerStep));
            }

            return new SectionStepBase(section.getIdentifier(), builder.build());
        } else {
            return step;
        }
    }

    /**
     * Returns an InputStreamReader for the given asset path.
     * @param assetPath The path to the asset to open.
     * @return An InputStreamReader for the given asset path.
     * @throws IOException if the given assetPath cannot be opened.
     */
    @NonNull
    private InputStreamReader getAsset(String assetPath) throws IOException {
        AssetManager assetManager = context.getAssets();
        return new InputStreamReader(assetManager.open(assetPath));
    }

    @NonNull
    @Override
    public InputStreamReader getJsonTaskAsset(String assetName) throws IOException {
        String assetPath = "task/" + assetName + ".json";
        return this.getAsset(assetPath);
    }


    @NonNull
    @Override
    public InputStreamReader getJsonTaskInfoAsset(String assetName) throws IOException {
        String assetPath = "task/info/" + assetName + ".json";
        return this.getAsset(assetPath);

    }

    @NonNull
    @Override
    public InputStreamReader getJsonTransformerAsset(String assetName) throws IOException {
        String assetPath = "task/transformer/" + assetName;
        return this.getAsset(assetPath);
    }

    @NonNull
    @Override
    public Single<TaskInfo> getTaskInfo(final String taskIdentifier) {
        return Single.fromCallable(() ->
                gson.fromJson(this.getJsonTaskInfoAsset(taskIdentifier), TaskInfo.class));
    }

    @NonNull
    @Override
    public Maybe<TaskResult> getTaskResult(final UUID taskRunUUID) {
        LOGGER.warn("Always returning no task result");
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Completable setTaskResult(final TaskResult taskResult) {
        return Completable.error(new UnsupportedOperationException("Not implemented"));
    }
}
