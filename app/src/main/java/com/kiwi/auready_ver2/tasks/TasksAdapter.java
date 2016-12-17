package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;

/**
 *
 */
public class TasksAdapter extends BaseExpandableListAdapter {

    private ArrayList<Friend> mMemberList = null;
    private ArrayList<ArrayList<Task>> mTasksList = null;
    private LayoutInflater mInflater = null;

    public TasksAdapter(Context context, ArrayList<Friend> memberList, ArrayList<ArrayList<Task>> tasksList) {
        super();

        mInflater = LayoutInflater.from(context);
        mMemberList = memberList;
        mTasksList = tasksList;
    }

    @Override
    public int getGroupCount() {
        return mMemberList.size();
    }

    @Override
    public int getChildrenCount(int memberPosition) {
        return mTasksList.get(memberPosition).size();
    }

    @Override
    public Object getGroup(int memberPosition) {
        return mMemberList.get(memberPosition);
    }

    @Override
    public Object getChild(int memberPosition, int taskPosition) {
        return mTasksList.get(memberPosition).get(taskPosition);
    }

    @Override
    public long getGroupId(int memberPosition) {
        return memberPosition;
    }

    @Override
    public long getChildId(int memberPosition, int taskPosition) {
        return taskPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int memberPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        GroupViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_group_view, parent, false);

            viewHolder = new GroupViewHolder();
            viewHolder.memberName = (TextView) view.findViewById(R.id.member_name);
            viewHolder.auready_btn = (Button) view.findViewById(R.id.member_add_bt);

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.memberName.setText(mMemberList.get(memberPosition).getName());
        return view;
    }

    @Override
    public View getChildView(int memberPosition, int taskPosition, boolean isLastTask, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_child_view, parent, false);

            viewHolder = new ChildViewHolder();
            viewHolder.taskTextView = (TextView) view.findViewById(R.id.task);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        ArrayList<Task> tasksList = mTasksList.get(memberPosition);
        viewHolder.taskTextView.setText(tasksList.get(taskPosition).getDescription());

        return view;
    }

    @Override
    public boolean isChildSelectable(int memberPosition, int taskPosition) {
        return true;
    }

    public void replaceMemberList(ArrayList<Friend> members) {
        setMemberList(members);
    }

    private void setMemberList(ArrayList<Friend> members) {
        mMemberList = members;
    }

    public void replaceTasksList(ArrayList<ArrayList<Task>> tasksList) {
        setTasksList(tasksList);
    }

    private void setTasksList(ArrayList<ArrayList<Task>> tasksList) {
        mTasksList = tasksList;
    }

    private class GroupViewHolder {
        TextView memberName;
        Button auready_btn;
    }

    private class ChildViewHolder {
        TextView taskTextView;
        CheckBox checkBox;
    }
}
