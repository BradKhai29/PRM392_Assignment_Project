package com.example.prm392_assignment_project.models.dtos.base;

import org.json.JSONException;
import org.json.JSONObject;

public interface IApiInputDto {
    JSONObject toJson() throws JSONException;
}
