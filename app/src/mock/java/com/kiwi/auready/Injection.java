package com.kiwi.auready;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready.data.source.FriendRepository;
import com.kiwi.auready.data.source.NotificationRepository;
import com.kiwi.auready.data.source.TaskRepository;
import com.kiwi.auready.data.source.local.FriendLocalDataSource;
import com.kiwi.auready.data.source.local.NotificationLocalDataSource;
import com.kiwi.auready.data.source.local.TaskLocalDataSource;
import com.kiwi.auready.data.source.remote.FriendRemoteDataSource;
import com.kiwi.auready.data.source.remote.NotificationRemoteDataSource;
import com.kiwi.auready.data.source.remote.TaskRemoteDataSource;
import com.kiwi.auready.friend.domain.usecase.DeleteFriend;
import com.kiwi.auready.friend.domain.usecase.GetFriends;
import com.kiwi.auready.friend.domain.usecase.SaveFriend;
import com.kiwi.auready.notification.domain.usecase.DeleteNotification;
import com.kiwi.auready.notification.domain.usecase.GetNotifications;
import com.kiwi.auready.notification.domain.usecase.GetNotificationsCount;
import com.kiwi.auready.notification.domain.usecase.ReadNotification;
import com.kiwi.auready.notification.domain.usecase.SaveNotification;
import com.kiwi.auready.taskheaddetail.domain.usecase.EditTaskHeadDetail;
import com.kiwi.auready.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready.taskheaddetail.domain.usecase.SaveTaskHeadDetail;
import com.kiwi.auready.taskheads.domain.usecase.DeleteTaskHeads;
import com.kiwi.auready.taskheads.domain.usecase.GetTaskHeadDetails;
import com.kiwi.auready.taskheads.domain.usecase.GetTaskHeadsCount;
import com.kiwi.auready.taskheads.domain.usecase.InitializeLocalData;
import com.kiwi.auready.taskheads.domain.usecase.UpdateTaskHeadOrders;
import com.kiwi.auready.tasks.domain.usecase.ChangeComplete;
import com.kiwi.auready.tasks.domain.usecase.ChangeOrders;
import com.kiwi.auready.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready.tasks.domain.usecase.EditTasks;
import com.kiwi.auready.tasks.domain.usecase.GetMembers;
import com.kiwi.auready.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready.tasks.domain.usecase.SaveTask;

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

    public static ChangeComplete provideChangeCompleted(@NonNull Context context) {
        return new ChangeComplete(Injection.provideTaskRepository(context));
    }

    public static ChangeOrders provideChangeOrders(@NonNull Context context) {
        return new ChangeOrders(Injection.provideTaskRepository(context));
    }

    public static InitializeLocalData provideInitializeLocalData(@NonNull Context context) {
        return new InitializeLocalData(Injection.provideTaskRepository(context));
    }

    /*
    * Notification
    * */
    public static SaveNotification provideSaveNotification(@NonNull Context context) {
        return new SaveNotification(Injection.provideNotificationRepository(context));
    }

    private static NotificationRepository provideNotificationRepository(@NonNull Context context) {
        checkNotNull(context);
        return NotificationRepository.getInstance(NotificationRemoteDataSource.getInstance(context), NotificationLocalDataSource.getInstance(context));
    }

    public static GetNotifications provideGetNotifications(@NonNull Context context) {
        return new GetNotifications(Injection.provideNotificationRepository(context));
    }

    public static GetNotificationsCount provideGetNewNotificationsCount(@NonNull Context context) {
        return new GetNotificationsCount(Injection.provideNotificationRepository(context));
    }

    public static ReadNotification provideReadNotification(@NonNull Context context) {
        return new ReadNotification(Injection.provideNotificationRepository(context));
    }

    public static DeleteNotification provideDeleteNotification(@NonNull Context context) {
        return new DeleteNotification(Injection.provideNotificationRepository(context));
    }
}
