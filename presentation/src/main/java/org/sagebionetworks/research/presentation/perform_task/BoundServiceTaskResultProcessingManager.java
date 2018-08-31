package org.sagebionetworks.research.presentation.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.sagebionetworks.research.presentation.perform_task.TaskResultManager.TaskResultManagerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
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

    private final ImmutableList<TaskResultProcessor> taskResultProcessors;

    @Inject
    public BoundServiceTaskResultProcessingManager(
            final @NonNull BoundServiceTaskResultManager boundServiceTaskResultManager,
            final @NonNull List<TaskResultProcessor> taskResultProcessors) {
        this.boundServiceTaskResultManager = checkNotNull(boundServiceTaskResultManager);
        this.taskResultProcessors = ImmutableList.copyOf(checkNotNull(taskResultProcessors));
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
                                Observable.fromIterable(taskResultProcessors)
                                        .flatMapCompletable(trp -> trmc.getFinalTaskResult()
                                                .flatMapCompletable(trp::processTaskResult))
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
