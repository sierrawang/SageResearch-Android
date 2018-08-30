package org.sagebionetworks.research.presentation.perform_task;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.presentation.perform_task.TaskResultService.TaskResultServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.SingleSubject;

public class BoundServiceTaskResultManager implements TaskResultManager {
    @VisibleForTesting
    static class BoundServiceTaskResultManagerConnection implements TaskResultManagerConnection {

        private final TaskResultServiceBinder binder;

        private Context context;

        private final TaskResultServiceConnection taskResultServiceConnection;

        private final UUID taskRunUUID;

        BoundServiceTaskResultManagerConnection(
                final Context context,
                final UUID taskRunUUID,
                final TaskResultServiceConnection taskResultServiceConnection,
                final TaskResultServiceBinder binder) {
            this.context = context;
            this.binder = binder;
            this.taskRunUUID = taskRunUUID;
            this.taskResultServiceConnection = taskResultServiceConnection;
        }

        @Override
        public void addAsyncActionResult(@NonNull final Maybe<? extends Result> actionActionResult) {
            binder.addAsyncActionResult(taskRunUUID, actionActionResult);
        }

        @Override
        public void addStepResult(@NonNull final Result stepResult) {
            binder.addStepResult(taskRunUUID, stepResult);
        }

        @Override
        public void disconnect() {
            binder.onDisconnect(taskRunUUID);
            context.unbindService(taskResultServiceConnection);
        }

        @Override
        public void finishTask() {
            binder.finishTask(taskRunUUID);
        }

        @NonNull
        @Override
        public Observable<ImmutableSet<Maybe<? extends Result>>> getAsyncResultsObservable() {
            return binder.getAsyncResultsObservable(taskRunUUID);
        }

        @NonNull
        @Override
        public Single<TaskResult> getFinalTaskResult() {
            return binder.getFinalTaskResult(taskRunUUID);
        }

        @NonNull
        @Override
        public TaskResult getLatestTaskResult() {
            return binder.getLatestTaskResult(taskRunUUID);
        }

        @NonNull
        @Override
        public Observable<TaskResult> getTaskResultObservable() {
            return binder.getTaskResultObservable(taskRunUUID);
        }
    }

    @VisibleForTesting
    static class TaskResultServiceConnection implements ServiceConnection {
        private static final Logger LOGGER = LoggerFactory.getLogger(TaskResultServiceConnection.class);

        private final CompositeDisposable compositeDisposable;

        private final Context context;

        private final String taskIdentifier;

        private final SingleSubject<TaskResultManagerConnection> taskResultManagerConnectionSingle;

        private final UUID taskRunUUID;

        TaskResultServiceConnection(final Context context, final String taskIdentifier,
                final UUID taskRunUUID) {
            this.context = context;
            this.taskIdentifier = taskIdentifier;
            this.taskRunUUID = taskRunUUID;
            taskResultManagerConnectionSingle = SingleSubject.create();
            compositeDisposable = new CompositeDisposable();
        }

        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            LOGGER.debug("Connected to service: {} for taskRunUUID: {}", name.toShortString(), taskRunUUID);
            TaskResultServiceBinder trsb = (TaskResultServiceBinder) service;
            compositeDisposable
                    .add(trsb.onConnect(taskIdentifier, taskRunUUID)
                            .subscribe(() -> {
                        taskResultManagerConnectionSingle
                                .onSuccess(
                                        new BoundServiceTaskResultManagerConnection(context, taskRunUUID, this,
                                                trsb));
                    }, taskResultManagerConnectionSingle::onError));
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            compositeDisposable.dispose();
        }

        @Override
        public void onBindingDied(final ComponentName name) {
            compositeDisposable.dispose();
        }

        Single<TaskResultManagerConnection> getTaskResultManagerConnection() {
            return taskResultManagerConnectionSingle;
        }
    }

    private final Context context;

    @Inject
    public BoundServiceTaskResultManager(Context context) {
        this.context = context;
    }

    @Override
    public Single<TaskResultManagerConnection> getTaskResultManagerConnection(@NonNull final String taskIdentifier,
            @NonNull final UUID taskRunUUID) {
        TaskResultServiceConnection conn = new TaskResultServiceConnection(context, taskIdentifier, taskRunUUID);
        if (!context
                .bindService(TaskResultService.createIntent(context), conn,
                        BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT)) {
            return Single.error(new IllegalStateException("Could not bind to service"));
        }
        return conn.getTaskResultManagerConnection();
    }
}