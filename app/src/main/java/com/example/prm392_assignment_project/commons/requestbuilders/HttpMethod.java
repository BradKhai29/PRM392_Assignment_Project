package com.example.prm392_assignment_project.commons.requestbuilders;

import com.android.volley.Request;

public enum HttpMethod {
    GET(Request.Method.GET),
    POST(Request.Method.POST),
    PUT(Request.Method.PUT),
    PATCH(Request.Method.PATCH),
    DELETE(Request.Method.DELETE);

    private final int methodCode;

    HttpMethod(int methodCode) {
        this.methodCode = methodCode;
    }

    public int getMethodCode() {
        return methodCode;
    }

    public static HttpMethod getMethodFromCode(int methodCode) {
        if (methodCode == GET.getMethodCode()) {
            return GET;
        }

        if (methodCode == POST.getMethodCode()) {
            return POST;
        }

        if (methodCode == PUT.getMethodCode()) {
            return PUT;
        }

        if (methodCode == PATCH.getMethodCode()) {
            return PATCH;
        }

        return GET;
    }
}
