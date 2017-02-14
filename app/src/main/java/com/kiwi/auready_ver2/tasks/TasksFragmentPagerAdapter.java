package com.kiwi.auready_ver2.tasks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kiwi.auready_ver2.data.Member;

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
                            member.getName()));
        }
    }

    @Override
    public Fragment getItem(int position) {
        final String getMemberId = mMembers.get(position).getId();
        mTaskViewListner.CreateViewCompleted(getMemberId);

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
}
