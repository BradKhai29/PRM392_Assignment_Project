package com.example.prm392_assignment_project.commons.requestbuilders;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

public class HttpRequestHeader {
    public final String headerName;
    public final String headerValue;

    private HttpRequestHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public static HttpRequestHeader getInstance(String headerName, String headerValue) {
        return new HttpRequestHeader(headerName, headerValue);
    }

    public static HttpRequestHeader ContentTypeJson() {
        return new HttpRequestHeader("Content-Type", "application/json; charset=utf-8");
    }
}
