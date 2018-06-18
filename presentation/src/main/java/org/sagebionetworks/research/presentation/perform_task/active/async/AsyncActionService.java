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

package org.sagebionetworks.research.presentation.perform_task.active.async;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import dagger.android.DaggerService;

public class AsyncActionService extends DaggerService {
    private static class AsyncActionBinder extends Binder implements AsyncAction {
        final UUID taskRunUUID;

        public AsyncActionBinder(UUID taskRunUUID) {
            this.taskRunUUID = taskRunUUID;
        }
    }

    public interface AsyncAction {

    }

    private static final String INTENT_EXTRA_KEY_TASK_RUN_UUID = "taskRunUUID";

    public static Intent createBindServiceIntent(@NonNull Context context, @NonNull UUID taskRunUUID) {
        checkNotNull(context);
        checkNotNull(taskRunUUID);

        return new Intent(context.getApplicationContext(), AsyncActionService.class)
                .putExtra(INTENT_EXTRA_KEY_TASK_RUN_UUID, new ParcelUuid(taskRunUUID));
    }

    public static void startService() {

    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        ParcelUuid taskRunUUID = intent.getParcelableExtra(INTENT_EXTRA_KEY_TASK_RUN_UUID);
        return new AsyncActionBinder(taskRunUUID.getUuid());
    }
}
