package com.example.prm392_assignment_project.commons.requestbuilders;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilder
{
    protected String apiUrl;
    protected JSONObject requestBody;
    protected IOnCallApiSuccessCallback successCallback;
    protected IOnCallApiFailedCallback failureCallback;
    protected HttpMethod httpMethod;
    private final Map<String, String> requestHttpHeaders;

    private static final JSONObject EMPTY_JSON_OBJECT;

    static
    {
        try {
            EMPTY_JSON_OBJECT = new JSONObject("{}");
        } catch (JSONException e) {
            throw new RuntimeException("FAIL TO INIT EMPTY JSON OBJECT");
        }
    }

    public RequestBuilder(String apiUrl) {
        this.apiUrl = apiUrl;
        requestHttpHeaders = new HashMap<>();
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void withMethod(HttpMethod method) {
        httpMethod = method;
    }

    public RequestBuilder addJsonBody(JSONObject requestBody) {
        this.requestBody = requestBody;
        HttpRequestHeader contentTypeJson = HttpRequestHeader.getInstance("Content-Type", "application/json; charset=utf-8");
        addRequestHeader(contentTypeJson);

        return this;
    }

    public RequestBuilder addRequestHeader(HttpRequestHeader requestHeader) {
        if (requestHttpHeaders.containsKey(requestHeader.headerName)) {
            return this;
        }

        requestHttpHeaders.put(requestHeader.headerName, requestHeader.headerValue);

        return this;
    }

    public RequestBuilder addJwtBearerToken(String jwtToken) {
        final String authorizationHeader = "Authorization";

        if (requestHttpHeaders.containsKey(authorizationHeader)) {
            return this;
        }

        final String bearerToken = "Bearer " + jwtToken;
        requestHttpHeaders.put(authorizationHeader, bearerToken);

        return this;
    }

    public RequestBuilder addOnSuccessCallback(IOnCallApiSuccessCallback callback) {
        successCallback = callback;

        return this;
    }

    public RequestBuilder addOnFailureCallback(IOnCallApiFailedCallback callback) {
        failureCallback = callback;

        return this;
    }

    /**
     * Build a new json object request instance
     * based on the provided implementation.
     * @return A new json object request to send to api.
     */
    public JsonObjectRequest buildJsonRequest() {
        if (apiUrl == null) {
            throw new IllegalArgumentException("Api Url cannot be null");
        }

        if (httpMethod == null) {
            throw new IllegalArgumentException("Http Method is not set");
        }

        if (requestBody == null) {
            requestBody = EMPTY_JSON_OBJECT;
        }

        if (successCallback == null) {
            throw new IllegalArgumentException("Success callback is not set");
        }

        if (failureCallback == null) {
            throw new IllegalArgumentException("Failure callback is not set");
        }

        JsonObjectRequest request = new JsonObjectRequest(
            httpMethod.getMethodCode(),
            apiUrl,
            requestBody,
            response -> successCallback.resolve(response),
            error -> failureCallback.resolve(error))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestHttpHeaders;
            }
        };

        return request;
    }

    public static RequestBuilder getInstance(String apiUrl) {
        return new RequestBuilder(apiUrl);
    }
}
