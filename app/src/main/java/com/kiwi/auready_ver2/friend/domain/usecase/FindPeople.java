package com.kiwi.auready_ver2.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.api_model.SearchedUser;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Find people - request to Remote
 */
public class FindPeople extends UseCase<FindPeople.RequestValues, FindPeople.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public FindPeople(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        mFriendRepository.findPeople(requestValues.getEmailOrName(), new FriendDataSource.LoadSearchedPeopleCallback() {

            @Override
            public void onSearchedPeopleLoaded(@NonNull List<SearchedUser> searchedPeople) {
                checkNotNull(searchedPeople);
                getUseCaseCallback().onSuccess(new ResponseValue(searchedPeople));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String emailOrName;

        public RequestValues(@NonNull String emailOrName) {
            this.emailOrName = checkNotNull(emailOrName, "emailOrName cannot be null");
        }

        public String getEmailOrName() {
            return emailOrName;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final List<SearchedUser> mSearchedPeople;

        public ResponseValue(@NonNull List<SearchedUser> searchedPeople) {
            mSearchedPeople = checkNotNull(searchedPeople, "searchedPeople cannot be null");
        }

        public List<SearchedUser> getSearchedPeople() {
            return mSearchedPeople;
        }
    }
}
