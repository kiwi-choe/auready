package com.kiwi.auready_ver2.taskheads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
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

    float startPos = -1;
    float endPos = -1;
    private boolean mIsActionMode = false;

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
            viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.taskhead_title);
            viewHolder.memberTextView = (TextView) rowView.findViewById(R.id.taskhead_member_list);
            viewHolder.reorderImage = (ImageView) rowView.findViewById(R.id.reorder);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

//        viewHolder.reorderImage.setVisibility(mIsActionMode ? View.VISIBLE : View.GONE);

        final TaskHead taskHead = getItem(position);

        viewHolder.titleTextView.setText(taskHead.getTitle());
        viewHolder.memberTextView.setText("궈니, 위니, 나무");

        if (mSelection.get(position) != null) {
            rowView.setSelected(true);
        } else {
            rowView.setSelected(false);
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

    public List<TaskHead> getTaskHeads() {
        return mTaskHeads;
    }

    public void setActionMode(boolean mIsActionMode) {
        this.mIsActionMode = mIsActionMode;
    }

    private class ViewHolder {
        TextView titleTextView;
        TextView memberTextView;
        ImageView reorderImage;
    }

    public void reorder(int from, int to) {
        TaskHead fromTaskHead = mTaskHeads.remove(from);
        mTaskHeads.add(to, fromTaskHead);

        notifyDataSetChanged();
    }

    // animation
    public void startAnimation(View view, boolean isDelete, long duration, Interpolator interpolator) {
        final float distance = view.getResources().getDimension(R.dimen.reorder_start_trans_x);
        startPos = isDelete ? 0 : distance;
        endPos = isDelete ? distance : 0;

        ArrayList<View> viewList = new ArrayList<>();

        ImageView imageview = (ImageView) view.findViewById(R.id.reorder);
        imageview.setTranslationX(endPos);
        imageview.animate().translationX(startPos);
        viewList.add(imageview);

        for (View animatedView : viewList) {
            animatedView.animate().setDuration(duration).setInterpolator(interpolator).start();
        }
    }
}