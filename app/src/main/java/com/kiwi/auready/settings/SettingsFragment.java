package com.kiwi.auready.settings;

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
import android.widget.TextView;

import com.kiwi.auready.R;
import com.kiwi.auready.data.source.local.AccessTokenStore;

public class SettingsFragment extends Fragment implements SettingsContract.View {

    public static final String TAG = "Tag_SettingsFragment";
    public static final String EXTRA_LOGOUT = "logout";

    private SettingsContract.Presenter mPresenter;

    private AccessTokenStore mAccessTokenStore;

    private View mRoot;
    private TextView mName;
    private TextView mEmail;
    private Button mLogoutButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRoot = inflater.inflate(R.layout.fragment_settings, container, false);
        mName = (TextView) mRoot.findViewById(R.id.txt_user_name);
        mEmail = (TextView) mRoot.findViewById(R.id.txt_user_email);
        mLogoutButton = (Button) mRoot.findViewById(R.id.bt_logout);

        return mRoot;
    }

    private void setAccountView() {
        mAccessTokenStore = AccessTokenStore.getInstance(getActivity());

        String loggedInName = mAccessTokenStore.getStringValue(AccessTokenStore.USER_NAME, "Not saved name");
        String loggedInEmail = mAccessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, "Not saved email");
        mName.setText(loggedInName);
        mEmail.setText(loggedInEmail);

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.logout(mAccessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, ""));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set Account View in onActivityCreated,
        // Coz activity context is needed to get AccessTokenStore Instance
        setAccountView();
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void clearUserInfoInLocalAndShowAccountView() {
        // clear userInfo in Local
        if (mAccessTokenStore != null) {
            mAccessTokenStore.logoutUser();
        }

        Intent intent = getActivity().getIntent();
        intent.putExtra(EXTRA_LOGOUT, true);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showLogoutFailMessage() {
        Snackbar.make(mRoot, getString(R.string.logout_fail_msg), Snackbar.LENGTH_SHORT).show();
    }
}
