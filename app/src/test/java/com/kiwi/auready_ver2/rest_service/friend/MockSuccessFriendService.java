package com.kiwi.auready_ver2.rest_service.friend;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;

import static com.kiwi.auready_ver2.StubbedData.FriendStub.FRIENDS;

/**
 * Mock Friend service
 */
public class MockSuccessFriendService implements IFriendService {
    private static final int STATUS_ACCEPTED = 1;
    private static User userInfo = new User("userid", "useremail", "username");
    public static final List<SearchedUser> SEARCHED_PEOPLE = Lists.newArrayList(
            new SearchedUser(userInfo, SearchedUser.NO_STATUS),
            new SearchedUser(userInfo, SearchedUser.PENDING));

    private final BehaviorDelegate<IFriendService> delegate;

    public MockSuccessFriendService(BehaviorDelegate<IFriendService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<FriendsResponse> getFriends() {
        FriendsResponse friendsResponse = new FriendsResponse(FRIENDS);
        return delegate.returningResponse(friendsResponse).getFriends();
    }

    @Override
    public Call<List<SearchedUser>> getUsers(@Path("search") String emailOrName) {
        return delegate.returningResponse(SEARCHED_PEOPLE).getUsers(emailOrName);
    }

    @Override
    public Call<Void> addFriend(@Path("name") String name) {
        return delegate.returningResponse(null).addFriend(name);
    }

    @Override
    public Call<Void> acceptFriendRequest(@Path("fromUserId") String fromUserId) {
        return null;
    }

    @Override
    public Call<Void> deleteFriendRequest(@Path("fromUserId") String fromUserId) {
        return null;
    }
}
