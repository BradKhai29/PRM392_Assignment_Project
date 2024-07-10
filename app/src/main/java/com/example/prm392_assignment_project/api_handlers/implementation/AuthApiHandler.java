package com.example.prm392_assignment_project.api_handlers.implementation;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.prm392_assignment_project.api_handlers.base.ApiHandler;
import com.example.prm392_assignment_project.commons.requestbuilders.HttpMethod;
import com.example.prm392_assignment_project.commons.requestbuilders.RequestBuilder;
import com.example.prm392_assignment_project.models.dtos.auths.LoginRequestDto;
import com.example.prm392_assignment_project.models.dtos.auths.LogoutDto;
import com.example.prm392_assignment_project.models.dtos.auths.RefreshAccessTokenDto;
import com.example.prm392_assignment_project.models.dtos.auths.RegisterDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;

import org.json.JSONException;

public class AuthApiHandler extends ApiHandler
{
    public static final String API_URL = BASE_URL + "/auth/user";
    public static final String LOGIN_ENDPOINT = API_URL + "/login";
    public static final String REGISTER_ENDPOINT = API_URL + "/register";
    public static final String LOGOUT_ENDPOINT = API_URL + "/logout";
    public static final String VERIFY_ACCESS_TOKEN_ENDPOINT = API_URL + "/verify";
    public static final String REFRESH_ACCESS_TOKEN_ENDPOINT = API_URL + "/refresh";

    public AuthApiHandler(Context context)
    {
        super(context);
    }

    public void login(
        LoginRequestDto loginRequestDto,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback) throws JSONException
    {
        RequestBuilder requestBuilder = new RequestBuilder(LOGIN_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addJsonBody(loginRequestDto.toJson());
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void register(
        RegisterDto registerDto,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback) throws JSONException
    {
        RequestBuilder requestBuilder = new RequestBuilder(REGISTER_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addJsonBody(registerDto.toJson());
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void verifyAccessToken(
        String accessToken,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback)
    {
        RequestBuilder requestBuilder = new RequestBuilder(VERIFY_ACCESS_TOKEN_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addJwtBearerToken(accessToken);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void refreshAgainAccessAndRefreshToken(
        RefreshAccessTokenDto refreshAccessTokenDto,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback) throws JSONException
    {
        RequestBuilder requestBuilder = new RequestBuilder(REFRESH_ACCESS_TOKEN_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addJsonBody(refreshAccessTokenDto.toJson());
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void logout(
        String accessToken,
        String refreshToken,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback) throws JSONException
    {
        RequestBuilder requestBuilder = new RequestBuilder(LOGOUT_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);

        requestBuilder.addJwtBearerToken(accessToken);
        requestBuilder.addJsonBody(LogoutDto.getInstance(refreshToken).toJson());

        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }
}
