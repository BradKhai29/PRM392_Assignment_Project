package com.example.prm392_assignment_project.views.view_callbacks;

import org.json.JSONObject;

/**
 * This interface provide mechanism to handle callback for UI update.
 */
public interface IOnSuccessCallback {
    void resolve(JSONObject response);
}
