package com.example.prm392_assignment_project.views.view_callbacks;

import com.android.volley.VolleyError;

public interface IOnCallApiFailedCallback {
    void resolve(VolleyError error);
}
