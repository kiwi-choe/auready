package com.kiwi.auready_ver2.taskheaddetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.friend.FriendsActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskHeadDetailFragment extends Fragment implements
        TaskHeadDetailContract.View {

    public static final String TAG_TASKHEADDETAILFRAG = "tag_TaskHeadDetailFragment";
    private static final String TAG_TASKHEADDETAILFRAG_DEBUG = "TaskHeadDetailView";

    public static final String EXTRA_TASKHEAD_ID = "extra_taskhead_id";

    // when getArguments().getInt()
    private static final int DEFAULT_INT = 0;

    private TaskHeadDetailContract.Presenter mPresenter;

    // Items of CustomActionBar
    private TextView mCancelBt;
    private TextView mCreateBt;
    private TextView mDoneBt;

    private String mTaskHeadId;
    private int mOrderOfTaskHead;

    private EditText mTitle;
    private MembersAdapter mMemberListAdapter;
    private List<Friend> mMembers;
    private ActionModeCallback mActionModeCallBack;
    private ActionMode mActionMode;

    public TaskHeadDetailFragment() {
        // Required empty public constructor
    }

    public static TaskHeadDetailFragment newInstance() {
        return new TaskHeadDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMembers = new ArrayList<>(0);
        if(getArguments() != null) {
            mTaskHeadId = getArguments().getString(TaskHeadDetailActivity.ARG_TASKHEAD_ID);
            if(mTaskHeadId == null) {
                initMembers();
                // set order for new taskHead
                mOrderOfTaskHead = getArguments().getInt(TaskHeadDetailActivity.ARG_CNT_OF_TASKHEADS, DEFAULT_INT);
            }
        }

        mMemberListAdapter = new MembersAdapter(getActivity().getApplicationContext(), R.layout.member_item, mMembers);
        mActionModeCallBack = new ActionModeCallback();
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull TaskHeadDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_taskhead_detail, container, false);

        // Set custom actionbar views
        ActionBar ab = ((TaskHeadDetailActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        View customView = inflater.inflate(
                R.layout.taskheaddetail_actionbar, null);
        mCancelBt = (TextView) customView.findViewById(R.id.cancel_taskhead);
        mCreateBt = (TextView) customView.findViewById(R.id.create_taskhead);
        mDoneBt = (TextView) customView.findViewById(R.id.done_taskhead);

        ab.setCustomView(customView);
        ab.setDisplayShowCustomEnabled(true);

        mTitle = (EditText) root.findViewById(R.id.taskheaddetail_title);

        ListView mMemberListView = (ListView) root.findViewById(R.id.taskheaddetail_member_list);
        View memberAddBtLayout = inflater.inflate(R.layout.member_add_bt, null, false);
        mMemberListView.addFooterView(memberAddBtLayout);
        mMemberListView.setAdapter(mMemberListAdapter);
        mMemberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActionMode = getActivity().startActionMode(mActionModeCallBack);
                return true;
            }
        });

        Button memberAddBt = (Button) memberAddBtLayout.findViewById(R.id.member_add_bt);
        memberAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivityForResult(intent, FriendsActivity.REQ_FRIENDS);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCreateTaskHead();
            }
        });
        mCreateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createTaskHead(mTitle.getText().toString(), mMembers, mOrderOfTaskHead);
            }
        });
        mDoneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editTaskHead(mTitle.getText().toString(), mMembers);
            }
        });
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setMembers(List<Friend> members) {
        mMembers.clear();
        mMembers.addAll(members);
        mMemberListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAddedTaskHead(String taskHeadId) {
        Intent intent = getActivity().getIntent();
        intent.putExtra(TaskHeadDetailFragment.EXTRA_TASKHEAD_ID, taskHeadId);
        sendResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void showEmptyTaskHeadError() {
        Snackbar.make(mTitle, getString(R.string.empty_taskhead_message), Snackbar.LENGTH_LONG).show();
        sendResult(Activity.RESULT_OK, null);
    }

    @Override
    public void cancelCreateTaskHead() {
        Intent intent = getActivity().getIntent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void initMembers() {
        // Add the current user to members
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(getActivity().getApplicationContext());
        // testing
        accessTokenStore.save_forTesting("userEmail", "userName", "myIdOfFriend");

        String myIdOfFriend = accessTokenStore.getStringValue(AccessTokenStore.MY_ID_OF_FRIEND, "");
        String myEmail = accessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, "");
        String myName = accessTokenStore.getStringValue(AccessTokenStore.USER_NAME, "");

        Friend me = new Friend(myIdOfFriend, myEmail, myName);
        mMembers.add(0, me);
    }

    @Override
    public void setNewTaskHeadView() {
        mCreateBt.setVisibility(View.VISIBLE);
        mDoneBt.setVisibility(View.GONE);
    }

    @Override
    public void setEditTaskHeadView() {
        mDoneBt.setVisibility(View.VISIBLE);
        mCreateBt.setVisibility(View.GONE);
    }

    @Override
    public void showEditedTaskHead() {
        sendResult(Activity.RESULT_OK, null);
    }

    @Override
    public void showSaveError() {
        Log.d(TAG_TASKHEADDETAILFRAG_DEBUG, "taskhead cannot saved.");
        sendResult(Activity.RESULT_CANCELED, null);
    }

    @Override
    public void addMembers(ArrayList<Friend> friends) {
        mMembers.addAll(friends);
        mMemberListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    private void sendResult(int resultCode, @Nullable Intent intent) {
        if(intent != null) {
            getActivity().setResult(resultCode, intent);
        } else {
            getActivity().setResult(resultCode);
        }
        getActivity().finish();
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Toast.makeText(getContext(), "onCreateActionMode", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    }
}
