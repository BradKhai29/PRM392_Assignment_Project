package com.example.prm392_assignment_project.models.dtos.auths;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONObject;

public class LoginResponseDto
{
    public final String accessToken;
    public final String refreshToken;

    private LoginResponseDto(String accessToken, String refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static DeserializeResult<LoginResponseDto> DeserializeFromJson(JSONObject jsonData)
    {
        try
        {
            String accessToken = jsonData.getString("accessToken");
            String refreshToken = jsonData.getString("refreshToken");

            LoginResponseDto loginResponseDto = new LoginResponseDto(accessToken, refreshToken);
            return DeserializeResult.success(loginResponseDto);
        }
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
