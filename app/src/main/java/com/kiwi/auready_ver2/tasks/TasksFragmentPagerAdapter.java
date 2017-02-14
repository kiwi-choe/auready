package com.kiwi.auready_ver2.tasks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;

import java.util.HashMap;
import java.util.List;

public class TasksFragmentPagerAdapter extends FragmentStatePagerAdapter {

    // key : Member ID, value : TasksFragment
    private HashMap<String, TasksFragment> tasksFragments;
    List<Member> mMembers;
    TasksActivity.TaskViewListener mTaskViewListner;

    public TasksFragmentPagerAdapter(FragmentManager fm, List<Member> members, TasksActivity.TaskViewListener taskViewListener) {
        super(fm);
        mMembers = members;
        mTaskViewListner = taskViewListener;

        tasksFragments = new HashMap<>();
        for (Member member : members) {
            tasksFragments.put(member.getId(),
                    TasksFragment.newInstance(
                            member.getId(),
                            member.getName(),
                            mTaskFragmentListener));
        }
    }

    @Override
    public Fragment getItem(int position) {
        final String getMemberId = mMembers.get(position).getId();
        mTaskViewListner.onCreateViewCompleted(getMemberId);

        return tasksFragments.get(getMemberId);
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    public Fragment getItem(String memberId) {
        return tasksFragments.get(memberId);
    }

    public List<Member> getMembers() {
        return mMembers;
    }


    interface TaskFragmentListener {
        void onAddTaskButtonClicked(Task task);

        void onTaskDeleteButtonClicked(String taskId);

        void onEditedTask(Task task);
    }

    private TaskFragmentListener mTaskFragmentListener = new TaskFragmentListener() {

        @Override
        public void onAddTaskButtonClicked(Task task) {
            mTaskViewListner.onTaskAddButtonClicked(task);
        }

        @Override
        public void onTaskDeleteButtonClicked(String taskId) {
            mTaskViewListner.onTaskDeleteButtonClicked(taskId);
        }

        @Override
        public void onEditedTask(Task task) {
            mTaskViewListner.onEditedTask(task);
        }
    };
}
