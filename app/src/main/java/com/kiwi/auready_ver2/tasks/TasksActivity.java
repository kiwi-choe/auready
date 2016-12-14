package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    public static final int REQ_TASKS = 2;

    private TasksFragment mTasksFragment;
    private TasksPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_toolbar);
        setSupportActionBar(toolbar);

        mTasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        String taskHeadId = null;
        if (mTasksFragment == null) {
            mTasksFragment = TasksFragment.newInstance();

            if(getIntent().hasExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID)) {

                taskHeadId = getIntent().getStringExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID);
                Bundle bundle = new Bundle();
                bundle.putString(TaskHeadsActivity.EXTRA_TASKHEAD_ID, taskHeadId);
                mTasksFragment.setArguments(bundle);
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mTasksFragment,
                    R.id.content_frame, TasksFragment.TAG_TASKSFRAGMENT);
        }

        // Create the presenter
        mPresenter = new TasksPresenter(
                Injection.provideUseCaseHandler(),
                taskHeadId,
                mTasksFragment,
                Injection.provideGetTaskHead(getApplicationContext()),
                Injection.provideGetTasks(getApplicationContext()),
                Injection.provideSaveTask(getApplicationContext()),
                Injection.provideDeleteTask(getApplicationContext()));
    }
}

