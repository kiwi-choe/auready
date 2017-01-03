package com.kiwi.auready_ver2.taskheaddetail;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.friend.FriendsActivity;
import com.kiwi.auready_ver2.friend.FriendsFragment;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHeadDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 11/2/16.
 */

public class TaskHeadDetailPresenter implements TaskHeadDetailContract.Presenter {

    @NonNull
    private final UseCaseHandler mUseCaseHandler;
    @Nullable
    private final String mTaskHeadId;
    private final TaskHeadDetailContract.View mView;
    @NonNull
    private final SaveTaskHeadDetail mSaveTaskHeadDetail;
    @NonNull
    private final GetTaskHeadDetail mGetTaskHeadDetail;

    public TaskHeadDetailPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String taskHeadId,
                                   @NonNull TaskHeadDetailContract.View view,
                                   @NonNull SaveTaskHeadDetail saveTaskHeadDetail,
                                   @NonNull GetTaskHeadDetail getTaskHeadDetail) {

        mUseCaseHandler = useCaseHandler;
        mTaskHeadId = taskHeadId;
        mView = view;
        mSaveTaskHeadDetail = saveTaskHeadDetail;
        mGetTaskHeadDetail = getTaskHeadDetail;

        mView.setPresenter(this);
    }


    @Override
    public void start() {
        if (mTaskHeadId != null) {
            populateTaskHeadDetail();
            mView.setEditTaskHeadView();
        } else {
            mView.setNewTaskHeadView();
        }
    }

    @Override
    public void createTaskHeadDetail(final String title, List<Member> members, int order) {
        final TaskHead newTaskHead = new TaskHead(title, order);
        TaskHeadDetail newTaskHeadDetail = new TaskHeadDetail(newTaskHead, members);
        if (newTaskHeadDetail.isEmpty()) {
            mView.showEmptyTaskHeadError();
        } else {
            mUseCaseHandler.execute(mSaveTaskHeadDetail, new SaveTaskHeadDetail.RequestValues(newTaskHeadDetail),
                    new UseCase.UseCaseCallback<SaveTaskHeadDetail.ResponseValue>() {

                        @Override
                        public void onSuccess(SaveTaskHeadDetail.ResponseValue response) {
                            mView.showAddedTaskHead(newTaskHead.getId(), title);
                        }

                        @Override
                        public void onError() {
                            mView.showSaveError();
                        }
                    });
        }
    }

//    @Override
//    public void editTaskHead(String title, List<Friend> members) {
//        if (mTaskHeadId == null) {
//            throw new RuntimeException("editTaskHead() was called but taskHead is new.");
//        }
//
//        mUseCaseHandler.execute(mEditTaskHead, new EditTaskHead.RequestValues(mTaskHeadId, title, members),
//                new UseCase.UseCaseCallback<EditTaskHead.ResponseValue>() {
//
//                    @Override
//                    public void onSuccess(EditTaskHead.ResponseValue response) {
//                        mView.showEditedTaskHead();
//                    }
//
//                    @Override
//                    public void onError() {
//                        mView.showSaveError();
//                    }
//                });
//    }

    @Override
    public void populateTaskHeadDetail() {
        if (mTaskHeadId == null) {
            throw new RuntimeException("populateTaskHeadDetail() was called but taskhead is new.");
        }
        mUseCaseHandler.execute(mGetTaskHeadDetail, new GetTaskHeadDetail.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetTaskHeadDetail.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeadDetail.ResponseValue response) {
                        showTaskHead(response.getTaskHeadDetail());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (FriendsActivity.REQ_FRIENDS == requestCode && Activity.RESULT_OK == resultCode) {
            if (data.hasExtra(FriendsFragment.EXTRA_KEY_SELECTED_FRIENDS)) {
                ArrayList<Friend> friends =
                        data.getParcelableArrayListExtra(FriendsFragment.EXTRA_KEY_SELECTED_FRIENDS);

                // Make new member List
                ArrayList<Member> members = new ArrayList<>();
                for (Friend friend : friends) {
                    Member newMember = new Member(mTaskHeadId, friend.getId(), friend.getName());
                    members.add(newMember);
                }

                mView.addMembers(members);
            }
        }
    }

    private void showTaskHead(TaskHeadDetail taskHeadDetail) {
        mView.setTitle(taskHeadDetail.getTaskHead().getTitle());
        mView.setMembers(taskHeadDetail.getMembers());
    }
}
