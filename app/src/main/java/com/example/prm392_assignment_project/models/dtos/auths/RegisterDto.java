package com.example.prm392_assignment_project.models.dtos.auths;

import com.example.prm392_assignment_project.models.dtos.base.IApiInputDto;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDto implements IApiInputDto
{
    public final String firstName;
    public final String lastName;
    public final String email;
    public final String password;

    private RegisterDto(String firstName, String lastName, String email, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public static RegisterDto getInstance(String firstName, String lastName, String email, String password)
    {
        return new RegisterDto(firstName, lastName, email, password);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("firstName", firstName);
        jsonBody.put("lastName", lastName);
        jsonBody.put("email", email);
        jsonBody.put("password", password);

        return jsonBody;
    }
}
