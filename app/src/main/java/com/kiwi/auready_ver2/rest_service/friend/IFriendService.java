package com.kiwi.auready_ver2.rest_service.friend;

import com.kiwi.auready_ver2.data.SearchedUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Friend service
 */

public interface IFriendService {
    @GET("/relationships/status/accepted")
    Call<FriendsResponse> getFriends();

    @GET("/users/{search}")
    Call<List<SearchedUser>> getUsers(@Path("search") String emailOrName);

    @POST("/relationships/{toUserId}")
    Call<Void> addFriend(@Path("toUserId") String name);

    @PUT("/relationships/{fromUserId}/accepted")
    Call<Void> acceptFriendRequest(@Path("fromUserId") String fromUserId);

    @DELETE("/relationships/{fromUserId}")
    Call<Void> deleteFriendRequest(@Path("fromUserId") String fromUserId);
}
