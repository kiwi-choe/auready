package com.kiwi.auready_ver2.rest_service.friend;

import com.kiwi.auready_ver2.login.IBaseUrl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Friend service test with mock
 */
public class FriendServiceTest {

    private MockRetrofit mockRetrofit;
    private Retrofit mRetrofit;

    @Before
    public void setup() throws Exception {
        mRetrofit = new Retrofit.Builder().baseUrl(IBaseUrl.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(mRetrofit)
                .networkBehavior(behavior)
                .build();
    }

    @Test
    public void getFriends_Success() throws IOException {
        BehaviorDelegate<IFriendService> delegate = mockRetrofit.create(IFriendService.class);
        IFriendService mockFriendService = new MockSuccessFriendService(delegate);

        // Actual test
        Call<FriendResponse> call = mockFriendService.getFriends();
        Response<FriendResponse> response = call.execute();
        // Asserting response
        assertTrue(response.isSuccessful());
        assertThat(response.body().getId(), is(MockSuccessFriendService.FRIEND_ID));
        assertThat(response.body().getEmail(), is(MockSuccessFriendService.FRIEND_EMAIL));
    }
}