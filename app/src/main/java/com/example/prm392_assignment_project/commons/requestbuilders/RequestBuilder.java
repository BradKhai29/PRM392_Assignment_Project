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
    // Private static constants.
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final JSONObject EMPTY_JSON_OBJECT;

    // Private fields section.
    private String _apiUrl;
    private JSONObject requestBody;
    private IOnCallApiSuccessCallback successCallback;
    private IOnCallApiFailedCallback failureCallback;
    private HttpMethod httpMethod;
    private final Map<String, String> httpRequestHeaders;

    static
    {
        try
        {
            EMPTY_JSON_OBJECT = new JSONObject("{}");
        }
        catch (JSONException e)
        {
            throw new RuntimeException("FAIL TO INIT EMPTY JSON OBJECT");
        }
    }

    public RequestBuilder(String apiUrl)
    {
        _apiUrl = apiUrl;
        httpRequestHeaders = new HashMap<>();
    }

    public void setApiUrl(String apiUrl)
    {
        _apiUrl = apiUrl;
    }

    public void withMethod(HttpMethod method) {
        httpMethod = method;
    }

    public RequestBuilder addJsonBody(JSONObject requestBody)
    {
        this.requestBody = requestBody;
        addRequestHeader(HttpRequestHeader.ContentTypeJson());

        return this;
    }

    public RequestBuilder addRequestHeader(HttpRequestHeader requestHeader)
    {
        if (httpRequestHeaders.containsKey(requestHeader.headerName))
        {
            return this;
        }

        // Add request header.
        httpRequestHeaders.put(requestHeader.headerName, requestHeader.headerValue);

        return this;
    }

    public RequestBuilder addJwtBearerToken(String jwtToken)
    {
        if (httpRequestHeaders.containsKey(AUTHORIZATION_HEADER))
        {
            return this;
        }

        final String bearerToken = "Bearer " + jwtToken;
        httpRequestHeaders.put(AUTHORIZATION_HEADER, bearerToken);

        return this;
    }

    /**
     * Add the callback that will be invoked when the api response has success status code (2xx).
     * @param callback The callback that will be invoked when response success.
     * @return Current request builder instance.
     */
    public RequestBuilder addOnSuccessCallback(IOnCallApiSuccessCallback callback)
    {
        successCallback = callback;

        return this;
    }

    /**
     * Add the callback that will be invoked when the api response has failed status code (4xx, 5xx).
     * @param callback The callback that will be invoked when response failed.
     * @return Current request builder instance.
     */
    public RequestBuilder addOnFailureCallback(IOnCallApiFailedCallback callback)
    {
        failureCallback = callback;

        return this;
    }

    /**
     * Build a new json object request instance
     * based on the provided implementation.
     * @return A new json object request to send to api.
     */
    public JsonObjectRequest buildJsonRequest()
    {
        if (_apiUrl == null)
        {
            throw new IllegalArgumentException("Api Url cannot be null");
        }

        if (httpMethod == null)
        {
            throw new IllegalArgumentException("Http Method is not set");
        }

        if (requestBody == null)
        {
            requestBody = EMPTY_JSON_OBJECT;
        }

        if (successCallback == null)
        {
            throw new IllegalArgumentException("Success callback is not set");
        }

        if (failureCallback == null)
        {
            throw new IllegalArgumentException("Failure callback is not set");
        }

        JsonObjectRequest request = new JsonObjectRequest(
            httpMethod.getMethodCode(),
            _apiUrl,
            requestBody,
            response -> successCallback.resolve(response),
            error -> failureCallback.resolve(error))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                return httpRequestHeaders;
            }
        };

        return request;
    }

    public static RequestBuilder getInstance(String apiUrl) {
        return new RequestBuilder(apiUrl);
    }
}
