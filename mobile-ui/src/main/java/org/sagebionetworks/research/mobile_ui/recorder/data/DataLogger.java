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

package org.sagebionetworks.research.mobile_ui.recorder.data;
import android.support.annotation.Nullable;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.sagebionetworks.research.domain.result.implementations.FileResultBase;
import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.threeten.bp.Instant;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * A JsonArrayDataRecorder subscribes to a source of JsonObjects and writes them out to a file. It is also a single
 * source the provides the file result it writes to on success, or the throwable that occurred if there is an error
 * anywhere in it's source of JsonObjects.
 *
 * The start time of the fileResult will be the time that onSubscribe was called and the end time will be the time
 * that onComplete was called.
 */
public class DataLogger implements Subscriber<String>, SingleOnSubscribe<FileResult> {
    protected String identifier;
    protected final Set<SingleEmitter<FileResult>> observers;
    protected final File outputDirectory;
    protected final PrintStream outputStream;
    protected final String start;
    protected final String end;
    protected final String deliminator;

    protected boolean isFirstJsonObject;
    protected Instant startTime;
    protected Instant endTime;

    /**
     * Creates a new Data Recorder with the given information.
     * @param identifier The identifier of the recorder, will also be used as the identifier of the file result.
     * @param outputDirectory The file to output data to.
     * @param start The string to start the output with, null will result in no prefix being added to the data.
     * @param end The string to end the output with, null will result in no suffix being added to the data.
     * @param delimnator The string to separate each piece of data passed to OnNext with.
     * @throws IOException If the given outputDirectory cannot be opened or written to.
     */
    public DataLogger(String identifier, File outputDirectory, @Nullable String start, @Nullable String end,
            @Nullable String delimnator) throws IOException {
        this.identifier = identifier;
        this.outputDirectory = outputDirectory;
        this.outputStream = new PrintStream(this.outputDirectory);
        this.observers = new HashSet<>();
        this.isFirstJsonObject = true;
        this.start = start == null ? "" : start;
        this.end = end == null ? "" : end;
        this.deliminator = delimnator == null ? "" : end;
    }

    @Override
    public void onSubscribe(final Subscription s) {
        this.startTime = Instant.now();
        // necessary to subscribe to as much data as can be provided.
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(String next) {
        // append optional comma for array separation
        String outputString = (isFirstJsonObject ? this.start : this.deliminator)
                + next;
        this.outputStream.println(outputString);
        this.isFirstJsonObject = false;
    }

    @Override
    public void onError(final Throwable t) {
        this.outputStream.close();
        for (SingleEmitter<FileResult> observer : this.observers) {
            observer.onError(t);
        }
    }

    @Override
    public void onComplete() {
        this.outputStream.append(this.end);
        this.outputStream.close();
        this.endTime = Instant.now();
        FileResult fileResult = new FileResultBase(this.identifier, this.startTime, this.endTime, "json",
                this.outputDirectory.getPath());
        for (SingleEmitter<FileResult> observer : this.observers) {
            observer.onSuccess(fileResult);
        }
    }

    @Override
    public void subscribe(final SingleEmitter<FileResult> emitter) throws Exception {
        this.observers.add(emitter);
    }
}

