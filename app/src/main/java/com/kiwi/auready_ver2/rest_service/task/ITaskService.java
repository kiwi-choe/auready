package com.kiwi.auready_ver2.rest_service.task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Task, TaskHead
 */

public interface ITaskService {

    @POST("/taskheads")
    Call<Void> saveTaskHeadDetail(@Body TaskHead_remote taskHeadRemote);
}
