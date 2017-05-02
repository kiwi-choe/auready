package com.kiwi.auready_ver2.rest_service.friend;

import com.google.gson.Gson;
import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.rest_service.ErrorResponse;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

/**
 * Mock Friend service - failure
 */
public class MockFailedFriendService implements IFriendService {

    static final int ERROR_CODE = 404;
    static final String ERROR_GETFRIENDS_MSG = "getFriends failed";
    public static final String ERROR_FINDPEOPLE_MSG = "findPeople_failed";
    public static final String ERROR_ADDFRIEND_MSG = "addFriend_failed";

    private final BehaviorDelegate<IFriendService> delegate;

    public MockFailedFriendService(BehaviorDelegate<IFriendService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<FriendsResponse> getFriends() {

        ErrorResponse error = new ErrorResponse(ERROR_CODE, ERROR_GETFRIENDS_MSG);
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(ERROR_CODE, ResponseBody.create(MediaType.parse("application/json"), json));
        return delegate.returning(Calls.response(response)).getFriends();
    }

    @Override
    public Call<List<SearchedUser>> getUsers(@Path("search") String emailOrName) {
        ErrorResponse error = new ErrorResponse(ERROR_CODE, ERROR_FINDPEOPLE_MSG);
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(ERROR_CODE, ResponseBody.create(MediaType.parse("application/json"), json));
        return delegate.returning(Calls.response(response)).getUsers(emailOrName);
    }

    @Override
    public Call<Void> addFriend(@Path("name") String name) {

        ErrorResponse error = new ErrorResponse(ERROR_CODE, ERROR_ADDFRIEND_MSG);
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(ERROR_CODE, ResponseBody.create(MediaType.parse("application/json"), json));
        return delegate.returning(Calls.response(response)).addFriend(name);
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
