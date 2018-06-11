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

package org.sagebionetworks.research.domain.inject;

import com.google.gson.JsonDeserializer;

import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.domain.task.TaskInfo;
import org.sagebionetworks.research.domain.task.TaskInfoBase;
import org.sagebionetworks.research.domain.task.navigation.OrderedStepNavigator;
import org.sagebionetworks.research.domain.task.navigation.StepNavigatorFactory;
import org.sagebionetworks.research.domain.task.navigation.TaskBase;
import org.sagebionetworks.research.domain.task.navigation.TreeNavigator;
import org.sagebionetworks.research.domain.task.navigation.strategy.StrategyBasedNavigator;
import org.sagebionetworks.research.domain.task.navigation.strategy.StrategyBasedNavigator.Factory;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import javax.inject.Singleton;

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassThroughDeserializer;

@Module(includes = {GsonModule.class})
public abstract class TaskModule {

    /**
     * @return The json Deserializer for a task info.
     */
    @Provides
    @IntoMap
    @ClassKey(TaskInfo.class)
    static JsonDeserializer<?> provideTaskInfoDeserializer() {
        return createPassThroughDeserializer(TaskInfoBase.class);
    }

    @Singleton
    @Provides
    static StepNavigatorFactory provideStepNavigatorFactory() {
        return new StrategyBasedNavigator.Factory();
    }

    /**
     * @return The json Deserializer for a task.
     */
    @Provides
    @IntoMap
    @ClassKey(Task.class)
    static JsonDeserializer<?> provideTaskDeserializer() {
        return createPassThroughDeserializer(TaskBase.class);
    }
}
