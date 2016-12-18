package com.kiwi.auready_ver2.friend;

import android.util.Log;
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

import static com.google.common.base.Preconditions.checkNotNull;

class FriendsAdapter extends BaseAdapter {

    private List<Friend> mFriends;

    private FriendsFragment.FriendItemListener mItemListener;

    private boolean[] mSelectedFriends;

    public FriendsAdapter(List<Friend> friends, FriendsFragment.FriendItemListener itemListener) {
        setList(friends);
        mItemListener = itemListener;

    }

    private void setList(List<Friend> friends) {
        mFriends = checkNotNull(friends);
        mSelectedFriends = new boolean[friends.size()];
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

        final Friend friend = getItem(position);
        viewHolder.friendName.setText(friend.getName());

        viewHolder.friendCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mSelectedFriends[position] = isChecked;
            }
        });

        viewHolder.friendCheckbox.setChecked(mSelectedFriends[position]);

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

    private class ViewHolder {
        CheckBox friendCheckbox;
        TextView friendName;
        Button deleteFriendBtn;
    }
}

