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

package org.sagebionetworks.research.mobile_ui.recorder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * The RecorderService handles the recorders that are needed for the task. Recorders can do things such as record
 * audio, phones motion, etc. Every recorder runs on it's own thread.
 */
public class RecorderService extends Service {
    public static final String RECORDER_TYPE_KEY = "RECORDER_TYPE";
    public static final String RECORDER_ID_KEY = "RECORDER_ID";
    public static final String RECORDER_ACTION_KEY = "RECORDER_ACTION";

    protected Looper serviceLooper;
    protected ServiceHandler serviceHandler;
    protected IBinder serviceBinder;
    protected Map<String, Recorder> recordersById;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return this.serviceBinder;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("RecorderService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        this.serviceLooper = thread.getLooper();
        this.serviceHandler = new ServiceHandler(this.serviceLooper);
        this.serviceBinder = new RecorderBinder();
    }

    /**
     * Starts the command defined by the Intent with the given startId. The Intent passed to this method should have
     * the Recorder to perform the action on, and a @RecorderActionType String that describes what action to perform
     * on the Recorder as part of it's extra's
     * @param intent the Intent describing the command to start, should have a Recorder and ActionType as part of the
     *               extra's
     * @param flags the flags for this command.
     * @param startId the id of the command to start.
     * @return An int which indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        @RecorderActionType String actionType = intent.getStringExtra(RECORDER_ACTION_KEY);
        String recorderIdentifier = intent.getStringExtra(RECORDER_ID_KEY);
        @RecorderType String recorderType = intent.getStringExtra(RECORDER_TYPE_KEY);
        Message message = this.serviceHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(RECORDER_ACTION_KEY, actionType);
        bundle.putString(RECORDER_ID_KEY, recorderIdentifier);
        bundle.putString(RECORDER_TYPE_KEY, recorderType);
        message.setData(bundle);
        this.serviceHandler.handleMessage(message);
        return START_STICKY;
    }

    /**
     * Returns an immutable map containing the active recorders keyed by their identifiers.
     * @return an immutable map containing the active recorders keyed by their identifiers.
     */
    public ImmutableMap<String, Recorder> getActiveRecorders() {
        return ImmutableMap.copyOf(this.recordersById);
    }

    protected Recorder createRecorder(@NonNull String id, @RecorderType String type) {
        Recorder recorder = new Recorder() {
            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }

            @Nullable
            @Override
            public String getStartStepIdentifier() {
                return null;
            }

            @Nullable
            @Override
            public String getStopStepIdentifier() {
                return null;
            }
        };

        this.recordersById.put(id, recorder);
        return recorder;
    }

    public final class RecorderBinder extends Binder {
        public RecorderService getService() {
            return RecorderService.this;
        }
    }

    /**
     * A ServiceHandler receives and handles messages from the RecorderService.
     */
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            @RecorderActionType String actionType = bundle.getString(RECORDER_ACTION_KEY);
            String recorderId = bundle.getString(RECORDER_ID_KEY);
            if (actionType == null || recorderId == null) {
                throw new IllegalArgumentException("Null actionType or recorderId");
            }

            Recorder recorder = recordersById.get(recorderId);
            switch (actionType) {
                case RecorderActionType.START:
                    if (recorder == null) {
                        @RecorderType String recorderType = bundle.getString(RECORDER_TYPE_KEY);
                        recorder = createRecorder(recorderId, recorderType);
                    }

                    recorder.start();
                    break;
                case RecorderActionType.STOP:
                    if (recorder == null) {
                        throw new IllegalArgumentException("Cannot stop recorder that isn't started.");
                    }

                    recordersById.remove(recorderId);
                    recorder.stop();
                    break;
                case RecorderActionType.CANCEL:
                    if (recorder == null) {
                        throw new IllegalArgumentException("Cannot cancel recorder that isn't started.");
                    }

                    recorder.cancel();
                    break;
            }
        }
    }
}
