package org.sagebionetworks.research.presentation.perform_task;

import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.TaskResult;

import java.util.UUID;

import io.reactivex.Completable;

/**
 * Manages processing of TaskResults.
 * <p>
 * Connects to TaskResultService for a current taskRunUUID. Calls the TaskResultProcessors with the final TaskResult
 * and waits for them to complete.
 */
public interface TaskResultProcessingManager {
    interface TaskResultProcessor {
        Completable processTaskResult(TaskResult taskResult);
    }

    /**
     * Registers a task run. Each task result processor will receive the final task result from TaskResultService for
     * processing.
     *
     * @param taskRunUUID
     *         the task run to process
     */
    void registerTaskRun(@NonNull String taskIdentifier, @NonNull UUID taskRunUUID);
}
