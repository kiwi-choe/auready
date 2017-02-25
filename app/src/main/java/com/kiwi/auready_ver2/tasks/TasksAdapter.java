package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends BaseAdapter {

    private final static int SECTION_ADD_BUTTON = 0;
    private final static int SECTION_LIST_VIEW = 1;

    private LayoutInflater mInflater;
    private ArrayList<Task> mTasks = new ArrayList<>();
    private TasksFragment.TaskItemListener mTaskItemListener;

    public TasksAdapter(Context context, TasksFragment.TaskItemListener taskItemListener) {
        mInflater = LayoutInflater.from(context);
        mTaskItemListener = taskItemListener;
    }

    public void updateTasks(List<Task> tasks) {
        mTasks.clear();
        mTasks.addAll(tasks);

        notifyDataSetChanged();
    }

    public int getAddButtonPosition() {
        return mTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        int buttonPosition = getAddButtonPosition();
        if (position == buttonPosition) {
            return SECTION_ADD_BUTTON;
        } else {
            return SECTION_LIST_VIEW;
        }
    }

    @Override
    public int getCount() {
        System.out.println(mTasks.size());
        return mTasks.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == getAddButtonPosition()) {
            return "Add button";
        }

        return mTasks.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int type = getItemViewType(position);
        if (type == SECTION_ADD_BUTTON) {
            return getAddButton(position);
        }

        final ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.tasks_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.reorderImage = (ImageView) convertView.findViewById(R.id.reorder_task);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.task_checkbox);
            viewHolder.editText = (EditText) convertView.findViewById(R.id.task_edittext);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.task_delete_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Task task = mTasks.get(position);

        viewHolder.deleteButton.setVisibility(View.GONE);
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskItemListener.onDeleteTaskButtonClicked(task.getId());
            }
        });

        viewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
            }
        });

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewHolder.checkBox.setChecked(isChecked);
                mTaskItemListener.onEditedTask(task.getId(),
                        task.getOrder(),
                        viewHolder.checkBox.isChecked(),
                        viewHolder.editText.getText().toString());
            }
        });

        viewHolder.editText.setText(task.getDescription());

        return convertView;
    }

    private View getAddButton(final int position) {
        View view = mInflater.inflate(R.layout.task_add_button, null);
        Button addButton = (Button) view.findViewById(R.id.add_task_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskItemListener.OnAddTaskButtonClicked(position);
            }
        });

        return view;
    }

    public void reorder(int from, int to) {
        Task fromTask = mTasks.remove(from);
        mTasks.add(to, fromTask);

        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView reorderImage;
        CheckBox checkBox;
        EditText editText;
        ImageButton deleteButton;
    }
}
