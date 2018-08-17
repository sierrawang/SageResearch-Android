package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import org.sagebionetworks.research.domain.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.ActiveUIStepViewBinding;
import org.sagebionetworks.research.presentation.model.interfaces.ActiveUIStepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;

public class ShowActiveUIStepFragment extends ShowActiveUIStepFragmentBase<ActiveUIStepView,
        ShowActiveUIStepViewModel<ActiveUIStepView>, ActiveUIStepViewBinding<ActiveUIStepView>> {
    @NonNull
    public static ShowActiveUIStepFragment newInstance(@NonNull StepView stepView) {
        if (!(stepView instanceof ActiveUIStepView)) {
            throw new IllegalArgumentException("Step view: " + stepView + " is not an ActiveUIStepView.");
        }

        ShowActiveUIStepFragment fragment = new ShowActiveUIStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    @LayoutRes
    public int getLayoutId() {
        return R.layout.rs2_show_active_ui_step_fragment_layout;
    }

    @Override
    @NonNull
    protected ActiveUIStepViewBinding<ActiveUIStepView> instantiateAndBindBinding(View view) {
        return new ActiveUIStepViewBinding<>(view);
    }


}
