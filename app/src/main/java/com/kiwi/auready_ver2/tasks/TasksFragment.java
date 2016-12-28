package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";

    private TextView mTitleView;
    private ExpandableListView mTasksView;
    private TasksAdapter mTasksAdapter;

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
        }

        mTasksAdapter = new TasksAdapter(getContext(), new ArrayList<Friend>(), new HashMap<String, ArrayList<Task>>(), mTaskItemListener);
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

        // Set ListView
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        mTasksView = (ExpandableListView) root.findViewById(R.id.expand_listview);
        mTasksView.setAdapter(mTasksAdapter);
        setHasOptionsMenu(true);

        mTasksView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                return startActionMode(position);
            }
        });

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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(String title) {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setMembers(List<Friend> members) {
        mTasksAdapter.replaceMemberList(members);
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mTasksAdapter.replaceTasksList(tasks);
    }

    @Override
    public void showTasks(String memberId, List<Task> tasks) {
        mTasksAdapter.replaceTasksList(memberId, tasks);
    }

    @Override
    public void showNoTasks() {

    }

    TaskItemListener mTaskItemListener = new TaskItemListener() {
        @Override
        public void onTaskItemClick(String taskHeadId) {

        }

        @Override
        public void onDeleteClick(String taskId) {
            mPresenter.deleteTask(taskId);
        }

        @Override
        public void onAddTaskClick(String memberId, String description, int order) {
            mPresenter.createTask(memberId, description, order);

        }

        @Override
        public void onDeleteTasksClick(int memberPosition) {
            startActionMode(memberPosition);
        }
    };

    private boolean startActionMode(int memberPosition) {
        if (mActionMode != null) {
            return false;
        }

        mActionMode = getActivity().startActionMode(mActionModeCallback);
        mTasksAdapter.setActionModeMember(memberPosition);
        return true;
    }

    public interface TaskItemListener {
        void onTaskItemClick(String taskHeadId);

        void onDeleteClick(String taskId);

        void onAddTaskClick(String memberId, String description, int order);

        void onDeleteTasksClick(int memberPosition);

        void onTaskChecked(String taskId);

        void onTaskDescEdited(String taskId);
    }

    ActionMode mActionMode = null;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item_delete:
                    actionMode.finish();
                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mTasksAdapter.setActionModeMember(-1);
            mActionMode = null;
        }
    };
}
