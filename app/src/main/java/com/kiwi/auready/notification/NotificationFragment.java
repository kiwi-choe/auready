package com.kiwi.auready.notification;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready.R;
import com.kiwi.auready.data.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements NotificationContract.View {

    public static final String TAG = "Tag_NotiFragment";

    private NotificationContract.Presenter mPresenter;

    private View mRoot;
    private ListView mListView;
    private TextView mNoNotificationView;
    private NotificationsAdapter mListAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new NotificationsAdapter(new ArrayList<Notification>(0), mItemListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRoot = inflater.inflate(R.layout.fragment_notification, container, false);

        mListView = (ListView) mRoot.findViewById(R.id.notification_list);
        mListView.setAdapter(mListAdapter);
        mNoNotificationView = (TextView) mRoot.findViewById(R.id.no_notification_view);

        return mRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();
    }

    @Override
    public void showNotifications(List<Notification> notifications) {
        mListAdapter.replaceData(notifications);

        mNoNotificationView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoNotification() {
        mListView.setVisibility(View.GONE);
        mNoNotificationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAcceptFriendRequestSuccessUI(String fromUserName) {
        String message = getString(R.string.friend_request_accept, fromUserName);
        Snackbar.make(mRoot, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showDeleteFriendRequestSuccessUI(String fromUserId) {
        Log.d(TAG, "Delete friend request is succeed");
    }

    @Override
    public void setPresenter(NotificationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /*
    * Listener for ListView
    * */
    interface NotificationItemListener {
        void onFriendRequestItemClicked(boolean isAccept, String fromUserId, String fromUserName, int notificationId);
    }

    NotificationItemListener mItemListener = new NotificationItemListener() {
        @Override
        public void onFriendRequestItemClicked(boolean isAccept, String fromUserId, String fromUserName, int notificationId) {
            if(isAccept) {
                mPresenter.acceptFriendRequest(fromUserId, fromUserName, notificationId);
            } else {
                mPresenter.deleteFriendRequest(fromUserId);
            }
        }
    };
}
