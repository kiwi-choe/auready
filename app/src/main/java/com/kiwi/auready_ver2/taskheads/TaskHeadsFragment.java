package com.kiwi.auready_ver2.taskheads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.customlistview.DragSortListView;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheads.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.tasks.TasksActivity;
import com.kiwi.auready_ver2.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsFragment extends Fragment implements TaskHeadsContract.View {

    public static final String TAG_TASKSFRAGMENT = "TAG_TasksFragment";

    private TaskHeadsContract.Presenter mPresenter;

    // interface
    private TasksFragmentListener mListener;
    private TaskHeadsAdapter mTaskHeadsAdapter;

    private TextView mNoTaskHeadTxt;
    private DragSortListView mTaskHeadListView;

    public TaskHeadsFragment() {
        // Required empty public constructor
    }

    public static TaskHeadsFragment newInstance() {

        return new TaskHeadsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskHeadsAdapter = new TaskHeadsAdapter(new ArrayList<TaskHead>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Destroy all menu and recall onCreateOptionsMenu
        getActivity().supportInvalidateOptionsMenu();
        mPresenter.start();
    }

    /*
        * Listener for clicks on taskHeads in the ListView
        * */
    TaskHeadsAdapter.TaskHeadItemListener
            mItemListener = new TaskHeadsAdapter.TaskHeadItemListener() {

        @Override
        public void onItemLongClick(TaskHead taskHead) {
            checkNotNull(taskHead);
            mPresenter.deleteTaskHead(taskHead.getId());
        }

        @Override
        public void onItemClick(TaskHead taskHead) {
            checkNotNull(taskHead);
//            mPresenter.editTaskHead(taskHead);
            openTasks(taskHead);
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
    public void setPresenter(TaskHeadsContract.Presenter tasksPresenter) {
        mPresenter = checkNotNull(tasksPresenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_taskheads, container, false);

        // Set ActionBar
        ActionBar ab = ((TaskHeadsActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Set AddTaskHead button
        Button addTaskHeadBt = (Button) root.findViewById(R.id.add_taskhead_bt);
        addTaskHeadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaskHeadSetting();
            }
        });

        // Set TaskHeadsView
        mTaskHeadListView = (DragSortListView) root.findViewById(R.id.taskhead_list);
        mTaskHeadListView.setAdapter(mTaskHeadsAdapter);
        mTaskHeadListView.setDropListener(mTaskHeadsAdapter);

        // Set no taskHead textview
        mNoTaskHeadTxt = (TextView) root.findViewById(R.id.no_taskhead_txt);

        // FIXME: 11/3/16 Temp button to show tasks view
        Button btTemp = (Button) root.findViewById(R.id.temp_show_tasksview_bt);
        btTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskHead taskHead = new TaskHead("temp taskhead title");
                openTasks(taskHead);
            }
        });
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
    public void openTasks(@NonNull TaskHead taskHead) {
        checkNotNull(taskHead);
        Intent intent = new Intent(getContext(), TasksActivity.class);
        intent.putExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID, taskHead.getId());
        intent.putExtra(TaskHeadsActivity.EXTRA_TASKHEAD_TITLE, taskHead.getTitle());
        startActivityForResult(intent, TaskHeadsActivity.REQ_ADD_TASK);
    }

    @Override
    public void showTaskHeadDeleted() {

        // Reload taskHeads after deleted
//        mPresenter.loadTaskHeads();
    }

    @Override
    public void showTaskHeads(List<TaskHead> taskHeads) {
        mNoTaskHeadTxt.setVisibility(View.GONE);
        mTaskHeadListView.setVisibility(View.VISIBLE);
        mTaskHeadsAdapter.replaceData(taskHeads);
    }

    @Override
    public void showNoTaskHeads() {
        mTaskHeadListView.setVisibility(View.GONE);
        mNoTaskHeadTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyTaskHeadError() {

        Snackbar.make(getView(), getString(R.string.taskhead_empty_err), Snackbar.LENGTH_LONG).show();
    }

    private void openTaskHeadSetting() {
        TaskHeadDetailFragment taskHeadDetailFragment = TaskHeadDetailFragment.newInstance();
        ActivityUtils.replaceFragment(getFragmentManager(),
                taskHeadDetailFragment, R.id.content_frame, TaskHeadDetailFragment.TAG_TASKHEADSETTING);
    }

    // Interface with TaskHeadsActivity
    public interface TasksFragmentListener {
        void onLoginSuccess();
    }
}
