package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;
import com.kiwi.auready_ver2.util.view.AnimatedExpandableListView;
import com.kiwi.auready_ver2.util.view.ColorPickerDialog;
import com.kiwi.auready_ver2.util.view.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "Tag_TasksFragment";
    public static final int REQ_EDIT_TASKHEAD = 0;

    private AnimatedExpandableListView mTasksView;
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
        if (getArguments() != null) {
            mTaskHeadId = getArguments().getString(TasksActivity.ARG_TASKHEAD_ID);
            mTaskHeadTitle = getArguments().getString(TasksActivity.ARG_TITLE);
        }

        mTasksAdapter = new TasksAdapter(getContext(), new ArrayList<Member>(), new HashMap<String, ArrayList<Task>>(), mTaskItemListener);
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Set title
        setTitle(mTaskHeadTitle);

        // Set ListView
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        mTasksView = (AnimatedExpandableListView) root.findViewById(R.id.expand_listview);
        mTasksView.setAdapter(mTasksAdapter);

//        View dummyFooterView = inflater.inflate(R.layout.tasks_dummy_view_for_padding, null);
//        mTasksView.addFooterView(dummyFooterView);

        // smooth collapse / expandItem animation
        mTasksView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mTasksView.isGroupExpanded(groupPosition)) {
//                    mTasksView.setDividerHeight(getContext().getResources()
//                            .getDimensionPixelSize(R.dimen.listview_padding_to_make_card_view));
                    mTasksView.collapseGroupWithAnimation(groupPosition);
                } else {
//                    mTasksView.setDividerHeight(0);
                    mTasksView.expandGroupWithAnimation(groupPosition);
                }

                return true;
            }
        });

        mTasksView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        mTasksView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        setHasOptionsMenu(true);
        return root;
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
                showTaskHeadDetail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTaskHeadDetail() {
        Intent intent = new Intent(getContext(), TaskHeadDetailActivity.class);
        intent.putExtra(TaskHeadDetailActivity.ARG_TASKHEAD_ID, mTaskHeadId);
        startActivityForResult(intent, REQ_EDIT_TASKHEAD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(String titleOfTaskHead) {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mTaskHeadTitle);
        }
    }

    @Override
    public void showMembers(List<Member> members) {
        mTasksView.setVisibility(View.VISIBLE);
        mTasksAdapter.replaceMemberList(members);
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mTasksView.setVisibility(View.VISIBLE);
        mTasksAdapter.replaceTasksList(tasks);
    }

    @Override
    public void showTasks(String memberId, List<Task> tasks) {
        mTasksView.setVisibility(View.VISIBLE);
        mTasksAdapter.replaceTasksList(memberId, tasks);
    }

    @Override
    public void showNoTasks() {
        mTasksView.setVisibility(View.GONE);
    }

    @Override
    public void scrollToAddButton() {
        mTasksView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int itemHeight = getResources().getDimensionPixelSize(R.dimen.lsitview_item_height);
                mTasksView.smoothScrollBy(itemHeight, 200);
            }
        }, 100);
    }

    TaskItemListener mTaskItemListener = new TaskItemListener() {

        @Override
        public void onAddTaskClick(String memberId, String description, int order) {
            mPresenter.createTask(memberId, description, order);
        }

        @Override
        public void onClickColorPicker(final View memberView, final int memberPosition) {
            ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
            int[] colors = memberView.getResources().getIntArray(R.array.color_picker);
            final int numColumns = 5;
            colorPickerDialog.initialize(R.string.color_picker_default_title,
                    colors, colors[0], numColumns, ColorPickerDialog.SIZE_SMALL);

            colorPickerDialog.show(getFragmentManager(), "colorPickerDialog");

            colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                @Override
                public void onColorSelected(int color) {
                    mTasksAdapter.setGroupBackground(memberPosition, color);
                }
            });
        }

        @Override
        public boolean onEnterKeyClicked(int memberPosition, int taskPosition) {

            Log.d("MY_LOG", "Focused Child : " + mTasksView.getFocusedChild());

            // check whether childview is add button
//            if (mTasksAdapter.getChildrenCount(memberPosition) == taskPosition + 2) {
//
//            }
//            if (mTasksView.getChildAt()) {
//
//            }

            return true;
        }

//        @Override
//        public void onStartEditMode(final int memberPosition, final View longClickedView) {
//            mTasksView.expandGroup(memberPosition, true);
//
//            // start animation
//            startAnimation(true);
//
//            // edit mode is launched by "Edit Mode" button
//            if (longClickedView == null) {
//                return;
//            }
//
//            // set Focus to long clicked editText after complete aniamtion
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mTasksAdapter.requestFocusToEditText(longClickedView);
//                    InputMethodManager keyboard =
//                            (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    keyboard.showSoftInput(longClickedView, 0);
//                }
//            }, ViewUtils.ANIMATION_DURATION);
//        }

//        @Override
//        public void onEnterKeyClicked(int memberPosition) {
//            if (memberPosition != -1) {
//                mTasksView.expandGroup(memberPosition);
//            }
//
//            // start animation
//            startAnimation(false);
//
//            // hide keyboard after complete aniamtion
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    InputMethodManager keyboard =
//                            (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    View view = getActivity().getCurrentFocus();
//                    if (view == null) {
//                        view = new View(getActivity());
//                    }
//                    keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//            }, ViewUtils.ANIMATION_DURATION);
//        }


        private void startAnimation(final boolean isGoingToEditMode) {
            final ViewTreeObserver viewTreeObserver = mTasksView.getViewTreeObserver();
            if (!viewTreeObserver.isAlive()) {
                return;
            }

            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    viewTreeObserver.removeOnPreDrawListener(this);

                    int count = mTasksView.getChildCount();
                    for (int i = 0; i < count; i++) {
                        mTasksAdapter.startAnimation(isGoingToEditMode, mTasksView.getChildAt(i));
                    }

                    return true;
                }
            });
        }

        @Override
        public void onDeleteTaskClick(String taskId) {
            ArrayList<String> deletedTaskId = new ArrayList<>();
            deletedTaskId.add(taskId);
            mPresenter.deleteTasks(deletedTaskId);
        }

        @Override
        public void onTaskDescEdited(String taskId) {

        }
    };

    public interface TaskItemListener {

        void onAddTaskClick(String memberId, String description, int order);

        void onDeleteTaskClick(String taskId);

        void onClickColorPicker(final View memberView, final int memberPosition);

        boolean onEnterKeyClicked(int memberPosition, int taskPosition);

        void onTaskDescEdited(String taskId);
    }
}
