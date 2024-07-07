package com.example.prm392_assignment_project.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.api_handlers.commons.HttpStatusCodes;
import com.example.prm392_assignment_project.api_handlers.implementation.AuthApiHandler;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.auths.RefreshAccessTokenDto;

import org.json.JSONObject;

/**
 * Manage related data about user authentication
 * such as access token and refresh token.
 * @implNote This class implements singleton pattern.
 */
public class UserAuthStateManager
{
    // Private fields.
    private final SharedPreferenceHelper sharedPreferenceHelper;
    private final Context context;
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
            throw new IllegalArgumentException("The input context is null when init user auth state manager");
        }

        this.context = context;
        authApiHandler = new AuthApiHandler(context);
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    public static UserAuthStateManager getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new UserAuthStateManager(context);
        }

        return instance;
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

    public void verifyCurrentAccessToken()
    {
        authApiHandler.verifyAccessToken(
            getAccessToken(),
            this::handleOnVerifyAccessTokenSuccess,
            this::handleOnVerifyAccessTokenFailed);
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
            accessTokenIsStillValid = true;

            Toast.makeText(context, "avatarUrl : " + userAvatarUrl, Toast.LENGTH_SHORT).show();
        }
        catch (Exception exception)
        {
            Log.e("Verify access token deserialized failed", "Cannot parse response body");
        }
    }

    private void handleOnVerifyAccessTokenFailed(VolleyError error)
    {
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

            return;
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

    public void clearAllTokens()
    {
        accessToken = null;
        refreshToken = null;
        sharedPreferenceHelper.removePreference(ACCESS_TOKEN_PREFERENCE_KEY);
        sharedPreferenceHelper.removePreference(REFRESH_TOKEN_PREFERENCE_KEY);
    }
}
