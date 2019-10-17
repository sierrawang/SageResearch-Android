package org.sagebionetworks.research.presentation.recorder.reactive;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.reactivestreams.Subscription;
import org.sagebionetworks.research.domain.result.implementations.FileResultBase;
import org.sagebionetworks.research.domain.result.interfaces.FileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.MaybeSubject;

public class ReactiveFileResultRecorder<E> extends ReactiveRecorder<E, FileResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveFileResultRecorder.class);

    public static final String JSON_MIME_CONTENT_TYPE = "application/json";

    private static final String JSON_FILE_START = "[";

    private static final String JSON_FILE_END = "]";

    private static final String JSON_OBJECT_DELIMINATOR = ",";

    protected final String deliminator;

    protected final String end;

    protected final File outputFile;

    protected PrintStream outputStream;

    protected final String start;

    private final CompositeDisposable compositeDisposable;

    private final String fileMimeType;

    private final MaybeSubject<FileResult> fileResultMaybeSubject;

    private final Gson gson;

    private final AtomicBoolean isFirstJsonObject = new AtomicBoolean(true);

    // allows us to cancel our subscription
    private Subscription reactiveDataSubscription;

    private final AtomicBoolean success = new AtomicBoolean(false);

    public static <E> ReactiveFileResultRecorder<E> createJsonArrayLogger(@NonNull String identifier,
            @NonNull Flowable<E> flowableData, @NonNull Gson gson, @NonNull File outputFile) {
        return new ReactiveFileResultRecorder<>(identifier, flowableData, gson,
                outputFile, JSON_MIME_CONTENT_TYPE, JSON_FILE_START, JSON_FILE_END, JSON_OBJECT_DELIMINATOR);
    }

    protected ReactiveFileResultRecorder(@NonNull String identifier,
            @NonNull Flowable<E> eventFlowable,
            @NonNull Gson gson, @NonNull File outputFile, @NonNull String fileMimeType, @NonNull String start,
            @NonNull String end, @NonNull String deliminator) {
        super(identifier, eventFlowable);

        this.gson = checkNotNull(gson);
        this.outputFile = checkNotNull(outputFile);
        checkArgument(!Strings.isNullOrEmpty(fileMimeType), "fileMimeType cannot be null or empty");
        this.fileMimeType = fileMimeType;
        this.start = start;
        this.end = end;
        this.deliminator = deliminator;

        this.fileResultMaybeSubject = MaybeSubject.create();

        this.compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(
                getEventFlowable()
                        .observeOn(Schedulers.io())
                        .doOnSubscribe(this::onReactiveDataSubscribe)
                        .doOnCancel(this::onReactiveDataCancel)
                        .doFinally(this::doReactiveDataFinally)
                        .subscribe(this::onReactiveDataNext, this::onReactiveDataError,
                                this::onReactiveDataComplete));
    }

    @Override
    @CallSuper
    public void cancelRecorder() {
        super.cancelRecorder();
        fileResultMaybeSubject.onComplete();
        reactiveDataSubscription.cancel();
    }

    @Override
    public Maybe<FileResult> getResult() {
        return fileResultMaybeSubject;
    }

    @VisibleForTesting
    public void onReactiveDataSubscribe(Subscription subscription) {
        LOGGER.debug("reactive data subscribed for {}", identifier);

        try {
            reactiveDataSubscription = subscription;

            // Creating a new PrintSteam object here will overwrite any
            // file that already exists at this location
            // If ever we want to allow for appending to a file,
            // call new PrintStream(new FileOutputStream(outputFile, true));
            outputStream = new PrintStream(this.outputFile);

            outputStream.print(this.start);
        } catch (Throwable t) {
            onReactiveDataError(t);
        }
    }

    @VisibleForTesting
    void doReactiveDataFinally() {
        outputStream.close();
        if (!success.get()) {
            LOGGER.debug("Deleting output file");
            outputFile.delete();
        }

        compositeDisposable.dispose();
    }

    @VisibleForTesting
    void onReactiveDataCancel() {
        LOGGER.debug("reactive data canceled for {}", identifier);

        fileResultMaybeSubject.onComplete();
    }

    @VisibleForTesting
    void onReactiveDataComplete() {
        LOGGER.debug("reactive data completed for {}", identifier);
        success.set(true);
        outputStream.append(this.end);

        fileResultMaybeSubject.onSuccess(
                new FileResultBase(identifier, startTime, stopTime, fileMimeType, outputFile.getPath()));
    }

    @VisibleForTesting
    void onReactiveDataError(Throwable t) {
        LOGGER.debug("reactive data errored for {}", identifier, t);

        fileResultMaybeSubject.onError(t);
    }

    @VisibleForTesting
    void onReactiveDataNext(E data) {
        LOGGER.trace("reactive data received: {}", data);
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
}
