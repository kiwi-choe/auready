package com.kiwi.auready_ver2.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.customlistview.BaseTasksAdapter;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 *
 */
public class TasksAdapter extends BaseTasksAdapter {

    private List<Task> mTasks;
    private final String mTaskHeadId;
    private TaskItemListener mItemListener;

    public TasksAdapter(List<Task> tasks, String taskHeadId, TaskItemListener itemListener) {
        super();
        setList(tasks);
        setButtonPosition();

        mTaskHeadId = taskHeadId;
        mItemListener = itemListener;
    }

    private void setButtonPosition() {
        initBtnPosition();
        int size = mTasks.size();
        for(int i = 0; i<size; i++) {
            if(mTasks.get(i).isActive()) {
                increaseButtonPosition();
            }
        }
    }

    @Override
    public int getCount() {
        return mTasks.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == getBtnPosition()) {
            return ADD_BUTTON;
        } else {
            return mTasks.get(position);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View rowView = view;
        if (rowView == null) {
            if(type == SECTION_ADD_BUTTON) {
                rowView = layoutInflater.inflate(R.layout.add_taskview, viewGroup, false);
            }
            else {
                rowView = layoutInflater.inflate(R.layout.task_item, viewGroup, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.complete = (CheckBox) rowView.findViewById(R.id.complete);
                viewHolder.description = (TextView) rowView.findViewById(R.id.description);
                viewHolder.delete = (Button) rowView.findViewById(R.id.delete);
                rowView.setTag(viewHolder);
            }
        }
        bindView(type, rowView, i);

        return rowView;
    }

    private void bindView(int type, View view, int i) {
        if(type == SECTION_ADD_BUTTON) {
            bindView_addButton(view);
        } else {
            bindView_task(view, i);
        }
    }

    private void bindView_task(View view, int i) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.ref = getDataPosition(i);

        final Task task = mTasks.get(viewHolder.ref);//(Task) getItem(viewHolder.ref);

        viewHolder.complete.setChecked(task.isCompleted());

        viewHolder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(task.isCompleted()) {
                    // to active task
//                    mItemListener.onActivateTaskClick(task);
                } else {
                    // to completed
//                    mItemListener.onCompleteTaskClick(task);

                }
            }
        });

        viewHolder.description.setText(task.getDescription());
        viewHolder.description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mItemListener.onDescriptionFocusChanged(
                            viewHolder.description.getText().toString(),
                            task.getId(),
                            viewHolder.ref
                    );
                }
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onDeleteTask(task);
            }
        });
    }

    private void bindView_addButton(View view) {
        Button addBtn = (Button) view.findViewById(R.id.add_taskview_bt);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mItemListener.onAddTaskButtonClick(getBtnPosition());
            }
        });
    }

    public void replaceData(List<Task> tasks) {
        setList(tasks);
        setButtonPosition();

        notifyDataSetChanged();
    }

    private void setList(List<Task> list) {
        mTasks = list;
    }

    @Override
    public void drop(int from, int to) {

    }

    public class ViewHolder {
        CheckBox complete;
        TextView description;
        public Button delete;
        public int ref;
    }

    public interface TaskItemListener {

        void onCompleteTaskClick(Task task);

        void onActivateTaskClick(Task task);

        void onAddTaskButtonClick(int newActiveTaskPosition);

        void onDescriptionFocusChanged(String description, String taskId, int taskOrder);

        void onDeleteTask(Task task);
    }
}
