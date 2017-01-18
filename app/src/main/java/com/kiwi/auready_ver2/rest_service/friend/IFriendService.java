package com.kiwi.auready_ver2.rest_service.friend;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Friend service
 */

public interface IFriendService {
    @GET("/friends")
    Call<FriendResponse> getFriends();
}
