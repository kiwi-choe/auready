package com.kiwi.auready_ver2.taskheaddetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;

public class TaskHeadDetailFragment extends Fragment {

    public static final String TAG_TASKHEADDETAILFRAG = "tag_TaskHeadDetailFragment";

    public TaskHeadDetailFragment() {
        // Required empty public constructor
    }

    public static TaskHeadDetailFragment newInstance() {
        return new TaskHeadDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
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
        return root;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
////        if(android.R.id.home == id) {
////            // Show fragment on back stack
////            TaskHeadsFragment taskHeadsFragment = TaskHeadsFragment.newInstance();
////            ActivityUtils.replaceFragment(getFragmentManager(),
////                    taskHeadsFragment, R.id.content_frame, TaskHeadsFragment.TAG_TASKHEADSFRAGMENT);
////            return true;
////        } else
//        if (R.id.menu_create == id) {
//            // Create new TaskHead
//            // Save this taskHead
//            // Send intent to TaskHeadDetailActivity
//        } else if(R.id.m)
//        return super.onOptionsItemSelected(item);
//    }


}
