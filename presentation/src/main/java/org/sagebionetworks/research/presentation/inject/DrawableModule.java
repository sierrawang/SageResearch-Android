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

package org.sagebionetworks.research.presentation.inject;

import android.support.annotation.DrawableRes;

import org.sagebionetworks.research.domain.presentation.R;
import org.sagebionetworks.research.domain.repository.TaskRepository;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;

import java.util.Map;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

@Module
public class DrawableModule {
    @MapKey
    public @interface ResourceNameKey {
        String value();
    }

    @Provides
    @IntoMap
    @DrawableRes
    @ResourceNameKey(DrawableName.CANCEL)
    static Integer provideCancelResource() {
        return R.drawable.rs2_cancel_icon;
    }

    @Provides
    @IntoMap
    @DrawableRes
    @ResourceNameKey(DrawableName.BACK)
    static Integer provideBackResource() {
        return R.drawable.rs2_back_icon;
    }

    @Provides
    @IntoMap
    @DrawableRes
    @ResourceNameKey(DrawableName.INFO)
    static Integer provideInfoResource() {
        return R.drawable.rs2_info_icon;
    }

    @Provides
    DrawableMapper provideDrawableMapper(TaskRepository taskRepository,
            Map<String, Integer> drawableMap) {
        return new DrawableMapper(taskRepository, drawableMap);
    }
}
