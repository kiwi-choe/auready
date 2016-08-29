package com.kiwi.auready_ver2.tasks;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    public static final int REQ_ADD_TASK = 1;
    public static final String EXTRA_TASKHEAD_ID = "TASKHEAD_ID";
    public static final String EXTRA_TASKHEAD_TITLE = "TASKHEAD_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        String taskHeadId = null;
        String taskHeadTitle = null;
        if (tasksFragment == null) {
            tasksFragment = TasksFragment.newInstance();

            taskHeadId = getIntent().getStringExtra(EXTRA_TASKHEAD_ID);
            taskHeadTitle = getIntent().getStringExtra(EXTRA_TASKHEAD_TITLE);
            actionBar.setTitle(taskHeadTitle);
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_TASKHEAD_ID, taskHeadId);
            bundle.putString(EXTRA_TASKHEAD_TITLE, taskHeadTitle);
            tasksFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.content_frame, TasksFragment.TAG_TASKSFRAGMENT);
        }

        // Create the presenter
        new TasksPresenter(Injection.provideUseCaseHandler(),
                taskHeadId,
                tasksFragment,
                Injection.provideGetTasks(getApplicationContext()),
                Injection.provideSaveTasks(getApplicationContext())
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
