package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;
import com.kiwi.auready_ver2.rest_service.HttpStatusCode.FriendStatusCode;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/12/16.
 */
public class FindPresenter implements FindContract.Presenter {

    private static final String TAG_FIND = "tag_FindPresenter";

    private final UseCaseHandler mUseCaseHandler;
    private final FindContract.View mFindView;
    private final SaveFriend mSaveFriend;
    private String mAccessToken;

    public FindPresenter(@NonNull String accessToken,
                         @NonNull UseCaseHandler useCaseHandler,
                         @NonNull FindContract.View findView,
                         @NonNull SaveFriend saveFriend) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mFindView = checkNotNull(findView, "findView cannot be null");

        mSaveFriend = checkNotNull(saveFriend, "saveFriend cannot be null");

        mFindView.setPresenter(this);

        // Set mAccessToken
        // todo Before set this presenter, AccessTokenStore should be already instantiated.
        mAccessToken = accessToken;
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
        IFriendService friendService =
                ServiceGenerator.createService(IFriendService.class, mAccessToken);

        Call<List<SearchedUser>> call = friendService.getUsers(emailOrName);
        call.enqueue(new Callback<List<SearchedUser>>() {
            @Override
            public void onResponse(Call<List<SearchedUser>> call, Response<List<SearchedUser>> response) {
                if(response.code() == FriendStatusCode.OK) {
                    List<SearchedUser> searchedUsers = response.body();
                    onFindPeopleSucceed(searchedUsers);
                } else if(response.code() == FriendStatusCode.NO_USERS) {
                    Log.d(TAG_FIND, "no users");
                    onFindPeopleFail();
                }
            }

            @Override
            public void onFailure(Call<List<SearchedUser>> call, Throwable t) {
                Log.d(TAG_FIND, "Called onFailure()", t);
                onFindPeopleFail();
            }
        });
    }

    @Override
    public void addFriend(@NonNull final String name) {
        checkNotNull(name);

        IFriendService friendService =
                ServiceGenerator.createService(IFriendService.class, mAccessToken);

        Call<Void> call = friendService.addFriend(name);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    onAddFriendSucceed(name);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onAddFriendFail(R.string.addfriend_fail_msg_onfailure);
            }
        });
    }

    @Override
    public void onAddFriendSucceed(String name) {
        mFindView.setAddFriendSucceedUI(name);
    }

    @Override
    public void onAddFriendFail(int stringResource) {
        mFindView.setAddFriendFailMessage(stringResource);
    }

    @Override
    public void onFindPeopleSucceed(@NonNull List<SearchedUser> searchedUsers) {
        checkNotNull(searchedUsers);
        mFindView.showSearchedPeople(searchedUsers);
    }

    @Override
    public void onFindPeopleFail() {
        mFindView.showNoSearchedPeople();
    }

}
