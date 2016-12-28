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
import java.util.HashMap;
import java.util.List;

public class TasksAdapter extends BaseExpandableListAdapter {

    final private TasksFragment.TaskItemListener mTaskItemListener;
    private ArrayList<Friend> mMemberList = null;
    private HashMap<String, ArrayList<Task>> mTasksList = null;
    private LayoutInflater mInflater = null;

    public TasksAdapter(Context context, ArrayList<Friend> memberList, HashMap<String, ArrayList<Task>> tasksList, TasksFragment.TaskItemListener taskItemListener) {
        super();

        mInflater = LayoutInflater.from(context);
        mMemberList = memberList;
        mTasksList = tasksList;
        mTaskItemListener = taskItemListener;
    }

    @Override
    public int getGroupCount() {
        return mMemberList.size();
    }

    @Override
    public int getChildrenCount(int memberPosition) {

        ArrayList<Task> tasks = mTasksList.get(getMemberId(memberPosition));
        if (tasks == null) {
            return 1;
        }

        // + 1 : footer view for each member list
        return tasks.size() + 1;
    }

    @Override
    public Object getGroup(int memberPosition) {
        return mMemberList.get(memberPosition);
    }

    @Override
    public Object getChild(int memberPosition, int taskPosition) {
        return mTasksList.get(getMemberId(memberPosition)).get(taskPosition);
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
            viewHolder.delete_member_btn = (Button) view.findViewById(R.id.delete_member_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.memberName.setText(mMemberList.get(memberPosition).getName());
        return view;
    }

    @Override
    public View getChildView(final int memberPosition, final int taskPosition, boolean isLastTask, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_child_view, parent, false);

            viewHolder = new ChildViewHolder();
            viewHolder.taskTextView = (TextView) view.findViewById(R.id.task);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.deleteTaskBtn = (Button) view.findViewById(R.id.delete_task_btn);

            viewHolder.addTaskBtn = (Button) view.findViewById(R.id.add_task_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        if (isLastTask) {
            viewHolder.addTaskBtn.setVisibility(View.VISIBLE);
            viewHolder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaskItemListener.onAddTaskClick(getMemberId(memberPosition), "empty : " + taskPosition, getChildrenCount(memberPosition) - 1);
                }
            });

            viewHolder.taskTextView.setVisibility(View.GONE);
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.deleteTaskBtn.setVisibility(View.GONE);
        } else {
            viewHolder.addTaskBtn.setVisibility(View.GONE);
            viewHolder.taskTextView.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.deleteTaskBtn.setVisibility(View.VISIBLE);


            final ArrayList<Task> tasksList = mTasksList.get(getMemberId(memberPosition));
            viewHolder.taskTextView.setText(tasksList.get(taskPosition).getDescription());
            viewHolder.checkBox.setChecked(tasksList.get(taskPosition).getCompleted());
            viewHolder.deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaskItemListener.onDeleteClick(tasksList.get(taskPosition).getId());
                }
            });
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int memberPosition, int taskPosition) {
        return true;
    }

    public void replaceMemberList(List<Friend> members) {
        setMemberList((ArrayList) members);
        notifyDataSetChanged();
    }

    private String getMemberId(int position) {
        return mMemberList.get(position).getId();
    }


    private void setMemberList(ArrayList<Friend> members) {
        mMemberList = members;
    }

    public void replaceTasksList(List<Task> tasks) {

        mTasksList.clear();

        for (Task task : tasks) {
            if (mTasksList.get(task.getMemberId()) == null) {
                mTasksList.put(task.getMemberId(), new ArrayList<Task>());
            }

            mTasksList.get(task.getMemberId()).add(task);
        }

        notifyDataSetChanged();
    }

    public void replaceTasksList(String memberId, List<Task> tasks) {

        mTasksList.get(memberId).clear();

        for (Task task : tasks) {
            mTasksList.get(memberId).add(task);
        }

        notifyDataSetChanged();

    }

    private class GroupViewHolder {
        TextView memberName;
        Button auready_btn;
        Button delete_member_btn;
    }

    private class ChildViewHolder {
        TextView taskTextView;
        CheckBox checkBox;
        Button deleteTaskBtn;
        Button addTaskBtn;
    }
}
