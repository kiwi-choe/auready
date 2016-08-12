package com.kiwi.auready_ver2.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.util.ActivityUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoginFragment extends Fragment implements
        LoginContract.View,
        View.OnClickListener,
        LoginActivity.LoginActivityListener {

    public static final String TAG_LOGINFRAGMENT = "Tag_LoginFragment";

    private EditText mEmail;
    private EditText mPassword;
    private EditText mName;
    private Button mBtLoginComplete;
    private TextView mBtSignupOpen;
    private Button mBtLogoutComplete;

    private LoginContract.Presenter mPresenter;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        mEmail = (EditText) root.findViewById(R.id.ed_email);
        mPassword = (EditText) root.findViewById(R.id.ed_password);
        mName = (EditText) root.findViewById(R.id.ed_name);

        mBtLoginComplete = (Button) root.findViewById(R.id.bt_login_complete);
        mBtSignupOpen = (TextView) root.findViewById(R.id.bt_signup_open);
//        mBtLogoutComplete = (Button) root.findViewById(R.id.bt_logout_complete);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBtLoginComplete.setOnClickListener(this);
        mBtSignupOpen.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showEmailError(int stringResourceName) {
        mEmail.requestFocus();
        mEmail.setError(getString(stringResourceName));
    }

    @Override
    public void showPasswordError(int stringResourceName) {
        String errMsg = getString(stringResourceName);
        mPassword.requestFocus();
        mPassword.setError(errMsg);
    }

    @Override
    public void setLoginSuccessUI(TokenInfo tokenInfo, String name, String email) {

        // 1. Save tokenInfo to SharedPreferences
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance();
        accessTokenStore.save(tokenInfo, name, email);

        // 2. popup message
        Snackbar.make(getView(), getString(R.string.login_success_msg), Snackbar.LENGTH_SHORT).show();
        // 3. Send result OK and the logged in email to TasksView
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showLoginFailMessage(int stringResource) {
        if (isAdded())
            Snackbar.make(getView(), getString(stringResource), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.bt_signup_open:
                startSignupFragment();
                break;

            case R.id.bt_login_complete:
                mPresenter.attemptLogin(
                        mEmail.getText().toString(),
                        mPassword.getText().toString(),
                        mName.getText().toString());
                break;

            default:
                break;
        }
    }


    private void startSignupFragment() {

        // Create new fragment and transaction
        SignupFragment signupFragment = SignupFragment.newInstance();
        ActivityUtils.replaceFragment(getFragmentManager(),
                signupFragment, R.id.content_frame, SignupFragment.TAG_SIGNUPFRAGMENT);
    }


    @Override
    public void onSendData(final String registeredEmail, final String registeredName) {
        /*
        * popBackStack of {@link FragmentManager}
        * don't process rightly, put into enqueueAction once.
        */
        mEmail.post(new Runnable() {
            @Override
            public void run() {
                mEmail.setText(registeredEmail);
            }
        });
        mName.post(new Runnable() {
            @Override
            public void run() {
                mName.setText(registeredName);
            }
        });
    }
}
