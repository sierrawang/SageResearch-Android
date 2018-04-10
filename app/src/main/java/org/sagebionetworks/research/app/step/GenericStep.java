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

package org.sagebionetworks.research.app.step;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.sagebionetworks.research.app.R;

public class GenericStep extends FrameLayout {

    public static class Binding {

        @BindView(R.id.description)
        public TextView description;

        @BindView(R.id.title)
        public TextView title;
    }

    private Binding binding;

    private LayoutInflater layoutInflater;

    public GenericStep(Context context) {
        super(context);
        init();
        TextView.inflate(context, 0, null);
    }

    public GenericStep(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.GenericStep, 0, 0);
        initAttrs(typedArray);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GenericStep(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.GenericStep, defStyleAttr, defStyleRes);
        initAttrs(typedArray);

    }

    @LayoutRes
    protected int getLayoutId() {
        return 0;
    }

    private void init() {
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(getLayoutId(), this, true);

        binding = new Binding();
        ButterKnife.bind(binding, this);
    }

    private void initAttrs(TypedArray typedArray) {

    }
}
