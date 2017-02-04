package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TasksAdapter extends BaseExpandableListAdapter {

    final private TasksFragment.TaskItemListener mTaskItemListener;
    private ArrayList<Member> mMemberList = null;
    private HashMap<String, ArrayList<Task>> mTasksList = null;
    private HashMap<String, ArrayList<Boolean>> mSelection = new HashMap<>();

    public final int INVALID_POSITION = -1;

    private LayoutInflater mInflater = null;
    int mCurrentEditModeMember = INVALID_POSITION;
    private View mCurrentSelectedDeleteTask = null;

    public TasksAdapter(Context context, ArrayList<Member> memberList, HashMap<String, ArrayList<Task>> tasksList, TasksFragment.TaskItemListener taskItemListener) {
        super();

        mInflater = LayoutInflater.from(context);
        mMemberList = memberList;
        mTasksList = tasksList;
        mTaskItemListener = taskItemListener;

        mCurrentEditModeMember = INVALID_POSITION;
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
        if (memberPosition >= mMemberList.size()) {
            return null;
        }

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
    public View getGroupView(final int memberPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        GroupViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_group_view, parent, false);

            viewHolder = new GroupViewHolder();
            viewHolder.memberName = (TextView) view.findViewById(R.id.member_name);
            viewHolder.auready_btn = (Button) view.findViewById(R.id.member_add_bt);
            viewHolder.delete_tasks_btn = (Button) view.findViewById(R.id.delete_tasks_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) view.getTag();
        }

        viewHolder.memberName.setText(mMemberList.get(memberPosition).getName());
        if (memberPosition == mCurrentEditModeMember) {
            view.setBackgroundColor(view.getResources().getColor(android.R.color.holo_red_dark));
            viewHolder.delete_tasks_btn.setText("Delete");
            viewHolder.delete_tasks_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaskItemListener.onDeleteTasksClick(memberPosition);
                }
            });
        } else {
            view.setBackgroundColor(view.getResources().getColor(android.R.color.white));
            viewHolder.delete_tasks_btn.setText("Edit tasks");
            viewHolder.delete_tasks_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaskItemListener.onStartActionMode(memberPosition);
                }
            });
        }

        return view;
    }

    @Override
    public View getChildView(final int memberPosition, final int taskPosition, boolean isLastTask, View convertView, ViewGroup parent) {
        View view = convertView;
        final ChildViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.taskview_expand_list_child_view, parent, false);

            viewHolder = new ChildViewHolder();
            viewHolder.editText = (EditText) view.findViewById(R.id.task_edittext);
            viewHolder.textView = (TextView) view.findViewById(R.id.task_textview);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.deleteTaskBtn = (Button) view.findViewById(R.id.delete_task_btn);
            viewHolder.addTaskBtn = (Button) view.findViewById(R.id.add_task_btn);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        viewHolder.editText.setVisibility(View.GONE);
        viewHolder.textView.setVisibility(View.GONE);
        viewHolder.checkBox.setVisibility(View.GONE);
        viewHolder.deleteTaskBtn.setVisibility(View.GONE);
        viewHolder.addTaskBtn.setVisibility(View.GONE);

        if (isLastTask) {
            viewHolder.addTaskBtn.setVisibility(View.VISIBLE);
            viewHolder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (memberPosition != mCurrentEditModeMember) {
                        mCurrentEditModeMember = memberPosition;
                        notifyDataSetInvalidated();
                    }

                    mTaskItemListener.onAddTaskClick(getMemberId(memberPosition), "empty : " + taskPosition, getChildrenCount(memberPosition) - 1);

                }
            });

            return view;
        }

        // set action mode
        if (memberPosition == mCurrentEditModeMember) {

            viewHolder.editText.setVisibility(View.VISIBLE);
            viewHolder.editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        setActionModeMember(INVALID_POSITION);
                        notifyDataSetInvalidated();
                        return true;
                    }

                    return false;
                }
            });

            viewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (mCurrentSelectedDeleteTask != null) {
                            mCurrentSelectedDeleteTask.setVisibility(View.GONE);
                        }

                        return;
                    }

                    mCurrentSelectedDeleteTask = viewHolder.deleteTaskBtn;
                    mCurrentSelectedDeleteTask.setVisibility(View.VISIBLE);
                }
            });

//            view.setBackgroundColor(view.getResources().getColor(android.R.color.holo_red_light));
        } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.VISIBLE);
//            view.setBackgroundColor(view.getResources().getColor(android.R.color.white));
        }

        final ArrayList<Task> tasksList = mTasksList.get(getMemberId(memberPosition));
        final Task task = tasksList.get(taskPosition);

        // set description
        viewHolder.textView.setText(task.getDescription());
        viewHolder.editText.setText(task.getDescription());
        if (task.getCompleted()) {
            viewHolder.textView.setTextColor(view.getResources().getColor(android.R.color.holo_blue_dark));
            viewHolder.editText.setTextColor(view.getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            viewHolder.textView.setTextColor(view.getResources().getColor(android.R.color.black));
            viewHolder.editText.setTextColor(view.getResources().getColor(android.R.color.black));
        }

        viewHolder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTaskItemListener.onTaskDescEdited(task.getId());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // set checkbox
        viewHolder.checkBox.setChecked(task.getCompleted());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : need to impelement Presenter
                mTaskItemListener.onTaskChecked(task.getId());
                viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
            }
        });

        // set reorder
        viewHolder.deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mTaskItemListener.onDeleteClick(tasksList.get(taskPosition).getId());
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int memberPosition, int taskPosition) {
        return true;
    }

    public void replaceMemberList(List<Member> members) {
        setMemberList((ArrayList) members);
        notifyDataSetChanged();
    }

    private String getMemberId(int position) {
        return mMemberList.get(position).getId();
    }


    private void setMemberList(ArrayList<Member> members) {
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

        if (mTasksList.get(memberId) == null) {
            mTasksList.put(memberId, new ArrayList<Task>());
        }

        mTasksList.get(memberId).clear();

        for (Task task : tasks) {
            mTasksList.get(memberId).add(task);
        }

        notifyDataSetChanged();
    }

    public void setActionModeMember(int memberPosition) {
        mCurrentEditModeMember = memberPosition;
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return mCurrentEditModeMember != INVALID_POSITION;
    }

    private class GroupViewHolder {
        TextView memberName;
        Button auready_btn;
        Button delete_tasks_btn;
    }

    private class ChildViewHolder {
        EditText editText;
        TextView textView;
        CheckBox checkBox;
        Button deleteTaskBtn;
        Button addTaskBtn;
    }
}
