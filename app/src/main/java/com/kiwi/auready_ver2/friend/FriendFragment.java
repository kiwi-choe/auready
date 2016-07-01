package com.kiwi.auready_ver2.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

public class FriendFragment extends Fragment implements FriendContract.View {

    private FriendContract.Presenter mPresenter;

    private FriendsAdapter mListAdapter;

    private LinearLayout mFriendsView;
    private LinearLayout mNoSearchedEmailView;

    public FriendFragment() {
        // Required empty public constructor
    }

    public static FriendFragment newInstance() {
        return new FriendFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new FriendsAdapter(new ArrayList<Friend>(0));
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
        ListView listView = (ListView) root.findViewById(R.id.friend_list);
        listView.setAdapter(mListAdapter);
        mFriendsView = (LinearLayout) root.findViewById(R.id.friend_list_layout);
        mNoSearchedEmailView = (LinearLayout) root.findViewById(R.id.no_searched_email_layout);

         return root;
    }

    @Override
    public void showFriends(List<Friend> friendList) {

        mListAdapter.replaceData(friendList);

        mFriendsView.setVisibility(View.VISIBLE);
        mNoSearchedEmailView.setVisibility(View.GONE);
    }

    @Override
    public void showSearchedEmailList(ArrayList<String> searchedEmailList) {

    }

    @Override
    public void showNoResultByEmail() {

    }

    @Override
    public void setPresenter(@NonNull FriendContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    private class FriendsAdapter extends BaseAdapter {

        private List<Friend> mFriends;

        public FriendsAdapter(List<Friend> friends) {
            setList(friends);
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
            if(rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.friend_item, parent, false);
            }

            String email = getItem(position).getEmail();
            TextView txtEmail = (TextView) rowView.findViewById(R.id.txt_email);
            txtEmail.setText(email);

            return rowView;
        }

        public void replaceData(List<Friend> friendList) {
            setList(friendList);
            notifyDataSetChanged();
        }
    }
}
