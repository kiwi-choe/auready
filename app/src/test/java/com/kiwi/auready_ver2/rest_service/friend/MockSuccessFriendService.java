package com.kiwi.auready_ver2.rest_service.friend;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.SearchedUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;

import static com.kiwi.auready_ver2.StubbedData.FriendStub.FRIENDS;

/**
 * Mock Friend service
 */
public class MockSuccessFriendService implements IFriendService {

    public static final List<SearchedUser> SEARCHED_PEOPLE = Lists.newArrayList(
            new SearchedUser("name1", 0),
            new SearchedUser("name2", 0));

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
}
