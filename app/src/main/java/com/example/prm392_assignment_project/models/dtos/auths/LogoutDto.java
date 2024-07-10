package com.example.prm392_assignment_project.models.dtos.auths;

import com.example.prm392_assignment_project.models.dtos.base.IApiInputDto;

import org.json.JSONException;
import org.json.JSONObject;

public class LogoutDto implements IApiInputDto
{
    public final String refreshToken;

    private LogoutDto(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public static LogoutDto getInstance(String refreshToken)
    {
        return new LogoutDto(refreshToken);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        JSONObject requestBody = new JSONObject();

        requestBody.put("refreshToken", refreshToken);

        return requestBody;
    }
}
