package com.example.prm392_assignment_project.views.screens.shopping_carts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private ImageButton btnBackHome;
    private Button btnBackHome2;
    private LinearLayout shoppingCartDetailSection;
    private LinearLayout emptyCartSection;

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

        // Bind components from layout.
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvSubTotal = findViewById(R.id.tvSubtotal);
        btnGoToCheckout = findViewById(R.id.btnCheckout);
        btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome2 = findViewById(R.id.btnBackHome2);
        emptyCartSection = findViewById(R.id.empty_cart_section);
        shoppingCartDetailSection = findViewById(R.id.shopping_cart_detail_section);

        // Set visibility for each section.
        emptyCartSection.setVisibility(View.GONE);
        shoppingCartDetailSection.setVisibility(View.VISIBLE);

        // Set on-click event listeners.
        btnBackHome.setOnClickListener(this::backHome);
        btnBackHome2.setOnClickListener(this::backHome);
        btnGoToCheckout.setOnClickListener(this::goToCheckoutActivity);

        setUpRecyclerView();
        loadShoppingCartInformation();
    }

    private void setUpRecyclerView()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        cartItemListRecyclerView = findViewById(R.id.cartItemListRecyclerView);
        cartItemListRecyclerView.setLayoutManager(linearLayoutManager);

        List<CartItemDto> cartItems = ShoppingCartStateManager.getShoppingCart().getCartItems();

        cartItemViewAdapter = new CartItemViewAdapter(this, cartItems, this::handleOnUpdateShoppingCart);
        cartItemListRecyclerView.setAdapter(cartItemViewAdapter);
    }

    private void loadShoppingCartInformation()
    {
        boolean isCartEmpty = ShoppingCartStateManager.getTotalItemsInCart() == 0;
        if (isCartEmpty)
        {
            emptyCartSection.setVisibility(View.VISIBLE);
            shoppingCartDetailSection.setVisibility(View.GONE);
        }
        else
        {
            emptyCartSection.setVisibility(View.GONE);
            shoppingCartDetailSection.setVisibility(View.VISIBLE);
        }

        String totalItemsText = String.valueOf(ShoppingCartStateManager.getTotalItemsInCart());
        tvTotalItems.setText(totalItemsText);

        String subTotalText = ShoppingCartStateManager.getTotalPrice() + " VND";
        tvSubTotal.setText(subTotalText);
    }

    private void handleOnUpdateShoppingCart(UpdateCartActionDetail updateCartActionDetail)
    {
        loadShoppingCartInformation();
    }

    private void backHome(View view)
    {
        finish();
    }

    private void goToCheckoutActivity(View view)
    {
        boolean isCartEmpty = ShoppingCartStateManager.getShoppingCart().getTotalItems() == 0;
        if (isCartEmpty)
        {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent goToCheckoutActivityIntent = new Intent(ShoppingCartDetailActivity.this, CheckoutActivity.class);
        startActivity(goToCheckoutActivityIntent);
    }
}