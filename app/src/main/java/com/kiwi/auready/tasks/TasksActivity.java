package com.kiwi.auready.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.kiwi.auready.Injection;
import com.kiwi.auready.R;
import com.kiwi.auready.data.Member;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.taskheaddetail.TaskHeadDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksActivity extends AppCompatActivity implements TasksContract.View {

    public static final String ARG_TASKHEAD_ID = "TASKHEAD_ID";
    public static final String ARG_TITLE = "TITLE";
    public static final String ARG_TASKHEAD_COLOR = "TASKHEAD_COLOR";
    public static final int REQ_EDIT_TASKHEAD = 0;

    public static final int OFF_SCREEN_PAGE_LIMIT = 5;
    private static final int DEFAULT_COLOR = R.color.color_picker_default_color;


    private Toolbar mToolbar;
    // tasks fragment view pager
    private ViewPager mViewPager;
    private TasksFragmentPagerAdapter mPagerAdapter;
    private ProgressBar mProgressBar;

    private String mTaskHeadId;

    private TasksPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        initViews();

        mTaskHeadId = null;
        if (getIntent().hasExtra(ARG_TASKHEAD_ID)) {
            mTaskHeadId = getIntent().getStringExtra(ARG_TASKHEAD_ID);
        }

        // Create the presenter
        mPresenter = new TasksPresenter(
                Injection.provideUseCaseHandler(),
                mTaskHeadId,
                this,
                Injection.provideGetMembers(getApplicationContext()),
                Injection.provideGetTasksOfMember(getApplicationContext()),
                Injection.provideSaveTask(getApplicationContext()),
                Injection.provideDeleteTasks(getApplicationContext()),
                Injection.provideEditTasks(getApplicationContext()),
                Injection.provideGetTaskHeadDetail(getApplicationContext()),
                Injection.provideChangeCompleted(getApplicationContext()),
                Injection.provideChangeOrders(getApplicationContext()));
    }

    private void initViews() {
        // Set up the toolbar.
        mToolbar = (Toolbar) findViewById(R.id.tasks_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra(ARG_TITLE)) {
            String title = getIntent().getStringExtra(ARG_TITLE);
            setTitle(title);
        }

        if(getIntent().hasExtra(ARG_TASKHEAD_COLOR)) {
            int toolbarColor = getIntent().getIntExtra(ARG_TASKHEAD_COLOR, DEFAULT_COLOR);
            setColor(toolbarColor);
        }

        initFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.editTasks();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tasks_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_menu:
                showTaskHeadDetail();
                break;

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTaskHeadDetail() {
        Intent intent = new Intent(this, TaskHeadDetailActivity.class);
        intent.putExtra(TaskHeadDetailActivity.ARG_TASKHEAD_ID, mTaskHeadId);
        startActivityForResult(intent, REQ_EDIT_TASKHEAD);
    }

    @Override
    public void setTitle(String titleOfTaskHead) {
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setTitle(titleOfTaskHead);
        }
    }

    @Override
    public void showMembers(List<Member> members) {
        mPagerAdapter.replaceMembers(members);

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        mViewPager.setVisibility(View.VISIBLE);
        if (members != null && members.size() == 1) {
            mViewPager.setPaddingRelative(getResources().getDimensionPixelSize(
                    R.dimen.viewpager_end_padding),
                    mViewPager.getPaddingTop(),
                    mViewPager.getPaddingRight(),
                    mViewPager.getPaddingBottom());
        }
    }

    @Override
    public void onEditTasksOfMemberError() {
        mPresenter.getTaskHeadDetailFromRemote();
    }

    private void initFragments() {
        mViewPager = (ViewPager) findViewById(R.id.tasks_fragment_pager);
        mPagerAdapter = new TasksFragmentPagerAdapter(
                this, getSupportFragmentManager(), new ArrayList<Member>(), mTaskViewListener);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_PAGE_LIMIT);

        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.viewpager_end_padding));

        mViewPager.setVisibility(View.INVISIBLE);
    }

    interface TaskViewListener {
        void onCreateViewCompleted(String memberId);

        void onAddTaskButtonClicked(Task task, List<Task> editingTasks);

        void onDeleteTaskButtonClicked(String memberId, String taskId, List<Task> editingTasks);

        void onUpdateTasksInMemory(String memberId, List<Task> tasks);

        void onChangeComplete(String memberId, String taskId, List<Task> editingTasks);

        void onReordering(String memberId, List<Task> editingTasks);

        void onAUReadyClicked(String memberId);
    }

    private TaskViewListener mTaskViewListener = new TaskViewListener() {

        @Override
        public void onCreateViewCompleted(String memberId) {
            mPresenter.getTasksOfMember(memberId);
        }

        @Override
        public void onAddTaskButtonClicked(Task task, List<Task> editingTasks) {
            mPresenter.createTask(task, editingTasks);
        }

        @Override
        public void onDeleteTaskButtonClicked(String memberId, String taskId, List<Task> editingTasks) {
            mPresenter.deleteTask(memberId, taskId, editingTasks);
        }

        @Override
        public void onUpdateTasksInMemory(String memberId, List<Task> tasks) {
            mPresenter.updateTasksInMemory(memberId, tasks);
        }

        @Override
        public void onChangeComplete(String memberId, String taskId, List<Task> editingTasks) {
            mPresenter.changeComplete(memberId, taskId, editingTasks);
        }

        @Override
        public void onReordering(String memberId, List<Task> editingTasks) {
            mPresenter.reorder(memberId, editingTasks);
        }

        @Override
        public void onAUReadyClicked(String userId) {
            mPresenter.notifyAUReady(userId);
        }
    };

    @Override
    public void showLoadProgressBar() {
        if (mProgressBar == null) {
            mProgressBar = (ProgressBar) findViewById(R.id.tasks_data_load_progress_bar);
        }

        mProgressBar.setVisibility(View.VISIBLE);

        if (mViewPager != null) {
            mViewPager.setVisibility(View.GONE);
        }
    }

    @Override
    public void showTasks(String memberId, List<Task> completed, List<Task> uncompleted) {
        TasksFragment fragment = (TasksFragment) mPagerAdapter.getItem(memberId);
        if (fragment != null) {
            fragment.showFilteredTasks(completed, uncompleted);
        }
    }

    @Override
    public void showNoTask(String memberId) {
        TasksFragment fragment = (TasksFragment) mPagerAdapter.getItem(memberId);
        if (fragment != null) {
            fragment.showNoTask();
        }
    }

    @Override
    public void setColor(int color) {
        mToolbar.setBackgroundColor(color);
//        LinearLayout baseLayoutOfFragments = (LinearLayout)findViewById(R.id.tasks_fragments_layout);
//        if (baseLayoutOfFragments != null) {
//            baseLayoutOfFragments.setBackgroundColor(color);
//        }
    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mPresenter = (TasksPresenter) checkNotNull(presenter);
    }

}