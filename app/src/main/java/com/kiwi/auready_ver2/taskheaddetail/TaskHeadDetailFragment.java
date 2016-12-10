package com.kiwi.auready_ver2.taskheaddetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.friend.FriendsActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadDetailFragment extends Fragment implements
        TaskHeadDetailContract.View {

    public static final String TAG_TASKHEADDETAILFRAG = "tag_TaskHeadDetailFragment";
    public static final String ARG_TASKHEAD_ID = "arg_taskhead_id";

    private TaskHeadDetailContract.Presenter mPresenter;

    // Items of CustomActionBar
    private TextView mCancelBt;
    private TextView mCreateBt;

    private EditText mTitle;
    private ListView mMemberListView;
    private MembersAdapter mMemberListAdapter;
    private List<String> mMembers;

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
        mMemberListAdapter = new MembersAdapter(new ArrayList<String>(0));
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

        mTitle = (EditText) root.findViewById(R.id.taskheaddetail_title);
        mMemberListView = (ListView) root.findViewById(R.id.taskheaddetail_member_list);
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
                startActivity(intent);
            }
        });

        // Todo
        List<String> list = new ArrayList<>();
        for(int i=0; i<10; i++){
            list.add("test item " + i);
        }

        setMembers(list);

        // Set custom actionbar views
        ActionBar ab = ((TaskHeadDetailActivity)getActivity()).getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);

        View customView = inflater.inflate(
                R.layout.taskheaddetail_actionbar, null);

        mCancelBt = (TextView) customView.findViewById(R.id.cancel_taskhead);
        mCreateBt = (TextView) customView.findViewById(R.id.create_taskhead);

        ab.setCustomView(customView);
        ab.setDisplayShowCustomEnabled(true);
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
                mPresenter.saveTaskHead(mTitle.getText().toString(), mMembers);
            }
        });

    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setMembers(List<String> members) {
        mMembers = members;
        mMemberListAdapter.replaceData(members);
    }

    @Override
    public void setResultToTaskHeadsView(String taskHeadId) {
        Intent intent = getActivity().getIntent();
        intent.putExtra(TaskHeadDetailFragment.ARG_TASKHEAD_ID, taskHeadId);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showEmptyTaskHeadError() {
        Snackbar.make(mTitle, getString(R.string.empty_taskhead_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void cancelCreateTaskHead() {
        Intent intent = getActivity().getIntent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    // TODO: 12/9/16 What type of Adapter is better?
    private static class MembersAdapter extends BaseAdapter {

        private List<String> mMembers;

        public MembersAdapter(List<String> members) {
            setList(members);
        }

        private void setList(List<String> members) {
            mMembers = checkNotNull(members);
        }

        public void replaceData(List<String> members) {
            setList(members);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mMembers.size();
        }

        @Override
        public String getItem(int position) {
            return mMembers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.member_item, parent, false);
            }
            final String memberId = getItem(position);

            TextView memberIdTV = (TextView) rowView.findViewById(R.id.member_id);
            memberIdTV.setText(memberId);

            return rowView;
        }
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
