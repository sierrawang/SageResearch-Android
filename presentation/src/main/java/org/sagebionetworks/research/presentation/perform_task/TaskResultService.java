package org.sagebionetworks.research.presentation.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.CheckResult;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.domain.result.implementations.TaskResultBase;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.android.DaggerService;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;

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

        TaskResultServiceBinder(@NonNull TaskResultService taskResultService) {
            this.taskResultService = checkNotNull(taskResultService);
        }

        /**
         * @param taskRunUUID
         * @param asyncResult
         */
        public void addAsyncActionResult(@NonNull final UUID taskRunUUID, @NonNull Maybe<? extends Result> asyncResult) {
            checkNotNull(taskRunUUID);
            checkNotNull(asyncResult);

            LOGGER.debug("addAsyncActionResult called for {}", asyncResult);
            if (isTaskFinished(null)) {
                LOGGER.warn("addAsyncActionResult called for finished task");
                return;
            }
            taskResultService.addAsyncResult(taskRunUUID, asyncResult);
        }

        /**
         * @param taskRunUUID
         * @param stepResult
         */
        public void addStepResult(@NonNull final UUID taskRunUUID, @NonNull Result stepResult) {
            checkNotNull(taskRunUUID);
            checkNotNull(stepResult);

            LOGGER.debug("addStepResult called for {}", stepResult);
            if (isTaskFinished(null)) {
                LOGGER.warn("addStepResult called for finished task");
                return;
            }

            if (isTaskFinished(null)) {
                return;
            }

            taskResultService.addStepResult(taskRunUUID, stepResult);
        }

        /**
         * Mark a task as finished. After this, results cannot be added.
         */
        public void finishTask(@NonNull final UUID taskRunUUID) {
            checkNotNull(taskRunUUID);

            LOGGER.debug("finishTask called");
            if (isTaskFinished(null)) {
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
        public Observable<ImmutableSet<Maybe<? extends Result>>> getAsyncResultsObservable(@NonNull final UUID taskRunUUID) {
            checkNotNull(taskRunUUID);

            LOGGER.debug("getAsyncResultsObservable called");

            return taskResultService.getAsyncResults(taskRunUUID);
        }

        /**
         * @return final task result after task and async results have completed
         */
        public Single<TaskResult> getFinalTaskResult(@NonNull final UUID taskRunUUID) {
            return taskResultService.getFinalTaskResult(taskRunUUID);
        }

        public TaskResult getLatestTaskResult(final UUID taskRunUUID) {
            return taskResultService.getLatestTaskResult(taskRunUUID);
        }

        /**
         * @return observable of the latest task result, which will terminate once the task is finished and all
         *         asynchronous results added to the final task result
         */
        public Observable<TaskResult> getTaskResultObservable(@NonNull final UUID taskRunUUID) {
            checkNotNull(taskRunUUID);

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
        public boolean isTaskFinished(final UUID taskRunUUID) {
            return taskResultService.isTaskFinished(taskRunUUID);
        }

        /**
         * @param taskIdentifier
         * @param taskRunUUID
         */
        @CheckResult
        public Completable onConnect(@NonNull String taskIdentifier, @NonNull UUID taskRunUUID) {
            checkNotNull(taskIdentifier);
            checkNotNull(taskRunUUID);

            return taskResultService.registerTaskRun(taskIdentifier, taskRunUUID);
        }

        public void onDisconnect(@NonNull UUID taskRunUUID) {
            checkNotNull(taskRunUUID);

            taskResultService.deregisterTaskRun(taskRunUUID);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskResultService.class);

    @Inject
    TaskRepository taskRepository;

    // TODO: maintain mapping of an identifier for the result so a result can be retrieved
    private final HashMap<UUID, Set<Maybe<? extends Result>>> taskToAsyncResultSet = new HashMap<>();

    private final HashMap<UUID, BehaviorSubject<ImmutableSet<Maybe<? extends Result>>>> taskToAsyncResultSetObservable
            = new HashMap<>();

    private final HashMap<UUID, AtomicInteger> taskToBinderCount = new HashMap<>();

    private final HashMap<UUID, CompositeDisposable> taskToDisposables = new HashMap<>();

    private final HashMap<UUID, CompletableSubject> taskToPerformTaskCompletable = new HashMap<>();

    private final ConcurrentHashMap<UUID, TaskResult> taskToTaskResult = new ConcurrentHashMap<>();

    private final HashMap<UUID, BehaviorSubject<TaskResult>> taskToTaskResultObservable = new HashMap<>();

    private final HashMap<UUID, Single<TaskResult>> taskToTaskResultSingle = new HashMap<>();

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, TaskResultService.class);
    }

    @Override
    public void onDestroy() {
        LOGGER.debug("onDestroy called");

        for (CompositeDisposable compositeDisposable : taskToDisposables.values()) {
            compositeDisposable.dispose();
        }
        taskToAsyncResultSet.clear();
        taskToAsyncResultSetObservable.clear();
        taskToTaskResultObservable.clear();
        taskToBinderCount.clear();
        taskToPerformTaskCompletable.clear();
        taskToTaskResult.clear();
        taskToDisposables.clear();
        taskToTaskResultObservable.clear();
        taskToTaskResultSingle.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return new TaskResultServiceBinder(this);
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return false;
    }

    public void deregisterTaskRun(final UUID taskRunUUID) {
        AtomicInteger bindCount = taskToBinderCount.get(taskRunUUID);
        if (bindCount == null) {
            LOGGER.warn("deregister called for unknown taskRunUUID: {}", taskRunUUID);
            return;
        }

        if (bindCount.decrementAndGet() < 1) {
            taskToAsyncResultSet.remove(taskRunUUID);
            taskToAsyncResultSetObservable.remove(taskRunUUID);
            taskToTaskResultObservable.remove(taskRunUUID);
            taskToBinderCount.remove(taskRunUUID);
            taskToPerformTaskCompletable.remove(taskRunUUID);
            taskToTaskResult.remove(taskRunUUID);
            taskToDisposables.remove(taskRunUUID).dispose();
            taskToTaskResultSingle.remove(taskRunUUID);
        }
    }

    /**
     * @return completes when task result is ready
     */
    @CheckResult
    public Completable registerTaskRun(@NonNull String taskIdentifier, @NonNull UUID taskRunUUID) {
        AtomicInteger bindCount = taskToBinderCount.get(taskRunUUID);
        if (bindCount == null) {
            bindCount = new AtomicInteger(1);
            taskToBinderCount.put(taskRunUUID, bindCount);
        } else {
            bindCount.incrementAndGet();
            return Completable.complete();
        }

        taskToAsyncResultSet.put(taskRunUUID, new HashSet<>());
        taskToAsyncResultSetObservable.put(taskRunUUID, BehaviorSubject.create());
        taskToPerformTaskCompletable.put(taskRunUUID, CompletableSubject.create());
        taskToDisposables.put(taskRunUUID, new CompositeDisposable());
        taskToTaskResultObservable.put(taskRunUUID, BehaviorSubject.create());
        taskToTaskResultSingle.put(taskRunUUID, taskToTaskResultObservable.get(taskRunUUID).lastOrError());

        CompositeDisposable taskRunDisposables = taskToDisposables.get(taskRunUUID);

        // let's log the task completion
        taskRunDisposables.add(
                taskToPerformTaskCompletable.get(taskRunUUID)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(() ->
                                LOGGER.debug("task completion received for taskRunUUID {}", taskRunUUID))
                        .subscribe(
                                () -> {},
                                throwable -> LOGGER.debug("task completion threw throwable {}", throwable))
        );

        // wait for all async results to come back, then mark the task result observable as finished
        taskRunDisposables.add(
                taskToAsyncResultSetObservable.get(taskRunUUID)
                        .observeOn(Schedulers.io())
                        .flatMap(Observable::fromIterable)
                        .distinct()
                        .flatMapMaybe(maybe -> maybe)
                        .subscribe(result -> addAsyncResult(taskRunUUID, result), t -> {
                            LOGGER.warn("Error getting async result", t);
                        }, () -> {
                            LOGGER.debug("completed work for taskRunUUID {}", taskRunUUID);
                            // async results complete and task complete, no more updates to TaskResult observable
                            taskToTaskResultObservable.get(taskRunUUID).onComplete();
                        }));

        // load the initial task result
        Single<TaskResult> taskResultSingle = taskRepository
                .getTaskResult(taskRunUUID)
                .subscribeOn(Schedulers.io())
                .toSingle(new TaskResultBase(taskIdentifier, taskRunUUID));

        taskRunDisposables.add(
                taskResultSingle
                        .subscribe(
                                tr -> {
                                    taskToTaskResultObservable.get(taskRunUUID).onNext(tr);
                                    taskToTaskResult.put(taskRunUUID, tr);
                                },
                                t -> taskToTaskResultObservable.get(taskRunUUID).onError(t)
                        ));

        return taskResultSingle.ignoreElement();
    }

    /**
     * Adds an async result to the task result associated with the taskRunUUID.
     * If an async result with the same identifier already exists,
     * The task result will only store the one with the most recent startTime.
     * @param taskRunUUID associated with the task result to add the async result to
     * @param asyncResult to add to the task result
     */
    @VisibleForTesting
    void addAsyncResult(UUID taskRunUUID, Result asyncResult) {
        boolean shouldAddAsyncResult = true;
        TaskResult taskResult = taskToTaskResult.get(taskRunUUID);
        Result previousResultWithSameIdentifier = null;
        // Find the previous result with the same identifier if one exists
        for (Result result: taskResult.getAsyncResults()) {
            if (result.getIdentifier().equals(asyncResult.getIdentifier())) {
                previousResultWithSameIdentifier = result;
            }
        }
        if (previousResultWithSameIdentifier != null) {
            // If we have a previous async result with the same identifier, only include the one started first
            shouldAddAsyncResult =
                    previousResultWithSameIdentifier.getStartTime()
                        .isBefore(asyncResult.getStartTime());

            // If we should still add the async result parameter, we need to delete the previous one
            if (shouldAddAsyncResult) {
                updateTaskResult(taskRunUUID, taskResult.removeAsyncResult(previousResultWithSameIdentifier));
            }
        }
        if (shouldAddAsyncResult) {
            updateTaskResult(taskRunUUID, taskResult.addAsyncResult(asyncResult));
        }
    }

    @VisibleForTesting
    void addAsyncResult(UUID taskRunUUID, Maybe<? extends Result> resultMaybe) {
        // this can be called when task is marked finished since it is used internally by the service
        taskToAsyncResultSet.get(taskRunUUID).add(resultMaybe.cache());

        taskToAsyncResultSetObservable.get(taskRunUUID)
                .onNext(ImmutableSet.copyOf(taskToAsyncResultSet.get(taskRunUUID)));
    }

    @VisibleForTesting
    void addStepResult(UUID taskRunUUID, Result stepResult) {
        checkState(!isTaskFinished(taskRunUUID));
        LOGGER.debug("received step result: {}, updating task result for uuid: {}", stepResult, taskRunUUID);

        updateTaskResult(taskRunUUID, taskToTaskResult.get(taskRunUUID).addStepHistory(stepResult));
    }

    @VisibleForTesting
    void finish(UUID taskRunUUID) {
        checkState(!isTaskFinished(taskRunUUID));
        LOGGER.debug("finished called for task run: {]", taskRunUUID);

        // task is complete
        taskToPerformTaskCompletable.get(taskRunUUID).onComplete();
        // source of async results complete, no more will be added
        taskToAsyncResultSetObservable.get(taskRunUUID).onComplete();
    }

    @SuppressWarnings("unchecked")
    Observable<ImmutableSet<Maybe<? extends Result>>> getAsyncResults(UUID taskRunUUID) {
        return taskToAsyncResultSetObservable.get(taskRunUUID);
    }

    Single<TaskResult> getFinalTaskResult(UUID taskRunUUID) {
        return taskToTaskResultSingle.get(taskRunUUID);
    }

    @VisibleForTesting
    TaskResult getLatestTaskResult(UUID taskRunUUID) {
        return taskToTaskResult.get(taskRunUUID);
    }

    Observable<TaskResult> getTaskResultObservable(UUID taskRunUUID) {
        return taskToTaskResultObservable.get(taskRunUUID);
    }

    @VisibleForTesting
    boolean isTaskFinished(UUID taskRunUUID) {
        CompletableSubject taskRunCompletable = taskToPerformTaskCompletable.get(taskRunUUID);

        return taskRunCompletable != null && taskRunCompletable.hasComplete();
    }

    void updateTaskResult(UUID taskRunUUID, TaskResult newTaskResult) {
        taskToTaskResult.put(taskRunUUID, newTaskResult);

        BehaviorSubject<TaskResult> taskResultBehaviorSubject = taskToTaskResultObservable.get(taskRunUUID);
        taskResultBehaviorSubject.onNext(newTaskResult);
    }
}
