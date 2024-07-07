package com.example.prm392_assignment_project.api_handlers.base;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public abstract class ApiHandler {
    public static final String BASE_URL = "https://ecom.odour.site/api";

    // Dependencies.
    protected final RequestQueue requestQueue;

    public ApiHandler(Context context)
    {
        this.requestQueue = Volley.newRequestQueue(context);
    }
}
