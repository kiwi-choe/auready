package com.kiwi.auready_ver2;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;
import com.kiwi.auready_ver2.data.FakeFriendRemoteDataSource;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.data.source.local.FriendLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.TaskHeadLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;
import com.kiwi.auready_ver2.data.source.remote.FakeTaskHeadRemoteDataSource;
import com.kiwi.auready_ver2.data.source.remote.FakeTaskRemoteDataSource;
import com.kiwi.auready_ver2.friend.domain.usecase.GetFriends;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;
import com.kiwi.auready_ver2.login.domain.usecase.SaveFriends;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;

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

    public static SaveFriends provideSaveFriends(@NonNull Context context) {
        return new SaveFriends(Injection.provideFriendRepository(context));
    }

    /*
    * TaskHead
    * */
    private static TaskHeadRepository provideTaskHeadRepository(@NonNull Context context) {
        checkNotNull(context);
        return TaskHeadRepository.getInstance(FakeTaskHeadRemoteDataSource.getInstance(),
                TaskHeadLocalDataSource.getInstance(context));
    }

    public static GetTaskHeads provideGetTaskHeads(@NonNull Context context) {
        return new GetTaskHeads(Injection.provideTaskHeadRepository(context));
    }

    public static DeleteTaskHead provideDeleteTaskHead(@NonNull Context context) {
        return new DeleteTaskHead(Injection.provideTaskHeadRepository(context));
    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(Injection.provideTaskRepository(context));
    }

    private static TaskRepository provideTaskRepository(@NonNull Context context) {
        checkNotNull(context);
        return TaskRepository.getInstance(FakeTaskRemoteDataSource.getInstance(),
                TaskLocalDataSource.getInstance(context));
    }

    public static SaveTasks provideSaveTasks(@NonNull Context context) {
        return new SaveTasks(Injection.provideTaskRepository(context));
    }
}
