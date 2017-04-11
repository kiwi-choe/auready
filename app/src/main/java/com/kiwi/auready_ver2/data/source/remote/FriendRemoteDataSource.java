package com.kiwi.auready_ver2.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.HttpStatusCode;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.friend.FriendsResponse;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;
import com.kiwi.auready_ver2.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 1/18/17.
 */

public class FriendRemoteDataSource implements FriendDataSource {

    // testing
    private static final String STUB_ACCESSTOKEN = "stubbedAccessToken";
    private static final String TAG = "tag_friendRemote";
    private static final int STATUS_ACCEPTED = 1;

    private static FriendRemoteDataSource INSTANCE;
    private String mAccessToken;
    private Context mContext;

    private FriendRemoteDataSource(@NonNull Context context) {
        mContext = context.getApplicationContext();
        // Before entering here, AccessTokenStore static Instance should be created
        mAccessToken = AccessTokenStore.getInstance(context)
                .getStringValue(AccessTokenStore.ACCESS_TOKEN, "");
    }

    public static FriendRemoteDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new FriendRemoteDataSource(context);
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

        // Check network
        if(!NetworkUtils.isOnline(mContext)) {
            callback.onDataNotAvailable();
        }

        // Request to Server
        IFriendService friendService = ServiceGenerator.createService(
                IFriendService.class, mAccessToken);

        Call<FriendsResponse> call = friendService.getFriends(STATUS_ACCEPTED);
        call.enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                if(response.code() == HttpStatusCode.FriendStatusCode.OK) {
                    callback.onFriendsLoaded(response.body().getFriends());
                } else if(response.code() == HttpStatusCode.FriendStatusCode.NO_FRIENDS) {
                    // There is no friend in Remote DB, so synchronize to Local DB
                    List<Friend> noFriends = new ArrayList<Friend>(0);
                    callback.onFriendsLoaded(noFriends);
                }
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

