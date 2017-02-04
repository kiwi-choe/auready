package com.kiwi.auready_ver2.rest_service.friend;

import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.login.IBaseUrl;
import com.kiwi.auready_ver2.rest_service.ErrorResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static com.kiwi.auready_ver2.StubbedData.FriendStub.FRIENDS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Friend service test with mock
 */
public class FriendServiceTest {

    private static final String STUB_ACCESSTOKEN = "stub_accessToken";

    private MockRetrofit mMockRetrofit;
    private Retrofit mRetrofit;

    @Before
    public void setup() throws Exception {

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
    public void getFriends_Success() throws IOException {

        IFriendService mockFriendService = createMockSuccessService();

        // Actual test
        Call<FriendsResponse> call = mockFriendService.getFriends();
        Response<FriendsResponse> response = call.execute();
        // Asserting response
        assertTrue(response.isSuccessful());
        assertThat(response.body().getFriends(), is(FRIENDS));
    }

    @Test
    public void getFriends_Failure() throws IOException {

        IFriendService mockFriendService = createMockFailedService();

        Call<FriendsResponse> call = mockFriendService.getFriends();
        Response<FriendsResponse> response = call.execute();

        assertFalse(response.isSuccessful());

        Converter<ResponseBody, ErrorResponse> errorConverter =
                mRetrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error = errorConverter.convert(response.errorBody());

        assertEquals(MockFailedFriendService.ERROR_CODE, response.code());
        assertEquals(MockFailedFriendService.ERROR_GETFRIENDS_MSG, error.getMessage());
    }

    @Test
    public void findPeople_Success() throws IOException {

        IFriendService mockFriendService = createMockSuccessService();

        // Actual test
        String emailOrName = "stubbedEmailOrName";
        Call<List<SearchedUser>> call = mockFriendService.getUsers(emailOrName);
        Response<List<SearchedUser>> response = call.execute();

        assertTrue(response.isSuccessful());
        assertThat(response.body(), is(MockSuccessFriendService.SEARCHED_PEOPLE));
    }

    @Test
    public void findPeople_Failure() throws IOException {

        IFriendService mockFriendService = createMockFailedService();

        // Actual test
        String emailOrName = "stubbedEmailOrName";
        Call<List<SearchedUser>> call = mockFriendService.getUsers(emailOrName);
        Response<List<SearchedUser>> response = call.execute();

        assertFalse(response.isSuccessful());

        ErrorResponse error = convertError(response);
        Assert.assertEquals(MockFailedFriendService.ERROR_CODE, response.code());
        Assert.assertEquals(MockFailedFriendService.ERROR_FINDPEOPLE_MSG, error.getMessage());
    }

    @Test
    public void addFriend_Success() throws IOException {
        IFriendService mockFriendService = createMockSuccessService();

        SearchedUser user = new SearchedUser("name", 0);
        Call<Void> call = mockFriendService.addFriend(user);
        Response<Void> response = call.execute();

        assertTrue(response.isSuccessful());
    }

    @Test
    public void addFriend_Failure() throws IOException {
        IFriendService mockFriendService = createMockFailedService();

        SearchedUser user = new SearchedUser("name", 0);
        Call<Void> call = mockFriendService.addFriend(user);
        Response<Void> response = call.execute();

        assertFalse(response.isSuccessful());

        ErrorResponse error = convertError(response);
        assertEquals(MockFailedFriendService.ERROR_CODE, response.code());
        assertEquals(MockFailedFriendService.ERROR_ADDFRIEND_MSG, error.getMessage());
    }

    private ErrorResponse convertError(Response response) throws IOException {
        Converter<ResponseBody, ErrorResponse> errorConverter =
                mRetrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        return errorConverter.convert(response.errorBody());
    }

    private IFriendService createMockSuccessService() {
        BehaviorDelegate<IFriendService> delegate = mMockRetrofit.create(IFriendService.class);
        return new MockSuccessFriendService(delegate);
    }
    private IFriendService createMockFailedService() {
        BehaviorDelegate<IFriendService> delegate = mMockRetrofit.create(IFriendService.class);
        return new MockFailedFriendService(delegate);
    }
}