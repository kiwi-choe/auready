package com.kiwi.auready_ver2.friend;

import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 8/12/16.
 */
public class FindPresenterTest {

    private static final String EMAIL = "aa@a.com";
    private static final String NAME = "nameOfaa";

    @Mock
    private FriendRepository mFriendRepository;
    @Mock
    private FindContract.View mFindView;

    private FindPresenter mFindPresenter;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mFindPresenter = givenFindPresenter();
    }

    private FindPresenter givenFindPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveFriend saveFriend = new SaveFriend(mFriendRepository);

        return new FindPresenter(useCaseHandler, mFindView, saveFriend);
    }

    @Test
    public void saveFriendAndShowSuccessMsg() {

        // Given a new Friend
        final Friend newFriend = new Friend(EMAIL, NAME);
        mFindPresenter.saveFriend(newFriend);
        FriendDataSource.SaveCallback saveCallback = Mockito.mock(FriendDataSource.SaveCallback.class);
        verify(mFriendRepository).saveFriend(newFriend, saveCallback);

        // Show Success Msg
        verify(mFindView).showSuccessMsgOfAddFriend(newFriend);
    }
}