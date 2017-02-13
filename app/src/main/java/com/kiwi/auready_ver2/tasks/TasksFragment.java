package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;
import com.kiwi.auready_ver2.util.view.ColorPickerDialog;
import com.kiwi.auready_ver2.util.view.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";
    public static final int REQ_EDIT_TASKHEAD = 0;

    private String mTaskHeadId;
    private String mTaskHeadTitle;

    private TasksContract.Presenter mPresenter;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTaskHeadId = getArguments().getString(TasksActivity.ARG_TASKHEAD_ID);
            mTaskHeadTitle = getArguments().getString(TasksActivity.ARG_TITLE);
        }
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Set title
        setTitle(mTaskHeadTitle);

        // Set ListView
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.taskhead_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_menu:
                showTaskHeadDetail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTaskHeadDetail() {
        Intent intent = new Intent(getContext(), TaskHeadDetailActivity.class);
        intent.putExtra(TaskHeadDetailActivity.ARG_TASKHEAD_ID, mTaskHeadId);
        startActivityForResult(intent, REQ_EDIT_TASKHEAD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(String titleOfTaskHead) {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mTaskHeadTitle);
        }
    }

    @Override
    public void showMembers(List<Member> members) {
    }

    @Override
    public void showTasks(List<Task> tasks) {
    }

    @Override
    public void showTasks(String memberId, List<Task> tasks) {
    }

    @Override
    public void showNoTasks() {
    }

    @Override
    public void scrollToAddButton() {
    }

    TaskItemListener mTaskItemListener = new TaskItemListener() {

        @Override
        public void onAddTaskClick(String memberId, String description, int order) {
            mPresenter.createTask(memberId, description, order);
        }

        @Override
        public void onClickColorPicker(final View memberView, final int memberPosition) {
            ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
            int[] colors = memberView.getResources().getIntArray(R.array.color_picker);
            final int numColumns = 5;
            colorPickerDialog.initialize(R.string.color_picker_default_title,
                    colors, colors[0], numColumns, ColorPickerDialog.SIZE_SMALL);

            colorPickerDialog.show(getFragmentManager(), "colorPickerDialog");

            colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                @Override
                public void onColorSelected(int color) {
                }
            });
        }

        @Override
        public boolean onEnterKeyClicked(int memberPosition, int taskPosition) {
            return true;
        }

        @Override
        public void onDeleteTaskClick(String taskId) {
            ArrayList<String> deletedTaskId = new ArrayList<>();
            deletedTaskId.add(taskId);
            mPresenter.deleteTasks(deletedTaskId);
        }

        @Override
        public void onTaskDescEdited(String taskId) {

        }
    };

    public interface TaskItemListener {

        void onAddTaskClick(String memberId, String description, int order);

        void onDeleteTaskClick(String taskId);

        void onClickColorPicker(final View memberView, final int memberPosition);

        boolean onEnterKeyClicked(int memberPosition, int taskPosition);

        void onTaskDescEdited(String taskId);
    }
}
