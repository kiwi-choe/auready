package com.kiwi.auready_ver2.tasks;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.view.DragSortController;
import com.kiwi.auready_ver2.util.view.DragSortItemView;
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
    private SplitView mSplitView;

    private ImageView mNoIteImageView;
    private ImageView mCompleteImageView;

    private static TasksActivity.TaskViewListener mTaskViewListener;

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

        final View root = inflater.inflate(R.layout.fragment_tasks, container, false);


        mSplitView = (SplitView) root.findViewById(R.id.split_view);
        if (root.getViewTreeObserver().isAlive()) {
            root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    root.getViewTreeObserver().removeOnPreDrawListener(this);

                    mSplitView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int splitViewMaxHeight = getDisplayContentHeight()
                            - mSplitView.getMeasuredHeight()
                            - getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin) * 2;

                    mSplitView.setMaxHeight(splitViewMaxHeight);

                    return true;
                }
            });
        }

        // set listView
        mUnCompleteListview = (DragSortListView) root.findViewById(R.id.tasks_listview);
        setListView(mUnCompleteListview, new NotCompleteTasksAdapter(getContext(), mTaskItemListener));

        mCompleteListview = (DragSortListView) root.findViewById(R.id.complete_tasks_listview);
        setListView(mCompleteListview, new CompleteTasksAdapter(getContext(), mTaskItemListener));

        mNoIteImageView = (ImageView) root.findViewById(R.id.no_tasks);
        mCompleteImageView = (ImageView) root.findViewById(R.id.complete_tasks);

        // add header view
        TextView memberText = (TextView) root.findViewById(R.id.member_name);
        memberText.setText(mMemberName);

        // set task add button
        TextView taskAddButton = (TextView) root.findViewById(R.id.add_task_btn);
        taskAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mUnCompleteListview.getInputAdapter().getCount();
                Task task = new Task(mMemberId, "new Item " + position, position);

                // Add Item to TasksAdapter
                TasksAdapter unCompleteAdapter = (TasksAdapter) mUnCompleteListview.getInputAdapter();
                unCompleteAdapter.addItem(task);

                mTaskViewListener.onAddTaskButtonClicked(task);
                mTaskViewListener.onEditTasksOfMember(mMemberId, getAllTasks());
            }
        });

        // set task delete button
        final TextView taskDeleteButton = (TextView) root.findViewById(R.id.delete_task_btn);
        taskDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeleteAnimation(mUnCompleteListview);
                startDeleteAnimation(mCompleteListview);

                if (!(v instanceof TextView)) {
                    return;
                }

                if (((TextView) v).getPaintFlags() == Paint.FAKE_BOLD_TEXT_FLAG) {
                    taskDeleteButton.setPaintFlags(0);
                } else {
                    taskDeleteButton.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
                }
            }
        });

        // set task reorder button
        final TextView taskReorderButton = (TextView) root.findViewById(R.id.reorder_task_btn);
        taskReorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReorderAnimation(mUnCompleteListview);
                startReorderAnimation(mCompleteListview);

                if (!(v instanceof TextView)) {
                    return;
                }

                if (((TextView) v).getPaintFlags() == Paint.FAKE_BOLD_TEXT_FLAG) {
                    taskReorderButton.setPaintFlags(0);
                } else {
                    taskReorderButton.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
                }
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    private void startReorderAnimation(final DragSortListView listView) {
        if (!listView.getViewTreeObserver().isAlive()) {
            return;
        }

        TasksAdapter tasksAdapter = (TasksAdapter) listView.getInputAdapter();
        tasksAdapter.toggleReorderDisplayed();

        listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                listView.getViewTreeObserver().removeOnPreDrawListener(this);

                int count = listView.getChildCount();
                for (int i = 0; i < count; i++) {
                    DragSortItemView childView = (DragSortItemView) listView.getChildAt(i);
                    TasksAdapter tasksAdapter = (TasksAdapter) listView.getInputAdapter();
                    tasksAdapter.startReorderAnimation(
                            childView.getChildAt(0),
                            ViewUtils.ANIMATION_DURATION,
                            ViewUtils.INTERPOLATOR);
                }

                return true;
            }
        });

        listView.invalidate();
    }

    private void startDeleteAnimation(final DragSortListView listView) {
        if (!listView.getViewTreeObserver().isAlive()) {
            return;
        }

        TasksAdapter tasksAdapter = (TasksAdapter) listView.getInputAdapter();
        tasksAdapter.toggleDeleteDisplayed();

        listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                listView.getViewTreeObserver().removeOnPreDrawListener(this);

                int count = listView.getChildCount();
                for (int i = 0; i < count; i++) {
                    DragSortItemView childView = (DragSortItemView) listView.getChildAt(i);
                    TasksAdapter tasksAdapter = (TasksAdapter) listView.getInputAdapter();
                    tasksAdapter.starDeleteAnimation(
                            childView.getChildAt(0),
                            ViewUtils.ANIMATION_DURATION,
                            ViewUtils.INTERPOLATOR);
                }

                return true;
            }
        });

        listView.invalidate();
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
//                mPresenter.updateOrders(mTaskHeadsAdapter.getTaskHeadDetails());
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

        updateTasksInMemory();
    }

    private void updateTasksInMemory() {
        ArrayList<Task> tasks = (ArrayList<Task>) getAllTasks();
        mTaskViewListener.onUpdateTasksInMemory(mMemberId, tasks);
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
        if (mUnCompleteListview == null || mCompleteListview == null ||
                mUnCompleteListview.getInputAdapter() == null || mCompleteListview.getInputAdapter() == null) {
            return;
        }

        controlTasksVisibility(uncompleted.size(), completed.size());

        ((TasksAdapter) mUnCompleteListview.getInputAdapter()).updateTasks(uncompleted);
        ((TasksAdapter) mCompleteListview.getInputAdapter()).updateTasks(completed);

        invalidateSplitView();
    }

    private void controlTasksVisibility(int unCompletedTaskCount, int completedTaskCount) {
        if (mUnCompleteListview == null || mCompleteImageView == null) {
            return;
        }

        if (unCompletedTaskCount == 0 && completedTaskCount == 0) {
            mUnCompleteListview.setVisibility(View.GONE);
            mCompleteImageView.setVisibility(View.GONE);
            mNoIteImageView.setVisibility(View.VISIBLE);
        } else if (unCompletedTaskCount == 0) {
            mUnCompleteListview.setVisibility(View.GONE);
            mCompleteImageView.setVisibility(View.VISIBLE);
            mNoIteImageView.setVisibility(View.GONE);
        } else {
            mUnCompleteListview.setVisibility(View.VISIBLE);
            mCompleteImageView.setVisibility(View.GONE);
            mNoIteImageView.setVisibility(View.GONE);
        }

        invalidateSplitView();
    }

    public void showNoTask() {
        if (mUnCompleteListview == null || mCompleteImageView == null) {
            return;
        }

        controlTasksVisibility(0, 0);

        ((TasksAdapter) mUnCompleteListview.getInputAdapter()).clearTasks();
        ((TasksAdapter) mCompleteListview.getInputAdapter()).clearTasks();
    }

    public interface TaskItemListener {
        void onTaskDeleteButtonClicked(String memberId, String taskId);

        void onChangeComplete(Task editedTask, boolean checked);
    }

    private TaskItemListener mTaskItemListener = new TaskItemListener() {
        @Override
        public void onTaskDeleteButtonClicked(String memberId, String taskId) {
            // remove focus
            if(getActivity().getCurrentFocus()!= null) {
                getActivity().getCurrentFocus().clearFocus();
            }

            // hide keyboard
            InputMethodManager im =
                    (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

            mTaskViewListener.onDeleteTaskButtonClicked(memberId, taskId);
            mTaskViewListener.onEditTasksOfMember(memberId, getAllTasks());
        }

        @Override
        public void onChangeComplete(Task editedTask, boolean checked) {
            if (editedTask == null) {
                return;
            }

            TasksAdapter completeAdapter = (TasksAdapter) mCompleteListview.getInputAdapter();
            TasksAdapter unCompleteAdapter = (TasksAdapter) mUnCompleteListview.getInputAdapter();

            if (checked) {
                completeAdapter.addItem(editedTask);
                mCompleteListview.smoothScrollToPosition(completeAdapter.getCount());
            } else {
                unCompleteAdapter.addItem(editedTask);
                mUnCompleteListview.smoothScrollToPosition(unCompleteAdapter.getCount());
            }

            controlTasksVisibility(unCompleteAdapter.getCount(), completeAdapter.getCount());

            mTaskViewListener.onChangeComplete(editedTask);
            mTaskViewListener.onEditTasksOfMember(editedTask.getMemberId(), getAllTasks());
//            invalidateSplitView();
        }
    };

    private void invalidateSplitView() {
        mSplitView.updateContentView();
    }

    public int getDisplayContentHeight() {
        // Calculate ActionBar's height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        // Calculate StatusBar's height
        Rect rectangle = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        // Calculate Screen's height
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        return screenHeight - statusBarHeight - actionBarHeight;
    }
}