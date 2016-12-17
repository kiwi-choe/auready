package com.kiwi.auready_ver2.taskheads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class TaskHeadsAdapter extends BaseAdapter {

    private final TaskHeadsFragment.TaskHeadItemListener mItemListener;
    private List<TaskHead> mTaskHeads;

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.taskhead_item, viewGroup, false);
        }

        final TaskHead taskHead = getItem(i);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemListener.onTaskHeadItemClick(taskHead.getId());
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
        return rowView;
    }
}