package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";

    private LinearLayout mTasksView;
    private TextView mNoTasksView;

    private ActiveTasksAdapter mActiveTasksAdapter;
    private CompletedTasksAdapter mCompletedTasksAdapter;

    private String mTaskHeadId;
    private String mTaskHeadTitle;

    private TasksContract.Presenter mPresenter;

    /*
        * Listener for clicks on activeTasks
        * */
    ActiveTasksAdapter.TaskItemListener mActiveTaskItemListener = new ActiveTasksAdapter.TaskItemListener() {
        @Override
        public void onCompleteTaskClick(Task completedTask) {
//            mPresenter.completeTask(completedTask);
        }
    };

    /*
      * Listener for clicks on completedTasks
      * */
    CompletedTasksAdapter.TaskItemListener mCompletedTaskItemListener = new CompletedTasksAdapter.TaskItemListener() {

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

        mActiveTasksAdapter = new ActiveTasksAdapter(new ArrayList<Task>(0), mActiveTaskItemListener);
        mCompletedTasksAdapter = new CompletedTasksAdapter(new ArrayList<Task>(0), mCompletedTaskItemListener);
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

        // Set TasksView
        mTasksView = (LinearLayout) root.findViewById(R.id.tasks_view);
        // Set NoTasksView
        mNoTasksView = (TextView) root.findViewById(R.id.no_tasks_view);

        // Set ListView
        final ListView activeTaskListView = (ListView) root.findViewById(R.id.active_task_list);
        activeTaskListView.setAdapter(mActiveTasksAdapter);
        ListView completeTaskListView = (ListView) root.findViewById(R.id.complete_task_list);
        completeTaskListView.setAdapter(mCompletedTasksAdapter);

        // Set Button
        Button addTaskViewBt = (Button) root.findViewById(R.id.add_taskview_bt);
        addTaskViewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task newTask = new Task(mTaskHeadId);
                mPresenter.saveTask(newTask);
            }
        });
        return root;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showNoTasks() {

    }

    @Override
    public void showEmptyTasksError() {

        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

//        Intent intent = new Intent();
//        intent.putExtra(TasksActivity.EXTRA_ISEMPTY_TASKS, true);
//        intent.putExtra(TasksActivity.EXTRA_TASKHEAD_ID, mTaskHeadId);
//        getActivity().setResult(Activity.RESULT_OK, intent);
//        getActivity().finish();
    }

    @Override
    public void showLoadingErrorTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    @Override
    public void showActiveTasks(List<Task> tasks) {
        mActiveTasksAdapter.replaceData(tasks);
    }

    @Override
    public void showCompletedTasks(List<Task> tasks) {
        mCompletedTasksAdapter.replaceData(tasks);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {

//        mActiveTasksAdapter.notifyDataSetChanged();
//        mPresenter.saveTasks(mTaskHeadTitle, );
        super.onPause();
    }
}
