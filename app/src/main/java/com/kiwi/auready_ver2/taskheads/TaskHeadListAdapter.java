package com.kiwi.auready_ver2.taskheads;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class TaskHeadListAdapter extends BaseAdapter {

    private final TaskHeadItemListener mItemListener;
    private List<TaskHead> mTaskHeads;

    public TaskHeadListAdapter(List<TaskHead> taskHeads, TaskHeadItemListener itemListener) {
        setList(taskHeads);
        mItemListener = itemListener;
    }

    @Override
    public int getCount() {
        return mTaskHeads.size();
    }

    @Override
    public Object getItem(int i) {
        return mTaskHeads.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View rowView = view;
        if(rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.taskhead_item, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.titleTV = (TextView) rowView.findViewById(R.id.title);

            rowView.setTag(viewHolder);
        }
        bindView(rowView, position);
        return rowView;
    }

    private void bindView(View view, int i) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        final TaskHead taskHead = mTaskHeads.get(i);

        viewHolder.titleTV.setText(taskHead.getTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onTaskHeadClick(taskHead);
            }
        });
    }

    public void setList(@NonNull List<TaskHead> list) {
        mTaskHeads = checkNotNull(list);
    }

    public void replaceData(List<TaskHead> taskHeads) {
        setList(taskHeads);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public TextView titleTV;
    }

    public interface TaskHeadItemListener {
        void onTaskHeadClick(TaskHead taskHead);
    }
}
