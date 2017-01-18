package com.kiwi.auready_ver2.rest_service.friend;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

/**
 * Mock Friend service
 */
public class MockSuccessFriendService implements IFriendService {

    public static final String FRIEND_ID = "stubbedFriendId";
    public static final String FRIEND_EMAIL = "stubbedFriendEmail";
    static final String FRIEND_NAME = "stubbedFriendName";

    private final BehaviorDelegate<IFriendService> delegate;

    public MockSuccessFriendService(BehaviorDelegate<IFriendService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<FriendResponse> getFriends() {
        FriendResponse response = new FriendResponse(FRIEND_ID, FRIEND_EMAIL, FRIEND_NAME);
        return delegate.returningResponse(response).getFriends();
    }
}
