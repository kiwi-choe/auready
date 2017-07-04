package com.kiwi.auready.rest_service.task;

import com.kiwi.auready.tasks.MemberTasks;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @PUT("/taskheads/{id}/details")
    Call<Void> editTaskHeadDetail(@Path("id") String id, @Body TaskHeadDetail_remote editTaskHeadDetail);

    @PUT("/taskheads/orders")
    Call<Void> updateTaskHeadOrders(@Body List<UpdatingOrder_remote> updatingOrders);

    /*
    * Task
    * */
    @PUT("/tasks/taskhead/{id}")
    Call<Void> editTasks(@Path("id") String taskHeadId, @Body List<MemberTasks> memberTasks);

    @PUT("/tasks/member/{id}/add")
    Call<List<Task_remote>> addTask(@Path("id") String memberId, @Body AddTaskData addTaskData);

    @PUT("/tasks/member/{memberid}/del/{id}")
    Call<List<Task_remote>> deleteTask(@Path("memberid") String memberId, @Path("id") String taskHeadId, @Body List<Task_remote> editingTasks);

    @PUT("/tasks/member/{memberid}/completed/{id}")
    Call<List<Task_remote>> changeComplete(@Path("memberid") String memberId, @Path("id") String taskId,
                                           @Body List<Task_remote> editingTasks);

    @PUT("/tasks/member/{id}/orders")
    Call<List<Task_remote>> changeOrders(@Path("id") String memberId, @Body List<Task_remote> editingTasks);

    @GET("/taskheads/")
    Call<TaskHeadDetail_remote> getTaskHeadDetail(@Query("id") String taskHeadId);

    @GET("/taskheads/{id}/members")
    Call<List<Member_remote>> getMembers(@Path("id") String taskHeadId);

    @GET("/tasks/{memberid}")
    Call<List<Task_remote>> getTasksOfMember(@Path("memberid") String memberId);
}
