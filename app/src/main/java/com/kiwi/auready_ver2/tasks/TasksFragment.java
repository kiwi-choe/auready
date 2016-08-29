package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";

    private TasksAdapter mActiveTasksAdapter;
    private TasksAdapter mCompleteTasksAdapter;

    private String mTaskHeadId;
    private String mTaskHeadTitle;

    private TasksContract.Presenter mPresenter;

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

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mTaskHeadId = getArguments().getString(TasksActivity.EXTRA_TASKHEAD_ID);
            mTaskHeadTitle = getArguments().getString(TasksActivity.EXTRA_TASKHEAD_TITLE);
        }

        mActiveTasksAdapter = new TasksAdapter(new ArrayList<Task>(0), mTaskItemListener);
        mCompleteTasksAdapter = new TasksAdapter(new ArrayList<Task>(0), mTaskItemListener);
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Set ListView
        final ListView activeTaskListView = (ListView) root.findViewById(R.id.active_task_list);
        activeTaskListView.setAdapter(mActiveTasksAdapter);
        ListView completeTaskListView = (ListView) root.findViewById(R.id.complete_task_list);
        completeTaskListView.setAdapter(mCompleteTasksAdapter);

        // Set Button
        Button addTaskViewBt = (Button) root.findViewById(R.id.add_taskview_bt);

        return root;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showTasks(List<Task> tasks) {

    }

    @Override
    public void showNoTasks() {

    }

    @Override
    public void showEmptyTasksError() {
        Intent intent = new Intent();
        intent.putExtra(TasksActivity.EXTRA_ISEMPTY_TASKS, true);
        intent.putExtra(TasksActivity.EXTRA_TASKHEAD_ID, mTaskHeadId);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onBackPressed() {
        // Save Tasks when onBackPressed
//        mPresenter.saveTasks(mTaskHeadTitle, );
    }


}
