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

package org.sagebionetworks.research.sdk.result;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class TaskResult extends ResultBase {

    @NonNull
    private final UUID taskRunUUID;
    @NonNull
    private final List<Result> stepResults;
    @NonNull
    private final Set<Result> asyncResults;

    public TaskResult(@NonNull Date startDate, @NonNull Date endDate) {
        super(startDate, endDate);

        taskRunUUID = UUID.randomUUID();
        stepResults = new ArrayList<>();
        asyncResults = new HashSet<>();
    }

    public void addStepResult(@NonNull Result stepResult) {
        stepResults.add(stepResult);
    }

    @NonNull
    public UUID getTaskRunUUID() {
        return taskRunUUID;
    }

    @NonNull
    public List<Result> getStepResults() {
        return stepResults;
    }

    @NonNull
    public Set<Result> getAsyncResults() {
        return asyncResults;
    }
}
