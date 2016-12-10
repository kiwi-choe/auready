package com.kiwi.auready_ver2.taskheaddetail;

import android.content.Intent;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;

import java.util.List;

/**
 * Created by kiwi on 11/2/16.
 */

public interface TaskHeadDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setMembers(List<String> members);

        void setResultToTaskHeadsView(String taskHeadId);

        void showEmptyTaskHeadError();

        void cancelCreateTaskHead();
    }

    interface Presenter extends BasePresenter {

        void saveTaskHead(String title, List<String> memberList);   // create or update

        void populateTaskHead();        // Get TaskHead if exists the taskHeadId

        void result(int requestCode, int resultCode, Intent data);
    }
}
