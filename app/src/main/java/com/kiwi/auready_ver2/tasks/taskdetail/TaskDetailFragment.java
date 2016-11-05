package com.kiwi.auready_ver2.tasks.taskdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.tasks.TasksActivity;

public class TaskDetailFragment extends Fragment {

    public static final String TAG_TASKDETAILFRAGMENT = "Tag_TaskDetailFragment";

    private Button mBackBt;

    public TaskDetailFragment() {
        // Required empty public constructor
    }

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_task_detail, container, false);

        // Set up Toolbar
        ActionBar ab = ((TasksActivity)getActivity()).getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);
            // Set custom toolbar
            View customView = inflater.inflate(R.layout.taskdetail_toolbar, null);
            ab.setCustomView(customView);

            // Set event to the views in custom toolbar
            mBackBt = (Button) ab.getCustomView().findViewById(R.id.back_bt);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBackBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

    }
}
