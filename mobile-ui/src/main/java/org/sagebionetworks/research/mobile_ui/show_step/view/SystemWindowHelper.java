package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.WindowInsetsCompat;
import android.view.ViewGroup;
import org.sagebionetworks.research.domain.mobile_ui.R;

/**
 * Provides OnApplyInsetsListener's that can be used to make views shift position by the system window insets to
 * ensure that view's like buttons do not end up behind the status bar or navigation bar.
 */
public abstract class SystemWindowHelper {
    /**
     * Represents one of the four sides of the screen.
     */
    public enum Direction {
        LEFT, RIGHT, TOP, BOTTOM
    }

    /**
     * Stores the value of the inset change in each of the four sides of the screen.
     */
    private static class InsetChange {
        private final int left, right, top, bottom;

        private InsetChange(int left, int right, int top, int bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
    }

    /**
     * Returns an InsetChange representing the difference between the oldInsets if any, and the newInsets.
     * @param oldInsets The old system window insets.
     * @param newInsets The new system window insets.
     * @return an InsetChange representing the difference between the oldInsets if any, and the newInsets.
     */
    private static InsetChange getInsetChange(@Nullable WindowInsetsCompat oldInsets,
                                             @NonNull WindowInsetsCompat newInsets) {
        if (oldInsets == null) {
            return new InsetChange(newInsets.getSystemWindowInsetLeft(), newInsets.getSystemWindowInsetRight(),
                    newInsets.getSystemWindowInsetTop(), newInsets.getSystemWindowInsetBottom());
        }

        return new InsetChange(newInsets.getSystemWindowInsetLeft() - oldInsets.getSystemWindowInsetLeft(),
                newInsets.getSystemWindowInsetRight() - oldInsets.getSystemWindowInsetRight(),
                newInsets.getSystemWindowInsetTop() - oldInsets.getSystemWindowInsetTop(),
                newInsets.getSystemWindowInsetBottom() - oldInsets.getSystemWindowInsetBottom());
    }

    /**
     * Returns an OnApplyWindowInsetsListener that will move the view provided to it by the value of the system insets
     * in each of the Directions provided. This is useful for constraining a view relative to the system insets when
     * it's parent is not constrained to the system insets (For Example: when an image view goes to the edge of the
     * screen, but a button inside it needs to not overlap with the status bar).
     * @param directions The directions to constrain the view with respect to the system insets in.
     * @return an OnApplyWindowInsetsListener that will move the view provided to it by the value of the system insets
     * in each of the directions provided.
     */
    public static OnApplyWindowInsetsListener getOnApplyWindowInsetsListener(final Direction... directions) {
        return (view, insets) -> {
            final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            final WindowInsetsCompat oldInsets =
                    (WindowInsetsCompat) view.getTag(R.id.rs2_system_window_helper_insets_tag_key);
            final InsetChange insetChange = SystemWindowHelper.getInsetChange(oldInsets, insets);
            for (Direction direction : directions) {
                switch (direction) {
                    case LEFT:
                        layoutParams.leftMargin += insetChange.left;
                        break;
                    case RIGHT:
                        layoutParams.rightMargin += insetChange.right;
                        break;
                    case TOP:
                        layoutParams.topMargin += insetChange.top;
                        break;
                    case BOTTOM:
                        layoutParams.bottomMargin += insetChange.bottom;
                        break;
                }
            }

            view.setLayoutParams(layoutParams);
            view.setTag(R.id.rs2_system_window_helper_insets_tag_key, insets);
            // Ask for the parent to get laid out again so the new insets take effect.
            view.getParent().requestLayout();
            return insets;
        };
    }
}
