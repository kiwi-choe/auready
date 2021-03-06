package com.kiwi.auready.data.source.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;

import com.kiwi.auready.data.source.FriendRepository;

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
    private static final String PREF_NAME = "userInfoInLocal";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLogin";
    // Access Token info key
    public static final String ACCESS_TOKEN = "access_token";
    // User info key
    public static final String USER_ID = "userId";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_NAME = "userName";

    public AccessTokenStore(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEditor = null;
    }

    // Create new AccessTokenStore
    public static AccessTokenStore getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (sSharedPrefs == null) {
            sSharedPrefs = new AccessTokenStore(context);
        }
        return sSharedPrefs;
    }

    public static AccessTokenStore getInstance() {
        if (sSharedPrefs == null) {
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
    public void save(String accessToken, String userEmail, String userName, String remote_userId) {
        // Set login status, accessToken
        mEditor = mPref.edit();
        mEditor.putBoolean(IS_LOGIN, true);

        mEditor.putString(ACCESS_TOKEN, accessToken);
        mEditor.putString(USER_EMAIL, userEmail);
        mEditor.putString(USER_NAME, userName);
        mEditor.putString(USER_ID, remote_userId);

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
