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

package org.sagebionetworks.research.domain.step.interfaces;

import android.support.annotation.Nullable;

import org.sagebionetworks.research.domain.step.ui.action.ActionHandler;

/**
 * A UIStep defines a display unit.
 * <p>
 * Depending upon the available real-estate, more than one ui step may be displayed at a time. For example, on an
 * tablet, you may choose to group a set of questions using a @see SectionStep.
 */
public interface UIStep extends Step, ActionHandler {
    /**
     * Additional detailed explanation for the step.
     * <p>
     * The font size and display of this property will depend upon the device type.
     *
     * @return detail text
     */
    @Nullable
    String getDetail();

    /**
     * The font size and display of this property will depend upon the device type.
     * <p>
     * The footnote is intended to be displayed in a smaller font at the bottom of the screen. It is intended to be
     * used in order to include disclaimer, copyright, etc. that is important to display in the step but should not
     * distract from the main purpose of the step.
     *
     * @return footnote text
     */
    @Nullable
    String getFootnote();

    /**
     * Additional text to display for the step in a localized string.
     * <p>
     * The additional text is often displayed in a smaller font below `title`. If you need to display a long question,
     * it can work well to keep the title short and put the additional content in the `text` property.
     *
     * @return additional text
     */
    @Nullable
    String getText();

    /**
     * The primary text to display for the step in a localized string.
     *
     * @return title text
     */
    @Nullable
    String getTitle();
}
