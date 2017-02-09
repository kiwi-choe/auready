package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
//                        mFindView.setAddFriendSucceedUI(response.getFriend());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void findPeople(@NonNull String emailOrName) {
        checkNotNull(emailOrName);
        // Find people by email or name
        // request to Server
//        mUseCaseHandler.execute(mFindPeople, new FindPeople.RequestValues(emailOrName),
//                new UseCase.UseCaseCallback<FindPeople.ResponseValue>() {
//
//                    @Override
//                    public void onSuccess(FindPeople.ResponseValue response) {
//                        mFindView.showSearchedPeople(response.getSearchedPeople());
//                    }
//
//                    @Override
//                    public void onError() {
//                        mFindView.showNoSearchedPeople();
//                    }
//                });
    }

    @Override
    public void addFriend(@NonNull final SearchedUser user) {
        checkNotNull(user);

        IFriendService friendService =
                ServiceGenerator.createService(IFriendService.class);

        Call<Void> call = friendService.addFriend(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    onAddFriendSucceed(user);
                } else if(response.code() == 400) {
                    onAddFriendFail(R.string.addfriend_fail_msg_400);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onAddFriendFail(R.string.addfriend_fail_msg_onfailure);
            }
        });
    }

    @Override
    public void onAddFriendSucceed(SearchedUser user) {
        mFindView.setAddFriendSucceedUI(user);
    }

    @Override
    public void onAddFriendFail(int stringResource) {
        mFindView.setAddFriendFailMessage(stringResource);
    }

}
