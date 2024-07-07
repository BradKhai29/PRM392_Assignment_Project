package com.example.prm392_assignment_project.views.screens.auths;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.helpers.UserAuthStateManager;
import com.example.prm392_assignment_project.views.fragments.LoginFragment;
import com.example.prm392_assignment_project.views.fragments.RegisterFragment;

public class AuthActivity extends AppCompatActivity {
    private final String LOGIN_FRAGMENT_TAG = "login_fragment";
    private final String REGISTER_FRAGMENT_TAG = "register_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        LoginFragment loginFragment = new LoginFragment(this::handleLoginSuccess);
        RegisterFragment registerFragment = new RegisterFragment();

//        fragmentTransaction.add(R.id.fragment_container_view, loginFragment, LOGIN_FRAGMENT_TAG);
        fragmentTransaction.add(R.id.fragment_container_view, registerFragment, REGISTER_FRAGMENT_TAG);

        fragmentTransaction.commit();
    }

    private void handleLoginSuccess()
    {
        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}