package com.kiwi.auready_ver2.taskheads;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
            viewHolder.titleTV = (TextView) rowView.findViewById(R.id.taskhead_title);
            viewHolder.reorderImage = (ImageView) rowView.findViewById(R.id.reorder);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        final TaskHead taskHead = getItem(position);

        viewHolder.titleTV.setText(taskHead.getTitle());

        if (mSelection.get(position) != null) {
            rowView.setBackgroundColor(rowView.getResources().getColor(android.R.color.darker_gray));
        } else {
            rowView.setBackgroundColor(rowView.getResources().getColor(android.R.color.background_light));
        }

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

    private class ViewHolder {
        TextView titleTV;
        ImageView reorderImage;
    }

    public void reorder(int from, int to) {
        TaskHead fromTaskHead = mTaskHeads.remove(from);
        mTaskHeads.add(to, fromTaskHead);
        notifyDataSetChanged();
    }
}