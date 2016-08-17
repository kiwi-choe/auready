package com.kiwi.auready_ver2.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    public static final String TAG_TASKSFRAGMENT = "TAG_TasksFragment";
    
    private TasksContract.Presenter mPresenter;

    private TextView mNavHeaderEmail;

    // interface
    private TasksFragmentListener mListener;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {

        return new TasksFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TasksFragmentListener) {
            mListener = (TasksFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TasksFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void setPresenter(TasksContract.Presenter tasksPresenter) {
        mPresenter = checkNotNull(tasksPresenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        // test
        Button button = (Button) root.findViewById(R.id.test_fragment_tasks);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onLoginSuccess();
                }

            }
        });

        // Set Floating button
        FloatingActionButton fb =
                (FloatingActionButton) root.findViewById(R.id.fab_add_task);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTask();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void setLoginSuccessUI() {

        if(mListener != null) {
            mListener.onLoginSuccess();
        }
    }

    // Interface with TasksActivity
    public interface TasksFragmentListener {
        void onLoginSuccess();
    }
}
