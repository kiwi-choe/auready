package com.kiwi.auready_ver2.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 * //// FIXME: 9/1/16 will change to CustomAdapter
 */
public class ActiveTasksAdapter extends BaseAdapter {

    private List<Task> mTasks;
    private TaskItemListener mItemListener;

    public ActiveTasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
        super();
        setList(tasks);
        mItemListener = itemListener;
    }


    @Override
    public int getCount() {
        return mTasks.size();
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
            viewHolder.delete = (Button) rowView.findViewById(R.id.delete);

            rowView.setTag(viewHolder);
        }
        bindView(rowView, i);
        return rowView;
    }

    private void bindView(View view, int i) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        final Task task = mTasks.get(i);

        viewHolder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onCompleteTaskClick(task);
            }
        });

        viewHolder.description.setText(task.getDescription());
    }

    public void replaceData(List<Task> tasks) {
        setList(tasks);
        notifyDataSetChanged();
    }

    public void setList(List<Task> list) {
        mTasks = list;
    }

    public class ViewHolder {
        CheckBox complete;
        TextView description;
        public Button delete;
    }

    public interface TaskItemListener {

        void onCompleteTaskClick(Task task);
    }
}
