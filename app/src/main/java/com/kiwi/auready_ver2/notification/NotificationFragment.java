package com.kiwi.auready_ver2.notification;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;

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
        Context context = getActivity().getApplicationContext();

        // Create Singleton AccessTokenStore
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(context);
        String accessToken = accessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, null);

        mListAdapter = new NotificationsAdapter(context, new ArrayList<Notification>(0), mItemListener);
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
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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
    public void showAcceptFriendRequestSuccessUI(String fromUserId) {
        String message = getString(R.string.friend_request_accept, fromUserId);
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
        void onFriendRequestItemClicked(boolean isAccept, String fromUserId, int notificationId);
    }

    NotificationItemListener mItemListener = new NotificationItemListener() {
        @Override
        public void onFriendRequestItemClicked(boolean isAccept, String fromUserId, int notificationId) {
            if(isAccept) {
                mPresenter.acceptFriendRequest(fromUserId, notificationId);
            } else {
                mPresenter.deleteFriendRequest(fromUserId);
            }
        }
    };
}
