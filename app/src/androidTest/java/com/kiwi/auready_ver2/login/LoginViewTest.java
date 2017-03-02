package com.kiwi.auready_ver2.login;

import android.content.Intent;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.rest_service.login.ClientCredential;
import com.kiwi.auready_ver2.rest_service.login.ILoginService;
import com.kiwi.auready_ver2.rest_service.login.LoginResponse;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginViewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Before
    public void setUp() throws Exception {
        retrofit = new Retrofit.Builder().baseUrl(IBaseUrl.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
    }


    public static Matcher<View> hasErrorText(final String expectedError) {
        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedError);
            }

            @Override
            protected boolean matchesSafely(View view) {

                if (!(view instanceof EditText)) {
                    return false;
                }

                EditText editText = (EditText) view;

                return expectedError.equals(editText.getError());
            }
        };
    }

    /*
    * Login Button
    * */
    @Test
    public void clickLoginButton() {
        // Launch Activity
        Intent intent = new Intent();
        mLoginActivityTestRule.launchActivity(intent);
        String email = "kiwi@kiwi.kiwi";
        clickLoginWith(email, "123");
        // Verify that login is succeeded

        onView(withText("Login succeed")).check(matches(isDisplayed()));

        // Verify that send result to taskHeadsView
        onView(withId(R.id.nav_email)).check(matches(isDisplayed()));
        onView(withText(email)).check(matches(isDisplayed()));
    }
//    @Test
//    public void showEmailFormatError_whenClickLoginButton() {
//        // Try to login with wrong email
//        clickLoginWith("wrong email", "123");
//
//        // Verify show email error of stringResourceName
//        Assert.assertEquals(mLoginActivityTestRule.getActivity().getString(R.string.email_format_err), "email format is invalid");
//        onView(withId(R.id.ed_email)).check(matches(hasErrorText(
//                mLoginActivityTestRule.getActivity().getString(R.string.email_format_err))));
//    }
//
//    @Test
//    public void showEmailEmptyError_whenClickLoginButton() {
//        //  try to login with the empty email
//        clickLoginWith("", "123");
//
//        // Verify show email error of stringResourceName
//        onView(withId(R.id.ed_email)).check(matches(hasErrorText(
//                mLoginActivityTestRule.getActivity().getString(R.string.email_empty_err))));
//    }
//
//    @Test
//    public void showPasswordError_whenClickLoginButton() {
//        // Try to login with empty password
//        clickLoginWith("validEmail@cc.com", "");
//
//        Assert.assertEquals(
//                mLoginActivityTestRule.getActivity().getString(R.string.password_empty_err), "password is empty");
//        // Verify show email error of stringResourceName
//        onView(withId(R.id.ed_password)).check(matches(hasErrorText(
//                mLoginActivityTestRule.getActivity().getString(R.string.password_empty_err))));
//    }
//
    private void clickLoginWith(String email, String password) {
        onView(withId(R.id.ed_email))
                .perform(replaceText(email), closeSoftKeyboard());
        onView(withId(R.id.ed_password))
                .perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.bt_login_complete)).perform(click());

    }
//
//    @Test
//    public void clickSignupButton_openSignupFragment() {
//        // Click signupButton
//        onView(withId(R.id.bt_signup_open)).perform(click());
//        // Check that Signup Fragment is opened
//        onView(withId(R.id.ed_name)).check(matches(isDisplayed()));
//    }

    //q 메인(TaskHeadsActivity)의 텍스트뷰에 대한 테스트는 어떻게 하지?
    // q How to check that LoginActivity is finished?
    @Test
    public void sendResultToTaskHeadsView_whenLoginSucceed() throws IOException {


        // Request login
        String loggedInEmail = "email@cc.com";
        String loggedInPassword = "123";

//        clickLoginWith(loggedInEmail, loggedInPassword);

        BehaviorDelegate<ILoginService> delegate = mockRetrofit.create(ILoginService.class);
        ILoginService mockLoginService = new MockSuccessLoginService_viewtest(delegate);

        // Create the loginInfo stub
        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.GRANT_TYPE,
                loggedInEmail,
                loggedInPassword);
        Call<LoginResponse> loginCall = mockLoginService.login(newCredentials);
        Response<LoginResponse> loginResponse = loginCall.execute();

        assertTrue(loginResponse.isSuccessful());


// Launch Activity
        Intent intent = new Intent();
        mLoginActivityTestRule.launchActivity(intent);

        onView(withText("Login succeed")).check(matches(isDisplayed()));

        // Verify that send result to taskHeadsView
//        onView(withId(R.id.nav_email)).check(matches(isDisplayed()));
//        onView(withText(loggedInEmail)).check(matches(isDisplayed()));
    }
}