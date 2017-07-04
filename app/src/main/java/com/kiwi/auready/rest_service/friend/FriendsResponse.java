package com.kiwi.auready.rest_service.friend;

import com.google.gson.annotations.SerializedName;
import com.kiwi.auready.data.Friend;

import java.util.List;

/**
 * friends response
 */
public class FriendsResponse {

    @SerializedName("friends")
    private List<Friend> friends;

    public FriendsResponse(List<Friend> friends) {
        this.friends = friends;
    }

    public List<Friend> getFriends() {
        return friends;
    }
}
