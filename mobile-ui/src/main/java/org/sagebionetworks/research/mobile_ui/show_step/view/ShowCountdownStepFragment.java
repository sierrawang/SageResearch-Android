package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.View;

import org.sagebionetworks.research.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.ActiveUIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.action.ActionViewBase;
import org.sagebionetworks.research.presentation.model.interfaces.CountdownStepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;

public class ShowCountdownStepFragment extends ShowActiveUIStepFragmentBase<CountdownStepView,
        ShowActiveUIStepViewModel<CountdownStepView>, ActiveUIStepViewBinding<CountdownStepView>> {

    @Override
    public void onStart() {
        super.onStart();
        if (!showStepViewModel.isCountdownRunning() && !showStepViewModel.isCountdownPaused()) {
            showStepViewModel.startCountdown();
        }
    }

    @NonNull
    public static ShowCountdownStepFragment newInstance(@NonNull StepView stepView) {
        if (!(stepView instanceof CountdownStepView)) {
            throw new IllegalArgumentException("Step view: " + stepView + " is not a CountdownStepView.");
        }

        ShowCountdownStepFragment fragment = new ShowCountdownStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    @LayoutRes
    public int getLayoutId() {
        return R.layout.rs2_show_countdown_step_fragment_layout;
    }

    @Override
    @NonNull
    protected ActiveUIStepViewBinding<CountdownStepView> instantiateAndBindBinding(View view) {
        return new ActiveUIStepViewBinding<>(view);
    }

    @Override
    protected ActionView getForwardButtonActionView(UIStepView stepView) {
        // Unless specified in JSON or through a sub-class, countdown should have a pause/resume button.
        return ActionViewBase.builder()
                .setButtonTitle(DisplayString.create(null, getPauseResumeCountdownButtonTitle()))
                .build();
    }

    /**
     * Called whenever one of this fragment's ActionButton's is clicked.
     * @param actionButton the ActionButton that was clicked by the user.
     */
    @Override
    protected void handleActionButtonClick(@NonNull ActionButton actionButton) {
        @ActionType String actionType = this.getActionTypeFromActionButton(actionButton);
        if (actionType.equals(ActionType.FORWARD)) {
            if (showStepViewModel.isCountdownRunning()) {
                showStepViewModel.pauseCountdown();
            } else {
                showStepViewModel.resumeCountdown();
            }
            ActionButton pauseResumeButton = stepViewBinding.getNextButton();
            if (pauseResumeButton != null) {
                pauseResumeButton.setText(getPauseResumeCountdownButtonTitle());
            }
        } else {
            super.handleActionButtonClick(actionButton);
        }
    }

    /**
     * @return the proper title for the countdown pause/resume button.
     */
    private String getPauseResumeCountdownButtonTitle() {
        if (showStepViewModel.isCountdownRunning()) {
            return getResources().getString(R.string.countdown_pause);
        } else {
            return getResources().getString(R.string.countdown_resume);
        }
    }
}
