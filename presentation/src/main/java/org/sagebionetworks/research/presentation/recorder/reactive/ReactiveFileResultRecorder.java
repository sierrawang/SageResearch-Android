package org.sagebionetworks.research.presentation.recorder.reactive;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.reactivestreams.Subscription;
import org.sagebionetworks.research.domain.result.implementations.FileResultBase;
import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.sagebionetworks.research.presentation.recorder.RecorderBase;
import org.threeten.bp.Instant;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.subjects.MaybeSubject;

public class ReactiveFileResultRecorder extends RecorderBase<FileResult> {
    public static final String JSON_MIME_CONTENT_TYPE = "application/json";

    private static final String JSON_FILE_START = "[";

    private static final String JSON_FILE_END = "]";

    private static final String JSON_OBJECT_DELIMINATOR = ",";

    private final MaybeSubject<FileResult> fileResultMaybeSubject;

    private final Gson gson;

    protected final File outputFile;


    private final String fileMimeType;

    protected final String start;

    protected final String end;

    protected final String deliminator;

    private final AtomicBoolean isFirstJsonObject = new AtomicBoolean(true);

    public static ReactiveFileResultRecorder createJsonArrayLogger(@NonNull String identifier,
            @NonNull Flowable flowableData, @NonNull Gson gson, @NonNull File outputFile)
            throws IOException {
        return new ReactiveFileResultRecorder(identifier, flowableData, gson,
                outputFile, JSON_MIME_CONTENT_TYPE, JSON_FILE_START, JSON_FILE_END, JSON_OBJECT_DELIMINATOR);
    }

    private final ConnectableFlowable<?> connectableFlowableData;

    private final CompositeDisposable compositeDisposable;

    private Instant startTime;

    private Instant stopTime;

    private Subscription reactiveDataSubscription;

    protected PrintStream outputStream;

    protected ReactiveFileResultRecorder(@NonNull String identifier, @NonNull Flowable flowableData, @NonNull Gson gson,
            @NonNull File outputFile, @NonNull String fileMimeType, @NonNull String start, @NonNull String end,
            @NonNull String deliminator) {
        super(identifier);

        this.connectableFlowableData = flowableData.onBackpressureBuffer().publish();
        this.gson = gson;
        this.outputFile = outputFile;
        this.fileMimeType = fileMimeType;
        this.start = start;
        this.end = end;
        this.deliminator = deliminator;

        this.fileResultMaybeSubject = MaybeSubject.create();

        this.compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(
                connectableFlowableData
                        .doOnCancel(this::onReactiveDataCancel)
                        .subscribe(this::onReactiveDataNext, this::onReactiveDataError, this::onReactiveDataComplete,
                                this::onReactiveDataSubscribe));
    }

    public void onReactiveDataSubscribe(Subscription subscription) {
        try {
            reactiveDataSubscription = subscription;
            outputStream = new PrintStream(this.outputFile);
            outputStream.print(this.start);
        } catch (Throwable t) {
            onReactiveDataError(t);
        }
    }

    public void onReactiveDataNext(Object data) {
        if (data != null) {
            try {
                String outputString = "";
                if (!isFirstJsonObject.compareAndSet(true, false)) {
                    outputString += this.deliminator;
                }
                outputString += gson.toJson(data);
                this.outputStream.print(outputString);
            } catch (Throwable t) {
                onReactiveDataError(t);
            }
        }
    }

    public void onReactiveDataError(Throwable t) {
        this.outputStream.close();
        fileResultMaybeSubject.onError(t);
    }

    public void onReactiveDataComplete() {
        outputStream.append(this.end);
        outputStream.close();
        fileResultMaybeSubject.onSuccess(
                new FileResultBase(identifier, startTime, stopTime, fileMimeType, outputFile.getPath()));
    }

    public void onReactiveDataCancel() {
        outputStream.close();
        fileResultMaybeSubject.onComplete();
        outputFile.delete();
    }

    @Override
    public Maybe<FileResult> getResult() {
        return fileResultMaybeSubject;
    }

    @Override
    public void startRecorder() {
        startTime = Instant.now();
        connectableFlowableData.connect();
    }

    @Override
    public void stopRecorder() {
        stopTime = Instant.now();
        compositeDisposable.dispose();
    }

    @Override
    public void cancelRecorder() {
        reactiveDataSubscription.cancel();
    }
}
