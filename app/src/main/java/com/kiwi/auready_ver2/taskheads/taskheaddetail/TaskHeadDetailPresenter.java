package com.kiwi.auready_ver2.taskheads.taskheaddetail;

/**
 * Created by kiwi on 11/2/16.
 */

public class TaskHeadDetailPresenter implements TaskHeadDetailContract.Presenter {

    private final TaskHeadDetailContract.View mView;

    public TaskHeadDetailPresenter(TaskHeadDetailContract.View view) {
        mView = view;
    }


    @Override
    public void start() {

    }
}
