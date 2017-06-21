package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.notification.NotificationActivity;
import com.kiwi.auready_ver2.notification.NotificationContract;
import com.kiwi.auready_ver2.settings.SettingsActivity;
import com.kiwi.auready_ver2.settings.SettingsFragment;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;
import com.kiwi.auready_ver2.tasks.TasksActivity;
import com.kiwi.auready_ver2.util.view.DragSortController;
import com.kiwi.auready_ver2.util.view.DragSortItemView;
import com.kiwi.auready_ver2.util.view.DragSortListView;
import com.kiwi.auready_ver2.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsFragment extends Fragment implements
        TaskHeadsContract.View,
        NotificationContract.MenuView {

    public static final String TAG_TASKHEADSFRAGMENT = "TAG_TaskHeadsFragment";

    private TaskHeadsContract.Presenter mPresenter;

    private TaskHeadsAdapter mTaskHeadsAdapter;

    private TextView mNoTaskHeadTxt;
    private DragSortListView mTaskHeadsView;
    private ActionMode mActionMode;

    // for noti menu
    private NotificationContract.MenuPresenter mNotificationPresenter;
    private int mNotificationsCount;

    private SwipeRefreshLayout mSwipleRefreshLayout;

    public TaskHeadsFragment() {
        // Required empty public constructor
    }

    public static TaskHeadsFragment newInstance() {

        return new TaskHeadsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mNotificationsCount = 0;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Destroy all menu and recall onCreateOptionsMenu
        getActivity().supportInvalidateOptionsMenu();
        mPresenter.start();
        mNotificationPresenter.getNotificationsCount();
    }

    @Override
    public void setPresenter(TaskHeadsContract.Presenter tasksPresenter) {
        mPresenter = checkNotNull(tasksPresenter);
    }

    @Override
    public void setMenuPresenter(NotificationContract.MenuPresenter presenter) {
        mNotificationPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_taskheads, container, false);
        setHasOptionsMenu(true);

        mNoTaskHeadTxt = (TextView) root.findViewById(R.id.no_taskhead_txt);
        mTaskHeadsView = (DragSortListView) root.findViewById(R.id.taskheads);
        mTaskHeadsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        mTaskHeadsView.setMultiChoiceModeListener(this);

//        View dummyFooterView = inflater.inflate(R.layout.task_head_dummy_view_for_padding, null);
////        mTaskHeadsView.addHeaderView(dummyFooterView);
//        mTaskHeadsView.addFooterView(dummyFooterView);

        mTaskHeadsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final TaskHead taskHead = mTaskHeadsAdapter.getItem(position);
                if (!mIsActionMode) {
                    showTasksView(taskHead.getId(), taskHead.getTitle(), taskHead.getColor());
                    return;
                }

                if (mActionMode == null) {
                    return;
                }

                if (position >= 0 && position < mTaskHeadsAdapter.getCount()) {
                    mTaskHeadsAdapter.toggleSelectedItem(position - mTaskHeadsView.getHeaderViewsCount());
                }

                mActionMode.setTitle(mTaskHeadsAdapter.getSelectedCount() + " " + getContext().getResources().getString(R.string.item_selected));
            }
        });

        mTaskHeadsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mIsActionMode) {
                    return false;
                }

                getActivity().startActionMode(mActionmodeCallback);
                mTaskHeadsAdapter.toggleSelectedItem(position - mTaskHeadsView.getHeaderViewsCount());
                mActionMode.setTitle(mTaskHeadsAdapter.getSelectedCount() + " " + getContext().getResources().getString(R.string.item_selected));
                return true;
            }
        });

        mController = buildController(mTaskHeadsView);
        mTaskHeadsView.setFloatViewManager(mController);
        mTaskHeadsView.setOnTouchListener(mController);
        mTaskHeadsView.setDragEnabled(true);

        mTaskHeadsAdapter = new TaskHeadsAdapter(new ArrayList<TaskHead>(0));
        mTaskHeadsView.setAdapter(mTaskHeadsAdapter);

        // Set swipeRefreshLayout
        mSwipleRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipleRefreshLayout.setColorSchemeResources(R.color.color_scheme_1_1, R.color.color_scheme_1_2,
                R.color.color_scheme_1_3, R.color.color_scheme_1_4);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_taskhead);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTaskHead();
            }
        });

        mTaskHeadsView.setDropListener(mDropListener);

        // SwipeRefreshLayout
        mSwipleRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTaskHeads(true);
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem no_notificationItem = menu.findItem(R.id.item_no_notification);
        MenuItem notificationItem = menu.findItem(R.id.item_notifications);

        // if there are new notifications,
        if (mNotificationsCount > 0) {
            no_notificationItem.setVisible(false);
            notificationItem.setVisible(true);
        } else {    // no notification,
            notificationItem.setVisible(false);
            no_notificationItem.setVisible(true);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_menu:
                // start Action mode to delete items
                getActivity().startActionMode(mActionmodeCallback);
                break;

            case R.id.item_notifications:
                showNotificationsView();
                break;

            case R.id.item_settings:
                showSettingsView();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsView() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        getActivity().startActivityForResult(intent, TaskHeadsActivity.REQ_SETTINGS);
    }

    @Override
    public void showTaskHeads(List<TaskHead> taskHeads) {
        setRefreshing(false);

        mTaskHeadsAdapter.replaceData(taskHeads);

        mNoTaskHeadTxt.setVisibility(View.GONE);
        mTaskHeadsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoTaskHeads() {
        setRefreshing(false);

        mTaskHeadsView.setVisibility(View.GONE);
        mNoTaskHeadTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTaskHeadDetail(int cntOfTaskHeads) {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        Intent intent = new Intent(getContext(), TaskHeadDetailActivity.class);
        intent.putExtra(TaskHeadDetailActivity.ARG_CNT_OF_TASKHEADS, cntOfTaskHeads);
        getActivity().startActivityForResult(intent, TaskHeadsActivity.REQ_ADD_TASKHEAD);
    }

    @Override
    public void showTasksView(String taskHeadId, String title, int color) {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        Intent intent = new Intent(getContext(), TasksActivity.class);
        intent.putExtra(TasksActivity.ARG_TASKHEAD_ID, taskHeadId);
        intent.putExtra(TasksActivity.ARG_TITLE, title);
        intent.putExtra(TasksActivity.ARG_TASKHEAD_COLOR, color);
        startActivity(intent);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if(mSwipleRefreshLayout.isRefreshing()) {
            mSwipleRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void showAccountView() {
        Intent intent = getActivity().getIntent();
        intent.putExtra(SettingsFragment.EXTRA_LOGOUT, true);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showNotificationSign(int numOfNewNotifications) {
        mNotificationsCount = numOfNewNotifications;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void showNoNotificationSign() {
        mNotificationsCount = 0;
        getActivity().invalidateOptionsMenu();
    }

    // For Action Mode(CHOICE_MODE_MULTIPLE_MODAL)
    private boolean mIsActionMode = false;


    void startAnimation(final boolean isDelete) {
        if (!mTaskHeadsView.getViewTreeObserver().isAlive()) {
            return;
        }

        mTaskHeadsView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mTaskHeadsView.getViewTreeObserver().removeOnPreDrawListener(this);

                int count = mTaskHeadsView.getChildCount();
                for (int i = 0; i < count; i++) {
                    // adapter view of taskhead set as child of DragSortItemView
                    DragSortItemView childView = (DragSortItemView) mTaskHeadsView.getChildAt(i);
                    mTaskHeadsAdapter.startAnimation(childView.getChildAt(0),
                            isDelete, ViewUtils.ANIMATION_DURATION, ViewUtils.INTERPOLATOR);
                }

                return true;
            }
        });
    }

    private ActionMode.Callback mActionmodeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mActionMode = actionMode;
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu_delete, menu);

            startAnimation(true);
            mIsActionMode = true;
            mTaskHeadsAdapter.setActionMode(mIsActionMode);

            mActionMode.setTitle(mTaskHeadsAdapter.getSelectedCount() + " " + getContext().getResources().getString(R.string.item_selected));

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
                    mPresenter.deleteTaskHeads(mTaskHeadsAdapter.getCurrentCheckedTaskHeads());
                    actionMode.finish();
                    return true;

                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            mTaskHeadsAdapter.clearSelection();

            startAnimation(false);
            mIsActionMode = false;
            mTaskHeadsAdapter.setActionMode(mIsActionMode);
        }
    };

    private void showNotificationsView() {

        Intent intent = new Intent(getContext(), NotificationActivity.class);
        startActivity(intent);
    }

    private DragSortListView.DropListener mDropListener =
            new DragSortListView.DragSortListener() {
                @Override
                public void drag(int from, int to) {
                }

                @Override
                public void drop(int from, int to) {
                    mTaskHeadsAdapter.reorder(from, to);
                    mPresenter.updateOrders(mTaskHeadsAdapter.getTaskHeads());
                }

                @Override
                public void remove(int which) {
                }
            };

    private DragSortController mController;

    public DragSortController buildController(DragSortListView list) {
        DragSortController controller = new DragSortController(list);
        controller.setDragHandleId(R.id.reorder);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DOWN);

        return controller;
    }
}
