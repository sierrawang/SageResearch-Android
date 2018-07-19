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

package org.sagebionetworks.research.domain.step.ui.active;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        Command.PLAY_SOUND_ON_START,
        Command.PLAY_SOUND_ON_FINISH,
        Command.PLAY_SOUND,
        Command.VIBRATE_ON_START,
        Command.VIBRATE_ON_FINISH,
        Command.VIBRATE,
        Command.START_TIMER_AUTOMATICALLY,
        Command.CONTINUE_ON_FINISH,
        Command.TRANSITION_AUTOMATICALLY,
        Command.SHOULD_DISABLE_IDLE_TIMER
})
public @interface Command {
    String PLAY_SOUND_ON_START = "playSoundOnStart";
    String PLAY_SOUND_ON_FINISH = "playSoundOnFinish";
    String PLAY_SOUND = "playSound";
    String VIBRATE_ON_START = "vibrateOnStart";
    String VIBRATE_ON_FINISH = "vibrateOnFinish";
    String VIBRATE = "vibrate";
    String START_TIMER_AUTOMATICALLY = "startTimerAutomatically";
    String CONTINUE_ON_FINISH = "continueOnFinish";
    String TRANSITION_AUTOMATICALLY = "transitionAutomatically";
    String SHOULD_DISABLE_IDLE_TIMER = "shouldDisableIdleTimer";
}
