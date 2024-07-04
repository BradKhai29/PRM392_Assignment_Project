package com.example.prm392_assignment_project.views.fragments;

import android.content.Context;
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
import com.example.prm392_assignment_project.models.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnFailureCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnSuccessCallback;

import org.json.JSONObject;

/**
 * A fragment to handle UI logic related to the shopping cart.
 */
public class ShoppingCartFragment extends Fragment {
    // Init shopping cart callbacks.
    private final IOnSuccessCallback onInitShoppingCartSuccessCallback;
    private final IOnFailureCallback onInitShoppingCartFailedCallback;

    // Load shopping cart callbacks.
    private final IOnSuccessCallback onLoadShoppingCartSuccessCallback;
    private final IOnFailureCallback onLoadShoppingCartFailedCallback;

    private Context context;

    // UI components.
    private Button btnShoppingCart;

    public ShoppingCartFragment() {
        // Init shopping cart.
        onInitShoppingCartSuccessCallback = this::handleInitShoppingCartResponse;
        onInitShoppingCartFailedCallback = this::handleInitShoppingCartFailure;

        // Load shopping cart.
        onLoadShoppingCartSuccessCallback = this::handleLoadShoppingCartResponse;
        onLoadShoppingCartFailedCallback = this::handleLoadShoppingCartFailure;
    }

    public void setContext(Context context) {
        this.context = context;
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

        loadShoppingCartFromApi();

        return view;
    }

    public void reloadShoppingCart() {
        String text = "Cart (" + ShoppingCartStateManager.getTotalItemsInCart() + ")";

        btnShoppingCart.setText(text);
    }

    private void loadShoppingCartFromApi() {
        // Process to init or load the shopping cart from the webapi.
        if (ShoppingCartStateManager.isShoppingCartPreferenceExisted()) {
            ShoppingCartStateManager.loadShoppingCartIdFromPreference();

            String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();

            ShoppingCartApiHandler shoppingCartApiHandler = new ShoppingCartApiHandler(context);
            shoppingCartApiHandler.loadShoppingCartById(cartId, onLoadShoppingCartSuccessCallback, onLoadShoppingCartFailedCallback);
        }
        else {
            ShoppingCartApiHandler shoppingCartApiHandler = new ShoppingCartApiHandler(context);
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
            String text = "Cart (" + ShoppingCartStateManager.getTotalItemsInCart() + ")";

            btnShoppingCart.setText(text);
        }
        catch (Exception exception) {
            Toast.makeText(context, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoadShoppingCartFailure(VolleyError error) {
        //Toast.makeText(this, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
    }
}