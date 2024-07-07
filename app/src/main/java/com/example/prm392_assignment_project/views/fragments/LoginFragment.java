package com.example.prm392_assignment_project.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.AuthApiHandler;
import com.example.prm392_assignment_project.helpers.UserAuthStateManager;
import com.example.prm392_assignment_project.helpers.input_validations.EmailValidationHelper;
import com.example.prm392_assignment_project.helpers.input_validations.InputValidationHelper;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.auths.LoginRequestDto;
import com.example.prm392_assignment_project.models.dtos.auths.LoginResponseDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnLoginSuccessCallback;

import org.json.JSONObject;

public class LoginFragment extends Fragment {
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLogin;
    private UserAuthStateManager userAuthStateManager;
    private final IOnLoginSuccessCallback loginSuccessCallback;

    public LoginFragment(IOnLoginSuccessCallback loginSuccessCallback)
    {
        this.loginSuccessCallback = loginSuccessCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userAuthStateManager = UserAuthStateManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Bind the components from view.
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.inputPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        // Set up on-click listeners.
        btnLogin.setOnClickListener(this::login);

        return view;
    }

    private void login(View view)
    {
        if (containsInvalidInputWhenLogin())
        {
            return;
        }

        // Create login request dto to call api.
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        LoginRequestDto loginDto = LoginRequestDto.getInstance(email, password);

        try
        {
            AuthApiHandler authApiHandler = new AuthApiHandler(getContext());
            authApiHandler.login(loginDto, this::handleLoginSuccess, this::handleLoginFailed);
        }
        catch (Exception exception)
        {
            popupToast("Something wrong when serialize the login request dto");
        }
    }

    private boolean containsInvalidInputWhenLogin()
    {
        if (InputValidationHelper.isEmpty(inputEmail))
        {
            popupToast("Vui lòng không để trống email");
            return true;
        }

        if (!EmailValidationHelper.isValid(inputEmail))
        {
            popupToast("Email không hợp lệ");
            return true;
        }

        if (InputValidationHelper.isEmpty(inputPassword))
        {
            popupToast("Vui lòng không để trống mật khẩu");
            return true;
        }

        return false;
    }

    private void handleLoginSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess)
        {
            popupToast("Something wrong when deserialize the login response");
            return;
        }

        ApiResponse apiResponse = result.value;

        try
        {
            JSONObject loginResponseInJson = apiResponse.getBodyAsJsonObject();

            DeserializeResult<LoginResponseDto> deserializeResult = LoginResponseDto.DeserializeFromJson(loginResponseInJson);
            if (!deserializeResult.isSuccess)
            {
                popupToast("Something wrong when deserialized login response dto");
            }

            LoginResponseDto loginResponseDto = deserializeResult.value;
            userAuthStateManager.setAccessToken(loginResponseDto.accessToken);
            userAuthStateManager.setRefreshToken(loginResponseDto.refreshToken);

            loginSuccessCallback.resolve();
        }
        catch (Exception exception)
        {
            popupToast("Something wrong when deserialized login response dto");
        }
    }

    private void handleLoginFailed(VolleyError error)
    {
        popupToast("Login failed due to invalid credentials");
    }

    private void popupToast(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}