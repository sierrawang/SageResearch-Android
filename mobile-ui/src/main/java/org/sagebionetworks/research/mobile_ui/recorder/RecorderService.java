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
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Map;

/**
 * The RecorderService handles the recorders that are needed for the task. Recorders can do things such as record
 * audio, phones motion, etc. Every recorder runs on it's own thread.
 */
public class RecorderService extends Service {
    public static final String RECORDER_KEY = "RECORDER";
    public static final String ACTION_KEY = "ACTION";

    public final class RecorderBinder extends Binder {
        public RecorderService getService() {
            return RecorderService.this;
        }
    }

    /**
     * A RecorderThread is a separate thread that handles one recorder.
     */
    private static final class RecorderThread extends Thread {
        private final Recorder recorder;

        public RecorderThread(final Recorder recorder) {
            this.recorder = recorder;
        }

        @Override
        public void run() {
            this.recorder.start();
        }

        public void stopRecorder() {
            this.recorder.stop();
            this.interrupt();
        }

        public void cancelRecorder() {
            this.recorder.cancel();
            this.interrupt();
        }
    }

    /**
     * A ServiceHandler receives and handles messages from the RecorderService.
     */
    private static final class ServiceHandler extends Handler {
        protected Map<Recorder, RecorderThread> recorderThreadMap;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Recorder recorder = (Recorder)bundle.getSerializable(RECORDER_KEY);
            @RecorderActionType String actionType = (String)bundle.getSerializable(ACTION_KEY);
            switch (actionType) {
                case RecorderActionType.START:
                    this.startRecorder(recorder);
                    break;
                case RecorderActionType.STOP:
                    this.stopRecorder(recorder);
                    break;
                case RecorderActionType.CANCEL:
                    this.cancelRecorder(recorder);
                    break;
            }
        }

        private void startRecorder(Recorder recorder) {
            RecorderThread thread = this.recorderThreadMap.get(recorder);
            if (thread == null) {
                thread = new RecorderThread(recorder);
                this.recorderThreadMap.put(recorder, thread);
            }

            thread.start();
        }

        private void stopRecorder(Recorder recorder) {
            RecorderThread thread = this.recorderThreadMap.remove(recorder);
            if (thread == null) {
                throw new IllegalStateException("Cannot stop recorder that hasn't been started.");
            }

            thread.stopRecorder();
        }

        private void cancelRecorder(Recorder recorder) {
            RecorderThread thread = this.recorderThreadMap.get(recorder);
            if (thread == null) {
                throw new IllegalStateException("Cannot cancel recorder that hasn't been started.");
            }

            thread.cancelRecorder();
        }
    }

    protected Looper serviceLooper;
    protected ServiceHandler serviceHandler;
    protected IBinder serviceBinder;

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
        Recorder recorder = (Recorder)intent.<Recorder>getSerializableExtra(RECORDER_KEY);
        @RecorderActionType String actionType = (String)intent.<String>getSerializableExtra(ACTION_KEY);
        Message message = serviceHandler.obtainMessage();
        message.arg1 = startId;
        Bundle bundle = new Bundle();
        bundle.putSerializable(RECORDER_KEY, recorder);
        bundle.putSerializable(ACTION_KEY, actionType);
        message.setData(bundle);
        serviceHandler.handleMessage(message);

        return START_STICKY;
    }

    /**
     * Returns the set of all recorders that are currently active for this service. An active recorder is either
     * running, or has been canceled but not stopped.
     * @return the set of all recorders that are currently active for this service.
     */
    public ImmutableSet<Recorder> getActiveRecorders() {
        return ImmutableSet.copyOf(this.serviceHandler.recorderThreadMap.keySet());
    }

    /**
     * Returns the set of all recorders that are currently running for this service. A running recorder is one that
     * has been started but not stopped or canceled.
     * @return the set of all running recorders for this service.
     */
    public ImmutableSet<Recorder> getRunningRecorders() {
        return ImmutableSet.copyOf(Sets.filter(this.getActiveRecorders(), Recorder::isRunning));
    }
}
