package com.kiwi.auready_ver2.data.remote;

import android.test.mock.MockContext;

import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.data.source.remote.FriendRemoteDataSource;
import com.kiwi.auready_ver2.login.IBaseUrl;
import com.kiwi.auready_ver2.rest_service.friend.FriendResponse;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;
import com.kiwi.auready_ver2.rest_service.friend.MockSuccessFriendService;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
 * Friend Remote Data Source test - with mock Server service
 */
public class FriendRemoteDataSourceTest {

    private static final String STUB_ACCESSTOKEN = "stub_accessToken";

    private FriendRemoteDataSource mRemoteDataSource = FriendRemoteDataSource.getInstance();

    private Retrofit mRetrofit;
    private MockRetrofit mMockRetrofit;

    private MockContext context;

    @Before
    public void setup() throws Exception {
        // Set AccessTokenStore
        context = new MockContext();
        AccessTokenStore.getInstance(context);

        // Set serviceBuilder
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .header("Authorization", "Bearer" + " " + STUB_ACCESSTOKEN)
                                .method(original.method(), original.body())
                                .build();
                        okhttp3.Response response = chain.proceed(request);
                        return response;
                    }
                });

        OkHttpClient client = httpClient.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(IBaseUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mMockRetrofit = new MockRetrofit.Builder(mRetrofit)
                .networkBehavior(behavior)
                .build();

    }

    @Test
    public void getFriends() throws Exception {

//        FriendDataSource.LoadFriendsCallback loadCallback =
//                Mockito.mock(FriendDataSource.LoadFriendsCallback.class);
//        mRemoteDataSource.getFriends(loadCallback);

        BehaviorDelegate<IFriendService> delegate = mMockRetrofit.create(IFriendService.class);
        IFriendService mockFriendService = new MockSuccessFriendService(delegate);

        // Actual test
        Call<FriendResponse> call = mockFriendService.getFriends();
        Response<FriendResponse> response = call.execute();

        // Asserting response
        assertTrue(response.isSuccessful());
        assertThat(response.body().getId(), is(MockSuccessFriendService.FRIEND_ID));
        assertThat(response.body().getEmail(), is(MockSuccessFriendService.FRIEND_EMAIL));

//        verify(loadCallback).onFriendsLoaded(FRIENDS);
    }
}
