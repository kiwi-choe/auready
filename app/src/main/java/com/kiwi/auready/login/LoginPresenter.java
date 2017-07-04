package com.kiwi.auready.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kiwi.auready.R;
import com.kiwi.auready.UseCaseHandler;
import com.kiwi.auready.data.User;
import com.kiwi.auready.rest_service.ServiceGenerator;
import com.kiwi.auready.rest_service.login.ClientCredential;
import com.kiwi.auready.rest_service.login.ILoginService;
import com.kiwi.auready.rest_service.login.LoginResponse;
import com.kiwi.auready.rest_service.notification.INotificationService;
import com.kiwi.auready.util.LoginUtils;

import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Login
 */
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "TAG_LoginPresenter";

    private final LoginContract.View mLoginView;

    private final UseCaseHandler mUseCaseHandler;

    public LoginPresenter(@NonNull UseCaseHandler useCaseHandler,
                          @NonNull LoginContract.View loginView) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mLoginView = checkNotNull(loginView, "loginView cannot be null");

        mLoginView.setPresenter(this);
    }

    @Override
    public boolean validateEmail(String email) {

        if (email == null || TextUtils.isEmpty(email)) {
            onEmailError(R.string.email_empty_err);
            return false;
        }
        // Check email format
        Matcher matcher = LoginUtils.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            onEmailError(R.string.email_format_err);
            return false;
        }

        return true;
    }

    @Override
    public boolean validatePassword(String password) {

        if (password == null || password.isEmpty()) {
            onPasswordError(R.string.password_empty_err);
            return false;
        }
        return true;
    }

    @Override
    public void onEmailError(int stringResourceName) {
        mLoginView.showEmailError(stringResourceName);
    }

    @Override
    public void onPasswordError(int stringResourceName) {
        mLoginView.showPasswordError(stringResourceName);
    }

    @Override
    public void attemptLogin(String email, String password) {

        if (validateEmail(email) && validatePassword(password)) {
            requestLogin(email, password);
        }
    }

    private void requestLogin(final String email, String password) {

        ILoginService loginService =
                ServiceGenerator.createService(ILoginService.class,
                        ClientCredential.CLIENT_ID, ClientCredential.CLIENT_SECRET);

        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.GRANT_TYPE,
                email,
                password);

        Call<LoginResponse> call = loginService.login(newCredentials);
        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // Save tokenInfo to sharedPreferences
                    onLoginSuccess(response.body());
                } else {    // 4xx code
                    onLoginFail(R.string.login_fail_message_400);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                Log.d("Exception in Signup: ", "Called OnFailure()", t);
                onLoginFail(R.string.login_fail_message_onfailure);
            }
        });
    }

    /*
    * after login succeeded, update 3 parts
    * 1. UI
    * 2. register instanceID for FCM
    * 3. Save loggedInUser info in SharedPreferences
    * */
    @Override
    public void onLoginSuccess(LoginResponse loginResponse) {

        // Stop progressBar

        // 1. send logged in email to MainView
        final User user = loginResponse.getUserInfo();
        mLoginView.setLoginSuccessUI(user.getEmail(), user.getName());

        final String accessToken = loginResponse.getAccessToken();

        // 2. Send instanceID to the app server
        Log.d(TAG, "entered into onLoginSuccess()");
        String instanceId = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(instanceId, accessToken);

        // 3. Set LoggedInUser info to SharedPreferences// 3. Set LoggedInUser info to SharedPreferences
        mLoginView.setLoggedInUserInfo(accessToken, user.getEmail(), user.getName(), user.getId());
    }

    private static void sendRegistrationToServer(String instanceId, String accessToken) {

        INotificationService service = ServiceGenerator.createService(INotificationService.class, accessToken);
        Call<Void> call = service.sendRegistration(instanceId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "success to send instanceID");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "fail to send instanceID, ", t);
            }
        });
    }

    @Override
    public void onLoginFail(int stringResource) {
        mLoginView.showLoginFailMessage(stringResource);
    }

    @Override
    public void attemptSocialLogin(String socialapp, String idToken) {
        if(socialapp != null && idToken != null) {
            requestLogin(socialapp, idToken);
        }
    }

    private void showSaveError() {
        // Show error, log, etc when save is failed.
        Log.d("ErrorInLoginPresenter", "Failed to save the friends");
    }

    @Override
    public void start() {

    }
}
