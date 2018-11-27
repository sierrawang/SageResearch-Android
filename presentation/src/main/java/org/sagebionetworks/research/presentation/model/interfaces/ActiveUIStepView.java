package org.sagebionetworks.research.presentation.model.interfaces;

import org.threeten.bp.Duration;

import java.util.Map;

public interface ActiveUIStepView extends UIStepView {
    Duration getDuration();

    Map<String, String> getSpokenInstructions();

    boolean isBackgroundAudioRequired();
}
