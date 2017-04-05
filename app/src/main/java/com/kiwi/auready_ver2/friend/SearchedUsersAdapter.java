package com.kiwi.auready_ver2.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Searched users adapter
 * on FindView
 */
public class SearchedUsersAdapter extends BaseAdapter {

    private final FindFragment.SearchedUserItemListener mItemListener;
    private List<SearchedUser> mSearchedUsers = new ArrayList<>();

    public SearchedUsersAdapter(List<SearchedUser> searchedUsers, FindFragment.SearchedUserItemListener itemListener) {
        setList(searchedUsers);
        mItemListener = itemListener;
    }

    private void setList(List<SearchedUser> searchedUsers) {
        mSearchedUsers.clear();
        mSearchedUsers.addAll(searchedUsers);
    }

    @Override
    public int getCount() {
        return mSearchedUsers.size();
    }

    @Override
    public SearchedUser getItem(int position) {
        return mSearchedUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = view;
        final ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.searched_user_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.txt_user_name);
            viewHolder.email = (TextView) rowView.findViewById(R.id.txt_user_email);
            viewHolder.addFriendRequestBt = (Button) rowView.findViewById(R.id.bt_add_friend_request);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Bind views
        final SearchedUser user = getItem(position);

        // Filter 1. me, 2. status; PENDING
        String myEmail = AccessTokenStore.getInstance().
                getStringValue(AccessTokenStore.USER_EMAIL, "");
        if (myEmail.equals(user.getUserInfo().getEmail())) {
            viewHolder.addFriendRequestBt.setVisibility(View.GONE);
        } else {
            viewHolder.addFriendRequestBt.setVisibility(View.VISIBLE);
        }

        viewHolder.name.setText(user.getUserInfo().getName());
        viewHolder.email.setText(user.getUserInfo().getEmail());
        viewHolder.addFriendRequestBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getStatus() == SearchedUser.PENDING) {
                    mItemListener.onClickUserPendingStatus(user.getUserInfo().getName());
                } else {
                    mItemListener.onClickUser(user);
                }
            }
        });

        return rowView;
    }

    public void replaceData(List<SearchedUser> searchedUsers) {
        setList(searchedUsers);
        notifyDataSetChanged();
    }

    public void updateStatusOf(String name) {
        int len = mSearchedUsers.size();
        for (int i = 0; i < len; i++) {
            if(mSearchedUsers.get(i).getUserInfo().getName().equals(name)) {
                SearchedUser updatedUser = new SearchedUser(mSearchedUsers.get(i).getUserInfo(), SearchedUser.PENDING);
                mSearchedUsers.set(i, updatedUser);
            }
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView name;
        TextView email;
        Button addFriendRequestBt;
    }
}
