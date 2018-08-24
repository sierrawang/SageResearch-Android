package org.sagebionetworks.research.presentation.async;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.Result;

import io.reactivex.Maybe;

@AnyThread
public interface AsyncActionController<R extends Result> {

    enum AsyncActionStatus {
        /**
         * Initial state before the controller has been started.
         */
        IDLE,

        /**
         * Status if the controller is currently requesting authorization. Once in this state and until the controller
         * is `starting`, the UI should be blocked from any view transitions.
         */

        REQUESTION_PERMISSION,
        /**
         * Status if the controller has granted permission, but not yet been started.
         */
        PERMISSION_GRANTED,
        /**
         * The controller is starting up. This is the state once `RSDAsyncActionController.start()` has been called
         * but before the recorder or request is running.
         */
        STARTING,

        /**
         * The action is running. For `RSDRecorderConfiguration` controllers, this means that the recording is open.
         * For `RSDRequestConfiguration` controllers, this means that the request is in-flight.
         */

        RUNNING,

        /**
         * Waiting for in-flight buffers to be appended and ready to close.
         */
        WAITING_TO_STOP,

        /**
         * Cleaning up by closing any buffers or file handles and processing any results that are returned by this
         * controller.
         */

        PROCESSING_RESULTS,

        /**
         * Stopping any sensor managers. The controller should move to this state **after** any results are processed.
         * - note: Once in this state, the async action should **not** be changing the results associated with this
         * action.
         */
        STOPPING,

        /**
         * The controller is finished running and ready to `dealloc`.
         */
        FINISHED,

        /**
         * The recorder or request was cancelled and any results may be invalid.
         */
        CANCELLED,
        /**
         * The recorder or request failed and any results may be invalid.
         */
        FAILED
    }

    /**
     * Start the asynchronous action with the given completion handler.
     */
    void start();

    /**
     * Pause the action, if applicable.
     */
    void pause();

    /**
     * Resume the paused action, if applicable.
     */
    void resume();

    /**
     * @return whether the action is currently paused
     */
    boolean isPaused();

    /**
     * Step the action.
     */
    void stop();

    /**
     * @return result for the action
     */
    @NonNull
    Maybe<R> getResult();

    /**
     * Cancel the action.
     */
    void cancel();
}
