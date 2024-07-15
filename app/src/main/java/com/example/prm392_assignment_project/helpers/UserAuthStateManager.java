package com.example.prm392_assignment_project.helpers;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.api_handlers.commons.HttpStatusCodes;
import com.example.prm392_assignment_project.api_handlers.implementation.AuthApiHandler;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.auths.RefreshAccessTokenDto;
import com.example.prm392_assignment_project.views.view_callbacks.ILogoutCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IVerifyAccessTokenFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IVerifyAccessTokenSuccessCallback;

import org.json.JSONObject;

import android.os.Handler;
import android.widget.Toast;

import java.util.Objects;

/**
 * Manage related data about user authentication
 * such as access token and refresh token.
 * @implNote This class implements singleton pattern.
 */
public class UserAuthStateManager
{
    // Private fields.
    private final Context context;
    private final SharedPreferenceHelper sharedPreferenceHelper;
    private final AuthApiHandler authApiHandler;

    private String accessToken;
    private String refreshToken;
    private String userAvatarUrl;
    private String userFullName;
    private boolean accessTokenIsStillValid;

    // Static fields and singleton instance.
    private static final String ACCESS_TOKEN_PREFERENCE_KEY = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_PREFERENCE_KEY = "REFRESH_TOKEN";

    private static UserAuthStateManager instance;

    private UserAuthStateManager(Context context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("The context is null when init user auth state manager");
        }

        this.context = context;
        authApiHandler = new AuthApiHandler(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    public static void setUp(Context context)
    {
        if (instance == null)
        {
            instance = new UserAuthStateManager(context);
        }
    }

    public static UserAuthStateManager getInstance()
    {
        if (instance == null)
        {
            throw new IllegalArgumentException("The context is null when init user auth state manager");
        }

        return instance;
    }

    public String getUserAvatarUrl()
    {
        return userAvatarUrl;
    }

    public String getUserFullName()
    {
        return userFullName;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
        sharedPreferenceHelper.putString(ACCESS_TOKEN_PREFERENCE_KEY, accessToken);
    }

    public String getAccessToken()
    {
        if (accessToken == null)
        {
            accessToken = sharedPreferenceHelper.getString(ACCESS_TOKEN_PREFERENCE_KEY);
        }

        return accessToken;
    }

    private void keepUserSignIn()
    {
        accessTokenIsStillValid = true;
    }

    public boolean isAccessTokenStillValid()
    {
        return accessTokenIsStillValid;
    }

    public String getRefreshToken()
    {
        if (refreshToken == null)
        {
            refreshToken = sharedPreferenceHelper.getString(REFRESH_TOKEN_PREFERENCE_KEY);
        }
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
        sharedPreferenceHelper.putString(REFRESH_TOKEN_PREFERENCE_KEY, refreshToken);
    }

    public void verifyCurrentAccessToken(
        IVerifyAccessTokenSuccessCallback verifyAccessTokenSuccessCallback,
        IVerifyAccessTokenFailedCallback verifyAccessTokenFailedCallback)
    {
        Handler handler = new android.os.Handler(Looper.getMainLooper());

        Thread verifyAccessTokenThread = new Thread(() ->
        {
            handler.post(() ->
            {
                authApiHandler.verifyAccessToken(
                    getAccessToken(),
                    (response) ->
                    {
                        handleOnVerifyAccessTokenSuccess(response);
                        verifyAccessTokenSuccessCallback.resolve();
                    },
                    (error) ->
                    {
                        handleOnVerifyAccessTokenFailed(error);
                        Objects.requireNonNull(verifyAccessTokenFailedCallback).resolve();
                    });
            });
        });

        verifyAccessTokenThread.start();
    }

    private void handleOnVerifyAccessTokenSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> deserializeResult = ApiResponse.DeserializeFromJson(response);

        if (!deserializeResult.isSuccess)
        {
            return;
        }

        ApiResponse apiResponse = deserializeResult.value;

        try
        {
            JSONObject responseBody = apiResponse.getBodyAsJsonObject();
            userAvatarUrl = responseBody.getString("avatarUrl");
            userFullName = responseBody.getString("fullName");
            keepUserSignIn();
        }
        catch (Exception exception)
        {
            Log.e("Verify access token deserialized failed", "Cannot parse response body");
        }
    }

    private void handleOnVerifyAccessTokenFailed(VolleyError error)
    {
        if (error.networkResponse == null)
        {
            return;
        }

        if (error.networkResponse.statusCode == HttpStatusCodes.UNAUTHORIZED)
        {
            Log.e("Access token is expired", "Please refresh again");

            RefreshAccessTokenDto refreshAccessTokenDto = RefreshAccessTokenDto.getInstance(accessToken, refreshToken);

            try
            {
                authApiHandler.refreshAgainAccessAndRefreshToken(
                    refreshAccessTokenDto,
                    this::handleRefreshAccessTokenSuccess,
                    this::handleRefreshAccessTokenFailed);
            }
            catch (Exception exception)
            {
                Log.e("Refresh access token serialized error", exception.getMessage());
            }
        }
    }

    private void handleRefreshAccessTokenSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> deserializeResult = ApiResponse.DeserializeFromJson(response);

        if (!deserializeResult.isSuccess)
        {
            return;
        }

        ApiResponse apiResponse = deserializeResult.value;

        try
        {
            JSONObject responseBody = apiResponse.getBodyAsJsonObject();
            // Get tokens from response
            String accessToken = responseBody.getString("accessToken");
            String refreshToken = responseBody.getString("refreshToken");

            // Set to shared preference.
            setAccessToken(accessToken);
            setRefreshToken(refreshToken);

            accessTokenIsStillValid = true;
        }
        catch (Exception exception)
        {
            Log.e("Verify access token deserialized failed", "Cannot parse response body");
        }
    }

    private void handleRefreshAccessTokenFailed(VolleyError error)
    {
        clearAllTokens();
        accessTokenIsStillValid = false;
    }

    public void clearAllState()
    {
        userAvatarUrl = null;
        userFullName = null;
    }

    public void clearAllTokens()
    {
        accessToken = null;
        refreshToken = null;
        sharedPreferenceHelper.removePreference(ACCESS_TOKEN_PREFERENCE_KEY);
        sharedPreferenceHelper.removePreference(REFRESH_TOKEN_PREFERENCE_KEY);
        accessTokenIsStillValid = false;
    }

    public void logoutUser(ILogoutCallback logoutCallback)
    {
        Handler handler = new android.os.Handler(Looper.getMainLooper());
        Thread logoutThread = new Thread(() ->
        {
            handler.post(() ->
            {
                try
                {
                    authApiHandler.logout(
                        getAccessToken(),
                        getRefreshToken(),
                        (response) ->
                        {
                            handleLogoutSuccess(response);
                            logoutCallback.resolve();
                        },
                        (error) ->
                        {
                            handleLogoutFailed(error);
                            logoutCallback.resolve();
                        });
                }
                catch (Exception exception)
                {
                    Toast.makeText(context, "Something wrong when try to build logout request", Toast.LENGTH_SHORT).show();
                }
            });
        });

        logoutThread.start();
    }

    private void handleLogoutSuccess(JSONObject response)
    {
        Log.i("Logout success", "Message from logout success==================");
        clearAllState();
        clearAllTokens();
    }

    private void handleLogoutFailed(VolleyError error)
    {
        clearAllState();
        clearAllTokens();
    }
}
