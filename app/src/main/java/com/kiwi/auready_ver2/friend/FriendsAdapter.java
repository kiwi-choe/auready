package com.kiwi.auready_ver2.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

class FriendsAdapter extends BaseAdapter {

    private List<Friend> mFriends;

    private FriendsFragment.FriendItemListener mItemListener;

    private boolean[] mSelectedFriends;

    private List<Friend> mSearchedFriends = new ArrayList<>();

    public FriendsAdapter(List<Friend> friends, FriendsFragment.FriendItemListener itemListener) {
        setList(friends);
        mItemListener = itemListener;
    }

    private void setList(List<Friend> friends) {
        mFriends = checkNotNull(friends);
        mSearchedFriends.clear();
        mSearchedFriends.addAll(mFriends);
        mSelectedFriends = new boolean[friends.size()];
    }

    @Override
    public int getCount() {
        return mSearchedFriends.size();
    }

    @Override
    public Friend getItem(int position) {
        return mSearchedFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        final ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.friend_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.friendCheckbox = (CheckBox) rowView.findViewById(R.id.friend_checkbox);
            viewHolder.friendName = (TextView) rowView.findViewById(R.id.friend_name);
            viewHolder.deleteFriendBtn = (Button) rowView.findViewById(R.id.delete_friend_btn);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // bind views
        final Friend friend = getItem(position);
        viewHolder.friendName.setText(friend.getName());
        viewHolder.friendCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                String checkedItemId = mSearchedFriends.get(position).getId();
                for (int i = 0; i < mFriends.size(); i++) {
                    Friend friend = mFriends.get(i);
                    if (friend.getId().equals(checkedItemId)) {
                        mSelectedFriends[i] = isChecked;
                        break;
                    }
                }
            }
        });

        for (int i = 0; i < mFriends.size(); i++) {
            if (friend.getId().equals(mFriends.get(i).getId())) {
                viewHolder.friendCheckbox.setChecked(mSelectedFriends[i]);
                break;
            }
        }

        return rowView;
    }

    public void replaceData(List<Friend> friendList) {
        setList(friendList);
        notifyDataSetChanged();
    }

    public List<Friend> getCheckedItems() {
        List<Friend> checkedItems = new ArrayList<>();

        for (int i = 0; i < mFriends.size(); i++) {
            if (mSelectedFriends[i]) {
                checkedItems.add(mFriends.get(i));
            }
        }

        return checkedItems;
    }

    public void inputSearchText(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());

        mSearchedFriends.clear();
        if (searchText.length() == 0) {
            mSearchedFriends.addAll(mFriends);
        } else {
            for (Friend friend : mFriends) {
                if (friend.getName().contains(searchText)) {
                    mSearchedFriends.add(friend);
                }
            }
        }

        if (mSearchedFriends.isEmpty()) {
            mItemListener.onNoSearchedFriend();
        } else {
            mItemListener.onFindSearchedFriends();
        }

        notifyDataSetChanged();
    }

    public void toggle(int position) {
        mSelectedFriends[position] = !mSelectedFriends[position];
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox friendCheckbox;
        TextView friendName;
        Button deleteFriendBtn;
    }


}

