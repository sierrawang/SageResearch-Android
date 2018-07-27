package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.view.View;
import android.view.ViewGroup;
import org.sagebionetworks.research.domain.mobile_ui.R;

public abstract class SystemWindowHelper {
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static WindowInsetsCompat insetListener(View view, WindowInsetsCompat insets) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        ViewGroup.MarginLayoutParams oldLayoutParams =
                (ViewGroup.MarginLayoutParams) view.getTag(R.id.rs2_system_window_helper_layout_params_tag_key);
        if (oldLayoutParams != null) {
            // We undo the old transformation if there was one.
            layoutParams.topMargin -= oldLayoutParams.topMargin;
        }

        layoutParams.topMargin += insets.getSystemWindowInsetTop();
        view.setLayoutParams(layoutParams);
        view.getParent().requestLayout();
        view.setTag(R.id.rs2_system_window_helper_layout_params_tag_key, layoutParams);
        return insets;
    }

    public static void adjustViewInsets(@Nullable View view) {
        if (view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view, SystemWindowHelper::insetListener);
        }
    }
}
