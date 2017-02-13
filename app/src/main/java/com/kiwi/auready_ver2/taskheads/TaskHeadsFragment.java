package com.kiwi.auready_ver2.taskheads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;
import com.kiwi.auready_ver2.tasks.TasksActivity;
import com.kiwi.auready_ver2.util.view.DragSortController;
import com.kiwi.auready_ver2.util.view.DragSortItemView;
import com.kiwi.auready_ver2.util.view.DragSortListView;
import com.kiwi.auready_ver2.util.view.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsFragment extends Fragment implements TaskHeadsContract.View, AbsListView.MultiChoiceModeListener {

    public static final String TAG_TASKHEADSFRAGMENT = "TAG_TasksFragment";

    private TaskHeadsContract.Presenter mPresenter;

    // interface
    private TaskHeadsFragmentListener mListener;
    private TaskHeadsAdapter mTaskHeadsAdapter;

    private TextView mNoTaskHeadTxt;
    private DragSortListView mTaskHeadsView;
    private ActionMode mActionMode;

    public TaskHeadsFragment() {
        // Required empty public constructor
    }

    public static TaskHeadsFragment newInstance() {

        return new TaskHeadsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Destroy all menu and recall onCreateOptionsMenu
//        getActivity().supportInvalidateOptionsMenu();
        mPresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskHeadsFragmentListener) {
            mListener = (TaskHeadsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskHeadsFragmentListener");
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
        setHasOptionsMenu(true);

        mNoTaskHeadTxt = (TextView) root.findViewById(R.id.no_taskhead_txt);
        mTaskHeadsView = (DragSortListView) root.findViewById(R.id.taskheads);
        mTaskHeadsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mTaskHeadsView.setMultiChoiceModeListener(this);

//        View dummyFooterView = inflater.inflate(R.layout.task_head_dummy_view_for_padding, null);
////        mTaskHeadsView.addHeaderView(dummyFooterView);
//        mTaskHeadsView.addFooterView(dummyFooterView);

        mTaskHeadsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final TaskHead taskHead = mTaskHeadsAdapter.getItem(position);
                if (!mIsActionMode) {
                    showTasksView(taskHead.getId(), taskHead.getTitle());
                }
            }
        });

        mTaskHeadsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                mTaskHeadsView.setItemChecked(position, true);
                return true;
            }
        });

        mController = buildController(mTaskHeadsView);
        mTaskHeadsView.setFloatViewManager(mController);
        mTaskHeadsView.setOnTouchListener(mController);
        mTaskHeadsView.setDragEnabled(true);

        mTaskHeadsAdapter = new TaskHeadsAdapter(new ArrayList<TaskHead>(0));
        mTaskHeadsView.setAdapter(mTaskHeadsAdapter);

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
                // start Action mode to delete items
                mTaskHeadsView.setItemChecked(-1, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLoginSuccessUI() {

        if (mListener != null) {
            mListener.onLoginSuccess();
        }
    }

    @Override
    public void showTaskHeads(List<TaskHead> taskHeads) {
        mTaskHeadsAdapter.replaceData(taskHeads);

        mNoTaskHeadTxt.setVisibility(View.GONE);
        mTaskHeadsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoTaskHeads() {
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
        startActivityForResult(intent, TaskHeadsActivity.REQ_ADD_TASKHEAD);
    }

    @Override
    public void showTasksView(String taskHeadId, String title) {
        if (mActionMode != null) {
            mActionMode.finish();
        }

        Intent intent = new Intent(getContext(), TasksActivity.class);
        intent.putExtra(TasksActivity.ARG_TASKHEAD_ID, taskHeadId);
        intent.putExtra(TasksActivity.ARG_TITLE, title);
        startActivity(intent);
    }

    @Override
    public void setLogoutSuccessUI() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Snackbar.make(mTaskHeadsView, "CANCELED", Snackbar.LENGTH_LONG).show();
        mPresenter.result(requestCode, resultCode, data);
    }

    // Interface with TaskHeadsActivity
    public interface TaskHeadsFragmentListener {
        void onLoginSuccess();
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

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
        if (position >= 0 && position < mTaskHeadsAdapter.getCount()) {
            if (checked) {
                mTaskHeadsAdapter.setNewSelection(position - mTaskHeadsView.getHeaderViewsCount(), checked);
            } else {
                mTaskHeadsAdapter.removeSelection(position - mTaskHeadsView.getHeaderViewsCount());
            }
        }

        actionMode.setTitle(mTaskHeadsAdapter.getSelectedCount() + " " + getContext().getResources().getString(R.string.item_selected));
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        mActionMode = actionMode;
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.contextual_menu_delete, menu);

        startAnimation(true);
        mIsActionMode = true;
        mTaskHeadsAdapter.setActionMode(mIsActionMode);

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
