package org.sagebionetworks.research.mobile_ui.perform_task;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.mobile_ui.R;
import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment.OnPerformTaskExitListener;
import org.sagebionetworks.research.presentation.model.TaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class PerformTaskActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        OnPerformTaskExitListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformTaskActivity.class);

    private static final String EXTRA_TASK_VIEW = "TASK_VIEW";

    private static final String EXTRA_TASK_RUN_UUID = "TASK_RUN_UUID";

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;


    public static Intent createIntent(@NonNull Context context, @NonNull TaskView taskView,
            @Nullable UUID taskRunUUID) {
        checkNotNull(context);
        checkNotNull(taskView);

        Intent launchIntent = new Intent(context, PerformTaskActivity.class)
                .putExtra(EXTRA_TASK_VIEW, taskView);
        if (taskRunUUID != null) {
            launchIntent.putExtra(EXTRA_TASK_RUN_UUID, new ParcelUuid(taskRunUUID));
        }
        return launchIntent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rs2_perform_task_activity_layout);

        TaskView taskView = getIntent().getParcelableExtra(EXTRA_TASK_VIEW);
        UUID taskRunUuid = null;

        ParcelUuid taskRunParcelableUuid = getIntent().getParcelableExtra(EXTRA_TASK_RUN_UUID);
        if (taskRunParcelableUuid != null) {
            taskRunUuid = taskRunParcelableUuid.getUuid();
        }
        if (taskRunUuid == null) {
            LOGGER.debug("No taskRunUuid found, generating random");
            taskRunUuid = UUID.randomUUID();
        }

        PerformTaskFragment performTaskFragment = (PerformTaskFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rs2_task_content_frame);

        if (performTaskFragment == null) {
            // TODO: use factory to get type of TaskFragment, e.g. PerformActiveTaskFragment for an ActiveUITaskView
            performTaskFragment = PerformTaskFragment.newInstance(taskView, taskRunUuid);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rs2_task_content_frame, performTaskFragment)
                    .commit();
        }
    }

    @Override
    public void onTaskExit(@NonNull final Status status, @NonNull final TaskResult taskResult) {
        LOGGER.info("Task exited with status: {}, taskResult: {}", status, taskResult);

        finish();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }
}
