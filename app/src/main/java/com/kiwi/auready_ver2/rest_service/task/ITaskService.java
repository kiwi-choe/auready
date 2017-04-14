package com.kiwi.auready_ver2.rest_service.task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Task, TaskHead
 */

public interface ITaskService {

    @POST("/taskheads/")
    Call<Void> saveTaskHeadDetail(@Body TaskHeadDetail_remote taskHeadRemote);
    @GET("/taskheads/{name}")
    Call<List<TaskHeadDetail_remote>> getTaskHeadDetails(@Path("name") String name);

    @HTTP(method = "DELETE", path = "/taskheads", hasBody = true)
    Call<Void> deleteTaskHeads(@Body DeletingIds_remote ids);
}
