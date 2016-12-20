package com.kiwi.auready_ver2.taskheaddetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TaskHeadDetailActivity extends AppCompatActivity {

    public static final int REQ_ADD_TASKHEAD = 1;

    public static final String ARG_TASKHEAD_ID = "arg_taskhead_id";

    public static final String ARG_USEREMAIL = "arg_userEmail";
    public static final String ARG_USERNAME = "arg_userName";
    public static final String ARG_MY_ID_OF_FRIEND = "arg_myIdOfFriend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_head_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TaskHeadDetailFragment taskHeadDetailFragment =
                (TaskHeadDetailFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        String taskHeadId = null;
        if (taskHeadDetailFragment == null) {
            taskHeadDetailFragment = TaskHeadDetailFragment.newInstance();

            if (getIntent().hasExtra(ARG_TASKHEAD_ID)) {
                taskHeadId = getIntent().getStringExtra(ARG_TASKHEAD_ID);
                Bundle bundle = new Bundle();
                bundle.putString(ARG_TASKHEAD_ID, taskHeadId);
                taskHeadDetailFragment.setArguments(bundle);
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    taskHeadDetailFragment, R.id.content_frame, TaskHeadDetailFragment.TAG_TASKHEADDETAILFRAG);
        }

        // Create the presenter
        TaskHeadDetailPresenter presenter = new TaskHeadDetailPresenter(
                Injection.provideUseCaseHandler(),
                taskHeadId,
                taskHeadDetailFragment,
                Injection.provideSaveTaskHead(getApplicationContext()),
                Injection.provideGetTaskHead(getApplicationContext()));

    }
}
