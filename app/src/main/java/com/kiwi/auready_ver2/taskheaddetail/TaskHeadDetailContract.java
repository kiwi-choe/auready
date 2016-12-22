package com.kiwi.auready_ver2.taskheaddetail;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Friend;

import java.util.List;

/**
 * Created by kiwi on 11/2/16.
 */
public interface TaskHeadDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setMembers(List<Friend> members);

        void setResultToTaskHeadsView(String taskHeadId);

        void showEmptyTaskHeadError();

        void cancelCreateTaskHead();

        void initMembers();

        void setNewTaskHeadView();

        void setEditTaskHeadView();
    }

    interface Presenter extends BasePresenter {

        // create
        void createTaskHead(String title, List<Friend> members);

        void updateTaskHead(String title, List<Friend> members, int orderOfTaskHead);

        // Get TaskHead if exists the taskHeadId
        void populateTaskHead();

        void result(int requestCode, int resultCode, Intent data);
    }
}
