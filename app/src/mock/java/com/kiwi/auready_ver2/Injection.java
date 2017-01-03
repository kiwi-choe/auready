package com.kiwi.auready_ver2;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.source.FakeFriendRemoteDataSource;
import com.kiwi.auready_ver2.data.source.FakeTaskHeadDetailRemoteDataSource;
import com.kiwi.auready_ver2.data.source.FakeTaskHeadRemoteDataSource;
import com.kiwi.auready_ver2.data.source.FakeTaskRemoteDataSource;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailRepository;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.data.source.local.FriendLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.TaskHeadDetailLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.TaskHeadLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;
import com.kiwi.auready_ver2.friend.domain.usecase.DeleteFriend;
import com.kiwi.auready_ver2.friend.domain.usecase.GetFriends;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;
import com.kiwi.auready_ver2.login.domain.usecase.InitFriend;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHeadDetail;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadsCount;
import com.kiwi.auready_ver2.taskheads.domain.usecase.UpdateTaskHeadsOrder;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for {@link FriendDataSource} at compile time.
 * This is useful for testing, since it allows us to use a fake instance of the class
 * to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    /*
    * Friend
    * */
    public static FriendRepository provideFriendRepository(@NonNull Context context) {
        checkNotNull(context);
        return FriendRepository.getInstance(FakeFriendRemoteDataSource.getInstance(),
                FriendLocalDataSource.getInstance(context));
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetFriends provideGetFriends(@NonNull Context context) {
        return new GetFriends(Injection.provideFriendRepository(context));
    }

    public static SaveFriend provideSaveFriend(@NonNull Context context) {
        return new SaveFriend(Injection.provideFriendRepository(context));
    }

    public static InitFriend provideSaveFriends(@NonNull Context context) {
        return new InitFriend(Injection.provideFriendRepository(context));
    }

    public static DeleteFriend provideDeleteFriend(@NonNull Context context) {
        return new DeleteFriend(Injection.provideFriendRepository(context));
    }

    /*
    * TaskHead
    * */
    public static TaskHeadRepository provideTaskHeadRepository(@NonNull Context context) {
        checkNotNull(context);
        return TaskHeadRepository.getInstance(FakeTaskHeadRemoteDataSource.getInstance(),
                TaskHeadLocalDataSource.getInstance(context));
    }

    public static GetTaskHeads provideGetTaskHeads(@NonNull Context context) {
        return new GetTaskHeads(Injection.provideTaskHeadRepository(context));
    }

    public static DeleteTaskHeads provideDeleteTaskHeads(@NonNull Context context) {
        return new DeleteTaskHeads(Injection.provideTaskHeadRepository(context));
    }

    public static GetTaskHeadsCount provideGetTaskHeadsCount(@NonNull Context context) {
        return new GetTaskHeadsCount(Injection.provideTaskHeadRepository(context));
    }

    public static UpdateTaskHeadsOrder provideUpdateTaskHeadsOrder(@NonNull Context context) {
        return new UpdateTaskHeadsOrder(Injection.provideTaskHeadRepository(context));
    }

    /*
    * TaskHeadDetail
    * */
    private static TaskHeadDetailRepository provideTaskHeadDetailRepository(@NonNull Context context) {
        checkNotNull(context);
        return TaskHeadDetailRepository.getInstance(FakeTaskHeadDetailRemoteDataSource.getInstance(),
                TaskHeadDetailLocalDataSource.getInstance(context));
    }

    public static GetTaskHeadDetail provideGetTaskHeadDetail(@NonNull Context context) {
        return new GetTaskHeadDetail(Injection.provideTaskHeadDetailRepository(context));
    }


    public static SaveTaskHeadDetail provideSaveTaskHeadDetail(@NonNull Context context) {
        return new SaveTaskHeadDetail(Injection.provideTaskHeadDetailRepository(context));
    }

    /*
    * Task
    * */
    public static TaskRepository provideTaskRepository(@NonNull Context context) {
        checkNotNull(context);
        return TaskRepository.getInstance(FakeTaskRemoteDataSource.getInstance(),
                TaskLocalDataSource.getInstance(context));
    }

    public static GetTasksOfMember provideGetTasksOfMember(@NonNull Context context) {
        return new GetTasksOfMember(Injection.provideTaskRepository(context));
    }

    public static SaveTask provideSaveTask(@NonNull Context context) {
        return new SaveTask(Injection.provideTaskRepository(context));
    }

    public static DeleteTask provideDeleteTask(@NonNull Context context) {
        return new DeleteTask(Injection.provideTaskRepository(context));
    }

    public static DeleteTasks provideDeleteTasks(@NonNull Context context) {
        return new DeleteTasks(Injection.provideTaskRepository(context));
    }

    public static GetTasksOfTaskHead provideGetTasksOfTaskHead(@NonNull Context context) {
        return new GetTasksOfTaskHead(Injection.provideTaskRepository(context));
    }
}
