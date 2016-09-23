package com.kiwi.auready_ver2.tasks;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    public static final String EXTRA_ISEMPTY_TASKHEAD = "ISEMPTY_TASKS_AND_NO_TITLE";

    private TasksFragment mTasksFragment;
    private TasksPresenter mPresenter;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);


        mTasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        String taskHeadId = null;
        String taskHeadTitle = null;
        if (mTasksFragment == null) {
            mTasksFragment = TasksFragment.newInstance();

            taskHeadId = getIntent().getStringExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID);
            taskHeadTitle = getIntent().getStringExtra(TaskHeadsActivity.EXTRA_TASKHEAD_TITLE);
            mActionBar.setTitle(taskHeadTitle);
            Bundle bundle = new Bundle();
            bundle.putString(TaskHeadsActivity.EXTRA_TASKHEAD_ID, taskHeadId);
            bundle.putString(TaskHeadsActivity.EXTRA_TASKHEAD_TITLE, taskHeadTitle);
            mTasksFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mTasksFragment, R.id.content_frame, TasksFragment.TAG_TASKSFRAGMENT);
        }

        // Create the presenter
        mPresenter = new TasksPresenter(Injection.provideUseCaseHandler(),
                taskHeadId,
                mTasksFragment,
                Injection.provideGetTasks(getApplicationContext()),
                Injection.provideSaveTask(getApplicationContext()),
                Injection.provideCompleteTask(getApplicationContext()),
                Injection.provideActivateTask(getApplicationContext()),
                Injection.provideSortTasks(getApplicationContext()),
                Injection.provideDeleteTask(getApplicationContext()),
                Injection.provideEditDescription(getApplicationContext()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }


    @Override
    public void onBackPressed() {
        if(mTasksFragment.isAdded()) {
            mTasksFragment.onBackPressed();
        }
    }

}

