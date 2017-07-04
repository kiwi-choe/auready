package com.kiwi.auready.login.google;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kiwi.auready.R;
import com.kiwi.auready.rest_service.ServiceGenerator;
import com.kiwi.auready.rest_service.login.ISignupService;
import com.kiwi.auready.rest_service.login.SocialSignupInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleSignInFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "GoogleSignInFragment";

    private static final int RC_GET_TOKEN = 9002;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInFragmentListener mListener;

    public GoogleSignInFragment() {
        // Required empty public constructor
    }

    public static GoogleSignInFragment newInstance() {
        return new GoogleSignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure googleSignIn options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_google_sign_in, container, false);

        root.findViewById(R.id.bt_google_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test IdToken", "clicked google signin button");
                getIdToken();
            }
        });
        return root;
    }

    private void getIdToken() {
        // Start progressBar

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            String idToken = result.getSignInAccount().getIdToken();
            Log.d(TAG, idToken);
            // Signup social account; with idToken, email
            // URI; POST social-account/signup/:socialapp
            requestSocialSignUp(idToken);
        }
    }

    private void requestSocialSignUp(final String idToken) {
        // Request to my app server
        ISignupService signupService =
                ServiceGenerator.createService(ISignupService.class);

        SocialSignupInfo socialSignupInfo =
                new SocialSignupInfo(SocialSignupInfo.GOOGLE, idToken);
        Call<Void> call = signupService.signupGoogle(socialSignupInfo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    onSignUpSuccess(SocialSignupInfo.GOOGLE, idToken);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onSignUpFail(getString(R.string.social_signup_fail_msg, SocialSignupInfo.GOOGLE));
            }
        });
    }

    private void onSignUpFail(String failMessage) {
        Snackbar.make(getView(), failMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void onSignUpSuccess(String socialapp, String idToken) {
        // Pass the role of login processing after social-account signup to LoginView
        if(mListener != null) {
            mListener.onGoogleSignupSuccess(socialapp, idToken);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("test idToken", "Connection is failed.");
        Toast.makeText(this.getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof GoogleSignInFragmentListener) {
            mListener = (GoogleSignInFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GoogleSignInFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Interface with LoginActivity
    public interface GoogleSignInFragmentListener {
        void onGoogleSignupSuccess(String socialapp, String idToken);
    }
}