package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.ActiveUIStepViewBinding;
import org.sagebionetworks.research.presentation.model.interfaces.CountdownStepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;

public class ShowCountdownStepFragment extends ShowActiveUIStepFragmentBase<CountdownStepView,
        ShowActiveUIStepViewModel<CountdownStepView>, ActiveUIStepViewBinding<CountdownStepView>> {
    @Override
    public void onStart() {
        super.onStart();
        this.showStepViewModel.startCountdown();
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
}
