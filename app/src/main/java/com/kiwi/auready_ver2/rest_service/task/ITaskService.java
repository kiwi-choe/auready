package com.kiwi.auready_ver2.rest_service.task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Task, TaskHead
 */

public interface ITaskService {

    @POST("/taskheads")
    Call<Void> saveTaskHeadDetail(@Body TaskHead_remote taskHeadRemote);
    @GET("/taskheads/{name}")
    Call<List<TaskHead_remote>> getTaskHeads(@Path("name") String name);
}
