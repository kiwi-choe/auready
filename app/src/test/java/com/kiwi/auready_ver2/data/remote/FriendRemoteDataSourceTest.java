package com.kiwi.auready_ver2.data.remote;

import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.remote.FriendRemoteDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Friend Remote Data Source test - with mock Server service
 */
public class FriendRemoteDataSourceTest {

    private FriendRemoteDataSource mRemoteDataSource = FriendRemoteDataSource.getInstance();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        // Set AccessTokenStore
//        MockContext context = new MockContext();
//        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(context);
//        TokenInfo tokenInfo = new TokenInfo("stubAccessToken", "tokenType");
//        accessTokenStore.save(tokenInfo, "userEmail", "userName", "myIdOfFriend");
    }

    @Test
    public void getFriends() throws Exception {

        final FriendDataSource.LoadFriendsCallback loadCallback =
                Mockito.mock(FriendDataSource.LoadFriendsCallback.class);
        mRemoteDataSource.getFriends(loadCallback);

        // Mock friendService
// 그 내부의 작동은 알 필요 없다!
//        verify(mockService).getFriends();
//        verify(mockFriendsCall).enqueue(callbackCaptor.capture());
//        Response<FriendsResponse> response = mockFriendsCall.execute();
//        callbackCaptor.getValue().onResponse(mockFriendsCall, response);


        // todo should be called onResponse using callbackCaptor
//        verify(loadCallback).onFriendsLoaded();
    }
}
