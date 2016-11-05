package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.customlistview.DragSortListView;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;
import com.kiwi.auready_ver2.tasks.taskdetail.TaskDetailFragment;
import com.kiwi.auready_ver2.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";

    private TextView mTitleView;
    private DragSortListView mTasksView;
    private TasksAdapter mTasksAdapter;

    private String mTaskHeadId;
    private String mTaskHeadTitle;

    private TasksContract.Presenter mPresenter;

    /*
        * Listener for clicks on Tasks
        * */
    TasksAdapter.TaskItemListener mTaskItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void onCompleteTaskClick(Task completedTask) {
            // FIXME: 11/5/16
            TaskDetailFragment detailFragment = new TaskDetailFragment();
            ActivityUtils.replaceFragment(
                    getFragmentManager(), detailFragment, R.id.content_frame, TaskDetailFragment.TAG_TASKDETAILFRAGMENT);
//            mPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task task) {
//            mPresenter.activateTask(task);
        }

        @Override
        public void onAddTaskButtonClick(int newActiveTaskPosition) {

            Task newTask = new Task(mTaskHeadId);
            newTask.setOrder(newActiveTaskPosition);
            mPresenter.addTask(newTask);
        }

        @Override
        public void onDescriptionFocusChanged(String description, String taskId, int taskOrder) {

            Log.d("kiwi_test", "Focus changed of task: " + description);
            Task editedTask = new Task(mTaskHeadId, taskId, description, taskOrder);
            mPresenter.editTask(editedTask);
        }

        @Override
        public void onDeleteTask(Task task) {
            mPresenter.deleteTask(task);
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
            mTaskHeadId = getArguments().getString(TaskHeadsActivity.EXTRA_TASKHEAD_ID);
            mTaskHeadTitle = getArguments().getString(TaskHeadsActivity.EXTRA_TASKHEAD_TITLE);
        }

        mTasksAdapter = new TasksAdapter(new ArrayList<Task>(0), mTaskHeadId, mTaskItemListener);
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

        // Set Toolbar
        ActionBar ab = ((TasksActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowTitleEnabled(false);
            // Set the actionbar to use the custom view
            View customActionBar = inflater.inflate(R.layout.tasks_toolbar, null);
            ab.setCustomView(customActionBar);
        }

        // Set Title
        mTitleView = (TextView) root.findViewById(R.id.title_tasklist);
        mTitleView.setText(mTaskHeadTitle);
        // Set ListView
        mTasksView = (DragSortListView) root.findViewById(R.id.task_list);
        mTasksView.setDropListener(mTasksAdapter);
        mTasksView.setAdapter(mTasksAdapter);

        return root;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showInvalidTaskHeadError() {


    }

    @Override
    public void showLoadingErrorTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    @Override
    public void showTaskHeadList(boolean isEmptyTaskHead) {

        Log.d("kiwi_test", "called showTaskHeadList");

        Intent intent = new Intent();
        intent.putExtra(TasksActivity.EXTRA_ISEMPTY_TASKHEAD, isEmptyTaskHead);
        intent.putExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID, mTaskHeadId);
        intent.putExtra(TaskHeadsActivity.EXTRA_TASKHEAD_TITLE, mTaskHeadTitle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mTasksAdapter.replaceData(tasks);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    public void onBackPressed() {

        boolean isEmptyTaskHead = mPresenter.validateEmptyTaskHead(mTaskHeadTitle, mTasksAdapter.getCount());
        showTaskHeadList(isEmptyTaskHead);
    }
}
