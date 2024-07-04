package com.example.prm392_assignment_project.models.commons;

import android.os.DeadObjectException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiResponse {
    private boolean isSuccess;
    private String body;

    public ApiResponse(boolean isSuccess, String body) {
        this.isSuccess = isSuccess;
        this.body = body;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public JSONObject getBodyAsJsonObject() throws JSONException {
        return new JSONObject(body);
    }

    public JSONArray getBodyAsJsonArray() throws JSONException {
        return new JSONArray(body);
    }

    public static DeserializeResult<ApiResponse> DeserializeFromJson(JSONObject jsonObject)
    {
        try {
            boolean isSuccess = jsonObject.getBoolean("isSuccess");
            String body = jsonObject.getString("body");

            ApiResponse response = new ApiResponse(isSuccess, body);

            return DeserializeResult.success(response);
        }
        catch (Exception exception) {
            return DeserializeResult.failed();
        }
    }
}
