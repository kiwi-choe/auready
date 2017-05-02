package com.kiwi.auready_ver2;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.data.source.local.FriendLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.NotificationLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;
import com.kiwi.auready_ver2.data.source.remote.FriendRemoteDataSource;
import com.kiwi.auready_ver2.data.source.remote.TaskRemoteDataSource;
import com.kiwi.auready_ver2.friend.domain.usecase.DeleteFriend;
import com.kiwi.auready_ver2.friend.domain.usecase.GetFriends;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNewNotificationsCount;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNotifications;
import com.kiwi.auready_ver2.notification.domain.usecase.ReadNotification;
import com.kiwi.auready_ver2.notification.domain.usecase.SaveNotification;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.EditTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHeadDetail;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadDetails;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadsCount;
import com.kiwi.auready_ver2.taskheads.domain.usecase.InitializeLocalData;
import com.kiwi.auready_ver2.taskheads.domain.usecase.UpdateTaskHeadOrders;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class Injection {

    /*
    * Friend
    * */
    public static FriendRepository provideFriendRepository(@NonNull Context context) {
        checkNotNull(context);
        return FriendRepository.getInstance(FriendLocalDataSource.getInstance(context),
                FriendRemoteDataSource.getInstance(context));
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

    public static DeleteFriend provideDeleteFriend(@NonNull Context context) {
        return new DeleteFriend(Injection.provideFriendRepository(context));
    }


    /*
    * Task
    * */
    public static TaskRepository provideTaskRepository(@NonNull Context context) {
        checkNotNull(context);
        return TaskRepository.getInstance(TaskRemoteDataSource.getInstance(context), TaskLocalDataSource.getInstance(context));
    }

    public static GetTaskHeadDetails provideGetTaskHeadDetails(@NonNull Context context) {
        return new GetTaskHeadDetails(Injection.provideTaskRepository(context));
    }

    public static GetTaskHeadsCount provideGetTaskHeadsCount(@NonNull Context context) {
        return new GetTaskHeadsCount(Injection.provideTaskRepository(context));
    }

    public static UpdateTaskHeadOrders provideUpdateTaskHeadsOrder(@NonNull Context context) {
        return new UpdateTaskHeadOrders(Injection.provideTaskRepository(context));
    }

    public static SaveTaskHeadDetail provideSaveTaskHeadDetail(@NonNull Context context) {
        return new SaveTaskHeadDetail(Injection.provideTaskRepository(context));
    }

    public static DeleteTaskHeads provideDeleteTaskHeads(@NonNull Context context) {
        return new DeleteTaskHeads(Injection.provideTaskRepository(context));
    }

    public static GetTaskHeadDetail provideGetTaskHeadDetail(@NonNull Context context) {
        return new GetTaskHeadDetail(Injection.provideTaskRepository(context));
    }

    public static EditTaskHeadDetail provideEditTaskHeadDetail(@NonNull Context context) {
        return new EditTaskHeadDetail(Injection.provideTaskRepository(context));
    }

    public static GetMembers provideGetMembers(@NonNull Context context) {
        return new GetMembers(Injection.provideTaskRepository(context));
    }

    public static GetTasksOfMember provideGetTasksOfMember(@NonNull Context context) {
        return new GetTasksOfMember(Injection.provideTaskRepository(context));
    }

    public static SaveTask provideSaveTask(@NonNull Context context) {
        return new SaveTask(Injection.provideTaskRepository(context));
    }

    public static DeleteTask provideDeleteTasks(@NonNull Context context) {
        return new DeleteTask(Injection.provideTaskRepository(context));
    }

    public static EditTasks provideEditTasks(@NonNull Context context) {
        return new EditTasks(Injection.provideTaskRepository(context));
    }

    public static EditTasksOfMember provideEditTasksOfMember(@NonNull Context context) {
        return new EditTasksOfMember(Injection.provideTaskRepository(context));
    }

    /*
    * Notification
    * */
    public static SaveNotification provideSaveNotification(@NonNull Context context) {
        return new SaveNotification(Injection.provideNotificationRepository(context));
    }

    private static NotificationLocalDataSource provideNotificationRepository(@NonNull Context context) {
        checkNotNull(context);
        return NotificationLocalDataSource.getInstance(context);
    }

    public static GetNotifications provideGetNotifications(@NonNull Context context) {
        return new GetNotifications(Injection.provideNotificationRepository(context));
    }

    public static GetNewNotificationsCount provideGetNewNotificationsCount(@NonNull Context context) {
        return new GetNewNotificationsCount(Injection.provideNotificationRepository(context));
    }

    public static ReadNotification provideReadNotification(@NonNull Context context) {
        return new ReadNotification(Injection.provideNotificationRepository(context));
    }

    public static InitializeLocalData provideInitializeLocalData(@NonNull Context context) {
        return new InitializeLocalData(Injection.provideTaskRepository(context));
    }
}
