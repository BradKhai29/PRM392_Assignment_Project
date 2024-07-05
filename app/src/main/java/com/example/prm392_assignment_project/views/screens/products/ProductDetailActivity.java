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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ProductApiHandler;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.products.DetailProductInfoDto;
import com.example.prm392_assignment_project.models.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.models.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.fragments.ShoppingCartFragment;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    // Private fields.

    private String productId;
    private String imageUrl;
    private String productName;
    private int unitPrice;
    private int addToCartQuantity;

    // View callbacks.
    private final IOnCallApiSuccessCallback onGetProductDetailSuccessCallback;
    private final IOnCallApiFailedCallback onGetProductDetailFailureCallback;
    private final IOnCallApiSuccessCallback onAddToCartSuccessCallback;
    private final IOnCallApiFailedCallback onAddToCartFailureCallback;
    private ShoppingCartApiHandler shoppingCartApiHandler;

    // UI components.
    private ShoppingCartFragment shoppingCartFragment;

    // Constants that used in app.
    private final String SHOPPING_CART_FRAGMENT_TAG = "shopping_cart";

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

        productId = getIntent().getStringExtra("productId");

        if (String.valueOf(productId).isEmpty()) {
            finish();
        }

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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        shoppingCartFragment = new ShoppingCartFragment();
        shoppingCartFragment.setContext(this);

        fragmentTransaction.add(R.id.fragment_container_view, shoppingCartFragment, SHOPPING_CART_FRAGMENT_TAG);
        fragmentTransaction.commit();

        setUpOnClickListener();
        shoppingCartApiHandler = new ShoppingCartApiHandler(this);

        ProductApiHandler productApiHandler = new ProductApiHandler(this);
        productApiHandler.getProductDetailById(productId, onGetProductDetailSuccessCallback, onGetProductDetailFailureCallback);
    }

    private void setUpOnClickListener() {
        btnIncreaseQuantity.setOnClickListener(this::increaseAddToCartQuantity);
        btnDecreaseQuantity.setOnClickListener(this::decreaseAddToCartQuantity);
        btnAddToCart.setOnClickListener(this::handleAddToCartListener);
    }

    private void increaseAddToCartQuantity(View view) {
        addToCartQuantity++;
        tvAddToCartQuantity.setText(String.valueOf(addToCartQuantity));
    }

    private void decreaseAddToCartQuantity(View view) {
        final int MIN_QUANTITY = 1;

        if (addToCartQuantity == MIN_QUANTITY) {
            return;
        }

        addToCartQuantity--;
        tvAddToCartQuantity.setText(String.valueOf(addToCartQuantity));
    }

    private void handleAddToCartListener(View view)
    {
        CartItemDto cartItem = CartItemDto.getInstanceToCallApi(
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
            productName = productDetail.getName();
            imageUrl = productDetail.getImageUrls().get(0);
            unitPrice = productDetail.getUnitPrice();

            // Populate data into view component.
            Picasso.get().load(imageUrl).into(productImage);
            tvProductName.setText(productName);
            tvProductPrice.setText(String.valueOf(unitPrice));
            tvProductDescription.setText(productDetail.getDescription());
        }
        catch (Exception exception) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
        }
    }

    private void handleGetProductDetailFailure(VolleyError error) {
        Toast.makeText(this, "Lấy sản phẩm thất bại", Toast.LENGTH_LONG).show();
    }

    private void handleAddToCartSuccess(JSONObject response)
    {
        ShoppingCartDto shoppingCart = ShoppingCartStateManager.getShoppingCart();

        String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();
        CartItemDto cartItem = CartItemDto.getInstanceToAdd(
            cartId,
            productId,
            productName,
            unitPrice,
            imageUrl,
            addToCartQuantity);

        shoppingCart.addCartItem(cartItem);
        ShoppingCartStateManager.addChanges();

        shoppingCartFragment.reloadShoppingCart();
        Toast.makeText(this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_LONG).show();
    }

    private void handleAddToCartFailure(VolleyError error) {
        Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_LONG).show();
    }
    ///endregion
}