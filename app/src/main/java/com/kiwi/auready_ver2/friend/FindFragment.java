package com.kiwi.auready_ver2.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.SearchedUser;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/*
* Find users to add friend into my friendList
* */
public class FindFragment extends Fragment implements
        FindContract.View {

    public static final String TAG_FINDFRAG = "Tag_FindFragment";
    private FindContract.Presenter mPresenter;

    // testing
    private ArrayList<Friend> TEST_FRIENDS;
    private int TEST_FRIENDS_CNT;

    public FindFragment() {
        // Required empty public constructor
    }

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // testing
        TEST_FRIENDS = Lists.newArrayList(
                new Friend("email1", "name1"),
                new Friend("email2", "name2"),
                new Friend("email3", "name3"),
                new Friend("email4", "name4"),
                new Friend("email5", "name5"),
                new Friend("email6", "name6"),
                new Friend("email7", "name7"),
                new Friend("email8", "name8"),
                new Friend("email9", "name9"),
                new Friend("email10", "name10"));
        TEST_FRIENDS_CNT = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_find, container, false);

        Button btSaveFriend = (Button)root.findViewById(R.id.bt_test_save_friend);
        btSaveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // testing
                // Stub a Friend
                TEST_FRIENDS_CNT = (TEST_FRIENDS_CNT+1) % 10;
                mPresenter.saveFriend(TEST_FRIENDS.get(TEST_FRIENDS_CNT));
            }
        });
        return root;
    }

    @Override
    public void setPresenter(@NonNull FindContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "presenter cannot be null");
    }

    @Override
    public void setViewWhenAddFriendSucceed(@NonNull SearchedUser user) {
        // Show message
        String successMsg = user.getName() + " " + getString(R.string.add_friend_success_msg);
        Snackbar.make(getView(), successMsg, Snackbar.LENGTH_SHORT);

        // Set ADD button to GONE

    }

    @Override
    public void showSearchedPeople(List<SearchedUser> searchedPeople) {

    }

    @Override
    public void showNoSearchedPeople() {

    }
}
