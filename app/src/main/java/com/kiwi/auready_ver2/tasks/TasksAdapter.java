package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;

import java.util.ArrayList;

/**
 *
 */
public class TasksAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private LayoutInflater mInflater = null;

    public TasksAdapter(Context context, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList) {
        super();

        mInflater = LayoutInflater.from(context);
        mGroupList = groupList;
        mChildList = childList;
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        GroupViewHolder viewHolder;

        if (view == null) {
            viewHolder = new GroupViewHolder();
            view = mInflater.inflate(R.layout.taskview_expand_list_group_view, parent, false);
            viewHolder.memberName = (TextView) view.findViewById(R.id.member_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.memberName.setText(mGroupList.get(groupPosition));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ChildViewHolder();
            view = mInflater.inflate(R.layout.taskview_expand_list_child_view, parent, false);
            viewHolder.task = (TextView) view.findViewById(R.id.member_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        String taskText = "test";
        viewHolder.task.setText(taskText);

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        TextView memberName;
    }

    private class ChildViewHolder {
        TextView task;
    }
}
