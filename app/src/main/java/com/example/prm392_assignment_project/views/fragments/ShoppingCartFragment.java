package com.example.prm392_assignment_project.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.screens.orders.OrderHistoryActivity;
import com.example.prm392_assignment_project.views.screens.shopping_carts.ShoppingCartDetailActivity;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;

import org.json.JSONObject;

/**
 * A fragment to handle UI logic related to the shopping cart.
 */
public class ShoppingCartFragment extends Fragment {
    /**
     * This field is used to identify if the current fragment is completely loaded or not.
     */
    private boolean isLoadSuccess;
    private ShoppingCartApiHandler shoppingCartApiHandler;

    // Init shopping cart callbacks.
    private final IOnCallApiSuccessCallback onInitShoppingCartSuccessCallback;
    private final IOnCallApiFailedCallback onInitShoppingCartFailedCallback;

    // Load shopping cart callbacks.
    private final IOnCallApiSuccessCallback onLoadShoppingCartSuccessCallback;
    private final IOnCallApiFailedCallback onLoadShoppingCartFailedCallback;

    private Context context;

    // UI components.
    private Button btnShoppingCart;
    private Button btnViewOrderHistory;

    public ShoppingCartFragment() {
        isLoadSuccess = false;

        // Init shopping cart.
        onInitShoppingCartSuccessCallback = this::handleInitShoppingCartResponse;
        onInitShoppingCartFailedCallback = this::handleInitShoppingCartFailure;

        // Load shopping cart.
        onLoadShoppingCartSuccessCallback = this::handleLoadShoppingCartResponse;
        onLoadShoppingCartFailedCallback = this::handleLoadShoppingCartFailure;
    }

    public void setContext(Context context) {
        this.context = context;
        shoppingCartApiHandler = new ShoppingCartApiHandler(context);
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
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        btnShoppingCart = view.findViewById(R.id.btnShoppingCart);
        btnViewOrderHistory = view.findViewById(R.id.btnViewOrderHistory);

        // Setup on click listener for shopping cart btn.
        btnShoppingCart.setOnClickListener(this::viewShoppingCartDetail);
        btnViewOrderHistory.setOnClickListener(this::viewOrderHistory);

        loadShoppingCartFromApi();

        return view;
    }

    private void viewShoppingCartDetail(View view)
    {
        if (!isLoadSuccess)
        {
            Toast.makeText(context, "The fragment isn't completely loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent viewShoppingCartDetailIntent = new Intent(context, ShoppingCartDetailActivity.class);
        context.startActivity(viewShoppingCartDetailIntent);
    }

    private void viewOrderHistory(View view)
    {
        Intent viewOrderHistoryIntent = new Intent(context, OrderHistoryActivity.class);
        context.startActivity(viewOrderHistoryIntent);
    }

    public void reloadShoppingCart() {
        String text = "Cart (" + ShoppingCartStateManager.getTotalItemsInCart() + ")";

        btnShoppingCart.setText(text);
    }

    private void loadShoppingCartFromApi() {
        if (ShoppingCartStateManager.isShoppingCartLoadSuccess()) {
            return;
        }

        // Process to init or load the shopping cart from the webapi.
        if (ShoppingCartStateManager.isShoppingCartPreferenceExisted()) {
            ShoppingCartStateManager.loadShoppingCartIdFromPreference();

            String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();

            shoppingCartApiHandler.loadShoppingCartById(cartId, onLoadShoppingCartSuccessCallback, onLoadShoppingCartFailedCallback);
        }
        else {
            shoppingCartApiHandler.initShoppingCart(onInitShoppingCartSuccessCallback, onInitShoppingCartFailedCallback);
        }
    }

    // Init shopping cart handler section.
    private void handleInitShoppingCartResponse(JSONObject response) {
        try
        {
            JSONObject shoppingCart = response.getJSONObject("body");
            String shoppingCartId = shoppingCart.getString("cartId");

            ShoppingCartStateManager.setCurrentShoppingCartId(shoppingCartId);
            ShoppingCartStateManager.setShoppingCartPreferenceValue(shoppingCartId);
            ShoppingCartStateManager.clearShoppingCart();

            String text = "Cart (" + ShoppingCartStateManager.getTotalItemsInCart() + ")";

            btnShoppingCart.setText(text);
            isLoadSuccess = true;
        }
        catch (Exception exception)
        {
            Log.e("Shopping Cart Controller error", "Something wrong when init shopping cart");
        }

        // UI update here.
        Log.d("", "Current shopping cart id : " + ShoppingCartStateManager.getCurrentShoppingCartId());
    }

    private void handleInitShoppingCartFailure(VolleyError error) {
        Log.d("", "Có lỗi xảy ra với tính năng giỏ hàng");
    }

    // Load shopping cart handler section.
    private void handleLoadShoppingCartResponse(JSONObject response) {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess) {
            return;
        }

        ApiResponse apiResponse = result.value;
        try {
            JSONObject responseBodyInJson = apiResponse.getBodyAsJsonObject();
            Log.i("JSON body", responseBodyInJson.toString());

            DeserializeResult<ShoppingCartDto> shoppingCartDtoDeserializeResult
                    = ShoppingCartDto.DeserializeFromJson(responseBodyInJson);

            if (!shoppingCartDtoDeserializeResult.isSuccess) {
                Log.e("Không deserialize được", "Không thành công");
                return;
            }

            Log.e("Lấy giỏ hàng", "Thành công");
            ShoppingCartStateManager.setShoppingCart(shoppingCartDtoDeserializeResult.value);
            ShoppingCartStateManager.loadShoppingCartSuccess();

            String text = "Cart (" + ShoppingCartStateManager.getTotalItemsInCart() + ")";

            btnShoppingCart.setText(text);
            isLoadSuccess = true;
        }
        catch (Exception exception) {
            Toast.makeText(context, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoadShoppingCartFailure(VolleyError error) {
        if (error.networkResponse.statusCode == 400) {
            shoppingCartApiHandler.initShoppingCart(onInitShoppingCartSuccessCallback, onInitShoppingCartFailedCallback);
            Toast.makeText(context, "Thử tạo lại giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
    }
}