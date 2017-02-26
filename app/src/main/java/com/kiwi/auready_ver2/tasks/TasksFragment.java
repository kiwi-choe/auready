package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.view.DragSortController;
import com.kiwi.auready_ver2.util.view.DragSortListView;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    public static final String ARG_MEMBER_ID = "MEMBER_ID";
    public static final String ARG_MEMBER_NAME = "MEMBER_NAME";
    public static final String ARG_FRAGMENT_LISTENER = "FRAGMENT_LISTENER";

    private String mMemberId;
    private String mMemberName;

    private DragSortListView mNotCompleteListview;
    private DragSortListView mCompleteListview;

    private boolean isFirstLaunch = true;

    private static TasksFragmentPagerAdapter.TaskFragmentListener mTaskFragmentListener;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance(String memberId, String memberName,
                                            TasksFragmentPagerAdapter.TaskFragmentListener taskFragmentListener) {

        mTaskFragmentListener = taskFragmentListener;
        TasksFragment fragment = new TasksFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_MEMBER_ID, memberId);
        bundle.putString(ARG_MEMBER_NAME, memberName);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMemberId = getArguments().getString(ARG_MEMBER_ID);
            mMemberName = getArguments().getString(ARG_MEMBER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        // set listview
        mNotCompleteListview = (DragSortListView) root.findViewById(R.id.tasks_listview);
        setListView(mNotCompleteListview);

        mCompleteListview = (DragSortListView) root.findViewById(R.id.complete_tasks_listview);
        setListView(mCompleteListview);

        // add header view
        TextView memberText = (TextView) root.findViewById(R.id.member_name);
        memberText.setText(mMemberName);

        // set add button
        View view = root.findViewById(R.id.add_task_button_layout);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mNotCompleteListview.getInputAdapter().getCount();
                Task task = new Task(mMemberId, "new Item " + position, position);
                mTaskFragmentListener.onAddTaskButtonClicked(task);
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    private void setListView(final DragSortListView listView) {
        // Set ListView
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        TasksAdapter tasksAdapter = new TasksAdapter(getContext(), mTaskItemListener);
        listView.setAdapter(tasksAdapter);

        // for drag and drop
        DragSortController controller = buildController(listView);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDragEnabled(true);

        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDragEnabled(true);
        listView.setDropListener(new DragSortListView.DragSortListener() {
            @Override
            public void drag(int from, int to) {

            }

            @Override
            public void drop(int from, int to) {
                TasksAdapter tasksAdapter = (TasksAdapter) listView.getInputAdapter();
                tasksAdapter.reorder(from, to);
//                mPresenter.updateOrders(mTaskHeadsAdapter.getTaskHeads());
            }

            @Override
            public void remove(int which) {

            }
        });
    }

    private DragSortController buildController(DragSortListView list) {
        DragSortController controller = new DragSortController(list);
        controller.setDragHandleId(R.id.reorder_task);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);

        return controller;
    }

    @Override
    public void onPause() {
        super.onPause();

        ArrayList<Task> tasks = (ArrayList<Task>) getAllTasks();
        mTaskFragmentListener.onEditedTask(mMemberId, tasks);
    }

    private List<Task> getAllTasks(){

        ArrayList<Task> tasks = new ArrayList<>();

        TasksAdapter notCompleteAdapter = (TasksAdapter) mNotCompleteListview.getInputAdapter();
        tasks.addAll(notCompleteAdapter.getItems());

        TasksAdapter completeAdapter = (TasksAdapter) mCompleteListview.getInputAdapter();
        tasks.addAll(completeAdapter.getItems());

        return tasks;
    }

    public void showTasks(List<Task> tasks) {

        ArrayList<Task> completeTasks = new ArrayList<>();
        ArrayList<Task> notCompleteTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completeTasks.add(task);
            } else {
                notCompleteTasks.add(task);
            }
        }

        ((TasksAdapter) mNotCompleteListview.getInputAdapter()).updateTasks(notCompleteTasks);
        ((TasksAdapter) mCompleteListview.getInputAdapter()).updateTasks(completeTasks);

        if (isFirstLaunch) {
//            View splitView = getView().findViewById(R.id.split_view);
//            splitView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//            int splitViewHeight = splitView.getMeasuredHeight();
//            ViewGroup.LayoutParams params = mNotCompleteListview.getLayoutParams();
//            params.height = splitViewHeight;
//            mNotCompleteListview.setLayoutParams(params);
//            mNotCompleteListview.requestLayout();
        }

        mNotCompleteListview.smoothScrollToPosition(mNotCompleteListview.getInputAdapter().getCount());


//        if(ViewUtils.getListViewHeightBasedOnChildren(mNotCompleteListview) - 100 < splitViewHeight){
//            ViewGroup.LayoutParams params = mNotCompleteListview.getLayoutParams();
//            params.height = ViewUtils.getListViewHeightBasedOnChildren(mNotCompleteListview);
//            mNotCompleteListview.setLayoutParams(params);
//            mNotCompleteListview.requestLayout();
//        }
    }

    public void showNoTasks() {

    }

    public interface TaskItemListener {
        void onTaskDeleteButtonClicked(String memberId, String taskId);

        void onEditedTask(int position, boolean checked);
    }

    private TaskItemListener mTaskItemListener = new TaskItemListener() {
        @Override
        public void onTaskDeleteButtonClicked(String memberId, String taskId) {
            ArrayList<Task> tasks = (ArrayList<Task>) getAllTasks();

            mTaskFragmentListener.onEditedTask(memberId, tasks);
            mTaskFragmentListener.onTaskDeleteButtonClicked(memberId, taskId);
        }

        @Override
        public void onEditedTask(int position, boolean checked) {

            TasksAdapter notCompleteAdapter = (TasksAdapter) mNotCompleteListview.getInputAdapter();
            ArrayList<Task> notCompleteTasks = new ArrayList<>();
            notCompleteTasks.addAll(notCompleteAdapter.getItems());

            TasksAdapter completeAdapter = (TasksAdapter) mCompleteListview.getInputAdapter();
            ArrayList<Task> completeTasks = new ArrayList<>();
            completeTasks.addAll(completeAdapter.getItems());

            if (checked) {
                Task removedTask = notCompleteTasks.remove(position);
                completeTasks.add(removedTask);
            } else {
                Task removedTask = completeTasks.remove(position);
                notCompleteTasks.add(removedTask);
            }

            notCompleteAdapter.updateTasks(notCompleteTasks);
            completeAdapter.updateTasks(completeTasks);
        }
    };
}

