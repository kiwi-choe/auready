package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    public static final String ARG_TASKHEAD_ID = "TASKHEAD_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_toolbar);
        setSupportActionBar(toolbar);

        TasksFragment tasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        String taskHeadId = null;
        if (tasksFragment == null) {
            tasksFragment = TasksFragment.newInstance();

            if(getIntent().hasExtra(ARG_TASKHEAD_ID)) {

                taskHeadId = getIntent().getStringExtra(ARG_TASKHEAD_ID);
                Bundle bundle = new Bundle();
                bundle.putString(ARG_TASKHEAD_ID, taskHeadId);
                tasksFragment.setArguments(bundle);
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tasksFragment,
                    R.id.content_frame, TasksFragment.TAG_TASKSFRAGMENT);
        }

        // Create the presenter
        TasksPresenter presenter = new TasksPresenter(
                Injection.provideUseCaseHandler(),
                taskHeadId,
                tasksFragment,
                Injection.provideGetTasksOfMember(getApplicationContext()),
                Injection.provideSaveTask(getApplicationContext()),
                Injection.provideDeleteTask(getApplicationContext()),
                Injection.provideGetTasksOfTaskHead(getApplicationContext()));
    }
}

