package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";

    private TextView mNoTasksView;
    private ListView mActiveTaskListView;
    private ListView mCompleteTaskListView;
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

        // Set No task message TextView
        mNoTasksView = (TextView) root.findViewById(R.id.no_tasks_view);

        // Set ListView
        mActiveTaskListView = (ListView) root.findViewById(R.id.active_task_list);
        mActiveTaskListView.setAdapter(mActiveTasksAdapter);
        mCompleteTaskListView = (ListView) root.findViewById(R.id.completed_task_list);
        mCompleteTaskListView.setAdapter(mCompletedTasksAdapter);
        // Set dynamic height for ListViews
        setDynamicHeight(mActiveTaskListView);
        setDynamicHeight(mCompleteTaskListView);

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

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    /*
    * Set listview height based on listview children
    * */
    private void setDynamicHeight(ListView listView) {

        ListAdapter adapter = listView.getAdapter();
        // Check adapter if null
        if(adapter == null) {
            return;
        }

        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for(int i =0; i<adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showEmptyTasksError() {

        mActiveTaskListView.setVisibility(View.GONE);
        mCompleteTaskListView.setVisibility(View.GONE);
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

        mActiveTaskListView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
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
