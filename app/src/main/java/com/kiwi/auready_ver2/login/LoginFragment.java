package com.kiwi.auready_ver2.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class LoginFragment extends Fragment implements
        LoginContract.View,
        View.OnClickListener,
        LoginActivity.LoginActivityListener {

    public static final String TAG_LOGINFRAGMENT = "Tag_LoginFragment";

    private EditText mEmail;
    private EditText mPassword;
    private Button mBtLoginComplete;
    private TextView mBtSignupOpen;
    private Button mBtLogoutComplete;

    private LoginContract.Presenter mLoginPresenter;

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

        mLoginPresenter = new LoginPresenter(this);
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
    public void setLoginSuccessUI(String loggedInEmail) {

        Snackbar.make(getView(), getString(R.string.login_success_msg), Snackbar.LENGTH_SHORT).show();

        // Send result OK and the logged in email to TasksView
        Intent intent = new Intent();
        intent.putExtra(LoginActivity.REGISTERED_EMAIL, loggedInEmail);

        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showLoginFailMessage(int stringResource) {
        if (isAdded())
            Snackbar.make(getView(), getString(stringResource), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.bt_signup_open:
                startSignupFragment();
                break;

            case R.id.bt_login_complete:
                mLoginPresenter.attemptLogin(
                        mEmail.getText().toString(),
                        mPassword.getText().toString());
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
    public void onSendData(final String registeredEmail) {
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
    }
}
