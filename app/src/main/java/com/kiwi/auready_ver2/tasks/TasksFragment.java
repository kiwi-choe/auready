package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;

import java.util.ArrayList;
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
        if(getArguments() != null) {
            mTaskHeadId = getArguments().getString(TaskHeadsActivity.EXTRA_TASKHEAD_ID);
        }

        ArrayList<String> groupList = new ArrayList<>();
        ArrayList<ArrayList<String>> childList= new ArrayList<ArrayList<String>>();
        ArrayList<String> childContents = new ArrayList<>();

        groupList.add("member 1");
        groupList.add("member 2");
        groupList.add("member 3");

        childContents.add("task 1");
        childContents.add("task 2");
        childContents.add("task 3");

        childList.add(childContents);
        childList.add(childContents);
        childList.add(childContents);

        mTasksAdapter = new TasksAdapter(getContext(), groupList, childList);
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

        // Set Title
        mTitleView = (TextView) root.findViewById(R.id.tasks_title);
        mTitleView.setText(mTaskHeadTitle);
        // Set ListView
        mTasksView = (ExpandableListView) root.findViewById(R.id.expand_listview);
        mTasksView.setAdapter(mTasksAdapter);

        mTasksView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(getContext(), "onGroupClick", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        return root;
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setMembers(List<Friend> members) {

    }

    @Override
    public void showTasks(List<Task> tasks) {

    }

    @Override
    public void showNoTasks() {

    }
}
