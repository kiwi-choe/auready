package com.kiwi.auready_ver2.rest_service.friend;

import com.google.gson.annotations.SerializedName;
import com.kiwi.auready_ver2.data.Friend;

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
