package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends BaseAdapter {

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

    public List<Task> getItems() {
        return mTasks;
    }

    private void setOrder() {
        for (int i = 0; i < mTasks.size(); i++) {
            mTasks.get(i).setOrder(i);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        setOrder();
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        setOrder();
        super.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.tasks_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.reorderImage = (ImageView) convertView.findViewById(R.id.reorder_task);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.task_checkbox);
            viewHolder.editText = (EditText) convertView.findViewById(R.id.task_edittext);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.task_delete_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ref = position;
        final Task task = mTasks.get(position);

        viewHolder.deleteButton.setVisibility(View.GONE);
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskItemListener.onTaskDeleteButtonClicked(task.getMemberId(), task.getId());
            }
        });

        viewHolder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("MY_LOG", "setOnFocusChangeListener : " + hasFocus + ", " + position);
                if (hasFocus) {
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
            }
        });

        viewHolder.editText.setText(task.getDescription());
        viewHolder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTasks.get(viewHolder.ref).setDescription(s.toString());
            }
        });

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task removedTask = removeItem(position);
                if (removedTask == null) {
                    Toast.makeText(v.getContext(), "Fail to edit task, position : "
                            + viewHolder.ref, Toast.LENGTH_SHORT).show();
                    return;
                }

                CheckBox checkBox = (CheckBox) v;
                removedTask.setCompleted(checkBox.isChecked());
                mTaskItemListener.onEditedTask(
                        removedTask,
                        checkBox.isChecked());
            }
        });

        return convertView;
    }

    public void addItem(Task task) {
        if (task == null) {
            return;
        }

        mTasks.add(task);
        notifyDataSetChanged();
    }

    public Task removeItem(int position) {
        if (position >= mTasks.size()) {
            return null;
        }

        Task task = mTasks.remove(position);
        notifyDataSetChanged();

        return task;
    }

    public void reorder(int from, int to) {
        if (from >= mTasks.size() || to >= mTasks.size()) {
            return;
        }

        Task fromTask = mTasks.remove(from);
        mTasks.add(to, fromTask);

        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView reorderImage;
        CheckBox checkBox;
        EditText editText;
        ImageButton deleteButton;

        int ref;
    }
}
