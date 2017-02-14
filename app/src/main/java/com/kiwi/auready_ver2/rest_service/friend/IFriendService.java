package com.kiwi.auready_ver2.rest_service.friend;

import com.kiwi.auready_ver2.data.SearchedUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Friend service
 */

public interface IFriendService {
    @GET("/friend")
    Call<FriendsResponse> getFriends();

    @GET("/user/{search}")
    Call<List<SearchedUser>> getUsers(@Path("search") String emailOrName);

    @POST("/relationship/{name}")
    Call<Void> addFriend(@Path("name") String name);
}
