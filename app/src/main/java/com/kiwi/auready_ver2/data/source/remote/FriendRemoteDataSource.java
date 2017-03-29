package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.friend.FriendsResponse;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kiwi on 1/18/17.
 */

public class FriendRemoteDataSource implements FriendDataSource {

    // testing
    private static final String STUB_ACCESSTOKEN = "stubbedAccessToken";
    private static final String TAG = "tag_friendRemote";

    private static FriendRemoteDataSource INSTANCE;
    private String mAccessToken;

    private FriendRemoteDataSource() {

        // Before entering here, AccessTokenStore static Instance should be created
//        mAccessToken = null;
        mAccessToken =
                AccessTokenStore.getInstance().getStringValue(AccessTokenStore.ACCESS_TOKEN, "");
    }

    public static FriendRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FriendRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void deleteAllFriends() {

    }

    @Override
    public void deleteFriend(@NonNull String id) {

    }

    @Override
    public void getFriends(@NonNull final LoadFriendsCallback callback) {

        // Request to Server
        IFriendService friendService = ServiceGenerator.createService(
                IFriendService.class, mAccessToken);

        Call<FriendsResponse> call = friendService.getFriends();
        call.enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {

                callback.onFriendsLoaded(response.body().getFriends());
            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                Log.d(TAG, "getFriends is failed");
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveFriend(@NonNull Friend friend, @NonNull SaveCallback callback) {

    }
}

