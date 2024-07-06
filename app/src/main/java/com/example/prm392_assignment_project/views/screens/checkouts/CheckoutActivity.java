package com.example.prm392_assignment_project.views.screens.checkouts;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.OrderApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.helpers.input_validations.EmailValidationHelper;
import com.example.prm392_assignment_project.helpers.input_validations.InputValidationHelper;
import com.example.prm392_assignment_project.helpers.input_validations.PhoneNumberValidationHelper;
import com.example.prm392_assignment_project.models.dtos.checkouts.CheckoutDetailDto;

import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity {
    private TextView tvCheckoutTotalPrice;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputPhone;
    private EditText inputEmail;
    private EditText inputDeliveryAddress;
    private EditText inputNote;
    private Button btnConfirmCheckout;

    private OrderApiHandler orderApiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind ui components.
        tvCheckoutTotalPrice = findViewById(R.id.tvCheckoutTotalPrice);
        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputPhone = findViewById(R.id.inputPhonoe);
        inputEmail = findViewById(R.id.inputEmail);
        inputDeliveryAddress = findViewById(R.id.inputDeliveryAddress);
        inputNote = findViewById(R.id.inputNote);
        btnConfirmCheckout = findViewById(R.id.btnConfirmCheckout);

        orderApiHandler = new OrderApiHandler(this);

        btnConfirmCheckout.setOnClickListener(this::confirmCheckout);
        tvCheckoutTotalPrice.setText(ShoppingCartStateManager.getTotalPrice() + " VND");
    }

    private void confirmCheckout(View view) {
        if (containsInvalidInputs()) return;

        CheckoutDetailDto checkoutDetail = new CheckoutDetailDto();
        checkoutDetail.cartId = ShoppingCartStateManager.getCurrentShoppingCartId();
        checkoutDetail.firstName = inputFirstName.getText().toString();
        checkoutDetail.lastName = inputLastName.getText().toString();
        checkoutDetail.phoneNumber = inputPhone.getText().toString();
        checkoutDetail.email = inputEmail.getText().toString();
        checkoutDetail.deliveryAddress = inputDeliveryAddress.getText().toString();
        checkoutDetail.orderNote = inputNote.getText().toString();

        try {
            orderApiHandler.checkout(
                checkoutDetail,
                this::handleCheckoutSuccess,
                this::handleCheckoutFailed);
        }
        catch (Exception exception) {
            popupToast(exception.getMessage());
        }
    }

    private boolean containsInvalidInputs() {
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

        if (InputValidationHelper.isEmpty(inputPhone)) {
            popupToast("Vui lòng không để trống số điện thoại");
            return true;
        }

        if (!PhoneNumberValidationHelper.isValid(inputPhone)) {
            popupToast("Số điện thoại không hợp lệ");
            return true;
        }

        if (InputValidationHelper.isEmpty(inputEmail)) {
            popupToast("Vui lòng không để trống email");
            return true;
        }

        if (!EmailValidationHelper.isValid(inputEmail)) {
            popupToast("Email không hợp lệ");
            return true;
        }

        if (InputValidationHelper.isEmpty(inputDeliveryAddress)) {
            popupToast("Vui lòng không để trống địa chỉ giao hàng");
            return true;
        }

        return false;
    }

    private void handleCheckoutSuccess(JSONObject response)
    {
        popupToast("Checkout success");

        ShoppingCartStateManager.clearShoppingCart();
        ShoppingCartStateManager.addChanges();

        finish();
    }

    private void handleCheckoutFailed(VolleyError error)
    {
        popupToast("Something wrong when checkout");
    }

    private void popupToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}