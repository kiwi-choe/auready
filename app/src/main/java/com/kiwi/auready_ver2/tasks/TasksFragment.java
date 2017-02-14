package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

public class TasksFragment extends Fragment {

    public static final String ARG_MEMBER_ID = "MEMBER_ID";
    public static final String ARG_MEMBER_NAME = "MEMBER_NAME";

    private String mMemberId;
    private String mMemberName;

    private ListView mListview;
    private TasksAdapter mTasksAdapter;

    private TextView mNoTaskTextView;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance(String memberId, String memberName) {
        TasksFragment fragment = new TasksFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_MEMBER_ID, memberId);
        bundle.putString(ARG_MEMBER_NAME, memberName);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMemberId = getArguments().getString(ARG_MEMBER_ID);
            mMemberName = getArguments().getString(ARG_MEMBER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("MY_LOG", "onCreateView");
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        TextView memberText = (TextView) root.findViewById(R.id.member_name);
        memberText.setText(mMemberName);

        mNoTaskTextView = (TextView) root.findViewById(R.id.no_task_textview);

        // Set ListView
        mListview = (ListView) root.findViewById(R.id.tasks_listview);

        mTasksAdapter = new TasksAdapter(getContext());
        mListview.setAdapter(mTasksAdapter);

        setHasOptionsMenu(true);
        return root;
    }

    public void showTasks(List<Task> tasks) {
        mListview.setVisibility(View.VISIBLE);
        mNoTaskTextView.setVisibility(View.GONE);

        mTasksAdapter.updateTasks(tasks);
    }

    public void showNoTasks() {
        mListview.setVisibility(View.GONE);
        mNoTaskTextView.setVisibility(View.VISIBLE);

        mNoTaskTextView.invalidate();
    }
}
