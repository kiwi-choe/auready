package com.kiwi.auready_ver2;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.FakeFriendRemoteDataSource;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.data.source.local.FriendLocalDataSource;
import com.kiwi.auready_ver2.friend.domain.usecase.GetFriend;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for {@link FriendDataSource} at compile time.
 * This is useful for testing, since it allows us to use a fake instance of the class
 * to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static FriendRepository provideFriendRepository(@NonNull Context context) {
        checkNotNull(context);
        return FriendRepository.getInstance(FakeFriendRemoteDataSource.getInstance(),
                FriendLocalDataSource.getInstance(context));
    }
    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetFriend provideGetFriend(@NonNull Context context) {
        return new GetFriend(Injection.provideFriendRepository(context));
    }
}
