package com.kiwi.auready.notification;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready.R;
import com.kiwi.auready.data.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Notification ListView adapter
 */

class NotificationsAdapter extends BaseAdapter {

    private final static String TAG = "TAG_NotificationAdapter";

    private List<Notification> mNotifications = new ArrayList<>();
    private final NotificationFragment.NotificationItemListener mItemListener;

    public NotificationsAdapter(List<Notification> notifications, NotificationFragment.NotificationItemListener itemListener) {
        setList(notifications);
        mItemListener = itemListener;
    }

    private void setList(List<Notification> notifications) {
        mNotifications.clear();
        mNotifications.addAll(notifications);
    }

    @Override
    public int getCount() {
        return mNotifications.size();
    }

    @Override
    public Notification getItem(int position) {
        return mNotifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View rowView = view;
        ViewHolder viewHolder;
        if (rowView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.noti_item_friend_request, parent, false);

            TextView message = (TextView) rowView.findViewById(R.id.txt_noti_message);
            Button acceptBt = (Button) rowView.findViewById(R.id.bt_accept_friend_request);
            Button deleteBt = (Button) rowView.findViewById(R.id.bt_delete_friend_request);
            viewHolder = new ViewHolder(message, acceptBt, deleteBt);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Bind views
        Notification notification = getItem(position);

        Log.d(TAG, notification.toString());
        // Set mMessage
        String message = notification.getMessage();
        Log.d(TAG, "message - " + message);
        viewHolder.mMessage.setText(message);
        // Set buttons
        final String fromUserId = notification.getFromUserId();
        final String fromUserName = notification.getFromUserName();
        final int notificationId = notification.getId();
        viewHolder.mAcceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onFriendRequestItemClicked(true, fromUserId, fromUserName, notificationId);
            }
        });
        viewHolder.mDeleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onFriendRequestItemClicked(false, fromUserId, fromUserName, notificationId);
            }
        });

        return rowView;
    }

    void replaceData(List<Notification> notifications) {
        setList(notifications);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView mMessage;
        Button mAcceptBt;
        Button mDeleteBt;

        ViewHolder(TextView message, Button acceptBt, Button deleteBt) {
            mMessage = message;
            mAcceptBt = acceptBt;
            mDeleteBt = deleteBt;
        }
    }
}
