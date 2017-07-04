package com.kiwi.auready.tasks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class NotCompleteTasksAdapter extends TasksAdapter {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (viewHolder.checkBox.isChecked()) {
            viewHolder.checkBox.setChecked(false);
        }

        return view;
    }

    public NotCompleteTasksAdapter(Context context, TasksFragment.TaskItemListener taskItemListener) {
        super(context, taskItemListener);
    }
}
