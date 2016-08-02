package com.kiwi.auready_ver2.friend;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.kiwi.auready_ver2.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

/**
 * Created by kiwi on 6/28/16.
 */
@RunWith(AndroidJUnit4.class)
public class FriendViewTest {

    private static final String EMAIL1 = "aa";

    @Rule
    public ActivityTestRule<FriendActivity> mActivityTestRule =
            new ActivityTestRule<>(FriendActivity.class);

    /*
        * A custom {@link Matcher} which matches an item in a {@link ListView} bt its text.
        *
        * View constraints:
        *   View must be a child of a {@link ListView}
        * @param itemText the text to match
        * @return Matcher that matches text in the given view
        * */
    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(ListView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("isDescendantOfA ListView with Text " + itemText);
            }
        };
    }

    @Test
    public void showFriendList() {

        // Check that set visible to friend_list_layout
        onView(withId(R.id.friend_list_layout))
                .check(matches(isDisplayed()));

        // Verify that all friends are shown
        onView(withItemText("aa")).check(matches(isDisplayed()));
        onView(withItemText("bb")).check(matches(isDisplayed()));
    }


    @Test
    public void longClickOnFriendItem_deleteFriend() {

        onView(withItemText(EMAIL1)).perform(click());

        // Verify it was deleted
        onView(withItemText(EMAIL1)).check(matches(not(isDisplayed())));
    }


}