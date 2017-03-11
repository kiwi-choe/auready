package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.view.DragSortController;
import com.kiwi.auready_ver2.util.view.DragSortListView;
import com.kiwi.auready_ver2.util.view.SplitView;
import com.kiwi.auready_ver2.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    public static final String ARG_MEMBER_ID = "MEMBER_ID";
    public static final String ARG_MEMBER_NAME = "MEMBER_NAME";
    public static final String ARG_FRAGMENT_LISTENER = "FRAGMENT_LISTENER";

    private String mMemberId;
    private String mMemberName;

    private DragSortListView mUnCompleteListview;
    private DragSortListView mCompleteListview;
    private View mDummyView;
    private SplitView mSplitView;

    private boolean mIsFirstLaunch = true;
    private static TasksActivity.TaskViewListener mTaskViewListener;

    private final int NONE = 0;
    private final int ADD_TASK = 1;
    private final int EDIT_TASK = 2;
    private final int DELETE_TASK = 3;
    private int mLastOperation = NONE;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance(String memberId, String memberName,
                                            TasksActivity.TaskViewListener taskViewListener) {

        mTaskViewListener = taskViewListener;
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

        mSplitView = (SplitView) root.findViewById(R.id.split_view);

        // set listview
        mUnCompleteListview = (DragSortListView) root.findViewById(R.id.tasks_listview);
        setListView(mUnCompleteListview, new NotCompleteTasksAdapter(getContext(), mTaskItemListener));

        mCompleteListview = (DragSortListView) root.findViewById(R.id.complete_tasks_listview);
        setListView(mCompleteListview, new CompleteTasksAdapter(getContext(), mTaskItemListener));

        mDummyView = root.findViewById(R.id.dummy_view);

        // add header view
        TextView memberText = (TextView) root.findViewById(R.id.member_name);
        memberText.setText(mMemberName);

        // set add button
        View view = root.findViewById(R.id.add_task_button_layout);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo : There is a timing issue between Edit and Add
                saveAllEditedDataInMemory();

                int position = mUnCompleteListview.getInputAdapter().getCount();
                Task task = new Task(mMemberId, "new Item " + position, position);
                mTaskViewListener.onAddTaskButtonClicked(task);
                mLastOperation = ADD_TASK;
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    private void setListView(final DragSortListView listView, final TasksAdapter tasksAdapter) {
        // Set ListView
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

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
        saveAllEditedDataInMemory();
    }

    private void saveAllEditedDataInMemory() {
        ArrayList<Task> tasks = (ArrayList<Task>) getAllTasks();
        mTaskViewListener.onEditedTask(mMemberId, tasks);

        mLastOperation = EDIT_TASK;
    }

    private List<Task> getAllTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        TasksAdapter notCompleteAdapter = (TasksAdapter) mUnCompleteListview.getInputAdapter();
        tasks.addAll(notCompleteAdapter.getItems());

        TasksAdapter completeAdapter = (TasksAdapter) mCompleteListview.getInputAdapter();
        tasks.addAll(completeAdapter.getItems());

        return tasks;
    }

    public void showFilteredTasks(List<Task> completed, List<Task> uncompleted) {
        ((TasksAdapter) mUnCompleteListview.getInputAdapter()).updateTasks(completed);
        ((TasksAdapter) mCompleteListview.getInputAdapter()).updateTasks(uncompleted);

        // it needs to move splitview when there is no completed Item
        if (mCompleteListview.getInputAdapter().getCount() == 0) {
            mDummyView.setVisibility(View.VISIBLE);
        } else {
            mDummyView.setVisibility(View.GONE);
        }
    }

    public void showTasks(List<Task> tasks) {
//        ArrayList<Task> completeTasks = new ArrayList<>();
//        ArrayList<Task> notCompleteTasks = new ArrayList<>();
//
//        for (Task task : tasks) {
//            if (task.isCompleted()) {
//                if (task.getOrder() >= completeTasks.size()) {
//                    completeTasks.add(task);
//                } else {
//                    completeTasks.add(task.getOrder(), task);
//                }
//            } else {
//                if (task.getOrder() >= notCompleteTasks.size()) {
//                    notCompleteTasks.add(task);
//                } else {
//                    notCompleteTasks.add(task.getOrder(), task);
//                }
//            }
//        }
//
//        ((TasksAdapter) mUnCompleteListview.getInputAdapter()).updateTasks(notCompleteTasks);
//        ((TasksAdapter) mCompleteListview.getInputAdapter()).updateTasks(completeTasks);
//
//        // it needs to move splitview when there is no completed Item
//        if (mCompleteListview.getInputAdapter().getCount() == 0) {
//            mDummyView.setVisibility(View.VISIBLE);
//        } else {
//            mDummyView.setVisibility(View.GONE);
//        }

//        if (mIsFirstLaunch) {
//            mIsFirstLaunch = false;
//            getView().getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
//                @Override
//                public void onDraw() {
//                    getView().getViewTreeObserver().removeOnDrawListener(this);
//                    if (mSplitView.getPrimaryContentSize() <= ViewUtils.getListViewHeightBasedOnChildren(mUnCompleteListview)) {
//                        mSplitView.setPrimaryContentSize(ViewUtils.getListViewHeightBasedOnChildren(mUnCompleteListview));
//                    }
//                }
//            });
//        } else {
//            invalidateSplitViewToFitWithUpperView();
//            if (mLastOperation == ADD_TASK) {
//                mUnCompleteListview.smoothScrollToPosition(mUnCompleteListview.getInputAdapter().getCount());
//                mLastOperation = NONE;
//            }
//        }
    }

    public interface TaskItemListener {
        void onTaskDeleteButtonClicked(String memberId, String taskId);

        void onEditedTask(Task editedTask, boolean checked);
    }

    private TaskItemListener mTaskItemListener = new TaskItemListener() {
        @Override
        public void onTaskDeleteButtonClicked(String memberId, String taskId) {
            // remove focus
            getActivity().getCurrentFocus().clearFocus();

            // hide keyboard
            InputMethodManager im =
                    (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            // update data
            saveAllEditedDataInMemory();
            mTaskViewListener.onDeleteTaskButtonClicked(memberId, taskId);

            mLastOperation = DELETE_TASK;
        }

        @Override
        public void onEditedTask(Task editedTask, boolean checked) {

            if (editedTask == null) {
                return;
            }

            TasksAdapter completeAdapter = (TasksAdapter) mCompleteListview.getInputAdapter();
            TasksAdapter notCompleteAdapter = (TasksAdapter) mUnCompleteListview.getInputAdapter();

            if (checked) {
                completeAdapter.addItem(editedTask);
                mCompleteListview.smoothScrollToPosition(completeAdapter.getCount());
            } else {
                notCompleteAdapter.addItem(editedTask);
                mUnCompleteListview.smoothScrollToPosition(notCompleteAdapter.getCount());
            }

            invalidateSplitViewToFitWithUpperView();
        }
    };

    private void invalidateSplitViewToFitWithUpperView() {

        if (mSplitView.getPrimaryContentSize() >= ViewUtils.getListViewHeightBasedOnChildren(mUnCompleteListview)
                || !mSplitView.isDragged()) {
            mSplitView.setPrimaryContentSize(ViewUtils.getListViewHeightBasedOnChildren(mUnCompleteListview));
        }

        // it needs to move splitview when there is no completed Item
        if (mCompleteListview.getInputAdapter().getCount() == 0) {
            mDummyView.setVisibility(View.VISIBLE);
        } else {
            mDummyView.setVisibility(View.GONE);
        }
    }
}