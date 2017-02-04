package com.kiwi.auready_ver2.rest_service.friend;

import com.kiwi.auready_ver2.data.SearchedUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Friend service
 */

public interface IFriendService {
    @GET("/friend")
    Call<FriendsResponse> getFriends();

    @GET("/user")
    Call<List<SearchedUser>> getUsers(@Body String emailOrName);

    @POST("/friending")
    Call<Void> addFriend(@Body SearchedUser user);
}
