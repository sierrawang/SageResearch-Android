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

package org.sagebionetworks.research.presentation.recorder;

import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RecorderBase contains some of the common code amongst recorder implementations.
 */
public abstract class RecorderBase<R extends Result> implements Recorder<R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecorderBase.class);

    @NonNull
    protected final String identifier;

    protected Instant startTime = null;

    protected Instant stopTime = null;

    private final AtomicBoolean isRecording;

    public RecorderBase(@NonNull String identifier) {
        this.identifier = identifier;

        this.isRecording = new AtomicBoolean(false);
    }

    public void cancelRecorder() {
        // no-op
    }

    @Override
    @NonNull
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public final void start() {
        LOGGER.debug("Start called on recorder with id: {}", identifier);

        if (isRecording.compareAndSet(false, true)) {
            startTime = Instant.now();
            startRecorder();
        } else {
            LOGGER.warn("Cannot start. Recorder with id: {} already started", identifier);
        }
    }

    @Override
    public final void stop() {
        LOGGER.debug("Stop called on recorder with id: {}", identifier);

        if (isRecording.compareAndSet(true, false)) {
            stopTime = Instant.now();
            stopRecorder();
        } else {
            LOGGER.info("Recorder with id: {} already stopped.", identifier);
        }
    }

    @Override
    public final void cancel() {
        LOGGER.debug("Cancel called on recorder with id: {}", identifier);

        cancelRecorder();
        stop();
    }

    @Override
    public boolean isRecording() {
        return this.isRecording.get();
    }

    @Override
    public void pause() {
        LOGGER.debug("Pause called on recorder with id: {}", identifier);
        // no-op
    }

    @Override
    public void resume() {
        LOGGER.debug("Resume called on recorder with id: {}", identifier);
        // no-op
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    public abstract void startRecorder();

    public abstract void stopRecorder();
}
