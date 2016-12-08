package com.kiwi.auready_ver2.taskheaddetail;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadDetailFragment extends Fragment implements
TaskHeadDetailContract.View {

    public static final String TAG_TASKHEADDETAILFRAG = "tag_TaskHeadDetailFragment";

    private TaskHeadDetailContract.Presenter mPresenter;
    private EditText mTitle;
    private ListView mMemberListView;
    private MembersAdapter mListAdapter;

    public TaskHeadDetailFragment() {
        // Required empty public constructor
    }

    public static TaskHeadDetailFragment newInstance() {
        return new TaskHeadDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new MembersAdapter(new ArrayList<String>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().supportInvalidateOptionsMenu();
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

        // Set Toolbar
        ActionBar ab = ((TaskHeadDetailActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowTitleEnabled(false);
            // Set Custom actionbar
            View customActionbar = inflater.inflate(R.layout.taskheaddetail_toolbar, null);
            ab.setCustomView(customActionbar);
        }

        mTitle = (EditText) root.findViewById(R.id.taskheaddetail_title);
        mMemberListView = (ListView) root.findViewById(R.id.taskheaddetail_member_list);
        mMemberListView.setAdapter(mListAdapter);

        return root;
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setMembers(List<String> members) {
        mListAdapter.replaceData(members);
    }

    @Override
    public void showTaskHeadsView() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showEmptyTaskHeadError() {
        Snackbar.make(mTitle, getString(R.string.empty_taskhead_message), Snackbar.LENGTH_LONG).show();
    }

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
            if(rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.member_item, parent, false);
            }
            final String memberId = getItem(position);

            TextView memberIdTV = (TextView) rowView.findViewById(R.id.member_id);
            memberIdTV.setText(memberId);

            return rowView;
        }
    }
}
