package org.sagebionetworks.research.presentation.perform_task;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;

import java.util.UUID;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface TaskResultManager {
    interface TaskResultManagerConnection {
        void addAsyncActionResult(@NonNull Maybe<Result> actionActionResult);

        void addStepResult(@NonNull Result stepResult);

        void disconnect();

        void finishTask();

        @NonNull
        Observable<ImmutableSet<Maybe<Result>>> getAsyncResultsObservable();

        @NonNull
        Single<TaskResult> getFinalTaskResult();

        @NonNull
        TaskResult getLatestTaskResult();

        @NonNull
        Observable<TaskResult> getTaskResultObservable();
    }

    @NonNull
    Single<TaskResultManagerConnection> getTaskResultManagerConnection(
            @NonNull String taskIdentifier, @NonNull UUID taskRunUUID);
}
