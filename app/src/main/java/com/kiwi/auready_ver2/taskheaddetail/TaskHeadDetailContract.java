package com.kiwi.auready_ver2.taskheaddetail;

import android.content.Intent;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 11/2/16.
 */
public interface TaskHeadDetailContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String title);

        void setMembers(List<Member> members);

        void setColor(int color);

        void showAddedTaskHead(String taskHeadId, String title, int color);

        void showEmptyTaskHeadError();

        void cancelCreateTaskHead();

        void setNewTaskHeadView();

        void showEditedTaskHead(String title, int color);

        void showSaveError();

        void addMembers(ArrayList<Member> members);
    }

    interface Presenter extends BasePresenter {

        void createTaskHeadDetail(String title, int order, List<Member> members, int color);

        // Get TaskHead if exists the taskHeadId
        void populateTaskHeadDetail();

        void result(int requestCode, int resultCode, Intent data);

        void editTaskHeadDetail(String editTitle, int order, List<Member> editMembers, int color);
    }
}
