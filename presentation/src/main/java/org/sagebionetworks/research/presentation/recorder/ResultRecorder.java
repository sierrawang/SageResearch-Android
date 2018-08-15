package org.sagebionetworks.research.presentation.recorder;


import org.sagebionetworks.research.domain.result.interfaces.Result;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ResultRecorder<ResultType extends Result> extends Recorder {
    /**
     * Returns the result for this recorder.
     * @return the result for this recorder.
     */
    Single<ResultType> getResult();
}
