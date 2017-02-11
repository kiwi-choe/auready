package com.kiwi.auready_ver2.taskheads;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.util.view.CircleProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsAdapter extends BaseAdapter {

    static final String TAG = "TaskHeadsAdatper";
    private List<TaskHead> mTaskHeads;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();

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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.taskhead_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.taskhead_title);
            viewHolder.memberTextView = (TextView) view.findViewById(R.id.taskhead_member_list);
            viewHolder.reorderImage = (ImageView) view.findViewById(R.id.reorder);
            viewHolder.circleProgressBar = (CircleProgressBar) view.findViewById(R.id.circle_progress_bar);
            viewHolder.progressText = (TextView) view.findViewById(R.id.progress_text);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final TaskHead taskHead = getItem(position);

        viewHolder.titleTextView.setText(taskHead.getTitle());
        viewHolder.memberTextView.setText("궈니, 위니, 나무");

        if (mSelection.get(position) != null) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
        Log.d(TAG, "getview : " + view.getTag());

        return view;
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
        CircleProgressBar circleProgressBar;
        TextView progressText;
    }

    public void reorder(int from, int to) {
        TaskHead fromTaskHead = mTaskHeads.remove(from);
        mTaskHeads.add(to, fromTaskHead);

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
        reorderImage.setTranslationX(endPos);
        reorderImage.animate().translationX(startPos);
        viewList.add(reorderImage);

        CircleProgressBar circleProgressBar = viewHolder.circleProgressBar;
        circleProgressBar.setTranslationX(startPos);
        circleProgressBar.animate().translationX(endPos);
        viewList.add(circleProgressBar);

        TextView progressText = viewHolder.progressText;
        progressText.setTranslationX(startPos);
        progressText.animate().translationX(endPos);
        viewList.add(progressText);

        for (View animatedView : viewList) {
            animatedView.animate().setDuration(duration).setInterpolator(interpolator).start();
        }
    }
}