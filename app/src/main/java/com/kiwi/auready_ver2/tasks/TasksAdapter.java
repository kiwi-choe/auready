package com.kiwi.auready_ver2.tasks;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.customlistview.DragSortListView;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 * //// FIXME: 9/1/16 will change to CustomAdapter
 */
public class TasksAdapter extends BaseAdapter implements
        DragSortListView.DropListener {

    private static final int SECTION_TASKS = 0;
    private static final int SECTION_ADD_BUTTON = 1;

    private static final int NUM_OF_VIEW_TYPES = 2;
    private static final String ADD_BUTTON = "Add button";

    private List<Task> mTasks;
    private TaskItemListener mItemListener;

    private int mAddBtnPos;

    public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
        super();
        initBtnPosition();
        setList(tasks);
        mItemListener = itemListener;
    }

    @Override
    public int getItemViewType(int i) {
        if(i == mAddBtnPos)
            return SECTION_ADD_BUTTON;
        else
            return SECTION_TASKS;
    }

    @Override
    public int getViewTypeCount() {
        return NUM_OF_VIEW_TYPES;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return i != mAddBtnPos;
    }

    @Override
    public int getCount() {
        return mTasks.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if(i == mAddBtnPos)
            return ADD_BUTTON;
        else
            return mTasks.get(i);
    }

    public int getDataPosition(int position) {
        return position > mAddBtnPos ? position - 1 : position;
    }

    public void initBtnPosition() {
        mAddBtnPos = 0;
    }

    public int getBtnPosition() {
        return mAddBtnPos;
    }

    public void increaseButtonPosition(){
        mAddBtnPos++;
    }

    public void decreaseButtonPosition(){
        mAddBtnPos--;
    }

    @Override
    public long getItemId(int i) {
        return i;
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
                rowView = layoutInflater.inflate(R.layout.active_task_item, viewGroup, false);

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

        viewHolder.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(task.isCompleted()) {
                    // to active task
                    mItemListener.onActivateTaskClick(task);
                } else {
                    // to completed
                    mItemListener.onCompleteTaskClick(task);
                }
            }
        });

        viewHolder.description.setText(task.getDescription());
    }

    private void bindView_addButton(View view) {
        Button addBtn = (Button) view.findViewById(R.id.add_taskview_bt);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onAddTaskButtonClick();
            }
        });
    }

    public void replaceData(List<Task> tasks) {
        setList(tasks);
        notifyDataSetChanged();
    }

    public void setList(List<Task> list) {
        mTasks = list;

        Log.d("test", "entered setList(): " + mTasks.size());
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

        void onAddTaskButtonClick();
    }
}
