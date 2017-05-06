package com.kiwi.auready_ver2.notification;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Notification ListView adapter
 */

class NotificationsAdapter extends BaseAdapter {

    private final static String TAG = "TAG_NotificationAdapter";
    /*
    * 1. friend request
    * 2. show message
    * */
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_FRIEND_REQUEST = 0;
    private static final int VIEW_TYPE_SHOW_MESSAGE = 1;

    private final Context mContext;
    private List<Notification> mNotifications = new ArrayList<>();
    private final NotificationFragment.NotificationItemListener mItemListener;

    public NotificationsAdapter(Context context, List<Notification> notifications, NotificationFragment.NotificationItemListener itemListener) {
        mContext = context;
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
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = mNotifications.get(position);
        int type = notification.getType();
        if(type == Notification.TYPES.friend_request.getIntType()) {
            return VIEW_TYPE_FRIEND_REQUEST;
        } else {
            return VIEW_TYPE_SHOW_MESSAGE;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (getItemViewType(position) == VIEW_TYPE_FRIEND_REQUEST) {
            return getFriendRequestView(position, view, parent);
        } else { //if(getItemViewType(position) == VIEW_TYPE_SHOW_MESSAGE) {
            return getShowMessageView(position, view, parent);
        }
    }

    private View getShowMessageView(int position, View view, ViewGroup parent) {
        View rowView = view;
        ShowMessage_ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.noti_item_show_message, parent, false);

            TextView message = (TextView) rowView.findViewById(R.id.txt_noti_message);

            viewHolder = new ShowMessage_ViewHolder(message);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ShowMessage_ViewHolder) view.getTag();
        }

        // Bind views
        Notification notification = getItem(position);
        String message = notification.getMessage();
        viewHolder.mMessage.setText(message);

        return rowView;
    }

    private View getFriendRequestView(int position, View view, ViewGroup parent) {

        View rowView = view;
        FriendRequest_ViewHolder viewHolder;
        if (rowView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.noti_item_friend_request, parent, false);

            TextView message = (TextView) rowView.findViewById(R.id.txt_noti_message);
            Button acceptBt = (Button) rowView.findViewById(R.id.bt_accept_friend_request);
            Button deleteBt = (Button) rowView.findViewById(R.id.bt_delete_friend_request);
            viewHolder = new FriendRequest_ViewHolder(message, acceptBt, deleteBt);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (FriendRequest_ViewHolder) view.getTag();
        }

        // Bind views
        Notification notification = getItem(position);
        if(notification == null) {
            Log.d(TAG, "notification is null");
        }

        Log.d(TAG, notification.toString());
        // Set mMessage
        String message = notification.getMessage();
        Log.d(TAG, "message - " + message);
        viewHolder.mMessage.setText(message);
        // Set buttons
        final String fromUserId = notification.getFromUserId();
        final int notificationId = notification.getId();
        viewHolder.mAcceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onFriendRequestItemClicked(true, fromUserId, notificationId);
            }
        });
        viewHolder.mDeleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onFriendRequestItemClicked(false, fromUserId, notificationId);
            }
        });

        return rowView;
    }

    void replaceData(List<Notification> notifications) {
        setList(notifications);
        notifyDataSetChanged();
    }

    private class FriendRequest_ViewHolder {
        TextView mMessage;
        Button mAcceptBt;
        Button mDeleteBt;

        FriendRequest_ViewHolder(TextView message, Button acceptBt, Button deleteBt) {
            mMessage = message;
            mAcceptBt = acceptBt;
            mDeleteBt = deleteBt;
        }
    }

    private class ShowMessage_ViewHolder {
        TextView mMessage;

        ShowMessage_ViewHolder(TextView message) {
            mMessage = message;
        }
    }
}
