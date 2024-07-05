package com.example.prm392_assignment_project.views.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ProductApiHandler;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.products.GeneralProductInfoDto;
import com.example.prm392_assignment_project.models.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.fragments.ShoppingCartFragment;
import com.example.prm392_assignment_project.views.recyclerviews.products.ProductItemViewAdapter;
import com.example.prm392_assignment_project.views.view_callbacks.IOnAddToCartCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    // Load product callbacks.
    private final IOnCallApiFailedCallback onLoadProductFailedCallback;
    private final IOnCallApiSuccessCallback onLoadProductSuccessCallback;

    // Init shopping cart callbacks.
    private final IOnCallApiSuccessCallback onInitShoppingCartSuccessCallback;
    private final IOnCallApiFailedCallback onInitShoppingCartFailedCallback;

    // Load shopping cart callbacks.
    private final IOnCallApiSuccessCallback onLoadShoppingCartSuccessCallback;
    private final IOnCallApiFailedCallback onLoadShoppingCartFailedCallback;

    // Add to cart callback.
    private final IOnAddToCartCallback onAddToCartCallback;

    // Private fields and components section.
    private RecyclerView productItemRecyclerView;
    private ShoppingCartFragment shoppingCartFragment;
    private final List<GeneralProductInfoDto> products;

    // Constants that used in app.
    private final String SHOPPING_CART_FRAGMENT_TAG = "shopping_cart";

    public HomeActivity() {
        // Load products.
        onLoadProductSuccessCallback = this::handleLoadAllProductsResponse;
        onLoadProductFailedCallback = this::handleLoadAllProductsFailure;

        // Init shopping cart.
        onInitShoppingCartSuccessCallback = this::handleInitShoppingCartResponse;
        onInitShoppingCartFailedCallback = this::handleInitShoppingCartFailure;

        // Load shopping cart.
        onLoadShoppingCartSuccessCallback = this::handleLoadShoppingCartResponse;
        onLoadShoppingCartFailedCallback = this::handleLoadShoppingCartFailure;

        // Add to cart.
        onAddToCartCallback = this::handleOnAddToCart;

        products = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpShoppingCartManager();
        setUpRecyclerView();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        shoppingCartFragment = new ShoppingCartFragment();
        shoppingCartFragment.setContext(this);

        fragmentTransaction.add(R.id.fragment_container_view, shoppingCartFragment, SHOPPING_CART_FRAGMENT_TAG);
        fragmentTransaction.commit();

        loadAllProductsFromApi();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ShoppingCartStateManager.hasChangesInState()) {
            shoppingCartFragment.reloadShoppingCart();
        }
    }

    ///region View components and dependency setup section.
    private void setUpShoppingCartManager() {
        // Set up the shopping cart helper for later operation.
        ShoppingCartStateManager.setupSharedPreferenceHelper(HomeActivity.this);
    }

    private void setUpRecyclerView() {
        productItemRecyclerView = findViewById(R.id.productListRecyclerView);

        // Configure the layout manager for recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productItemRecyclerView.setLayoutManager(linearLayoutManager);
    }
    ///endregion

    ///region Private methods.
    private void loadAllProductsFromApi() {
        // Calling api using product controller.
        ProductApiHandler productApiHandler = new ProductApiHandler(this);
        productApiHandler.getAllProducts(4, onLoadProductSuccessCallback, onLoadProductFailedCallback);
    }

    private void loadShoppingCartFromApi() {
        // Process to init or load the shopping cart from the webapi.
        if (ShoppingCartStateManager.isShoppingCartPreferenceExisted()) {
            ShoppingCartStateManager.loadShoppingCartIdFromPreference();

            String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();

            ShoppingCartApiHandler shoppingCartApiHandler = new ShoppingCartApiHandler(this);
            shoppingCartApiHandler.loadShoppingCartById(cartId, onLoadShoppingCartSuccessCallback, onLoadShoppingCartFailedCallback);
        }
        else {
            ShoppingCartApiHandler shoppingCartApiHandler = new ShoppingCartApiHandler(this);
            shoppingCartApiHandler.initShoppingCart(onInitShoppingCartSuccessCallback, onInitShoppingCartFailedCallback);
        }
    }
    ///endregion

    ///region Callback handler section.

    // Init shopping cart handler section.
    private void handleInitShoppingCartResponse(JSONObject response) {
        try
        {
            JSONObject shoppingCart = response.getJSONObject("body");
            String shoppingCartId = shoppingCart.getString("cartId");

            ShoppingCartStateManager.setCurrentShoppingCartId(shoppingCartId);
            ShoppingCartStateManager.setShoppingCartPreferenceValue(shoppingCartId);
        }
        catch (Exception exception)
        {
            Log.e("Shopping Cart Controller error", "Something wrong when init shopping cart");
        }

        // UI update here.
        Toast.makeText(this, "Current shopping cart id : " + ShoppingCartStateManager.getCurrentShoppingCartId(), Toast.LENGTH_SHORT).show();
    }

    private void handleInitShoppingCartFailure(VolleyError error) {
        Toast.makeText(this, "Có lỗi xảy ra với tính năng giỏ hàng", Toast.LENGTH_LONG).show();
    }

    // Load shopping cart handler section.
    private void handleLoadShoppingCartResponse(JSONObject response) {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            return;
        }

        ApiResponse apiResponse = result.value;
        try {
            JSONObject responseBodyInJson = apiResponse.getBodyAsJsonObject();
            DeserializeResult<ShoppingCartDto> shoppingCartDtoDeserializeResult
                    = ShoppingCartDto.DeserializeFromJson(responseBodyInJson);

            if (!shoppingCartDtoDeserializeResult.isSuccess) {
                Toast.makeText(this, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Lấy giỏ hàng từ API thành công", Toast.LENGTH_LONG).show();
            ShoppingCartStateManager.setShoppingCart(shoppingCartDtoDeserializeResult.value);
        }
        catch (Exception exception) {
            Toast.makeText(this, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoadShoppingCartFailure(VolleyError error) {
        Toast.makeText(this, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
    }

    // Load all product handler section.
    private void handleLoadAllProductsResponse(JSONObject response) {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            return;
        }

        ApiResponse apiResponse = result.value;

        try {
            JSONArray productListInJson = apiResponse.getBodyAsJsonArray();
            int productListLength = productListInJson.length();

            for (byte index = 0; index < productListLength; index++) {
                JSONObject productJson = productListInJson.getJSONObject(index);
                DeserializeResult<GeneralProductInfoDto> deserializeResult = GeneralProductInfoDto.DeserializeFromJson(productJson);

                if (deserializeResult.isSuccess) {
                    GeneralProductInfoDto productItem = deserializeResult.value;

                    products.add(productItem);
                }
            }

            ProductItemViewAdapter productItemViewAdapter = new ProductItemViewAdapter(HomeActivity.this, products, onAddToCartCallback);
            productItemRecyclerView.setAdapter(productItemViewAdapter);
            productItemRecyclerView.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Lấy danh sách sản phẩm thành công", Toast.LENGTH_LONG).show();
        }
        catch (Exception exception) {
            Toast.makeText(this, "Có lỗi xảy ra khi lấy sản phẩm", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoadAllProductsFailure(VolleyError error) {
        Toast.makeText(this, "Có lỗi xảy ra khi lấy sản phẩm", Toast.LENGTH_LONG).show();
    }

    private void handleOnAddToCart() {
        shoppingCartFragment.reloadShoppingCart();
    }
    ///endregion
}