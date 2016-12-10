package com.kiwi.auready_ver2.tasks;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class TasksContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setMembers(List<String> members);
    }

    interface Presenter extends BasePresenter {

        void populateTaskHead();
    }
}
