package org.sagebionetworks.research.presentation.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import dagger.android.DaggerService;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * A service which manages the state of a TaskResult.
 * <p>
 * This service allows synchronous and asynchronous Results to be added for a task run. Asynchronous task results will
 * be waited on and added to the task result.
 */
public class TaskResultService extends DaggerService {
    /**
     * Provides a public API to the TaskResultService.
     * <p>
     * TODO: allow cancellation of task run, which will also cancel pending results
     */
    @MainThread
    public static class TaskResultServiceBinder extends Binder {
        private Logger LOGGER = LoggerFactory.getLogger(TaskResultServiceBinder.class);

        private final TaskResultService taskResultService;

        private final UUID taskRunUUID;

        TaskResultServiceBinder(@NonNull UUID taskRunUUID, @NonNull TaskResultService taskResultService) {
            this.taskRunUUID = checkNotNull(taskRunUUID);
            this.taskResultService = checkNotNull(taskResultService);
        }

        /**
         * @param asyncResult
         *         async result to add
         */
        public void addAsyncActionResult(Maybe<Result> asyncResult) {
            LOGGER.debug("addAsyncActionResult called for {}", asyncResult);
            if (isTaskFinished()) {
                LOGGER.warn("addAsyncActionResult called for finished task");
                return;
            }
            taskResultService.addAsyncResult(taskRunUUID, asyncResult);
        }

        /**
         * @param stepResult
         *         step result to add
         */
        public void addStepResult(Result stepResult) {
            LOGGER.debug("addStepResult called for {}", stepResult);
            if (isTaskFinished()) {
                LOGGER.warn("addStepResult called for finished task");
                return;
            }

            if (isTaskFinished()) {
                return;
            }

            taskResultService.addStepResult(taskRunUUID, stepResult);
        }

        /**
         * Mark a task as finished. After this, results cannot be added.
         */
        public void finishTask() {
            LOGGER.debug("finishTask called");
            if (isTaskFinished()) {
                LOGGER.warn("finishTask called for finished task");
                return;
            }

            taskResultService.finish(taskRunUUID);
        }

        /**
         * TODO: may be nice to have a key to identify the result by, i.e. asyncResultID/recorderId
         *
         * @return observable which will produce each of the async results added to this task run, each async result
         *         is cached so multiple subscriptions will not cause the async result to run again
         */
        public Observable<Maybe<Result>> getAsyncResultsObservable() {
            LOGGER.debug("getAsyncResultsObservable called");

            return taskResultService.getAsyncResults(taskRunUUID);
        }

        /**
         * @return observable of the latest task result, which will terminate once the task is finished and all
         *         asynchronous results added to the final task result
         */
        public Observable<TaskResult> getTaskResultObservable() {
            LOGGER.debug("getTaskResultObservable called");

            return taskResultService.getTaskResultObservable(taskRunUUID);
        }

        /**
         * Indicates whether a task is finished. A task is finished once the participant has completed all steps. This
         * means no more results (whether synchronous or asynchronous) may be added. However, the TaskResult may still
         * change as asynchronous Results arrive.
         *
         * @return whether the task is finished
         */
        public boolean isTaskFinished() {
            return taskResultService.isTaskFinished(taskRunUUID);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskResultService.class);

    private static final String TAG_TASK_RUN_UUID = "taskRunUUID";

    private final HashMap<UUID, ReplaySubject<Maybe<Result>>> taskToAsyncResultsObservable = new HashMap<>();

    private final HashMap<UUID, AtomicInteger> taskToBinderCount = new HashMap<>();

    private final HashMap<UUID, CompletableSubject> taskToCompletable = new HashMap<>();

    private final HashMap<UUID, CompositeDisposable> taskToDisposables = new HashMap<>();

    private final HashMap<UUID, BehaviorSubject<TaskResult>> taskToTaskResultObservable = new HashMap<>();

    public static Intent createIntent(@NonNull Context context, @NonNull UUID taskRunUUID) {
        return new Intent(context, TaskResultService.class)
                .putExtra(TAG_TASK_RUN_UUID, new ParcelUuid(taskRunUUID));
    }

    @Override
    public void onDestroy() {
        LOGGER.debug("onDestroy called");

        for (CompositeDisposable compositeDisposable : taskToDisposables.values()) {
            compositeDisposable.dispose();
        }
        taskToAsyncResultsObservable.clear();
        taskToBinderCount.clear();
        taskToCompletable.clear();
        taskToDisposables.clear();
        taskToTaskResultObservable.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        UUID taskRunUUID = getTaskRunUUID(intent);
        LOGGER.debug("onBind called for taskRunUUID {}", taskRunUUID);
        init(taskRunUUID);
        return new TaskResultServiceBinder(taskRunUUID, this);
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        UUID taskRunUUID = getTaskRunUUID(intent);
        LOGGER.debug("onUnbind called for taskRunUUID {}", taskRunUUID);

        if (taskToBinderCount.get(taskRunUUID).decrementAndGet() < 1) {
            taskToAsyncResultsObservable.remove(taskRunUUID);
            taskToBinderCount.remove(taskRunUUID);
            taskToCompletable.remove(taskRunUUID);
            taskToDisposables.remove(taskRunUUID).dispose();
            taskToTaskResultObservable.remove(taskRunUUID);
        }
        return false;
    }

    @VisibleForTesting
    void addAsyncResult(UUID taskRunUUID, Maybe<Result> resultMaybe) {
        checkState(!isTaskFinished(taskRunUUID));

        taskToAsyncResultsObservable
                .get(taskRunUUID)
                .onNext(resultMaybe.doOnSuccess(
                        asyncResult ->
                        {
                            BehaviorSubject<TaskResult> taskResultBehaviorSubject = taskToTaskResultObservable
                                    .get(taskRunUUID);
                            TaskResult taskResult = taskResultBehaviorSubject.getValue();
                            LOGGER.debug("received async result: {}, updating task result for uuid: {}", asyncResult,
                                    taskRunUUID);
                            taskResultBehaviorSubject.onNext(taskResult.addAsyncResult(asyncResult));
                        }
                ).cache());
    }

    @VisibleForTesting
    void addStepResult(UUID taskRunUUID, Result stepResult) {
        checkState(!isTaskFinished(taskRunUUID));
        LOGGER.debug("received step result: {}, updating task result for uuid: {}", stepResult, taskRunUUID);

        BehaviorSubject<TaskResult> taskResultBehaviorSubject = taskToTaskResultObservable.get(taskRunUUID);
        taskResultBehaviorSubject.onNext(
                taskResultBehaviorSubject.getValue().addStepHistory(stepResult));
    }

    @VisibleForTesting
    void finish(UUID taskRunUUID) {
        checkState(!isTaskFinished(taskRunUUID));

        // task is complete
        taskToCompletable.get(taskRunUUID).onComplete();
        // source of async results complete, no more will be added
        taskToAsyncResultsObservable.get(taskRunUUID).onComplete();
    }

    @SuppressWarnings("unchecked")
    Observable<Maybe<Result>> getAsyncResults(UUID taskRunUUID) {
        return taskToAsyncResultsObservable.get(taskRunUUID);
    }

    Observable<TaskResult> getTaskResultObservable(UUID taskRunUUID) {
        return taskToTaskResultObservable.get(taskRunUUID);
    }

    void init(UUID taskRunUUID) {
        Observable<TaskResult> taskResultObservable = taskToTaskResultObservable.get(taskRunUUID);
        if (taskResultObservable != null) {
            // task run already initialized
            return;
        }
        taskToBinderCount.put(taskRunUUID, new AtomicInteger(1));
        taskToAsyncResultsObservable.put(taskRunUUID, ReplaySubject.create());
        taskToCompletable.put(taskRunUUID, CompletableSubject.create());
        taskToDisposables.put(taskRunUUID, new CompositeDisposable());
        taskToTaskResultObservable.put(taskRunUUID, BehaviorSubject.create());

        Observable<Result> asyncResults = taskToAsyncResultsObservable.get(taskRunUUID).flatMap(Maybe::toObservable);

        taskToDisposables.get(taskRunUUID)
                .add(asyncResults
                        .flatMapCompletable(c -> Completable.complete())
                        .mergeWith(taskToCompletable.get(taskRunUUID)
                                .doOnComplete(() ->
                                        LOGGER.debug("task completion received for taskRunUUID {}", taskRunUUID)))
                        .doOnComplete(() -> {
                            LOGGER.debug("completed work for taskRunUUID {}", taskRunUUID);
                            // async results complete and task complete, no more updates to TaskResult observable
                            taskToTaskResultObservable.get(taskRunUUID).onComplete();
                        }).subscribe());
    }

    @VisibleForTesting
    boolean isTaskFinished(UUID taskRunUUID) {
        return taskToCompletable.get(taskRunUUID).hasComplete();
    }

    private static UUID getTaskRunUUID(Intent intent) {
        return ((ParcelUuid) intent.getParcelableExtra(TAG_TASK_RUN_UUID)).getUuid();
    }
}
