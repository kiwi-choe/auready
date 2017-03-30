package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.kiwi.auready_ver2.R;

public class CompleteTasksAdapter extends TasksAdapter {

    private final Context mContext;

    public CompleteTasksAdapter(Context context, TasksFragment.TaskItemListener taskItemListener) {
        super(context, taskItemListener);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (!viewHolder.checkBox.isChecked()) {
            viewHolder.checkBox.setChecked(true);
        }

        viewHolder.editText.setTextColor(ContextCompat.getColor(mContext, R.color.tasks_item_description_dim_color));
        viewHolder.editText.setTypeface(null, Typeface.ITALIC);
        viewHolder.editText.setPaintFlags(viewHolder.editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return view;
    }
}
