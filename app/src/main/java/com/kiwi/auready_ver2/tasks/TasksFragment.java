package com.kiwi.auready_ver2.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiwi.auready_ver2.R;

public class TasksFragment extends Fragment {

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {

        return new TasksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

}
