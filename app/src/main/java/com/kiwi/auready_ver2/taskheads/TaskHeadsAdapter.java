package com.kiwi.auready_ver2.taskheads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsAdapter extends BaseAdapter {

    private List<TaskHead> mTaskHeads;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();

    float mCheckboxStartX = -1;
    float mCheckboxEndX = -1;

    public TaskHeadsAdapter(List<TaskHead> taskHeads) {
        setList(taskHeads);
    }

    public void replaceData(List<TaskHead> taskHeads) {
        setList(taskHeads);
        notifyDataSetChanged();
    }

    private void setList(List<TaskHead> taskHeads) {
        mTaskHeads = checkNotNull(taskHeads);
    }

    @Override
    public int getCount() {
        return mTaskHeads.size();
    }

    @Override
    public TaskHead getItem(int i) {
        return mTaskHeads.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View view, ViewGroup viewGroup) {
        View rowView = view;
        final ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.taskhead_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.delete_check_box);
            viewHolder.titleTV = (TextView) rowView.findViewById(R.id.taskhead_title);
            viewHolder.reorderImage = (ImageView) rowView.findViewById(R.id.reorder);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        HoldPosition(rowView);
        final TaskHead taskHead = getItem(position);

        viewHolder.titleTV.setText(taskHead.getTitle());

        if (mSelection.get(position) != null) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        rowView.setBackground(rowView.getResources().getDrawable(R.drawable.listview_state_drawable));

        return rowView;
    }

    // for delete Item
    public void clearSelection() {
        mSelection = new HashMap<>();
        notifyDataSetChanged();
    }

    public void setNewSelection(int position, boolean checked) {
        mSelection.put(position, checked);
        notifyDataSetChanged();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelection.size();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public List<TaskHead> getCurrentCheckedTaskHeads() {
        ArrayList<TaskHead> taskHeads = new ArrayList<>();
        int count = getCount();
        for (int i = 0; i < count; i++) {
            if (isPositionChecked(i)) {
                taskHeads.add(getItem(i));
            }
        }

        return taskHeads;
    }

    public List<TaskHead> getTaskHeads() {
        return mTaskHeads;
    }

    private class ViewHolder {
        CheckBox checkBox;
        TextView titleTV;
        ImageView reorderImage;
    }

    public void reorder(int from, int to) {
        TaskHead fromTaskHead = mTaskHeads.remove(from);
        mTaskHeads.add(to, fromTaskHead);

        notifyDataSetChanged();
    }

    // animation
    public void startAnimation(View view, boolean isDelete) {
        final float distance = view.getResources().getDimension(R.dimen.checkbox_start_trans_x);
        mCheckboxStartX = isDelete ? distance : 0;
        mCheckboxEndX = isDelete ? 0 : distance;

        CheckBox checkbox = (CheckBox) view.findViewById(R.id.delete_check_box);
        checkbox.setTranslationX(mCheckboxStartX);
        checkbox.animate().translationX(mCheckboxEndX);

        TextView textview = (TextView) view.findViewById(R.id.taskhead_title);
        textview.setTranslationX(mCheckboxStartX - distance);
        textview.animate().translationX(mCheckboxEndX - distance);

        ImageView imageview = (ImageView) view.findViewById(R.id.reorder);
        imageview.setTranslationX(mCheckboxStartX - distance);
        imageview.animate().translationX(mCheckboxEndX - distance);
    }

    private void HoldPosition(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        final float distance = view.getResources().getDimension(R.dimen.checkbox_start_trans_x);
        if (mCheckboxStartX == -1) {
            return;
        }

        if (holder.checkBox.getTranslationX() != mCheckboxEndX) {
            holder.checkBox.setTranslationX(mCheckboxEndX);
            holder.titleTV.setTranslationX(mCheckboxEndX - distance);
            holder.reorderImage.setTranslationX(mCheckboxEndX - distance);
        }
    }
}