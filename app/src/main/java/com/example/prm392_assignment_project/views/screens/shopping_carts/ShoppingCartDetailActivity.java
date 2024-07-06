package com.example.prm392_assignment_project.views.screens.shopping_carts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.views.recyclerviews.shopping_carts.CartItemViewAdapter;
import com.example.prm392_assignment_project.views.screens.checkouts.CheckoutActivity;
import com.example.prm392_assignment_project.views.view_callbacks.support_models.UpdateCartActionDetail;

import java.util.List;

public class ShoppingCartDetailActivity extends AppCompatActivity {
    private TextView tvSubTotal;
    private TextView tvTotalItems;
    private Button btnGoToCheckout;
    private RecyclerView cartItemListRecyclerView;
    private CartItemViewAdapter cartItemViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvSubTotal = findViewById(R.id.tvSubtotal);
        btnGoToCheckout = findViewById(R.id.btnCheckout);
        btnGoToCheckout.setOnClickListener(this::goToCheckoutActivity);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        cartItemListRecyclerView = findViewById(R.id.cartItemListRecyclerView);
        cartItemListRecyclerView.setLayoutManager(linearLayoutManager);

        List<CartItemDto> cartItems = ShoppingCartStateManager.getShoppingCart().getCartItems();

        cartItemViewAdapter = new CartItemViewAdapter(this, cartItems, this::handleOnUpdateShoppingCart);

        cartItemListRecyclerView.setAdapter(cartItemViewAdapter);

        loadShoppingCartInformation();
    }



    private void loadShoppingCartInformation()
    {
        String totalItemsText = "Giỏ hàng (" + ShoppingCartStateManager.getTotalItemsInCart() + ")";
        tvTotalItems.setText(totalItemsText);

        String subTotalText = ShoppingCartStateManager.getTotalPrice() + " VND";
        tvSubTotal.setText(subTotalText);
    }

    private void handleOnUpdateShoppingCart(UpdateCartActionDetail updateCartActionDetail)
    {
        loadShoppingCartInformation();
    }

    private void goToCheckoutActivity(View view)
    {
        Intent goToCheckoutActivityIntent = new Intent(ShoppingCartDetailActivity.this, CheckoutActivity.class);

        startActivity(goToCheckoutActivityIntent);
    }
}