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

import static org.sagebionetworks.research.domain.task.navigation.TreeNavigator.SECTION_STEP_PREFIX_SEPARATOR;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import org.sagebionetworks.research.domain.async.AsyncActionConfiguration;
import org.sagebionetworks.research.domain.async.RecorderConfiguration;
import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.step.interfaces.SectionStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.domain.step.interfaces.TransformerStep;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class ResourceTaskRepository implements TaskRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceTaskRepository.class);

    protected final Context context;

    protected final Gson gson;

    @Inject
    public ResourceTaskRepository(Context context, Gson gson) {
        this.context = context;
        this.gson = gson;
    }

    /**
     * Returns and InputStreamReader corresponding to the given task asset.
     *
     * @param assetName
     *         The name of the asset to get.
     * @return An InputStreamReader corresponding to the Task asset with the given name.
     * @throws IOException
     *         If the asset of the given name cannot be opened.
     */
    @NonNull
    public InputStreamReader getJsonTaskAsset(String assetName) throws IOException {
        String assetPath = "task/" + assetName + ".json";
        return this.getAsset(assetPath);
    }

    /**
     * Returns an InputStreamReader that corresponds to the given taskInfo asset.
     *
     * @param assetName
     *         The name of the asset to get.
     * @return An InputStreamReader corresponding to the taskInfo asset with the given name.
     * @throws IOException
     *         If the asset of the given name cannot be opened.
     */
    @NonNull
    public InputStreamReader getJsonTaskInfoAsset(String assetName) throws IOException {
        String assetPath = "task/info/" + assetName + ".json";
        return this.getAsset(assetPath);

    }

    /**
     * Returns an InputStreamReader that corresponds to the given Transformer asset.
     *
     * @param assetName
     *         The name of the asset to get.
     * @return An InputStreamReader corresponding to the Transformer asset with the given name.
     * @throws IOException
     *         If the asset of the given name cannot be opened.
     */
    @NonNull
    public InputStreamReader getJsonTransformerAsset(String assetName) throws IOException {
        String assetPath = "task/transformer/" + assetName;
        return this.getAsset(assetPath);
    }

    @NonNull
    @Override
    public Single<Task> getTask(final String taskIdentifier) {
        return Single.fromCallable(() -> {
            Task task = gson.fromJson(this.getJsonTaskAsset(taskIdentifier), Task.class);
            ImmutableList<Step> taskSteps = task.getSteps();
            List<Step> steps = new ArrayList<>();
            for (Step step : taskSteps) {
                steps.add(resolveTransformers(step, ""));
            }

            task = task.copyWithSteps(steps);
            task = task.copyWithAsyncActions(getAsyncActions(task));
            return task;
        })
                .doOnSuccess(t -> {
                    LOGGER.debug("Successfully loaded task: {}", t);
                })
                .doOnError(
                        throwable -> {
                            LOGGER.warn("Error loading task with id: {}", taskIdentifier, throwable);
                        });
    }

    @NonNull
    @Override
    public Single<TaskInfoView> getTaskInfo(final String taskIdentifier) {
        return Single.fromCallable(() ->
                gson.fromJson(this.getJsonTaskInfoAsset(taskIdentifier), TaskInfoView.class));
    }

    @NonNull
    @Override
    public Maybe<TaskResult> getTaskResult(final UUID taskRunUUID) {
        LOGGER.warn("Always returning no task result");
        return Maybe.empty();
    }

    @Override
    @DrawableRes
    public int resolveDrawableFromString(@NonNull String name) throws NotFoundException {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if (resId == 0) {
            // The resource was not found
            throw new NotFoundException("Resource " + name + " couldn't be resolved as a drawable.");
        } else {
            return resId;
        }
    }

    @NonNull
    @Override
    public Completable setTaskResult(final TaskResult taskResult) {
        return Completable.error(new UnsupportedOperationException("Not implemented"));
    }

    /**
     * Returns an InputStreamReader for the given asset path.
     *
     * @param assetPath
     *         The path to the asset to open.
     * @return An InputStreamReader for the given asset path.
     * @throws IOException
     *         if the given assetPath cannot be opened.
     */
    @NonNull
    private InputStreamReader getAsset(String assetPath) throws IOException {
        AssetManager assetManager = context.getAssets();
        return new InputStreamReader(assetManager.open(assetPath), UTF_8);
    }

    /**
     * Returns the given step with all of the transformers that are substeps of it, recursively replaced with the
     * result of getting their resource and creating a SectionStep from it.
     *
     * @param step
     *         The step to replace all the transformer substeps of.
     * @return The givne step with all the transformer substeps replaced with the result of turning their
     *         resourcesinto section steps.
     * @throws IOException
     *         If any of the transformer steps has a resource that cannot be opened.
     */
    private Step resolveTransformers(Step step, String prefix) throws IOException {
        if (step instanceof TransformerStep) {
            TransformerStep transformer = (TransformerStep) step;
            // For now the transformer only supports SectionSteps.
            SectionStep result = gson.fromJson(
                    this.getJsonTransformerAsset(transformer.getResourceName()), SectionStep.class);
            result = result.copyWithIdentifier(prefix + transformer.getIdentifier());
            return resolveTransformers(result, prefix);
        } else if (step instanceof SectionStep) {
            SectionStep section = (SectionStep) step;
            ImmutableList<Step> steps = section.getSteps();
            ImmutableList.Builder<Step> builder = new ImmutableList.Builder<>();
            for (Step innerStep : steps) {
                builder.add(resolveTransformers(innerStep,
                        prefix + section.getIdentifier() + SECTION_STEP_PREFIX_SEPARATOR));
            }

            return section.copyWithSteps(builder.build());
        } else {
            Step copiedStep = step.copyWithIdentifier(prefix + step.getIdentifier());
            if (copiedStep.getClass() != step.getClass()) {
                LOGGER.warn("Copied step ({}) has different class than the original" +
                        "({})", copiedStep, step);
            }

            return step.copyWithIdentifier(prefix + step.getIdentifier());
        }
    }

    private static Set<AsyncActionConfiguration> getAsyncActions(Task task) {
        return ResourceTaskRepository.getAsyncActionsHelper(task.getSteps(), new HashSet<>(task.getAsyncActions()));
    }

    private static Set<AsyncActionConfiguration> getAsyncActionsHelper(List<Step> steps,
            Set<AsyncActionConfiguration> accumlator) {
        for (Step step : steps) {
            // A step's defaultStartIdentifier is it's identifier or in the case of a SectionStep the identifier of it's leftmost child.
            String defaultStartIdentifier = step.getIdentifier();
            // A step's defaultStopIdentifier is null, or in the case of a SectionStep the identifier of it's rightmost child.
            String defaultStopIdentifier = null;
            if (step instanceof SectionStep) {
                defaultStartIdentifier = getLeftMostChild(step).getIdentifier();
                defaultStopIdentifier = getRightMostChild(step).getIdentifier();
                SectionStep sectionStep = (SectionStep) step;
                // Recurse on the section step's substeps.
                ResourceTaskRepository.getAsyncActionsHelper(sectionStep.getSteps(), accumlator);
            }

            for (AsyncActionConfiguration asyncAction : step.getAsyncActions()) {
                AsyncActionConfiguration copy = asyncAction;
                if (asyncAction.getStartStepIdentifier() == null) {
                    copy = copy.copyWithStartStepIdentifier(defaultStartIdentifier);
                }

                if (copy instanceof RecorderConfiguration) {
                    RecorderConfiguration recorderConfiguration = (RecorderConfiguration) copy;
                    if (recorderConfiguration.getStopStepIdentifier() == null) {
                        recorderConfiguration = recorderConfiguration
                                .copyWithStopStepIdentifier(defaultStopIdentifier);
                        copy = recorderConfiguration;
                    }
                }

                copy = copy.copyWithIdentifier(
                        step.getIdentifier() + SECTION_STEP_PREFIX_SEPARATOR + copy.getIdentifier());
                accumlator.add(copy);
            }
        }

        return accumlator;
    }

    private static Step getLeftMostChild(Step step) {
        while (step instanceof SectionStep) {
            step = ((SectionStep) step).getSteps().get(0);
        }

        return step;
    }

    private static Step getRightMostChild(Step step) {
        while (step instanceof SectionStep) {
            List<Step> steps = ((SectionStep) step).getSteps();
            step = steps.get(steps.size() - 1);
        }

        return step;
    }
}
