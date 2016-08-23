package com.kiwi.auready_ver2.addedittask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;

public class AddEditTaskFragment extends Fragment {

    public static final String TAG_ADDEDITTASKFRAGMENT = "Tag_AddEditTaskFragment";
    public static final String ARG_TASKHEAD_ID = "arg_taskHeadId";

    private TasksAdapter mActiveTasksAdapter;
    private TasksAdapter mCompleteTasksAdapter;

    private String mTaskHeadId;

    /*
        * Listener for clicks on tasks in th listview
        * */
    TasksAdapter.TaskItemListener mTaskItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void onCompleteTaskClick(Task completedTask) {
//            mPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
//            mPresenter.activateTask(activatedTask);
        }
    };

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    public static AddEditTaskFragment newInstance(String taskHeadId) {

        AddEditTaskFragment fragment = new AddEditTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASKHEAD_ID, taskHeadId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mTaskHeadId = getArguments().getString(ARG_TASKHEAD_ID);
        }

        mActiveTasksAdapter = new TasksAdapter(new ArrayList<Task>(0), mTaskItemListener, mTaskHeadId);
        mCompleteTasksAdapter = new TasksAdapter(new ArrayList<Task>(0), mTaskItemListener, mTaskHeadId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_edit_task, container, false);

        // Set ListView
        final ListView activeTaskListView = (ListView) root.findViewById(R.id.active_task_list);
        activeTaskListView.setAdapter(mActiveTasksAdapter);
        ListView completeTaskListView = (ListView) root.findViewById(R.id.complete_task_list);
        completeTaskListView.setAdapter(mCompleteTasksAdapter);

        // Set Button
        Button addTaskViewBt = (Button) root.findViewById(R.id.add_taskview_bt);

        return root;
    }
}
