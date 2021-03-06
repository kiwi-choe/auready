package com.kiwi.auready.taskheads;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiwi.auready.R;
import com.kiwi.auready.data.TaskHead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsAdapter extends BaseAdapter {

    static final String TAG = "TaskHeadsAdatper";
    private List<TaskHead> mTaskHeads;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();

    private boolean mIsActionMode = false;
    private int mReorderWidth = -1;

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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.taskhead_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.taskhead_title);
            viewHolder.reorderImage = (ImageView) view.findViewById(R.id.reorder);
            viewHolder.pickerColorImage = (ImageView) view.findViewById(R.id.taskhead_picker_color);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Bind views
        final TaskHead taskHead = getItem(position);

        viewHolder.titleTextView.setText(taskHead.getTitle());

        // Set color to SelectingImageView
        int colorOfTaskHead = taskHead.getColor();
        StateListDrawable pickerColorSelector = (StateListDrawable) viewHolder.pickerColorImage.getBackground();
        DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) pickerColorSelector.getConstantState();
        Drawable[] children = dcs.getChildren();
        for (Drawable child : children) {
            // 1. selected status item
            if (child instanceof LayerDrawable) {
                LayerDrawable selectedDrawable = (LayerDrawable) child;
                GradientDrawable pickerColor = (GradientDrawable) selectedDrawable.getDrawable(0);
                pickerColor.setColor(colorOfTaskHead);
            }
            // 2. default status item
            if (child instanceof GradientDrawable) {
                GradientDrawable pickerColor = (GradientDrawable) child;
                pickerColor.setColor(colorOfTaskHead);
            }
        }

        if (mSelection.get(position) != null) {
            viewHolder.pickerColorImage.setSelected(true);
        } else {
            viewHolder.pickerColorImage.setSelected(false);
        }

        if (mReorderWidth == -1) {
            mReorderWidth = (int) view.getResources().getDimension(R.dimen.reorder_start_trans_x);
        }

        float reorderEndPos = mIsActionMode ? mReorderWidth : 0;
        viewHolder.reorderImage.setTranslationX(reorderEndPos);

        return view;
    }

    // for delete Item
    public void clearSelection() {
        mSelection = new HashMap<>();
        notifyDataSetChanged();
    }

    public void toggleSelectedItem(int position) {
        if (mSelection.get(position) == null) {
            mSelection.put(position, true);
        } else {
            mSelection.remove(position);
        }

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
        for (int i = 0; i < mTaskHeads.size(); i++) {
            Log.d("test_reorder", mTaskHeads.get(i).getTitle() + ", order: " + mTaskHeads.get(i).getOrder());
        }
        return mTaskHeads;
    }

    public void setActionMode(boolean mIsActionMode) {
        this.mIsActionMode = mIsActionMode;
    }

    private class ViewHolder {
        ImageView pickerColorImage;
        TextView titleTextView;
        ImageView reorderImage;
    }

    public void reorder(int from, int to) {
        TaskHead fromTaskHead = mTaskHeads.remove(from);
        mTaskHeads.add(to, fromTaskHead);

        Log.d("test_reorder", "to: " + to + " from: " + from);

        notifyDataSetChanged();
    }

    // animation
    public void startAnimation(View view, boolean isDelete, long duration, Interpolator interpolator) {
        if (view == null || view.getTag() == null) {
            Log.d(TAG, "fail startAnimation! view  : " + view + ", view.getTag() : " + view.getTag());
            return;
        }

        if (view.getTag() instanceof ViewHolder == false) {
            Log.d(TAG, "fail startAnimation! view.getTag() instanceof ViewHolder == false");
            return;
        }

        final float distance = view.getResources().getDimension(R.dimen.reorder_start_trans_x);
        float startPos = isDelete ? 0 : distance;
        float endPos = isDelete ? distance : 0;

        ArrayList<View> viewList = new ArrayList<>();
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ImageView reorderImage = viewHolder.reorderImage;
        reorderImage.setTranslationX(startPos);
        reorderImage.animate().translationX(endPos);
        viewList.add(reorderImage);

        for (View animatedView : viewList) {
            animatedView.animate().setDuration(duration).setInterpolator(interpolator).start();
        }
    }
}