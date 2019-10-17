package org.sagebionetworks.research.presentation.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.presentation.perform_task.TaskResultManager.TaskResultManagerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Manages processing of TaskResults via a bound service.
 */
public class BoundServiceTaskResultProcessingManager implements TaskResultProcessingManager {
    /**
     * Upon connection to TaskResultService, calls each TaskProcessor with the final TaskResult and waits for every
     * processor to complete. Upon completion of processors, unbinds from TaskResultService.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BoundServiceTaskResultProcessingManager.class);

    private BoundServiceTaskResultManager boundServiceTaskResultManager;

    private final CompositeDisposable compositeDisposable;

    private final ImmutableSet<TaskResultProcessor> taskResultProcessors;

    @Inject
    public BoundServiceTaskResultProcessingManager(
            final @NonNull BoundServiceTaskResultManager boundServiceTaskResultManager,
            final @NonNull Set<TaskResultProcessor> taskResultProcessors) {
        this.boundServiceTaskResultManager = checkNotNull(boundServiceTaskResultManager);
        this.taskResultProcessors = ImmutableSet.copyOf(checkNotNull(taskResultProcessors));
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void registerTaskRun(@NonNull String taskIdentifier,
            @NonNull UUID taskRunUUID) {
        Single<TaskResultManagerConnection> connection = boundServiceTaskResultManager
                .getTaskResultManagerConnection(taskIdentifier, taskRunUUID);
        compositeDisposable.add(
                connection
                        .observeOn(Schedulers.io())
                        .flatMapCompletable(trmc ->
                                Completable.mergeDelayError(
                                        Flowable.fromIterable(taskResultProcessors)
                                                .map(trp -> trmc.getFinalTaskResult()
                                                        .flatMapCompletable(trp::processTaskResult)
                                                ))
                                        .doOnComplete(trmc::disconnect))
                        .subscribe(
                                () -> {
                                    LOGGER.debug("Finished processing task for identifier: {}, task run: {}",
                                            taskIdentifier, taskRunUUID);
                                },
                                t -> {
                                    LOGGER.warn("Error processing task result", t);
                                }));
    }
}
