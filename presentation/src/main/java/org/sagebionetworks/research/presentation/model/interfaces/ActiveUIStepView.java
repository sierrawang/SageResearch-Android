package org.sagebionetworks.research.presentation.model.interfaces;

import com.google.common.collect.ImmutableSet;

import org.threeten.bp.Duration;

import java.util.Map;

public interface ActiveUIStepView extends UIStepView {

    /**
     * The set of commands to apply to this active step. These indicate actions to fire at the beginning
     * and end of the step such as playing a sound as well as whether or not to automatically start and
     * finish the step.
     */
    ImmutableSet<String> getCommands();

    /**
     * The duration of time to run the step. If `0`, then this value is ignored.
     */
    Duration getDuration();

    /**
     * Localized text that represents an instructional voice prompt. Instructional speech begins when the
     * step passes the time indicated by the given time.  If `timeInterval` is greater than or equal to
     * `duration`, then the spoken instruction returned should be for when the step is finished.
     */
    Map<String, String> getSpokenInstructions();

    /**
     * TODO: mdephillips 12/4/18 Is this needed on Android?  It's only in iOS because that's a special case permission
     */
    boolean isBackgroundAudioRequired();
}
