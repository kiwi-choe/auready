package com.kiwi.auready_ver2.tasks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.kiwi.auready_ver2.data.Member;

import java.util.HashMap;
import java.util.List;

public class TasksFragmentPagerAdapter extends FragmentStatePagerAdapter {

    // key : Member ID, value : TasksFragment
    private HashMap<String, TasksFragment> mTasksFragments;
    List<Member> mMembers;
    TasksActivity.TaskViewListener mTaskViewListener;

    public TasksFragmentPagerAdapter(FragmentManager fm, List<Member> members, TasksActivity.TaskViewListener taskViewListener) {
        super(fm);
        mMembers = members;
        mTaskViewListener = taskViewListener;

        mTasksFragments = new HashMap<>();
        for (Member member : members) {
            mTasksFragments.put(member.getId(),
                    TasksFragment.newInstance(
                            member.getId(),
                            member.getName(),
                            mTaskViewListener));
        }
    }

    @Override
    public Fragment getItem(int position) {
        final String getMemberId = mMembers.get(position).getId();
        mTaskViewListener.onCreateViewCompleted(getMemberId);

        return mTasksFragments.get(getMemberId);
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    public Fragment getItem(String memberId) {
        return mTasksFragments.get(memberId);
    }

    public List<Member> getMembers() {
        return mMembers;
    }
}
