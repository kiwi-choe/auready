package com.kiwi.auready_ver2.taskheads;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TaskHeadsListViewTest {

    private static final String TITLE1 = "Don't be hurry";
    private static final String TITLE2 = "just";
    private static final String TITLE3 = "keep going";
    private static final String USER_NAME = "KIWIYA";
    private static final String USER_EMAIL = "KIWIYA@gmail.com";

    private TaskHeadsActivity mActivity;

    @Rule
    public ActivityTestRule<TaskHeadsActivity> mActivityTestRule =
            new ActivityTestRule<>(TaskHeadsActivity.class);

    @Before
    public void setup() {
        mActivity = mActivityTestRule.getActivity();
    }

    @After
    public void cleanup() {
        Injection.provideTaskHeadRepository(InstrumentationRegistry.getTargetContext())
                .deleteAllTaskHeads();

        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(mActivity.getApplicationContext());
        accessTokenStore.logoutUser();
    }
}
