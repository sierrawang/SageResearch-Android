/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.presentation.recorder.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import dagger.android.DaggerService;
import org.sagebionetworks.research.domain.async.RecorderType;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.presentation.inject.RecorderModule.RecorderFactory;
import org.sagebionetworks.research.presentation.recorder.Recorder;
import org.sagebionetworks.research.presentation.recorder.RecorderActionType;
import org.sagebionetworks.research.presentation.recorder.RecorderConfigPresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The RecorderService handles the recorders that are needed for the task. Recorders can do things such as record
 * audio, phones motion, etc. Every recorder runs on it's own thread.
 * <p>
 * This service supports both being started and bound to. Intents passed to the service should have the following
 * extras RECORDER_ACTION_KEY -> one of the constants in RecorderActionType corresponding to the action that should be
 * performed.
 * <p>
 * TASK_ID_KEY -> the UUID of the task.
 * <p>
 * RECORDER_ID_KEY -> the identifier of the recorder to perform the action on.
 * <p>
 * TODO: create a reactive binding to the service, like for TaskResultService. Currently, step transitions and adding
 * of recorders can be missed when the service is not yet bound @liujoshua 08/26/2018
 */
public class RecorderService extends DaggerService {
    public class RecorderInstantiationException extends Exception {
        @Nullable
        private final String message;

        @NonNull
        private final String recorderId;

        @NonNull
        @RecorderType
        private final String recorderType;

        public RecorderInstantiationException(@NonNull String recorderId, @NonNull @RecorderType String recorderType,
                @Nullable String message) {
            this.recorderId = recorderId;
            this.recorderType = recorderType;
            this.message = message;
        }

        public RecorderInstantiationException(@NonNull String recorderId,
                @NonNull @RecorderType String recorderType) {
            this(recorderId, recorderType, null);
        }

        @Override
        @Nullable
        public String getMessage() {
            return message;
        }

        public String toString() {
            if (message != null) {
                return message;
            } else {
                return "Failed to instantiate recorder " + this.recorderId + " of type " + this.recorderType;
            }
        }

        @NonNull
        public String getRecorderId() {
            return recorderId;
        }

        @NonNull
        @RecorderType
        public String getRecorderType() {
            return recorderType;
        }
    }

    /**
     * Allows the RecorderService to be bound to.
     */
    public final class RecorderBinder extends Binder {
        public RecorderService getService() {
            return RecorderService.this;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RecorderService.class);

    public static final String RECORDER_TYPE_KEY = "RECORDER_TYPE";

    public static final String TASK_ID_KEY = "TASK_ID";

    public static final String RECORDER_ID_KEY = "RECORDER_ID";

    public static final String RECORDER_ACTION_KEY = "RECORDER_ACTION";

    // Maps Task Id to a Map of Recorder Id to Recorder.
    protected Map<UUID, Map<String, Recorder<? extends Result>>> recorderMapping;

    protected IBinder serviceBinder;

    @Inject
    RecorderFactory recorderFactory;

    public RecorderService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.serviceBinder = new RecorderBinder();
        this.recorderMapping = new HashMap<>();
    }

    /**
     * Cancels the recorder with the given identifier.
     *
     * @param taskIdentifier
     *         The identifier of the task the recorder belongs to.
     * @param recorderIdentifier
     *         The identifier of the recorder to cancel.
     */
    public void cancelRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cancelling recorder: " + recorderIdentifier);
        }

        Recorder recorder = this.getRecorder(taskIdentifier, recorderIdentifier);
        if (recorder == null) {
            LOGGER.warn("Cannot cancel recorder that isn't started.");
        }

        recorder.cancel();
    }

    public Recorder<? extends Result> createRecorder(@NonNull UUID taskIdentifier,
            @NonNull RecorderConfigPresentation recorderConfiguration)
            throws IOException {
        Recorder<? extends Result> recorder = this.recorderFactory.create(recorderConfiguration, taskIdentifier);

        if (!this.recorderMapping.containsKey(taskIdentifier)) {
            this.recorderMapping.put(taskIdentifier, new HashMap<>());
        }

        Map<String, Recorder<? extends Result>> taskRecorderMapping = this.recorderMapping.get(taskIdentifier);
        taskRecorderMapping.put(recorder.getIdentifier(), recorder);
        return recorder;
    }

    /**
     * Get the active recorders for a task run.
     *
     * @param taskRunUUID
     *         identifier for a task run
     * @return an immutable map containing the active recorders keyed by their identifiers
     */
    @NonNull
    public ImmutableMap<String, Recorder<? extends Result>> getActiveRecorders(@NonNull UUID taskRunUUID) {
        Map<String, Recorder<? extends Result>> map = this.recorderMapping.get(taskRunUUID);
        if (map == null) {
            this.recorderMapping.put(taskRunUUID, new HashMap<>());
            return ImmutableMap.of();
        }
        return ImmutableMap.copyOf(map);
    }

    /**
     * Starts the command defined by the Intent with the given startId. The Intent passed to this method should have
     * the Recorder to perform the action on, and a @RecorderActionType String that describes what action to perform
     * on the Recorder as part of it's extra's
     *
     * @param intent
     *         the Intent describing the command to start, should have a Recorder and ActionType as part of the
     *         extra's
     * @param flags
     *         the flags for this command.
     * @param startId
     *         the id of the command to start.
     * @return An int which indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        @RecorderActionType String actionType = intent.getStringExtra(RECORDER_ACTION_KEY);
        UUID taskIdentifier = (UUID) intent.getSerializableExtra(TASK_ID_KEY);
        String recorderIdentifier = intent.getStringExtra(RECORDER_ID_KEY);
        switch (actionType) {
            case RecorderActionType.START:
                this.startRecorder(taskIdentifier, recorderIdentifier);
                break;
            case RecorderActionType.STOP:
                this.stopRecorder(taskIdentifier, recorderIdentifier);
                break;
            case RecorderActionType.CANCEL:
                this.cancelRecorder(taskIdentifier, recorderIdentifier);
                break;
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return this.serviceBinder;
    }

    /**
     * Starts the recorder with the given identifier, or throws an IllegalArgumentException if the recorder with this
     * identifier doesn't exist.
     *
     * @param taskIdentifier
     *         The identifier of the task the recorder belongs to.
     * @param recorderIdentifier
     *         The identifier of the recorder to start.
     */
    public void startRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting recorder: " + recorderIdentifier);
        }

        Recorder recorder = this.getRecorder(taskIdentifier, recorderIdentifier);
        if (recorder == null) {
            LOGGER.warn("Cannot start recorder that hasn't been created");
            return;
        }

        recorder.start();
    }

    /**
     * Stops the recorder with the given identifier.
     *
     * @param taskIdentifier
     *         The identifier of the task the recorder belongs to.
     * @param recorderIdentifier
     *         The identifier of the recorder to stop.
     */
    public void stopRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Stopping recorder: " + recorderIdentifier);
        }

        Recorder recorder = this.getRecorder(taskIdentifier, recorderIdentifier);
        if (recorder == null) {
            LOGGER.warn("Cannot stop recorder that isn't started.");
            return;
        }

        recorder.stop();
        // Remove the recorder for the mapping of active recorders
        this.recorderMapping.get(taskIdentifier).remove(recorderIdentifier);
    }

    private Recorder getRecorder(@NonNull UUID taskIdentifier, @NonNull String recorderIdentifier) {
        Map<String, Recorder<? extends Result>> taskRecorderMap = this.recorderMapping.get(taskIdentifier);
        if (taskRecorderMap == null) {
            return null;
        }

        return taskRecorderMap.get(recorderIdentifier);
    }
}
