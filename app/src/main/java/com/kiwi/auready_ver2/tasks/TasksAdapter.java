package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
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

    private boolean mIsReorderDisplayed = false;
    private boolean mIsDeleteDisplayed = false;

    private int mViewWidth = -1;
    private int mReorderWidth = -1;
    private int mDeleteWidth = -1;
    private int mCheckBoxWidth = -1;


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

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskItemListener.onTaskDeleteButtonClicked(task.getMemberId(), task.getId());
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

        if (mReorderWidth == -1) {
            viewHolder.reorderImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mReorderWidth = viewHolder.reorderImage.getMeasuredWidth();
        }

        if (mDeleteWidth == -1) {
            viewHolder.deleteButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mDeleteWidth = viewHolder.deleteButton.getMeasuredWidth();
        }

        if (mCheckBoxWidth == -1) {
            viewHolder.checkBox.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mCheckBoxWidth = viewHolder.checkBox.getMeasuredWidth();
        }

        float reorderEndPos = mIsReorderDisplayed ? 0 : -mReorderWidth;
        viewHolder.reorderImage.setTranslationX(reorderEndPos);
        viewHolder.checkBox.setTranslationX(reorderEndPos);
        viewHolder.editText.setTranslationX(reorderEndPos);

        float deleteEndPos = mIsDeleteDisplayed ? 0 : mDeleteWidth;
        viewHolder.deleteButton.setTranslationX(deleteEndPos);

        viewHolder.editText.setPaddingRelative(0, 0, (mIsDeleteDisplayed ? mDeleteWidth : 0) + (mIsReorderDisplayed ? mReorderWidth : 0), 0);

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

    // animation
    public void startReorderAnimation(View view, long duration, Interpolator interpolator) {
        if (view == null || view.getTag() == null) {
            return;
        }

        if (view.getTag() instanceof ViewHolder == false) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        float startPos = mIsReorderDisplayed ? -mReorderWidth : 0;
        float endPos = mIsReorderDisplayed ? 0 : -mReorderWidth;

        ArrayList<View> viewList = new ArrayList<>();
        viewHolder.reorderImage.setTranslationX(startPos);
        viewHolder.reorderImage.animate().translationX(endPos);
        viewList.add(viewHolder.reorderImage);

        viewHolder.checkBox.setTranslationX(startPos);
        viewHolder.checkBox.animate().translationX(endPos);
        viewList.add(viewHolder.checkBox);

        viewHolder.editText.setTranslationX(startPos);
        viewHolder.editText.animate().translationX(endPos);
        viewList.add(viewHolder.editText);

        for (View animatedView : viewList) {
            animatedView.animate().setDuration(duration).setInterpolator(interpolator).start();
        }

        viewHolder.editText.setPaddingRelative(0, 0, (mIsDeleteDisplayed ? mDeleteWidth : 0) + (mIsReorderDisplayed ? mReorderWidth : 0), 0);
    }

    public void starDeleteAnimation(View view, long duration, Interpolator interpolator) {
        if (view == null || view.getTag() == null) {
            return;
        }

        if (view.getTag() instanceof ViewHolder == false) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        float startPos = mIsDeleteDisplayed ? mDeleteWidth : 0;
        float endPos = mIsDeleteDisplayed ? 0 : mDeleteWidth;

        ArrayList<View> viewList = new ArrayList<>();
        viewHolder.deleteButton.setTranslationX(startPos);
        viewHolder.deleteButton.animate().translationX(endPos);
        viewList.add(viewHolder.deleteButton);

        for (View animatedView : viewList) {
            animatedView.animate().setDuration(duration).setInterpolator(interpolator).start();
        }

        viewHolder.editText.setPaddingRelative(0, 0, (mIsDeleteDisplayed ? mDeleteWidth : 0) + (mIsReorderDisplayed ? mReorderWidth : 0), 0);
    }

    public void toggleReorderDisplayed() {
        mIsReorderDisplayed = !mIsReorderDisplayed;
    }

    public void toggleDeleteDisplayed() {
        mIsDeleteDisplayed = !mIsDeleteDisplayed;
    }
}
