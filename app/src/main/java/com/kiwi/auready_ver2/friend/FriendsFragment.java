package com.kiwi.auready_ver2.friend;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    public static final String EXTRA_KEY_SELECTED_FRIENDS = "extraKeyOfSelectedFriends";
    public static final String EXTRA_KEY_MEMBERS = "extraKey_members";

    private FriendsContract.Presenter mPresenter;

    private FriendsAdapter mListAdapter;

    private LinearLayout mFriendsView;
    private LinearLayout mNoFriendsView;
    private TextView mLoadingIndicator;
    private ListView mListView;

    private ArrayList<String> mMemberIds = new ArrayList<>();

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMemberIds = getArguments().getStringArrayList(EXTRA_KEY_MEMBERS);
        }
        mListAdapter = new FriendsAdapter(new ArrayList<Friend>(0), mItemListener, mMemberIds);

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
        mLoadingIndicator = (TextView) root.findViewById(R.id.loading_indicator);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_friend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFindView();
            }
        });
    }

    SearchView mSearchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...
        mSearchView.setQueryHint("Search...");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mListAdapter.inputSearchText(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mListAdapter.inputSearchText(newText);
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }

        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        };

        // Get the MenuItem for the action item
        MenuItem actionMenuItem = menu.findItem(R.id.action_search);

        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_confirm) {
            ArrayList<Friend> selectedFriends = (ArrayList<Friend>) mListAdapter.getCheckedItems();
            setResultToTaskHeadDetailView(selectedFriends);
        }
//        } else if (id == R.id.delete) {
//            // Todo : start action mode
//        }

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
    public void setResultToTaskHeadDetailView(ArrayList<Friend> selectedFriends) {
        Intent intent = getActivity().getIntent();
        intent.putParcelableArrayListExtra(EXTRA_KEY_SELECTED_FRIENDS, selectedFriends);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
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

    private void showNoSearchedFriend() {
        mFriendsView.setVisibility(View.GONE);
        mNoFriendsView.setVisibility(View.VISIBLE);

        TextView noSearchedFriend = (TextView) mNoFriendsView.findViewById(R.id.txt_no_friends);
        noSearchedFriend.setText("There is no searched Item..");
    }

    private void showSearchedFriends() {
        mFriendsView.setVisibility(View.VISIBLE);
        mNoFriendsView.setVisibility(View.GONE);
    }

    public interface FriendItemListener {

        void onDeleteFriend(Friend clickedFriend);

        void onNoSearchedFriend();

        void onFindSearchedFriends();
    }

    /*
    * Listener for clicks on friends in the ListView
    * */
    FriendItemListener mItemListener = new FriendItemListener() {
        @Override
        public void onDeleteFriend(Friend clickedFriend) {
            showDeleteFriendAlert(clickedFriend);
        }

        @Override
        public void onNoSearchedFriend() {
            showNoSearchedFriend();
        }

        @Override
        public void onFindSearchedFriends() {
            showSearchedFriends();
        }


    };

}
