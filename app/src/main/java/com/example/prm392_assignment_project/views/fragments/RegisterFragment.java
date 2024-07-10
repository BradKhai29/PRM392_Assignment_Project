package com.example.prm392_assignment_project.views.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.AuthApiHandler;
import com.example.prm392_assignment_project.helpers.input_validations.EmailValidationHelper;
import com.example.prm392_assignment_project.helpers.input_validations.InputValidationHelper;
import com.example.prm392_assignment_project.models.dtos.auths.RegisterDto;
import com.example.prm392_assignment_project.views.view_callbacks.IGoToLoginCallback;

import org.json.JSONObject;

public class RegisterFragment extends Fragment {
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private LinearLayout registerInputSection;
    private CardView registerSuccessPopup;

    // Callbacks.
    private final IGoToLoginCallback goToLoginCallback;

    public RegisterFragment(IGoToLoginCallback goToLoginCallback)
    {
        this.goToLoginCallback = goToLoginCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        registerInputSection = view.findViewById(R.id.register_input_section);
        registerSuccessPopup = view.findViewById(R.id.registerSuccessPopup);

        inputFirstName = view.findViewById(R.id.inputFirstName);
        inputLastName = view.findViewById(R.id.inputLastName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.inputPassword);
        Button btnRegister = view.findViewById(R.id.btnRegister);
        TextView btnGoToLogin = view.findViewById(R.id.tvGoToLogin);
        Button btnGoToLogin2 = view.findViewById(R.id.btnGoToLogin);

        // Set up on-click listener.
        btnRegister.setOnClickListener(this::register);
        btnGoToLogin.setOnClickListener(this::goToLogin);
        btnGoToLogin2.setOnClickListener(this::goToLogin);

        return view;
    }

    private void goToLogin(View view)
    {
        goToLoginCallback.resolve();
    }

    private void register(View view)
    {
        if (containsInvalidInputWhenRegister())
        {
            return;
        }

        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        RegisterDto registerDto = RegisterDto.getInstance(firstName, lastName, email, password);

        try
        {
            AuthApiHandler authApiHandler = new AuthApiHandler(getContext());
            authApiHandler.register(registerDto, this::handleRegisterSuccess, this::handleRegisterFailed);
        }
        catch (Exception exception)
        {
            popupToast(exception.getMessage());
        }
    }

    private boolean containsInvalidInputWhenRegister()
    {
        if (InputValidationHelper.isEmpty(inputLastName)) {
            popupToast("Vui lòng không để trống họ");
            return true;
        }

        if (!InputValidationHelper.containsLetterAndWhiteSpace(inputLastName)) {
            popupToast("Họ nhập không hợp lệ, chỉ nhập chữ cái");
            return true;
        }

        if (InputValidationHelper.isEmpty(inputFirstName)) {
            popupToast("Vui lòng không để trống tên");
            return true;
        }

        if (!InputValidationHelper.containsLetterAndWhiteSpace(inputFirstName)) {
            popupToast("Tên nhập không hợp lệ, chỉ nhập chữ cái");
            return true;
        }

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

    private void handleRegisterSuccess(JSONObject response)
    {
        registerInputSection.setVisibility(View.GONE);
        registerSuccessPopup.setVisibility(View.VISIBLE);
    }

    private void handleRegisterFailed(VolleyError error)
    {
        popupToast("Lỗi xảy ra khi đăng ký");
    }

    private void popupToast(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}