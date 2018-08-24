package org.sagebionetworks.research.presentation.recorder.location;

import android.location.Location;
import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.presentation.recorder.reactive.ReactiveRecorder;
import org.sagebionetworks.research.presentation.recorder.reactive.source.ReactiveLocationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Flowable;

public abstract class LocationRecorder<R extends Result> extends ReactiveRecorder<Location, R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationRecorder.class);

    private final ReactiveLocationFactory reactiveLocationFactory;

    public LocationRecorder(@NonNull final String identifier,
            @NonNull ReactiveLocationFactory reactiveLocationFactory) {
        super(identifier);
        this.reactiveLocationFactory = reactiveLocationFactory;
    }

    @Override
    public void startRecorder() {
        // TODO: check permission
        super.startRecorder();
    }

    @NonNull
    @Override
    protected Flowable<Location> initializeEventFlowable() {
        return reactiveLocationFactory.getLocation();
    }
}
