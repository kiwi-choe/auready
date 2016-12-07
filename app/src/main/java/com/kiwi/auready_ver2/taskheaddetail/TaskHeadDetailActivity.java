package com.kiwi.auready_ver2.taskheaddetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TaskHeadDetailActivity extends AppCompatActivity {

    public static final int REQ_ADD_TASKHEAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_head_detail);

        TaskHeadDetailFragment taskHeadDetailFragment =
                (TaskHeadDetailFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(taskHeadDetailFragment == null) {
            taskHeadDetailFragment = TaskHeadDetailFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), taskHeadDetailFragment, R.id.content_frame, TaskHeadDetailFragment.TAG_TASKHEADDETAILFRAG);
        }
    }
}
