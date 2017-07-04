package com.kiwi.auready.tasks;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kiwi.auready.R;
import com.kiwi.auready.data.Member;

import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragmentPagerAdapter extends FragmentStatePagerAdapter {

    // key : Member ID, value : TasksFragment
    private HashMap<String, TasksFragment> mTasksFragments;
    private List<Member> mMembers;
    private TasksActivity.TaskViewListener mTaskViewListener;
    private Context mContext;

    public TasksFragmentPagerAdapter(Context context, FragmentManager fm, List<Member> members, TasksActivity.TaskViewListener taskViewListener) {
        super(fm);

        mTaskViewListener = taskViewListener;
        mTasksFragments = new HashMap<>();
        setMembers(members);
        mContext = context;
    }

    private void setMembers(List<Member> members) {
//        mMembers.clear();
        mMembers = checkNotNull(members);

        setTasksFragmentList(members);
    }

    private void setTasksFragmentList(List<Member> members) {
        if (mTasksFragments.size() > members.size()) {
            deleteMember(members);
        } else {
            addMembers(members);
        }
    }

    private void addMembers(List<Member> members) {
        for (Member member : members) {
            if (!mTasksFragments.containsKey(member.getId())) {
                mTasksFragments.put(member.getId(),
                        TasksFragment.newInstance(member, mTaskViewListener));
            }
        }
    }

    private void deleteMember(List<Member> members) {
        for(String key:mTasksFragments.keySet()) {
            for(Member member:members) {
                if(!key.equals(member.getId())) {
                    mTasksFragments.remove(key);
                }
            }
        }
    }

    void replaceMembers(List<Member> members) {
        setMembers(members);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        final String getMemberId = mMembers.get(position).getId();
        mTaskViewListener.onCreateViewCompleted(getMemberId);

        return mTasksFragments.get(getMemberId);
    }

    @Override
    public int getItemPosition(Object item) {
        if (item instanceof TasksFragment) {
            if (!mTasksFragments.containsValue(item)) {
                Log.d("Tag_showmembers", "POSITION_NONE");
                return POSITION_NONE;
            }
        }
        return super.getItemPosition(item);
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

    @Override
    public float getPageWidth(int position) {
        if (getMembers().size() == 1) {
            return super.getPageWidth(position);
        }

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float smallestWidth = displayMetrics.widthPixels;
        float margin = mContext.getResources().getDimension(R.dimen.viewpager_end_padding);
        return (smallestWidth - margin) / smallestWidth;
    }
}
