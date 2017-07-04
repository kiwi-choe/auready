package com.kiwi.auready.friend;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kiwi.auready.R;
import com.kiwi.auready.data.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

class FriendsAdapter extends BaseAdapter {

    private static final String TAG = "FriendsAdapter";

    private ArrayList<String> mAddedMemberIds = new ArrayList<>(0);

    private List<Friend> mFriends;

    private FriendsFragment.FriendItemListener mItemListener;

    private HashMap<Friend, Boolean> mSelectedFriends = new HashMap<>();

    private List<Friend> mSearchedFriends = new ArrayList<>();

    public FriendsAdapter(List<Friend> friends, FriendsFragment.FriendItemListener itemListener, ArrayList<String> addedMembers) {
        setList(friends);
        mAddedMemberIds = addedMembers;
        mItemListener = itemListener;
        mSelectedFriends.clear();
    }

    private void setList(List<Friend> friends) {
        mFriends = checkNotNull(friends);
        mSearchedFriends.clear();
        mSearchedFriends.addAll(mFriends);
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
//            viewHolder.deleteFriendBtn = (Button) rowView.findViewById(R.id.delete_friend_btn);

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // bind views
        final Friend friend = getItem(position);
        viewHolder.friendName.setText(friend.getName());
        viewHolder.friendCheckbox.setChecked(mSelectedFriends.get(friend) == null ? false : true);

        viewHolder.friendCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "entered into friendCheckbox setOnClickListener");
                setCheck(position);
            }
        });

        // Set check to the added member already
        for(String id:mAddedMemberIds) {
            if(friend.getUserId().equals(id)) {
                viewHolder.friendCheckbox.setEnabled(false);
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

        for (Friend friend : mFriends) {
            if (mSelectedFriends.get(friend) != null) {
                checkedItems.add(friend);
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
//                if (friend.getName().toLowerCase().matches(".*" + searchText + ".*")) {
                if (friend.getName().toLowerCase().contains(searchText)) {
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

    public void setCheck(int position) {
        Friend friend = getItem(position);
        if (mSelectedFriends.get(friend) == null) {
            mSelectedFriends.put(friend, true);
        } else {
            mSelectedFriends.remove(friend);
        }

        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox friendCheckbox;
        TextView friendName;
//        Button deleteFriendBtn;
    }
}

