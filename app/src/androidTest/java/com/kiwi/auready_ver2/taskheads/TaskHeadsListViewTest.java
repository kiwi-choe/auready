package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.FakeTaskHeadRemoteDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class TaskHeadsListViewTest {

    private static final String TITLE1 = "Don't be hurry";
    private static final String TITLE2 = "just";
    private static final String TITLE3 = "keep going";
    private static final List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
            new Friend("email3", "name3"));
    private static final String USER_NAME = "KIWIYA";
    private static final String USER_EMAIL = "KIWIYA@gmail.com";

    private static List<TaskHead> TASKHEADS = Lists.newArrayList(new TaskHead(TITLE1, MEMBERS, 0),
            new TaskHead(TITLE2, MEMBERS, 1), new TaskHead(TITLE3, MEMBERS, 2));

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
