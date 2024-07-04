package com.example.prm392_assignment_project.commons.requestbuilders;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.prm392_assignment_project.views.view_callbacks.IOnFailureCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnSuccessCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestBuilder {
    protected String apiUrl;
    protected JSONObject requestBody;
    protected IOnSuccessCallback successCallback;
    protected IOnFailureCallback failureCallback;
    protected HttpMethod httpMethod;

    private static final JSONObject EMPTY_JSON_OBJECT;

    static {
        try {
            EMPTY_JSON_OBJECT = new JSONObject("{}");
        } catch (JSONException e) {
            throw new RuntimeException("FAIL TO INIT EMPTY JSON OBJECT");
        }
    }

    public RequestBuilder(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void withMethod(HttpMethod method) {
        httpMethod = method;
    }

    public RequestBuilder addBody(JSONObject requestBody) {
        this.requestBody = requestBody;

        return this;
    }

    public RequestBuilder addOnSuccessCallback(IOnSuccessCallback callback) {
        successCallback = callback;

        return this;
    }

    public RequestBuilder addOnFailureCallback(IOnFailureCallback callback) {
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

        return new JsonObjectRequest(
            httpMethod.getMethodCode(),
            apiUrl,
            requestBody,
            response -> successCallback.resolve(response),
            error -> failureCallback.resolve(error));
    }

    public StringRequest build() {
        if (apiUrl == null) {
            throw new IllegalArgumentException("Api Url cannot be null");
        }

        if (httpMethod == null) {
            throw new IllegalArgumentException("Http Method is not set");
        }

        if (successCallback == null) {
            throw new IllegalArgumentException("Success callback is not set");
        }

        if (failureCallback == null) {
            throw new IllegalArgumentException("Failure callback is not set");
        }

        return new StringRequest(
            httpMethod.getMethodCode(),
            apiUrl,
            response -> successCallback.resolve(null),
            error -> failureCallback.resolve(null));
    }

    public static RequestBuilder getInstance(String apiUrl) {
        return new RequestBuilder(apiUrl);
    }
}
