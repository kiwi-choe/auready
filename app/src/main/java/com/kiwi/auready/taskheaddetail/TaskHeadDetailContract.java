package com.kiwi.auready.taskheaddetail;

import android.content.Intent;

import com.kiwi.auready.BasePresenter;
import com.kiwi.auready.BaseView;
import com.kiwi.auready.data.Member;

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
