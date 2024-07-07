package com.example.prm392_assignment_project.models.dtos.auths;

import com.example.prm392_assignment_project.models.dtos.base.IApiInputDto;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRequestDto implements IApiInputDto
{
    private final String email;
    private final String password;

    private LoginRequestDto(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public static LoginRequestDto getInstance(String email, String password)
    {
        return new LoginRequestDto(email, password);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        JSONObject loginDataInJson = new JSONObject();

        loginDataInJson.put("email", email);
        loginDataInJson.put("password", password);

        return loginDataInJson;
    }
}
