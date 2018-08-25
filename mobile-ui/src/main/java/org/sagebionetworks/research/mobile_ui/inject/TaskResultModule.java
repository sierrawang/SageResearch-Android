package org.sagebionetworks.research.mobile_ui.inject;

import org.sagebionetworks.research.presentation.perform_task.BoundServiceTaskResultManager;
import org.sagebionetworks.research.presentation.perform_task.BoundServiceTaskResultProcessingManager;
import org.sagebionetworks.research.presentation.perform_task.TaskResultManager;
import org.sagebionetworks.research.presentation.perform_task.TaskResultProcessingManager;
import org.sagebionetworks.research.presentation.perform_task.TaskResultService;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class TaskResultModule {
    @ContributesAndroidInjector
    abstract TaskResultService contributeRecorderService();

    @Binds
    abstract TaskResultManager procideTaskResultManager(BoundServiceTaskResultManager boundServiceTaskResultManager);

    @Binds
    abstract TaskResultProcessingManager provideTaskResultProcessingManager(
            BoundServiceTaskResultProcessingManager boundServiceTaskResultProcessingManager);
}
