package com.example.prm392_assignment_project.models.dtos.auths;

import com.example.prm392_assignment_project.models.dtos.base.IApiInputDto;

import org.json.JSONException;
import org.json.JSONObject;

public class RefreshAccessTokenDto implements IApiInputDto
{
    public final String accessToken;
    public final String refreshToken;

    private RefreshAccessTokenDto(String accessToken, String refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static RefreshAccessTokenDto getInstance(String accessToken, String refreshToken)
    {
        return new RefreshAccessTokenDto(accessToken, refreshToken);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);

        return jsonObject;
    }
}
