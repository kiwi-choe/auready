package com.kiwi.auready_ver2.taskheaddetail;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TaskHeadDetailActivity extends AppCompatActivity {


    public static final String ARG_TASKHEAD_ID = "arg_taskhead_id";
    public static final String ARG_CNT_OF_TASKHEADS = "arg_countOfTaskHeads";

    private TaskHeadDetailFragment mTaskHeadDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_head_detail);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        mTaskHeadDetailFragment =
                (TaskHeadDetailFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        String taskHeadId = null;
        if (mTaskHeadDetailFragment == null) {
            mTaskHeadDetailFragment = TaskHeadDetailFragment.newInstance();

            Bundle bundle = new Bundle();
            // Edit
            if (getIntent().hasExtra(ARG_TASKHEAD_ID)) {
                taskHeadId = getIntent().getStringExtra(ARG_TASKHEAD_ID);
                bundle.putString(ARG_TASKHEAD_ID, taskHeadId);
            }
            // New
            else {
                if (getIntent().hasExtra(ARG_CNT_OF_TASKHEADS)) {
                    int cntOfTaskheads = getIntent().getIntExtra(ARG_CNT_OF_TASKHEADS, 0);
                    bundle.putInt(ARG_CNT_OF_TASKHEADS, cntOfTaskheads);
                }
            }
            mTaskHeadDetailFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    mTaskHeadDetailFragment, R.id.content_frame, TaskHeadDetailFragment.TAG_TASKHEADDETAILFRAG);
        }

        // Create the presenter
        TaskHeadDetailPresenter presenter = new TaskHeadDetailPresenter(
                Injection.provideUseCaseHandler(),
                taskHeadId,
                mTaskHeadDetailFragment,
                Injection.provideSaveTaskHead(getApplicationContext()),
                Injection.provideGetTaskHead(getApplicationContext()),
                Injection.provideEditTaskHead(getApplicationContext()),
                Injection.provideAddMembers(getApplicationContext()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mTaskHeadDetailFragment.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mTaskHeadDetailFragment != null) {
            mTaskHeadDetailFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
