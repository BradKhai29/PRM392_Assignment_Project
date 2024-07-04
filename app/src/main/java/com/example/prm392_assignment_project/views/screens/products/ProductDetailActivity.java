package com.example.prm392_assignment_project.views.screens.products;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ProductApiHandler;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.products.DetailProductInfoDto;
import com.example.prm392_assignment_project.models.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnFailureCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnSuccessCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    private String productId;
    private int addToCartQuantity;
    // View callbacks.
    private final IOnSuccessCallback onGetProductDetailSuccessCallback;
    private final IOnFailureCallback onGetProductDetailFailureCallback;
    private final IOnSuccessCallback onAddToCartSuccessCallback;
    private final IOnFailureCallback onAddToCartFailureCallback;
    private ShoppingCartApiHandler shoppingCartApiHandler;

    public ProductDetailActivity() {
        addToCartQuantity = 1;
        onGetProductDetailSuccessCallback = this::handleGetProductDetailResponse;
        onGetProductDetailFailureCallback = this::handleGetProductDetailFailure;
        onAddToCartSuccessCallback = this::handleAddToCartSuccess;
        onAddToCartFailureCallback = this::handleAddToCartFailure;
    }

    // UI Components
    private ImageView productImage;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDescription;
    private TextView tvAddToCartQuantity;
    private Button btnIncreaseQuantity;
    private Button btnDecreaseQuantity;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind the components from layout.
        productImage = findViewById(R.id.product_image);
        tvProductName = findViewById(R.id.product_name);
        tvProductPrice = findViewById(R.id.product_price);
        tvProductDescription = findViewById(R.id.product_description);

        // Add to cart components section.
        tvAddToCartQuantity = findViewById(R.id.addToCartQuantity);
        btnDecreaseQuantity = findViewById(R.id.btnMinus);
        btnIncreaseQuantity = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        productId = getIntent().getStringExtra("productId");

        if (String.valueOf(productId).isEmpty()) {
            finish();
            return;
        }

        shoppingCartApiHandler = new ShoppingCartApiHandler(this);
        ProductApiHandler productApiHandler = new ProductApiHandler(this);
        productApiHandler.getProductDetailById(productId, onGetProductDetailSuccessCallback, onGetProductDetailFailureCallback);
    }

    private void setUpOnClickListener() {
        btnIncreaseQuantity.setOnClickListener(this::increaseAddToCartQuantity);
        btnDecreaseQuantity.setOnClickListener(this::decreaseAddToCartQuantity);
        btnAddToCart.setOnClickListener(this::addToCart);
    }

    private void increaseAddToCartQuantity(View view) {
        addToCartQuantity++;
        tvAddToCartQuantity.setText(String.valueOf(addToCartQuantity));
    }

    private void decreaseAddToCartQuantity(View view) {
        if (addToCartQuantity == 1) {
            return;
        }

        addToCartQuantity--;
        tvAddToCartQuantity.setText(String.valueOf(addToCartQuantity));
    }

    private void addToCart(View view) {
        CartItemDto cartItem = CartItemDto.getInstance(
            ShoppingCartStateManager.getCurrentShoppingCartId(),
            productId,
            addToCartQuantity);

        try {
            shoppingCartApiHandler.addToCart(cartItem, onAddToCartSuccessCallback, onAddToCartFailureCallback);
        }
        catch (JSONException jsonException) {
            Toast.makeText(this, "Have error with JSON body when add to cart", Toast.LENGTH_LONG).show();
        }
    }

    ///region Callback handlers
    private void handleGetProductDetailResponse(JSONObject response) {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            return;
        }

        ApiResponse apiResponse = result.value;

        try {
            JSONObject productDetailInJson = apiResponse.getBodyAsJsonObject();
            DeserializeResult<DetailProductInfoDto> deserializeResult = DetailProductInfoDto.DeserializeFromJson(productDetailInJson);

            if (!deserializeResult.isSuccess) {
                Toast.makeText(this, "Có lỗi hiển thị sản phẩm", Toast.LENGTH_LONG).show();
            }

            DetailProductInfoDto productDetail = deserializeResult.value;

            // Populate data into view component.
            Picasso.get().load(productDetail.getImageUrls().get(0)).into(productImage);
            tvProductName.setText(productDetail.getName());
            tvProductPrice.setText(String.valueOf(productDetail.getUnitPrice()));
            tvProductDescription.setText(productDetail.getDescription());
        }
        catch (Exception exception) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
        }
    }

    private void handleGetProductDetailFailure(VolleyError error) {

    }

    private void handleAddToCartSuccess(JSONObject response) {

    }

    private void handleAddToCartFailure(VolleyError error) {

    }
    ///endregion
}