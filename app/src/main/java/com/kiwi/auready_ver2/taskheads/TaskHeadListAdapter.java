package com.kiwi.auready_ver2.taskheads;

import android.support.annotation.NonNull;
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
public class TaskHeadListAdapter extends BaseAdapter {

    private List<TaskHead> mTaskHeads;

    public TaskHeadListAdapter(List<TaskHead> taskHeads, TaskHeadItemListener itemListener) {
        setList(taskHeads);
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
//            viewHolder.deleteBtn = (Button) rowView.findViewById(R.id.delete_btn);
//            viewHolder.undoBtn = (Button) rowView.findViewById(R.id.undo_btn);

            rowView.setTag(viewHolder);
        }
        bindView(rowView, position);
        return rowView;
    }

    private void bindView(View view, final int position) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        final TaskHead taskHead = mTaskHeads.get(position);

        viewHolder.titleTV.setText(taskHead.getTitle());

//
//        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mTouchListener.processPendingDismisses(position);
//                Toast.makeText(v.getContext(), "delete!! : " + mTaskHeads.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//                mTouchListener.getLog();
//            }
//        });
//
//        viewHolder.undoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mTouchListener.undoPendingDismiss(position);
//                Toast.makeText(v.getContext(), "undo!! : " + mTaskHeads.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//                mTouchListener.getLog();
//            }
//        });
    }

    public void setList(@NonNull List<TaskHead> list) {
        mTaskHeads = checkNotNull(list);
        for(int i=0; i<10; i++){
            mTaskHeads.add(new TaskHead("title" + i));
        }
    }

    public void replaceData(List<TaskHead> taskHeads) {
        setList(taskHeads);
        notifyDataSetChanged();
    }

    public void removeData(int position){
        if(position >= mTaskHeads.size()){
            return;
        }

        mTaskHeads.remove(position);
        notifyDataSetChanged();
    }

//    private SwipeToDismissTouchListener mTouchListener;
//    public void setSwipeTouchListener(SwipeToDismissTouchListener touchListener){
//        mTouchListener = touchListener;
//    }

    private class ViewHolder {
        public TextView titleTV;
        public Button deleteBtn;
        public Button undoBtn;
    }

    public interface TaskHeadItemListener {

        void onClick(TaskHead taskHead);
        void onLongClick(TaskHead taskHead);
    }
}
