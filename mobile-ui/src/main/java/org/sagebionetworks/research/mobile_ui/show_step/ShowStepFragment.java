package org.sagebionetworks.research.mobile_ui.show_step;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;

public abstract class ShowStepFragment extends Fragment {
    public abstract void setPerformTaskFragment(@NonNull PerformTaskFragment performTaskFragment);
}
