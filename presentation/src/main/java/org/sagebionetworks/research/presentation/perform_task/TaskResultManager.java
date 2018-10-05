package org.sagebionetworks.research.presentation.perform_task;

import androidx.annotation.NonNull;
import com.google.common.collect.ImmutableSet;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;

import java.util.UUID;

public interface TaskResultManager {
    @NonNull
    Single<TaskResultManagerConnection> getTaskResultManagerConnection(
            @NonNull String taskIdentifier, @NonNull UUID taskRunUUID);

    interface TaskResultManagerConnection {
        void addAsyncActionResult(@NonNull Maybe<? extends Result> actionActionResult);

        void addStepResult(@NonNull Result stepResult);

        void disconnect();

        void finishTask();

        @NonNull
        Observable<ImmutableSet<Maybe<? extends Result>>> getAsyncResultsObservable();

        @NonNull
        Single<TaskResult> getFinalTaskResult();

        @NonNull
        TaskResult getLatestTaskResult();

        @NonNull
        Observable<TaskResult> getTaskResultObservable();
    }
}
