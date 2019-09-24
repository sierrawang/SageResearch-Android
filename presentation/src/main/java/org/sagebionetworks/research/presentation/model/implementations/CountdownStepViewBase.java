package org.sagebionetworks.research.presentation.model.implementations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.sagebionetworks.research.domain.step.StepType;
import org.sagebionetworks.research.domain.step.interfaces.CountdownStep;
import org.sagebionetworks.research.domain.step.interfaces.Step;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;
import org.sagebionetworks.research.presentation.model.ColorThemeView;
import org.sagebionetworks.research.presentation.model.ImageThemeView;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.interfaces.CountdownStepView;
import org.threeten.bp.Duration;

import java.util.Map;

public class CountdownStepViewBase extends ActiveUIStepViewBase implements CountdownStepView {
    public static final String TYPE = StepType.COUNTDOWN;

    public CountdownStepViewBase(@NonNull String identifier,
                                 @NonNull ImmutableMap<String, ActionView> actions,
                                 @Nullable DisplayString title, @Nullable DisplayString text,
                                 @Nullable DisplayString detail, @Nullable DisplayString footnote,
                                 @Nullable ColorThemeView colorTheme, @Nullable ImageThemeView imageTheme,
                                 @NonNull Duration duration, @NonNull Map<String, String> spokenInstructions,
                                 @NonNull final ImmutableSet<String> commands, boolean isBackgroundAudioRequired) {
        super(identifier, actions, title, text, detail, footnote, colorTheme, imageTheme, duration,
                spokenInstructions, commands, isBackgroundAudioRequired);
    }

    public static CountdownStepViewBase fromCountdownStep(Step step, DrawableMapper mapper) {
        if (!(step instanceof CountdownStep)) {
            throw new IllegalArgumentException("Provided step: " + step + " is not a CountdownStep");
        }

        ActiveUIStepViewBase activeStep = ActiveUIStepViewBase.fromActiveUIStep(step, mapper);
        return new CountdownStepViewBase(activeStep.getIdentifier(),
                activeStep.getActions(), activeStep.getTitle(), activeStep.getText(),
                activeStep.getDetail(), activeStep.getFootnote(), activeStep.getColorTheme(),
                activeStep.getImageTheme(), activeStep.getDuration(), activeStep.getSpokenInstructions(),
                activeStep.getCommands(), activeStep.isBackgroundAudioRequired());
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
