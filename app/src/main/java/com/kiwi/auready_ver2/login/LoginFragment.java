package com.kiwi.auready_ver2.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kiwi.auready_ver2.R;

public class LoginFragment extends Fragment implements
        LoginContract.View,
        View.OnClickListener {

    private EditText mEmail;
    private EditText mPassword;
    private Button mBtLoginComplete;
    private Button mBtSignupComplete;
    private Button mBtLogoutComplete;

    private LoginContract.UserActionsListener mLoginPresenter;

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
        mBtSignupComplete = (Button) root.findViewById(R.id.bt_signup_complete);
        mBtLogoutComplete = (Button) root.findViewById(R.id.bt_logout_complete);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBtSignupComplete.setOnClickListener(this);
        mBtLoginComplete.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void showSignupFailMessage() {
        Snackbar.make(getView(), getString(R.string.signup_fail_message), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setSignupSuccessUI(String email) {

        // 1. Set email value to ed_email
        mEmail.setText(email);
    }

    @Override
    public void showEmailError() {

    }

    @Override
    public void showPasswordError() {

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.bt_signup_complete:
                mLoginPresenter.validateAccountCredentials(
                        mEmail.getText().toString(),
                        mPassword.getText().toString());
                break;

            case R.id.bt_login_complete:
                mLoginPresenter.validateAccountCredentials(
                        mEmail.getText().toString(),
                        mPassword.getText().toString());

            default:
                break;
        }
    }
}
