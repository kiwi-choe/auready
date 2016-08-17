package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/12/16.
 */
public class FindPresenter implements FindContract.Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final FindContract.View mFindView;
    private final SaveFriend mSaveFriend;

    public FindPresenter(@NonNull UseCaseHandler useCaseHandler,
                         @NonNull FindContract.View findView,
                         @NonNull SaveFriend saveFriend) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mFindView = checkNotNull(findView, "findView cannot be null");

        mSaveFriend = checkNotNull(saveFriend, "saveFriend cannot be null");

        mFindView.setPresenter(this);
    }

    @Override
    public void saveFriend(@NonNull Friend friend) {
        checkNotNull(friend);
        mUseCaseHandler.execute(mSaveFriend, new SaveFriend.RequestValues(friend),
                new UseCase.UseCaseCallback<SaveFriend.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveFriend.ResponseValue response) {
                        checkNotNull(response.getFriend(), "response.getFriend() cannot be null");
                        mFindView.showSuccessMsgOfAddFriend(response.getFriend());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


}
