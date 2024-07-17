package com.example.prm392_assignment_project.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.commons.HttpStatusCodes;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.helpers.UserAuthStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.receivers.WifiReceiver;
import com.example.prm392_assignment_project.views.screens.GoogleMapActivity;
import com.example.prm392_assignment_project.views.screens.auths.AuthActivity;
import com.example.prm392_assignment_project.views.screens.orders.OrderHistoryActivity;
import com.example.prm392_assignment_project.views.screens.shopping_carts.ShoppingCartDetailActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

/**
 * A fragment to handle UI logic related to the shopping cart.
 */
public class BottomNavigationBar extends Fragment {
    /**
     * This field is used to identify if the current fragment is completely loaded or not.
     */
    private boolean isLoadSuccess;
    private boolean isPaddingBottomSet = false;
    private int paddingBottom = -1;

    private final ShoppingCartApiHandler shoppingCartApiHandler;
    private final Context context;
    private final Activity containerActivity;
    private BadgeDrawable shoppingCartBadge;
    private WifiReceiver wifiReceiver;

    public BottomNavigationBar(Activity containerActivity)
    {
        this.containerActivity = containerActivity;
        context = containerActivity;
        shoppingCartApiHandler = new ShoppingCartApiHandler(containerActivity);
        isLoadSuccess = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_bottom_navigation_bar, container, false);
        setPaddingBottomForNavigationBar();

        // Set up for bottom navigation view.
        BottomNavigationView navigationView = view.findViewById(R.id.bottomNavigationBar);

        setUpShoppingCartBadge(navigationView);
        setUpOnItemSelectedListener(navigationView);

        // Register WifiReceiver.
        IntentFilter checkWifiStateIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // Load the shopping cart from api when the wifi is enable and the internet is available.
        WifiReceiver wifiReceiver = new WifiReceiver(this::loadShoppingCartFromApi, null);

        context.registerReceiver(wifiReceiver, checkWifiStateIntentFilter);

        return view;
    }

    /**
     * Set the bottom padding value for this navigation bar for better looks and feel in UI.
     */
    private void setPaddingBottomForNavigationBar()
    {
        ViewCompat.setOnApplyWindowInsetsListener(containerActivity.getWindow().getDecorView(), (v, insets) ->
        {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (!isPaddingBottomSet)
            {
                // Subtract the insets from the bottom padding
                paddingBottom = v.getPaddingBottom() - systemInsets.bottom - 16;

                v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    paddingBottom);

                isPaddingBottomSet = true;
            }

            return insets;
        });
    }

    private void setUpOnItemSelectedListener(BottomNavigationView navigationView)
    {
        navigationView.setOnItemSelectedListener(item ->
        {
            if (item.getItemId() == R.id.google_map)
            {
                viewMap();
            }

            if (item.getItemId() == R.id.shopping_cart)
            {
                viewShoppingCartDetail();
            }

            if (item.getItemId() == R.id.order_history)
            {
                viewOrderHistory();
            }

            return true;
        });
    }

    ///region Navigate to other activity section.
    private void viewMap()
    {
        Intent viewMapIntent = new Intent(context, GoogleMapActivity.class);
        context.startActivity(viewMapIntent);
    }

    private void viewShoppingCartDetail()
    {
        Intent viewShoppingCartDetailIntent = new Intent(context, ShoppingCartDetailActivity.class);

        context.startActivity(viewShoppingCartDetailIntent);
    }

    private void viewOrderHistory()
    {
        Intent viewOrderHistoryIntent = new Intent(context, OrderHistoryActivity.class);

        context.startActivity(viewOrderHistoryIntent);
    }
    ///endregion

    private void setUpShoppingCartBadge(BottomNavigationView navigationView)
    {
        final int EMPTY_CART_ITEMS = 0;

        shoppingCartBadge = navigationView.getOrCreateBadge(R.id.shopping_cart);
        shoppingCartBadge.setBackgroundColor(getResources().getColor(R.color.primary, null));
        shoppingCartBadge.setBadgeTextColor(getResources().getColor(R.color.white, null));
        shoppingCartBadge.setVerticalOffset(1);
        shoppingCartBadge.setNumber(EMPTY_CART_ITEMS);
    }

    public void loadShoppingCartBadge()
    {
        int shoppingCartTotalItems = ShoppingCartStateManager.getTotalItemsInCart();
        shoppingCartBadge.setNumber(shoppingCartTotalItems);
    }

    private void loadShoppingCartFromApi()
    {
        if (ShoppingCartStateManager.isShoppingCartLoadSuccess())
        {
            return;
        }

        // Process to init or load the shopping cart from the webapi.
        if (ShoppingCartStateManager.isShoppingCartPreferenceExisted())
        {
            Thread loadShoppingCartThread = getLoadShoppingCartThread();
            loadShoppingCartThread.start();
        }
        else
        {
            Handler initShoppingCartHandler = new Handler(Looper.getMainLooper());
            Thread initShoppingCartThread = new Thread(() ->
            {
                initShoppingCartHandler.post(() ->
                {
                    shoppingCartApiHandler.initShoppingCart(
                        this::handleInitShoppingCartSuccess,
                        this::handleLoadShoppingCartFailed);
                });
            });

            initShoppingCartThread.start();
        }
    }

    @NonNull
    private Thread getLoadShoppingCartThread() {
        ShoppingCartStateManager.loadShoppingCartIdFromPreference();
        String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();
        Handler loadShoppingCartHandler = new Handler(Looper.getMainLooper());

        return new Thread(() ->
        {
            loadShoppingCartHandler.post(() ->
            {
                shoppingCartApiHandler.loadShoppingCartById(
                    cartId,
                    this::handleLoadShoppingCartSuccess,
                    this::handleLoadShoppingCartFailed);
            });
        });
    }

    // Init shopping cart handler section.
    private void handleInitShoppingCartSuccess(JSONObject response)
    {
        try
        {
            JSONObject shoppingCartInJson = response.getJSONObject("body");
            String shoppingCartId = shoppingCartInJson.getString("cartId");

            ShoppingCartStateManager.setCurrentShoppingCartId(shoppingCartId);
            ShoppingCartStateManager.setShoppingCartPreferenceValue(shoppingCartId);
            ShoppingCartStateManager.setShoppingCart(ShoppingCartDto.empty(shoppingCartId));
            ShoppingCartStateManager.loadShoppingCartSuccess();

            loadShoppingCartBadge();

            isLoadSuccess = true;
        }
        catch (Exception exception)
        {
            Toast.makeText(context, "Tạo giỏ hàng từ API thất bại", Toast.LENGTH_LONG).show();
        }
    }

    private void handleInitShoppingCartFailed(VolleyError error)
    {
        Log.e("Có lỗi xảy ra", "Có lỗi xảy ra với tính năng giỏ hàng");
    }

    // Load shopping cart handler section.
    private void handleLoadShoppingCartSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess)
        {
            return;
        }

        ApiResponse apiResponse = result.value;
        try
        {
            JSONObject responseBodyInJson = apiResponse.getBodyAsJsonObject();

            DeserializeResult<ShoppingCartDto> shoppingCartDtoDeserializeResult
                = ShoppingCartDto.DeserializeFromJson(responseBodyInJson);

            if (!shoppingCartDtoDeserializeResult.isSuccess)
            {
                Log.e("Không deserialize được json", "Không thành công");
                return;
            }

            ShoppingCartStateManager.setShoppingCart(shoppingCartDtoDeserializeResult.value);
            ShoppingCartStateManager.loadShoppingCartSuccess();

            loadShoppingCartBadge();
            if (ShoppingCartStateManager.getTotalItemsInCart() > 0)
            {
                Toast.makeText(context, "Giỏ hàng có sản phẩm", Toast.LENGTH_SHORT).show();
            }

            isLoadSuccess = true;
        }
        catch (Exception exception)
        {
            Toast.makeText(context, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoadShoppingCartFailed(VolleyError error)
    {
        if (error.networkResponse == null)
        {
            return;
        }

        if (error.networkResponse.statusCode == HttpStatusCodes.BAD_REQUEST)
        {
            shoppingCartApiHandler.initShoppingCart(
                this::handleInitShoppingCartSuccess,
                this::handleInitShoppingCartFailed);

            Toast.makeText(context, "Thử tạo lại giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "Có lỗi xảy ra khi lấy giỏ hàng từ API", Toast.LENGTH_LONG).show();
        }
    }
}