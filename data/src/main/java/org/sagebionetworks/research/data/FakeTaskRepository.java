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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.data.model.step.ConcreteUIStep;
import org.sagebionetworks.research.domain.Schema;
import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.TaskResult;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.ui.UIStep;
import org.sagebionetworks.research.domain.task.Task;
import org.threeten.bp.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Single;
import javax.inject.Inject;

public class FakeTaskRepository implements TaskRepository {
    @Inject public FakeTaskRepository() {}
    @Override
    public Single<Task> getTask(final String taskIdentifier) {
        return Single.<Task>just(new Task() {
            @NonNull
            @Override
            public String getIdentifier() {
                return UUID.randomUUID().toString();
            }

            @Nullable
            @Override
            public Schema getSchema() {
                return null;
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

            @Nullable
            @Override
            public Duration getEstimatedDuration() {
                return null;
            }

            @Override
            public int getIcon() {
                return 0;
            }
        });
    }

    @Override
    public Single<List<Step>> getTaskSteps(final Task task) {
        return Single.just(Arrays.<Step>asList(
                createUIStep(UUID.randomUUID().toString()),
                createUIStep(UUID.randomUUID().toString())
        ));
    }

    @NonNull
    @Override
    public Completable setTaskResult(final TaskResult taskResult) {
        return Completable.error(new UnsupportedOperationException("Not implemented yet"));
    }

    private UIStep createUIStep(String id) {
        return ConcreteUIStep.builder()
                .setIdentifier(id)
                .setTitle("Step: " + id)
                .setText("Step description for " + id)
                .setDetail("Detail for " + id)
                .setFootnote("Footnote for " + id)
                .build();
    }
}
