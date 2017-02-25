package com.kiwi.auready_ver2.tasks;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.view.DragSortController;
import com.kiwi.auready_ver2.util.view.DragSortListView;

import java.util.List;

public class TasksFragment extends Fragment {

    public static final String ARG_MEMBER_ID = "MEMBER_ID";
    public static final String ARG_MEMBER_NAME = "MEMBER_NAME";
    public static final String ARG_FRAGMENT_LISTENER = "FRAGMENT_LISTENER";

    private String mMemberId;
    private String mMemberName;

    private DragSortListView mListview;
    private TasksAdapter mTasksAdapter;

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

        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        setListView(root);

        // add header view
        TextView memberText = (TextView) root.findViewById(R.id.member_name);
        memberText.setText(mMemberName);

        setHasOptionsMenu(true);
        return root;
    }

    private void setListView(View root) {
        // Set ListView
        mListview = (DragSortListView) root.findViewById(R.id.tasks_listview);
        mListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        mTasksAdapter = new TasksAdapter(getContext(), mTaskItemListener);
        mListview.setAdapter(mTasksAdapter);

        // for drag and drop
        mListview.setFloatViewManager(mController);
        mListview.setOnTouchListener(mController);
        mListview.setDragEnabled(true);

        mController = buildController(mListview);
        mListview.setFloatViewManager(mController);
        mListview.setOnTouchListener(mController);
        mListview.setDragEnabled(true);
        mListview.setDropListener(mDropListener);
    }

    private DragSortListView.DropListener mDropListener =
            new DragSortListView.DragSortListener() {
                @Override
                public void drag(int from, int to) {
                }

                @Override
                public void drop(int from, int to) {
                    mTasksAdapter.reorder(from, to);
//                    mPresenter.updateOrders(mTaskHeadsAdapter.getTaskHeads());
                }

                @Override
                public void remove(int which) {
                }
            };

    private DragSortController mController;

    private DragSortController buildController(DragSortListView list) {
        DragSortController controller = new SectionController(list, mTasksAdapter);
        controller.setDragHandleId(R.id.reorder_task);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);

        return controller;
    }

    public void showTasks(List<Task> tasks) {
        mListview.setVisibility(View.VISIBLE);
        mTasksAdapter.updateTasks(tasks);
    }

    interface TaskItemListener {
        void OnAddTaskButtonClicked(int position);

        void onDeleteTaskButtonClicked(String taskId);

        void onEditedTask(String taskId, int order, boolean checked, String text);
    }

    private TaskItemListener mTaskItemListener = new TaskItemListener() {
        @Override
        public void OnAddTaskButtonClicked(int position) {
            Task task = new Task(mMemberId, "new Item " + position, position);
            mTaskViewListener.onAddTaskButtonClicked(task);
        }

        @Override
        public void onDeleteTaskButtonClicked(String taskId) {
            mTaskViewListener.onDeleteTaskButtonClicked(taskId);
        }

        @Override
        public void onEditedTask(String taskId, int order, boolean checked, String text) {
            Task task = new Task(taskId, mMemberId, text, checked, order);
            mTaskViewListener.onEditedTask(task);
        }
    };

    private class SectionController extends DragSortController {

        private int mPos;

        private TasksAdapter mAdapter;

        DragSortListView mDslv;

        public SectionController(DragSortListView dslv, TasksAdapter adapter) {
            super(dslv, R.id.text, DragSortController.ON_DOWN, 0);
            setRemoveEnabled(false);
            mDslv = dslv;
            mAdapter = adapter;
        }

        @Override
        public int startDragPosition(MotionEvent ev) {
            int res = super.dragHandleHitPosition(ev);
            if (res == mAdapter.getAddButtonPosition()) {
                return DragSortController.MISS;
            }

            int width = mDslv.getWidth();

            if ((int) ev.getX() < width / 3) {
                return res;
            } else {
                return DragSortController.MISS;
            }
        }

        @Override
        public View onCreateFloatView(int position) {
            mPos = position;

            View v = mAdapter.getView(position, null, mDslv);
//            if (position < mDivPos) {
//                v.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_handle_section1));
//            } else {
//                v.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_handle_section2));
//            }
            v.getBackground().setLevel(10000);
            return v;
        }

        private int origHeight = -1;

        @Override
        public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
            final int first = mDslv.getFirstVisiblePosition();
            final int lvDivHeight = mDslv.getDividerHeight();

            if (origHeight == -1) {
                origHeight = floatView.getHeight();
            }

            View div = mDslv.getChildAt(mAdapter.getAddButtonPosition() - first);

            if (touchPoint.x > mDslv.getWidth() / 2) {
                float scale = touchPoint.x - mDslv.getWidth() / 2;
                scale /= (float) (mDslv.getWidth() / 5);
                ViewGroup.LayoutParams lp = floatView.getLayoutParams();
                lp.height = Math.max(origHeight, (int) (scale * origHeight));
                floatView.setLayoutParams(lp);
            }

            if (div != null) {
                if (mPos > mAdapter.getAddButtonPosition()) {
                    // don't allow floating View to go above
                    // section divider
                    final int limit = div.getBottom() + lvDivHeight;
                    if (floatPoint.y < limit) {
                        floatPoint.y = limit;
                    }
                } else {
                    // don't allow floating View to go below
                    // section divider
                    final int limit = div.getTop() - lvDivHeight - floatView.getHeight();
                    if (floatPoint.y > limit) {
                        floatPoint.y = limit;
                    }
                }
            }
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }

    }
}

