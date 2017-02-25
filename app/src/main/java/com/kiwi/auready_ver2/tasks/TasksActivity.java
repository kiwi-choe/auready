package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksActivity extends AppCompatActivity implements TasksContract.View {

    public static final String ARG_TASKHEAD_ID = "TASKHEAD_ID";
    public static final String ARG_TITLE = "TITLE";
    public static final int REQ_EDIT_TASKHEAD = 0;

    // tasks fragment view pager
    private ViewPager mViewPager;
    private TasksFragmentPagerAdapter mPagerAdapter;

    private String mTaskHeadId;

    private TasksPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_toolbar);
        setSupportActionBar(toolbar);

        // set Title
        if (getIntent().hasExtra(ARG_TITLE)) {
            String title = getIntent().getStringExtra(ARG_TITLE);
            setTitle(title);
        }

        // Create the presenter
        mTaskHeadId = null;
        if (getIntent().hasExtra(ARG_TASKHEAD_ID)) {
            mTaskHeadId = getIntent().getStringExtra(ARG_TASKHEAD_ID);
        }

        mPresenter = new TasksPresenter(
                Injection.provideUseCaseHandler(),
                mTaskHeadId,
                this,
                Injection.provideGetMembers(getApplicationContext()),
                Injection.provideGetTasksOfMember(getApplicationContext()),
                Injection.provideSaveTask(getApplicationContext()),
                Injection.provideDeleteTasks(getApplicationContext()),
                Injection.provideEditTasks(getApplicationContext()),
                Injection.provideGetTasksOfTaskHead(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
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
        initFragments(members);
    }

    private void initFragments(List<Member> members) {
        mViewPager = (ViewPager) findViewById(R.id.tasks_fragment_pager);
        mPagerAdapter = new TasksFragmentPagerAdapter(getSupportFragmentManager(), members, mTaskViewListener);
        mViewPager.setAdapter(mPagerAdapter);
    }

    interface TaskViewListener {
        void onCreateViewCompleted(String memberId);

        void onAddTaskButtonClicked(Task task);

        void onDeleteTaskButtonClicked(String taskId);

        void onEditedTask(Task task);
    }

    private TaskViewListener mTaskViewListener = new TaskViewListener() {

        @Override
        public void onCreateViewCompleted(String memberId) {
            mPresenter.getTasksOfMember(memberId);
        }

        @Override
        public void onAddTaskButtonClicked(Task task) {
            mPresenter.createTask(task.getMemberId(), task.getDescription(), task.getOrder());
        }

        @Override
        public void onDeleteTaskButtonClicked(String taskId) {
            ArrayList<String> deleteTask = new ArrayList<>();
            deleteTask.add(taskId);
            mPresenter.deleteTasks(deleteTask);
        }

        @Override
        public void onEditedTask(Task task) {
            ArrayList<Task> editedTask = new ArrayList<>();
            editedTask.add(task);
            mPresenter.editTasks(editedTask);
        }
    };

    @Override
    public void showTasks(String memberId, List<Task> tasks) {
        TasksFragment fragment = (TasksFragment) mPagerAdapter.getItem(memberId);
        fragment.showTasks(tasks);
    }

    @Override
    public void scrollToAddButton() {

    }

    @Override
    public void showFilteredTasks(List<Task> completed, List<Task> uncompleted) {

    }

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        mPresenter = (TasksPresenter) checkNotNull(presenter);
    }
}

