package org.sagebionetworks.research.app.inject;

import org.sagebionetworks.research.mobile_ui.inject.PerformTaskModule;

import dagger.Module;

@Module(includes = {PerformTaskModule.class, AppStepModule.class})
public class AppTaskModule {
}
