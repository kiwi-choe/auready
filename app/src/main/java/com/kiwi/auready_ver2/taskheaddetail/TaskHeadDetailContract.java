package com.kiwi.auready_ver2.taskheaddetail;

import android.content.Intent;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 11/2/16.
 */
public interface TaskHeadDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setMembers(List<Friend> members);

        void showAddedTaskHead(String taskHeadId);

        void showEmptyTaskHeadError();

        void showEditedTaskHead();

        void showSaveError();

        void addMembers(ArrayList<Friend> friends);
    }

    interface Presenter extends BasePresenter {

        void createTaskHead(String title, List<Friend> members, int order);

        void editTaskHead(String title, List<Friend> members);

        // Get TaskHead if exists the taskHeadId
        void populateTaskHead();

        void result(int requestCode, int resultCode, Intent data);
    }
}
