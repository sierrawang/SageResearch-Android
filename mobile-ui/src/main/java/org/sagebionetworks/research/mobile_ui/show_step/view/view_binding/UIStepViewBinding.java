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

package org.sagebionetworks.research.mobile_ui.show_step.view.view_binding;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.sagebionetworks.research.domain.mobile_ui.R2.id;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import javax.annotation.Nullable;

/**
 * A UIStepViewBinding provides the binding for a basic step view. This view has up to three subviews, a header,
 * footer and body. Each of these subviews can contain any of the components that can be bound to a
 * NavigationViewBinding. UIStepViewBinding provides convenient functionality like being able to validate the view's
 * configuration and get its components without regard to which subview they are in. In the event that a view
 * requires a component that cannot be bound to a UIStepViewBinding a subclass should be made and this component
 * should be added.
 */
public class UIStepViewBinding {
    @Nullable
    @BindView(id.navigationHeaderView)
    protected View navigationHeaderView;

    @Nullable
    @BindView(id.navigationFooterView)
    protected View navigationFooterView;

    @Nullable
    @BindView(id.navigationBodyView)
    protected View navigationBodyView;

    protected Unbinder unbinder;
    // Invariant: if this.navigationHeaderView != null, neither are these fields.
    protected NavigationViewBinding headerViewBinding;
    protected Unbinder headerUnbinder;
    // Invariant: if this.navigationFooterView != null, neither are these fields.
    protected NavigationViewBinding footerViewBinding;
    protected Unbinder footerUnbinder;
    // Invariant: if this.navigationBodyView != null, neither are these fields.
    protected NavigationViewBinding bodyViewBinding;
    protected Unbinder bodyUnbinder;

    public UIStepViewBinding(View view) {
        this.unbinder = ButterKnife.bind(this, view);
        this.initializeHeaderViewBinding();
        this.initializeFooterViewBinding();
        this.initializeBodyViewBinding();
    }

    /**
     * Initializes the binding for the navigationHeaderView. In the event that a navigationHeaderView that is not
     * of the type NavigationViewBinding is used, the subclass should override this method to initialize this binding
     * accordingly.
     */
    protected void initializeHeaderViewBinding() {
        if (this.navigationHeaderView != null) {
            this.headerViewBinding = new NavigationViewBinding();
            this.headerUnbinder = ButterKnife.bind(this.headerViewBinding, this.navigationHeaderView);
        }
    }

    /**
     * Initializes the binding for the navigationFooterView. In the event that a navigationFooterView that is not
     * of the type NavigationViewBinding is used, the subclass should override this method to initialize this binding
     * accordingly.
     */
    protected void initializeFooterViewBinding() {
        if (this.navigationFooterView != null) {
            this.footerViewBinding = new NavigationViewBinding();
            this.footerUnbinder = ButterKnife.bind(this.footerViewBinding, this.navigationFooterView);
        }
    }

    /**
     * Initializes the binding for the navigationBodyView. In the event that a navigationBodyView that is not
     * of the type NavigationViewBinding is used, the subclass should override this method to initialize this binding
     * accordingly.
     */
    protected void initializeBodyViewBinding() {
        if (this.navigationBodyView != null) {
            this.bodyViewBinding = new NavigationViewBinding();
            this.bodyUnbinder = ButterKnife.bind(this.bodyViewBinding, this.navigationBodyView);
        }
    }

    /**
     * Returns true is this binding represents a valid configuration for a step view, and false otherwise. A valid
     * binding has at most 1 component of each type (i.e at most 1 imageView, titleLabel, textLabel, etc.), at least
     * one of the header, footer, or body views, and does not have just a header, or just a footer.
     * @return true if this binding represents a valid configuration for a step view, and false otherwise.
     */
    public boolean isValid() {
        if (this.navigationBodyView == null && (this.navigationHeaderView == null || this.navigationFooterView == null)) {
            // if the navigation body is not present, and at least one of the header or footer is also not present
            // this configuration is invalid
            return false;
        }

        // If any of the header, footer, or body are null we use an empty NavigationViewBinding in their place
        // to simplify the code.
        NavigationViewBinding header = this.hasHeader() ? this.headerViewBinding :
                new NavigationViewBinding();
        NavigationViewBinding footer = this.hasFooter() ? this.footerViewBinding :
                new NavigationViewBinding();
        NavigationViewBinding body = this.hasFooter() ? this.bodyViewBinding :
                new NavigationViewBinding();

        // If there are any duplicate children (e.g. both the header and footer contain an imageView) this
        // configuration is invalid.
        boolean result = true;
        result &= atMost1NonNull(header.imageView, footer.imageView, body.imageView);
        result &= atMost1NonNull(header.titleLabel, footer.titleLabel, body.titleLabel);
        result &= atMost1NonNull(header.textLabel, footer.textLabel, body.textLabel);
        result &= atMost1NonNull(header.detailLabel, footer.detailLabel, body.detailLabel);
        result &= atMost1NonNull(header.footnoteLabel, footer.footnoteLabel, body.footnoteLabel);
        result &= atMost1NonNull(header.progressLabel, footer.progressLabel, body.progressLabel);
        result &= atMost1NonNull(header.progressBar, footer.progressBar, body.progressBar);
        result &= atMost1NonNull(header.cancelButton, footer.cancelButton, body.cancelButton);
        result &= atMost1NonNull(header.nextButton, footer.nextButton, body.nextButton);
        result &= atMost1NonNull(header.backButton, footer.backButton, body.backButton);
        result &= atMost1NonNull(header.skipButton, footer.skipButton, body.skipButton);
        return result;
    }

    /**
     * Returns true if at most one of the given objects is non-null.
     * @param objects The objects to test.
     * @return true if at most one of the given objects is non-null.
     */
    protected static boolean atMost1NonNull(Object... objects) {
        int nonNullCount = 0;
        for (int i = 0; i < objects.length; i++) {
            nonNullCount += objects[i] != null ? 1 : 0;
        }

        return nonNullCount <= 1;
    }

    /**
     * Returns true if this binding has a header view, and false otherwise.
     * @return true if this binding has a header view, and false otherwise.
     */
    public boolean hasHeader() {
        return this.navigationHeaderView != null;
    }

    /**
     * Returns true if this binding has a footer view, and false otherwise.
     * @return true if this binding has a footer view, and false otherwise.
     */
    public boolean hasFooter() {
        return this.navigationFooterView != null;
    }

    /**
     * Returns true if this binding has a body view, and false otherwise.
     * @return true if this binding has a body view, and false otherwise.
     */
    public boolean hasBody() {
        return this.navigationBodyView != null;
    }

    /**
     * The view that is the header from this binding. Note: this method should only be used to interact directly
     * with the header view. In cases where a child view is what is wanted the appropriate getter should be called
     * instead.
     * @return The header view from this binding, or null if !this.hasHeader().
     */
    @Nullable
    public View getHeaderView() {
        return this.navigationHeaderView;
    }

    /**
     * The view that is the footer from this binding. Note: this method should only be used to interact directly
     * with the footer view. In cases where a child view is what is wanted the appropriate getter should be called
     * instead.
     * @return The footer view from this binding, or null if !this.hasFooter().
     */
    @Nullable
    public View getFooterView() {
        return this.navigationFooterView;
    }

    /**
     * The view that is the body from this binding. Note: this method should only be used to interact directly
     * with the body view. In cases where a child view is what is wanted the appropriate getter should be called
     * instead.
     * @return The body view from this binding, or null if !this.hasBody().
     */
    @Nullable
    public View getBodyView() {
        return this.navigationBodyView;
    }

    /**
     * Unbinds this binding and all of the bindings it contains. After this method is called this binding is
     * useless.
     */
    public void unbind() {
        if (this.hasHeader()) {
            this.headerUnbinder.unbind();
            // We set the header view to null to avoid accessing anything that has been unbound.
            this.navigationHeaderView = null;
        }

        if (this.hasFooter()) {
            this.footerUnbinder.unbind();
            // We set the footer view to null to avoid accessing anything that has been unbound.
            this.navigationFooterView = null;
        }

        if (this.hasBody()) {
            this.bodyUnbinder.unbind();
            // We set the body view to null to avoid accessing anything that has been unbound.
            this.navigationBodyView = null;
        }

        this.unbinder.unbind();
    }

    // region Subview Getters

    /**
     * Convenience method for getting the image view without having to worry about which subview it is a part of.
     * @return The image view for this binding or null if no such binding exists
     */
    @Nullable
    public ImageView getImageView() {
        if (this.hasHeader() && this.headerViewBinding.imageView != null) {
            return this.headerViewBinding.imageView;
        } else if (this.hasFooter() && this.footerViewBinding.imageView != null) {
            return this.footerViewBinding.imageView;
        } else if (this.hasBody() && this.bodyViewBinding.imageView != null) {
            return this.bodyViewBinding.imageView;
        }

        return null;
    }

    /**
     * Convenience method for getting the title label without having to worry about which subview it is a part of.
     * @return The title label for this binding or null if no such binding exists.
     */
    @Nullable
    public TextView getTitleLabel() {
        if (this.hasHeader() && this.headerViewBinding.titleLabel != null) {
            return this.headerViewBinding.titleLabel;
        } else if (this.hasFooter() && this.footerViewBinding.titleLabel != null) {
            return this.footerViewBinding.titleLabel;
        } else if (this.hasBody() && this.bodyViewBinding.titleLabel != null) {
            return this.bodyViewBinding.titleLabel;
        }

        return null;
    }

    /**
     * Convenience method for getting the text label without having to worry about which subview it is a part of.
     * @return The text label for this binding or null if no such binding exists.
     */
    @Nullable
    public TextView getTextLabel() {
        if (this.hasHeader() && this.headerViewBinding.textLabel != null) {
            return this.headerViewBinding.textLabel;
        } else if (this.hasFooter() && this.footerViewBinding.textLabel != null) {
            return this.footerViewBinding.textLabel;
        } else if (this.hasBody() && this.bodyViewBinding.textLabel != null) {
            return this.bodyViewBinding.textLabel;
        }

        return null;
    }

    /**
     * Convenience method for getting the detail label without having to worry about which subview it is a part of.
     * @return The detail label for this binding or null if no such binding exists.
     */
    @Nullable
    public TextView getDetailLabel() {
        if (this.hasHeader() && this.headerViewBinding.detailLabel != null) {
            return this.headerViewBinding.detailLabel;
        } else if (this.hasFooter() && this.footerViewBinding.detailLabel != null) {
            return this.footerViewBinding.detailLabel;
        } else if (this.hasBody() && this.bodyViewBinding.detailLabel != null) {
            return this.bodyViewBinding.detailLabel;
        }

        return null;
    }

    /**
     * Convenience method for getting the footnote label without having to worry about which subview it is a part of.
     * @return The footnote label for this binding or null if no such binding exists.
     */
    @Nullable
    public TextView getFootnoteLabel() {
        if (this.hasHeader() && this.headerViewBinding.footnoteLabel != null) {
            return this.headerViewBinding.footnoteLabel;
        } else if (this.hasFooter() && this.footerViewBinding.footnoteLabel != null) {
            return this.footerViewBinding.footnoteLabel;
        } else if (this.hasBody() && this.bodyViewBinding.footnoteLabel != null) {
            return this.bodyViewBinding.footnoteLabel;
        }

        return null;
    }

    /**
     * Convenience method for getting the progress label without having to worry about which subview it is a part of.
     * @return The progress label for this binding or null if no such binding exists.
     */
    @Nullable
    public TextView getProgressLabel() {
        if (this.hasHeader() && this.headerViewBinding.progressLabel != null) {
            return this.headerViewBinding.progressLabel;
        } else if (this.hasFooter() && this.footerViewBinding.progressLabel != null) {
            return this.footerViewBinding.progressLabel;
        } else if (this.hasBody() && this.bodyViewBinding.progressLabel != null) {
            return this.bodyViewBinding.progressLabel;
        }

        return null;
    }

    /**
     * Convenience method for getting the progress bar without having to worry about which subview it is a part of.
     * @return The progress bar for this binding or null if no such binding exists.
     */
    @Nullable
    public ProgressBar getProgressBar() {
        if (this.hasHeader() && this.headerViewBinding.progressBar != null) {
            return this.headerViewBinding.progressBar;
        } else if (this.hasFooter() && this.footerViewBinding.progressBar != null) {
            return this.footerViewBinding.progressBar;
        } else if (this.hasBody() && this.bodyViewBinding.progressBar != null) {
            return this.bodyViewBinding.progressBar;
        }

        return null;
    }

    /**
     * Convenience method for getting the cancel button without having to worry about which subview it is a part of.
     * @return The cancel button for this binding or null if no such binding exists.
     */
    @Nullable
    public Button getCancelButton() {
        if (this.hasHeader() && this.headerViewBinding.cancelButton != null) {
            return this.headerViewBinding.cancelButton;
        } else if (this.hasFooter() && this.footerViewBinding.cancelButton != null) {
            return this.footerViewBinding.cancelButton;
        } else if (this.hasBody() && this.bodyViewBinding.cancelButton != null) {
            return this.bodyViewBinding.cancelButton;
        }

        return null;
    }

    /**
     * Convenience method for getting the next button without having to worry about which subview it is a part of.
     * @return The next button for this binding or null if no such binding exists.
     */
    @Nullable
    public Button getNextButton() {
        if (this.hasHeader() && this.headerViewBinding.nextButton != null) {
            return this.headerViewBinding.nextButton;
        } else if (this.hasFooter() && this.footerViewBinding.nextButton != null) {
            return this.footerViewBinding.nextButton;
        } else if (this.hasBody() && this.bodyViewBinding.nextButton != null) {
            return this.bodyViewBinding.nextButton;
        }

        return null;
    }

    /**
     * Convenience method for getting the back button without having to worry about which subview it is a part of.
     * @return The back button for this binding or null if no such binding exists.
     */
    @Nullable
    public Button getBackButton() {
        if (this.hasHeader() && this.headerViewBinding.backButton != null) {
            return this.headerViewBinding.backButton;
        } else if (this.hasFooter() && this.footerViewBinding.backButton != null) {
            return this.footerViewBinding.backButton;
        } else if (this.hasBody() && this.bodyViewBinding.backButton != null) {
            return this.bodyViewBinding.backButton;
        }

        return null;
    }

    /**
     * Convenience method for getting the skip button without having to worry about which subview it is a part of.
     * @return The skip button for this binding or null if no such binding exists.
     */
    @Nullable
    public Button getSkipButton() {
        if (this.hasHeader() && this.headerViewBinding.skipButton != null) {
            return this.headerViewBinding.skipButton;
        } else if (this.hasFooter() && this.footerViewBinding.skipButton != null) {
            return this.footerViewBinding.skipButton;
        } else if (this.hasBody() && this.bodyViewBinding.skipButton != null) {
            return this.bodyViewBinding.skipButton;
        }

        return null;
    }
    // endregion
}
