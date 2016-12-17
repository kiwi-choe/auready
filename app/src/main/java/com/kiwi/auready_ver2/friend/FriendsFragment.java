package com.kiwi.auready_ver2.friend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class FriendsFragment extends Fragment implements FriendsContract.View {

    public static final String TAG_FRIENDFRAG = "TAG_FriendFragment";
    public static final String ARG_FRIENDS = "arg_friends";

    private FriendsContract.Presenter mPresenter;

    private FriendsAdapter mListAdapter;

    private LinearLayout mFriendsView;
    private LinearLayout mNoFriendsView;
    private LinearLayout mNoSearchedEmailView;
    private TextView mLoadingIndicator;
    private ListView mListView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new FriendsAdapter(new ArrayList<Friend>(0), mItemListener);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_friend, container, false);

        // Set up Friends view
        mListView = (ListView) root.findViewById(R.id.friend_list);
        mListView.setAdapter(mListAdapter);
        mFriendsView = (LinearLayout) root.findViewById(R.id.friend_list_layout);

        // Set up no friends and searching user view
        mNoFriendsView = (LinearLayout) root.findViewById(R.id.no_friends_layout);
        mNoSearchedEmailView = (LinearLayout) root.findViewById(R.id.no_searched_email_layout);
        mLoadingIndicator = (TextView) root.findViewById(R.id.loading_indicator);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_findview) {
            openFindView();
            return true;
        } else if (id == R.id.action_confirm) {
//            List<Friend> selectedFriends = mListAdapter.getCheckedItems();
//            setResultToTaskHeadDetailView(selectedFriends);
        }
        return false;
    }

    private void openFindView() {
        Intent intent =
                new Intent(getActivity(), FindActivity.class);
        startActivity(intent);
    }

    @Override
    public void showFriends(List<Friend> friendList) {

        mListAdapter.replaceData(friendList);

        mFriendsView.setVisibility(View.VISIBLE);
        mNoFriendsView.setVisibility(View.GONE);
    }

    @Override
    public void showSearchedEmailList(ArrayList<String> searchedEmailList) {

    }

    @Override
    public void showNoResultByEmail() {

    }

    @Override
    public void setPresenter(@NonNull FriendsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (active) {
            mLoadingIndicator.setText(R.string.loading_friends);
        } else {
            mLoadingIndicator.setText("");
        }
    }

    @Override
    public void setResultToTaskHeadDetailView(List<Friend> selectedFriends) {

    }

    @Override
    public void showNoFriends() {
        mFriendsView.setVisibility(View.GONE);
        mNoFriendsView.setVisibility(View.VISIBLE);
    }

    private void showDeleteFriendAlert(@NonNull final Friend clickedFriend) {
        checkNotNull(clickedFriend, "clickedFriend cannot be null!");

        // show popup view to confirm if delete the requested friend or cancel.
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.alert_title_confirm_delete_friend);
        dialog.setMessage(clickedFriend.getEmail() + R.string.alert_msg_confirm_delete_friend);

        // OK
        dialog.setPositiveButton(R.string.alert_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteFriend(clickedFriend.getId());
            }
        });
        // Cancel
        dialog.setNegativeButton(R.string.alert_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private class FriendsAdapter extends BaseAdapter {

        private List<Friend> mFriends;
        private FriendItemListener mItemListener;

        public FriendsAdapter(List<Friend> friends, FriendItemListener itemListener) {
            setList(friends);
            mItemListener = itemListener;
        }

        private void setList(List<Friend> friends) {
            mFriends = checkNotNull(friends);
        }

        @Override
        public int getCount() {
            return mFriends.size();
        }

        @Override
        public Friend getItem(int position) {
            return mFriends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.friend_item, parent, false);
            }

            final Friend friend = getItem(position);
            String name = friend.getName();
            TextView txtName = (TextView) rowView.findViewById(R.id.txt_name);
            txtName.setText(name);

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mItemListener.onLongClick(friend);
                    return true;    // true if the callback consumed the long click.
                }
            });
            return rowView;
        }

        public void replaceData(List<Friend> friendList) {
            setList(friendList);
            notifyDataSetChanged();
        }
    }

    public interface FriendItemListener {

        void onLongClick(Friend clickedFriend);
    }

    /*
    * Listener for clicks on friends in the ListView
    * */
    FriendItemListener mItemListener = new FriendItemListener() {
        @Override
        public void onLongClick(Friend clickedFriend) {
            showDeleteFriendAlert(clickedFriend);
        }
    };

}
