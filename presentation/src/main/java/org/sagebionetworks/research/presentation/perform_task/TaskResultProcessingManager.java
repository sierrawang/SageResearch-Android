package org.sagebionetworks.research.presentation.perform_task;

import static android.content.Context.BIND_ABOVE_CLIENT;
import static android.content.Context.BIND_AUTO_CREATE;

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.presentation.perform_task.TaskResultService.TaskResultServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Manages processing of TaskResults.
 * <p>
 * Connects to TaskResultService for a current taskRunUUID. Calls the TaskResultProcessors with the final TaskResult
 * and waits for them to complete.
 */
public class TaskResultProcessingManager {
    /**
     * Upon connection to TaskResultService, calls each TaskProcessor with the final TaskResult and waits for every
     * processor to complete. Upon completion of processors, unbinds from TaskResultService.
     */
    public static class TaskResultServiceConnection implements ServiceConnection {
        private static final Logger LOGGER = LoggerFactory.getLogger(TaskResultServiceConnection.class);

        private TaskResultServiceBinder binder = null;

        private final CompositeDisposable compositeDisposable;

        private final Context context;

        private final ImmutableList<TaskResultProcessor> taskResultProcessors;

        private final UUID taskRunUUID;

        public TaskResultServiceConnection(@NonNull Context context, @NonNull UUID taskRunUUID,
                @NonNull ImmutableList<TaskResultProcessor> taskResultProcessors) {
            this.context = checkNotNull(context);
            this.taskRunUUID = checkNotNull(taskRunUUID);
            this.taskResultProcessors = taskResultProcessors;
            this.compositeDisposable = new CompositeDisposable();
        }

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            LOGGER.debug("Connected to service: {} for taskRunUUID: {}", name.toShortString(), taskRunUUID);

            binder = (TaskResultServiceBinder) service;

            // noinspection RxSubscribeOnError
            compositeDisposable.add(
                    binder.getTaskResultObservable()
                            .lastOrError()
                            .flatMapCompletable(taskResult ->
                                    Observable.fromIterable(taskResultProcessors)
                                            .flatMapCompletable(taskResultProcessor -> {
                                                LOGGER.debug("Calling taskResultProcessor {} for taskRunUUID: {}",
                                                        taskResultProcessor,
                                                        taskRunUUID);
                                                return taskResultProcessor.processTaskResult(taskResult);
                                            }))
                            .onErrorResumeNext(t -> {
                                LOGGER.warn("Error while processing task result", t);
                                return Completable.complete();
                            })
                            .doOnComplete(() -> context.unbindService(this))
                            .subscribe()
            );
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            LOGGER.debug("Disconnected from service: {} for taskRunUUID: {}", name.toShortString(), taskRunUUID);
            compositeDisposable.dispose();
        }
    }

    public interface TaskResultProcessor {
        Completable processTaskResult(TaskResult taskResult);
    }

    private final Context context;

    private final ImmutableList<TaskResultProcessor> taskResultProcessors;

    @Inject
    public TaskResultProcessingManager(@NonNull Application context,
            @NonNull List<TaskResultProcessor> taskResultProcessors) {
        this.context = checkNotNull(context);
        checkNotNull(taskResultProcessors);
        this.taskResultProcessors = ImmutableList.copyOf(taskResultProcessors);
    }

    /**
     * Registers a task run. Each task result processor will receive the final task result from TaskResultService for
     * processing.
     *
     * @param taskRunUUID
     *         the task run to process
     */
    public void registerTaskResultProcessors(@NonNull UUID taskRunUUID) {
        checkNotNull(taskRunUUID);
        checkNotNull(taskResultProcessors);

        TaskResultServiceConnection conn = new TaskResultServiceConnection(context, taskRunUUID,
                taskResultProcessors);

        context.bindService(TaskResultService.createIntent(context, taskRunUUID), conn,
                BIND_ABOVE_CLIENT | BIND_AUTO_CREATE);
    }
}
