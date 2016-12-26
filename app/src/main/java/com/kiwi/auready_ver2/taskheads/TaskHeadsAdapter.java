package com.kiwi.auready_ver2.taskheads;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class TaskHeadsAdapter extends BaseAdapter implements View.OnTouchListener {

    private final TaskHeadsFragment.TaskHeadItemListener mItemListener;
    private List<TaskHead> mTaskHeads;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();

    public TaskHeadsAdapter(List<TaskHead> taskHeads, TaskHeadsFragment.TaskHeadItemListener itemListener) {
        setList(taskHeads);
        mItemListener = itemListener;
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
            viewHolder.reorderBtn = (Button) rowView.findViewById(R.id.reorder);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        final TaskHead taskHead = getItem(position);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemListener.onTaskHeadItemClick(taskHead.getId(), position);

            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mItemListener.onTaskHeadItemLongClick(view, position);
                return true;
            }
        });

        viewHolder.titleTV.setText(taskHead.getTitle());

        final View finalRowView = rowView;
        viewHolder.reorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onReorder(finalRowView, viewHolder.lastTouchedX, viewHolder.lastTouchedY);
            }
        });

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

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public List<TaskHead> getCurrentCheckedTaskHeads() {
        ArrayList<TaskHead> taskHeads = new ArrayList<>();

        for (int i = 0; i < mSelection.size(); i++) {
            if (isPositionChecked(i)) {
                taskHeads.add(getItem(i));
                Log.d("MY_LOG", "i : " + i);
            }
        }

        return taskHeads;
    }

    // for drag and drop (reorder)
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.lastTouchedX = motionEvent.getX();
        viewHolder.lastTouchedY = motionEvent.getY();

        return false;
    }

    private class ViewHolder {
        TextView titleTV;
        Button reorderBtn;

        float lastTouchedX;
        float lastTouchedY;
    }
}