package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.customlistview.DragSortListView;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";

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
            mPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task task) {
            mPresenter.activateTask(task);
        }

        @Override
        public void onAddTaskButtonClick() {

            Task newTask = new Task(mTaskHeadId);
            mPresenter.addTask(newTask);
        }

        @Override
        public void onDescriptionFocusChanged(String description, String taskId) {

            Log.d("kiwi_test", "Focus changed of task: " + description);
            Task editedTask = new Task(mTaskHeadId, taskId, description);
            mPresenter.editTask(editedTask);
        }

        @Override
        public void onDeleteTask(String taskId) {
            mPresenter.deleteTask(taskId);
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

        // Set ListView
        mTasksView = (DragSortListView) root.findViewById(R.id.task_list);
        mTasksView.setDropListener(mTasksAdapter);
        mTasksView.setAdapter(mTasksAdapter);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_edit_title:
                editTitle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editTitle() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.edit_title));
        final EditText titleEditText = new EditText(getActivity());
        alert.setView(titleEditText);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = titleEditText.getText().toString();
                ((TasksActivity)getActivity()).setActionBarTitle(value);

                mTaskHeadTitle = value;
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.show();
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
