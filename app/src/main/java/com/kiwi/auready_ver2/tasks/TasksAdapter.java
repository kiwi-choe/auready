package com.kiwi.auready_ver2.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 * Created by kiwi on 8/19/16.
 */
public class TasksAdapter extends BaseAdapter {

    private List<Task> mTasks;
    private TaskItemListener mItemListener;

    public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
        super();
        setList(tasks);
        mItemListener = itemListener;
    }

    public void setList(List<Task> list) {
        mTasks = list;
    }

    @Override
    public int getCount() {
        return mTasks.size() + 1;
    }

    @Override
    public Task getItem(int i) {
        return mTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View rowView = view;
        if (rowView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            rowView = layoutInflater.inflate(R.layout.task_item, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.complete = (CheckBox) rowView.findViewById(R.id.complete);
            viewHolder.description = (TextView) rowView.findViewById(R.id.description);
            rowView.setTag(viewHolder);
        }
        bindView(rowView, i);
        return rowView;
    }

    private void bindView(View view, int i) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        final Task task = mTasks.get(i);

        viewHolder.complete.setChecked(task.isCompleted());
        viewHolder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!task.isCompleted()) {
                    mItemListener.onCompleteTaskClick(task);
                    // move this task from active_list to complete_list, update listviews
                } else {
                    mItemListener.onActivateTaskClick(task);
                    // move this task from complete_list to active_list, update listviews
                }
            }
        });
        
        viewHolder.description.setText(task.getDescription());
    }

    public class ViewHolder {
        CheckBox complete;
        TextView description;
    }

    public interface TaskItemListener {

        void onCompleteTaskClick(Task task);

        void onActivateTaskClick(Task task);
    }
}
