package com.kiwi.auready_ver2.taskheads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.addedittask.AddEditTaskActivity;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadFragment extends Fragment implements TaskHeadContract.View {

    public static final String TAG_TASKSFRAGMENT = "TAG_TasksFragment";

    private TaskHeadContract.Presenter mPresenter;

    // interface
    private TasksFragmentListener mListener;
    private TaskHeadListAdapter mTaskHeadListAdapter;

    private LinearLayout mTaskHeadsView;
    private LinearLayout mNoTaskHeadsView;

    public TaskHeadFragment() {
        // Required empty public constructor
    }

    public static TaskHeadFragment newInstance() {

        return new TaskHeadFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskHeadListAdapter = new TaskHeadListAdapter(new ArrayList<TaskHead>(0), mItemListener);
    }

    /*
    * Listener for clicks on taskHeads in the ListView
    * */
    TaskHeadListAdapter.TaskHeadItemListener
            mItemListener = new TaskHeadListAdapter.TaskHeadItemListener() {
        @Override
        public void onTaskHeadClick(TaskHead clickedTaskHead) {
            mPresenter.openTask(clickedTaskHead);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TasksFragmentListener) {
            mListener = (TasksFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TasksFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void setPresenter(TaskHeadContract.Presenter tasksPresenter) {
        mPresenter = checkNotNull(tasksPresenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Set Floating button
        FloatingActionButton fb =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTask();
            }
        });

        // Set TaskHeadsView
        ListView taskHeadListView = (ListView) root.findViewById(R.id.taskhead_list);
        taskHeadListView.setAdapter(mTaskHeadListAdapter);
        mTaskHeadsView = (LinearLayout) root.findViewById(R.id.taskheads_view);
        mNoTaskHeadsView = (LinearLayout) root.findViewById(R.id.no_taskhead_view);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void setLoginSuccessUI() {

        if (mListener != null) {
            mListener.onLoginSuccess();
        }
    }

    @Override
    public void openAddEditTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQ_ADD_TASK);
    }

    @Override
    public void showTaskHeads(List<TaskHead> taskHeads) {
        mTaskHeadListAdapter.replaceData(taskHeads);

        mTaskHeadsView.setVisibility(View.VISIBLE);
        mNoTaskHeadsView.setVisibility(View.GONE);
    }

    @Override
    public void showNoTaskHeads() {

    }

    // Interface with TaskHeadActivity
    public interface TasksFragmentListener {
        void onLoginSuccess();
    }
}
