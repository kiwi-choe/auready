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

import static com.google.common.base.Preconditions.checkNotNull;

class FriendsAdapter extends BaseAdapter {

    private List<Friend> mFriends;

    private FriendsFragment.FriendItemListener mItemListener;

    private ArrayList<Friend> mSelectedFriends;

    public FriendsAdapter(List<Friend> friends, FriendsFragment.FriendItemListener itemListener) {
        setList(friends);
        mItemListener = itemListener;
        mSelectedFriends = new ArrayList<>();
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
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        final ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.friend_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.friendCheckbox = (CheckBox) rowView.findViewById(R.id.friend_checkbox);
            viewHolder.friendName = (TextView) rowView.findViewById(R.id.friend_name);
            viewHolder.deleteFriendBtn= (Button) rowView.findViewById(R.id.delete_friend_btn);
            viewHolder.isChecked = false;

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Friend friend = getItem(position);
        viewHolder.friendName.setText(friend.getName());

        viewHolder.friendCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    mSelectedFriends.add(friend);
                } else {
                    mSelectedFriends.remove(friend);
                }

            }
        });

        viewHolder.friendCheckbox.setChecked(viewHolder.isChecked);

        return rowView;
    }

    public void replaceData(List<Friend> friendList) {
        setList(friendList);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox friendCheckbox;
        TextView friendName;
        Button deleteFriendBtn;

        boolean isChecked;
    }
}

