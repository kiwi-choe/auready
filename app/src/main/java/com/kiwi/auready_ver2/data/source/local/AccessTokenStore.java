package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.rest_service.login.TokenInfo;
import com.kiwi.auready_ver2.data.source.FriendRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Store that TokenInfo and Friend info to access to the data easily.
 * Not called -Repository like {@link FriendRepository}, because two of roles are different.
 */
public class AccessTokenStore {

    private static AccessTokenStore sSharedPrefs = null;
    // Shared Preferences
    private SharedPreferences mPref;
    // Editor for Shared Preferences
    private Editor mEditor;

    // SharedPref file name
    private static final String PREF_NAME = "localAccessToken";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLogin";

    // Access Token info key
    public static final String ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_NAME = "userName";
    public static final String MY_ID_OF_FRIEND = "myIdOfFriend";

    public AccessTokenStore(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEditor = null;
    }

    // Create new AccessTokenStore
    public static AccessTokenStore getInstance(@NonNull Context context) {
        checkNotNull(context);
        if(sSharedPrefs == null) {
            sSharedPrefs = new AccessTokenStore(context);
        }
        return sSharedPrefs;
    }
    public static AccessTokenStore getInstance() {
        if(sSharedPrefs == null) {
            throw new IllegalStateException(
                    "Should use getInstance(Context) at least once before using this method.");
            // or, can create a new instance here.
            // ex) getInstance(MyCustomApplication.getAppContext());
        }
        return sSharedPrefs;
    }

    // Get String value
    public String getStringValue(String key, String defValue) {
        return mPref.getString(key, defValue);
    }

    // Save Access token
    public void save(TokenInfo tokenInfo, String userEmail, String userName, String myIdOfFriend) {
        // Set login status, accessToken
        mEditor = mPref.edit();
        mEditor.putBoolean(IS_LOGIN, true);

        mEditor.putString(ACCESS_TOKEN, tokenInfo.getAccessToken());
        mEditor.putString(TOKEN_TYPE, tokenInfo.getTokenType());
        mEditor.putString(USER_EMAIL, userEmail);
        mEditor.putString(USER_NAME, userName);
        mEditor.putString(MY_ID_OF_FRIEND, myIdOfFriend);

        // commit changes
        mEditor.apply();
        mEditor = null;
    }

    // testing in TaskHeadsViewTest
    public void setLoggedInStatus() {
        mEditor = mPref.edit();
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.apply();
        mEditor = null;
    }
    // testing for adding ME to members in TaskHeadDetail
    public void save_forTesting(String userEmail, String userName, String myIdOfFriend) {

        mEditor = mPref.edit();

        mEditor.putString(USER_EMAIL, userEmail);
        mEditor.putString(USER_NAME, userName);
        mEditor.putString(MY_ID_OF_FRIEND, myIdOfFriend);

        // commit changes
        mEditor.apply();
        mEditor = null;
    }

    // Get Login status
    public boolean isLoggedIn() {
        return mPref.getBoolean(IS_LOGIN, false);
    }

    // Clear session details when requestLogout
    public void logoutUser() {
        mEditor = mPref.edit();
        // Clear all data from SharedPreferences
        mEditor.clear();
        mEditor.apply();
        mEditor = null;
    }
}
