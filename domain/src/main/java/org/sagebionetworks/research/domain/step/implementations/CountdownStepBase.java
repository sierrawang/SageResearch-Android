package org.sagebionetworks.research.domain.step.implementations;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.CountdownStep;
import org.sagebionetworks.research.domain.step.ui.action.interfaces.Action;
import org.sagebionetworks.research.domain.step.ui.theme.ColorTheme;
import org.sagebionetworks.research.domain.step.ui.theme.ImageTheme;

import java.util.Map;

public class CountdownStepBase extends ActiveUIStepBase implements CountdownStep {
    public static final String TYPE_KEY = StepType.COUNTDOWN;

    public CountdownStepBase(@NonNull final String identifier, @NonNull final Map<String, Action> actions,
                             @Nullable final String title, @Nullable final String text,
                             @Nullable final String detail, @Nullable final String footnote,
                             @Nullable final ColorTheme colorTheme, @Nullable final ImageTheme imageTheme,
                             @Nullable final Double duration, final boolean backgroundAudioRequired) {
        super(identifier, actions, title, text, detail, footnote, colorTheme, imageTheme, duration,
                backgroundAudioRequired);
    }

    @Override
    @NonNull
    public CountdownStepBase copyWithIdentifier(@NonNull String identifier) {
        return new CountdownStepBase(identifier, this.getActions(), this.getTitle(), this.getText(),
                this.getDetail(), this.getFootnote(), this.getColorTheme(), this.getImageTheme(),
                this.getDuration(), this.isBackgroundAudioRequired());
    }

    @Override
    public String getType() {
        return TYPE_KEY;
    }
}
