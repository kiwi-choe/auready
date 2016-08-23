package com.kiwi.auready_ver2.addedittask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQ_ADD_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        String taskHeadId = getIntent().getStringExtra(AddEditTaskFragment.ARG_TASKHEAD_ID);

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance(taskHeadId);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), addEditTaskFragment, R.id.content_frame, AddEditTaskFragment.TAG_ADDEDITTASKFRAGMENT);
        }

    }
}
