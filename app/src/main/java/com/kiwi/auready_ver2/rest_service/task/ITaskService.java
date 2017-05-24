package com.kiwi.auready_ver2.rest_service.task;

import com.kiwi.auready_ver2.tasks.MemberTasks;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Task, TaskHead
 */

public interface ITaskService {

    /*
    * TaskHead(Detail)
    * */
    @POST("/taskheads/")
    Call<Void> saveTaskHeadDetail(@Body TaskHeadDetail_remote taskHeadRemote);
    @GET("/taskheads/{userid}")
    Call<List<TaskHeadDetail_remote>> getTaskHeadDetails(@Path("userid") String userId);

    @HTTP(method = "DELETE", path = "/taskheads", hasBody = true)
    Call<Void> deleteTaskHeads(@Body DeletingIds_remote ids);

    @PUT("/taskheads/{id}")
    Call<Void> editTaskHeadDetail(@Path("id") String id, @Body TaskHeadDetail_remote editTaskHeadDetail);

    /*
    * Task
    * */
    @POST("/tasks/{memberid}")
    Call<Void> saveTask(@Path("memberid") String memberId, @Body Task_remote task_remote);

    @PUT("/tasks/taskhead/{id}")
    Call<Void> editTasks(@Path("id") String taskHeadId, @Body List<MemberTasks> memberTasks);

    @PUT("/tasks/member/{id}")
    Call<Void> editTasksOfMember(@Path("id") String memberId, @Body List<Task_remote> editingTasks);

    @PUT("/tasks/{id}")
    Call<Void> changeCompleted(@Path("id") String taskId, @Body Task_remote editedTask);

    @DELETE("/tasks/{id}")
    Call<Void> deleteTask(@Path("id") String id);

    @GET("/taskheads/")
    Call<TaskHeadDetail_remote> getTaskHeadDetail(@Query("id") String taskHeadId);

    @GET("/taskheads/{id}/members")
    Call<List<Member_remote>> getMembers(@Path("id") String taskHeadId);

    @GET("/tasks/{memberid}")
    Call<List<Task_remote>> getTasksOfMember(@Path("memberid") String memberId);
}
