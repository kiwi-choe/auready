package com.kiwi.auready_ver2.rest_service.notification;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * FCM messaging service
 */
public interface INotificationService {

    @POST("/notifications/{instanceId}")
    Call<Void> sendRegistration(@Path("instanceId") String instanceId);

    @GET("/relationships/status/pending")
    Call<PendingRequestList> getFriendRequestPending();

    @GET("/notifications/auready/{userId}")
    Call<Void> notifyAUReady(@Path("userId") String userId);
}