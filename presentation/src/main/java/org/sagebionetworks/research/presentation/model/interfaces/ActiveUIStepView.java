package org.sagebionetworks.research.presentation.model.interfaces;

import org.threeten.bp.Duration;

public interface ActiveUIStepView extends UIStepView {
    Duration getDuration();

    boolean isBackgroundAudioRequired();
}
