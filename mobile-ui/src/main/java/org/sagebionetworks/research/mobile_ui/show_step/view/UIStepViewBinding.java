/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R2;
import org.sagebionetworks.research.domain.mobile_ui.R2.id;

import butterknife.BindView;
import javax.annotation.Nullable;

/**
 *  A UIStepViewBinding stores the various views that a UIStep view may contain, and provides the ability to check
 *  whether these bindings represent a valid StepView configuration. Supported Views are:
 *      NOTE: the format for these is ID : TYPE - DESCRIPTION.
 *      - navigationHeaderView : View - The root view for the top part of this view.
 *      - navigationFooterView : View - The root view for the bottom part of this view.
 *      - navigationBodyView : View - The root view for the entire step view in the absence of header and footer views
 *      - imageView : ImageView - The view to display an images for the step on.
 *      - titleLabel : TextView - The view to display the title of the step on.
 *      - textLabel : TextView - The view to display the text of the step on.
 *      - detailLabel : TextView - The view to display the detail of the step on.
 *      - footnoteLabel : TextView - The view to display the footnote of the step on.
 *      - progressLabel : TextView - The view to display a String representation of the user's progress on.
 *      - progressBar : ProgressBar - The view to display a Visual representation of the user's progress on.
 *      - cancelButton : Button - The button which ends the current active task when pressed.
 *      - nextButton : Button - The button which causes the task to navigate to the next step when pressed.
 *      - backButton : Button - The button which causes the task to navigate to the previous step when pressed.
 *      - skipButton : Button - The button which causes the task to skip the current step when pressed. (Note: this
 *          button is different then the next button because no result will be created for the current step)
 */
public class UIStepViewBinding {
    /**
     * Step views can optionally have a view with the id `navigationHeaderView`. While the navigation header may
     * contain content such as the imageView and/or titleLabel this binding is only used for directly interacting
     * with the header view. In order to access the children of the header view the bindings for imageView/titleLabel/
     * textLabel etc should be used.
     */
    @Nullable
    @BindView(R2.id.navigationHeaderView)
    public View navigationHeaderView;

    /**
     * Step views can optionally have a view with the id `navigationFooterView`. While the navigation footer may
     * contain content such as next/back buttons this binding is only used for directly interacting with the
     * footer view. In order to access the buttons on the navigationFooter the bindings for nextButton/backButton/
     * skipButton should be used.
     */
    @Nullable
    @BindView(R2.id.navigationFooterView)
    public View navigtaionFooterView;

    /**
     * In lieu of header/footer views, Step views can instead have a view with the id `navigationBodyView`. If present
     * this view represents the container for the entire content of the step view. While this view may contain
     * children this binding should only be used to interact with the body view itself. In order to access the
     * children the specific bindings for the child components should be used.
     */
    @Nullable
    @BindView(R2.id.navigationBodyView)
    public View navigationBodyView;

    /**
     * Step views can optionally have an image view with the id `imageView`. This view generally displays and image
     * or icon that is associated with the step.
     */
    @Nullable
    @BindView(R2.id.imageView)
    public ImageView imageView;

    /**
     * Step views can optionally have a title view with the id `titleLabel`. This view generally displays a large
     * title consisting of a brief description of what the step represents.
     **/
    @Nullable
    @BindView(R2.id.titleLabel)
    public TextView titleLabel;

    /**
     * Step views can optionally have a text view with the id `textLabel`. This view generally displays the main
     * content for the step in medium size font.
     */
    @Nullable
    @BindView(R2.id.textLabel)
    public TextView textLabel;

    /**
     * Step views can optionally have a text view with the id `detailLabel`. This view generally displays further
     * information related to the information displayed on the textLabel.
     */
    @Nullable
    @BindView(R2.id.detailLabel)
    public TextView detailLabel;

    /**
     * Step views can optionally have a text view with the id `footnoteLabel`. This view generally displays the
     * footnote of the step that is being displayed.
     */
    @Nullable
    @BindView(R2.id.footnoteLabel)
    public TextView footnoteLabel;

    /**
     * Step views can optionally have a text view with the id `progressLabel`. This view generally displays a string
     * indicating how much progress the user has made into the current active task (i.e. "STEP 1 OF 6").
     */
    @Nullable
    @BindView(R2.id.progressLabel)
    public TextView progressLabel;

    /**
     * Step views can optionally have a progress bar with the id `progressBar1. This progress bar generally displays
     * a visual indication of how much progress the user has made into the current active task. This corresponds
     * to the string displayed on the progressLabel.
     */
    @Nullable
    @BindView(R2.id.progressBar)
    public ProgressBar progressBar;

    /**
     * Step views can optionally have a button with the id `cancelButton`. This button generally ends the current
     * active task when pressed
     */
    @Nullable
    @BindView(R2.id.cancelButton)
    public Button cancelButton;

    /**
     * Step views can optionally have a button with the id `nextButton`. This button generally moves on to the next
     * step in the active task, or completes the task when pressed.
     */
    @Nullable
    @BindView(R2.id.nextButton)
    public Button nextButton;

    /**
     * Step views can optionally have a button with the id `backButton`. This button generally causes the task to
     * navigate to the previous step when pressed.
     */
    @Nullable
    @BindView(R2.id.backButton)
    public Button backButton;

    /**
     * Step views can optionally have a button with the id `skipButton`. This button generally causes the current
     * step to be skipped when pressed.
     */
    @Nullable
    @BindView(R2.id.skipButton)
    public Button skipButton;

    /**
     * Returns true if the current bindings in this object represent a valid configuration for a step view and false
     * otherwise. Intended to be used for debugging, and ensuring that unsupported configurations are not used.
     * Valid configurations are
     * // TODO: rkolmos 05/18/2018 Define the rules for what configurations are valid in step views.
     * @return true if the current bindings represent a valid step view, false otherwise.
     */
    public boolean isValid() {
        // TODO: rkolmos 05/18/2018 implement this method.
        return true;
    }
}
