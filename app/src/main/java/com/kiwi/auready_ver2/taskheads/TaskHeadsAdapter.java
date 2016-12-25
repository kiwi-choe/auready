package com.kiwi.auready_ver2.taskheads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class TaskHeadsAdapter extends BaseAdapter {

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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.taskhead_item, viewGroup, false);
        }

        final TaskHead taskHead = getItem(position);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemListener.onTaskHeadItemClick(taskHead.getId());
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mItemListener.onTaskHeadItemLongClick(view, position);
                return true;
            }
        });

        TextView titleTV = (TextView) rowView.findViewById(R.id.taskhead_title);
        titleTV.setText(taskHead.getTitle());
        Button deleteBt = (Button) rowView.findViewById(R.id.delete_bt);
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onDeleteClick(taskHead);
            }
        });

        if (mSelection.get(position) != null) {
            rowView.setBackgroundColor(rowView.getResources().getColor(android.R.color.darker_gray));
        } else {
            rowView.setBackgroundColor(rowView.getResources().getColor(android.R.color.background_light));
        }

        return rowView;
    }

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

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }
}