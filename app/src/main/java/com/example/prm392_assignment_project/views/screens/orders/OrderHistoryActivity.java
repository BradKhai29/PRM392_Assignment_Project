package com.example.prm392_assignment_project.views.screens.orders;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.OrderApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.orders.OrderHistoryItemDto;
import com.example.prm392_assignment_project.receivers.WifiReceiver;
import com.example.prm392_assignment_project.views.recyclerviews.orders.order_history.OrderHistoryItemViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private OrderHistoryItemViewAdapter orderHistoryItemViewAdapter;
    private final List<OrderHistoryItemDto> orderHistoryItems = new ArrayList<>();
    private ImageButton btnBackHome;
    private Button btnBackHome2;
    private LinearLayout emptySection;
    private LinearLayout orderHistorySection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind components from view.
        btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome2 = findViewById(R.id.btnBackHome2);
        emptySection = findViewById(R.id.empty_section);
        orderHistorySection = findViewById(R.id.order_history_section);

        // Set on-click listener.
        btnBackHome.setOnClickListener(this::backHome);
        btnBackHome2.setOnClickListener(this::backHome);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView orderHistoryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        orderHistoryRecyclerView.setLayoutManager(layoutManager);

        orderHistoryItemViewAdapter = new OrderHistoryItemViewAdapter(orderHistoryItems, this);
        orderHistoryRecyclerView.setAdapter(orderHistoryItemViewAdapter);

        // Register WifiReceiver.
        IntentFilter checkWifiStateIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        WifiReceiver wifiReceiver = new WifiReceiver(this::getAllOrders, null);

        registerReceiver(wifiReceiver, checkWifiStateIntentFilter);
    }

    private void backHome(View view)
    {
        finish();
    }

    private void getAllOrders()
    {
        OrderApiHandler orderApiHandler = new OrderApiHandler(this);

        String guestId = ShoppingCartStateManager.getCurrentShoppingCartId();

        orderApiHandler.getAllOrdersByGuestId(
            guestId,
            this::handleGetAllOrdersSuccess,
            this::handleGetAllOrdersFailed);
    }

    private void handleGetAllOrdersSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess)
        {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            return;
        }

        ApiResponse apiResponse = result.value;

        try
        {
            JSONArray orderHistoryItemListInJson = apiResponse.getBodyAsJsonArray();
            int listLength = orderHistoryItemListInJson.length();

            for (byte index = 0; index < listLength; index++)
            {
                JSONObject orderHistoryItemInJson = orderHistoryItemListInJson.getJSONObject(index);

                DeserializeResult<OrderHistoryItemDto> deserializeResult =
                        OrderHistoryItemDto.DeserializeFromJson(orderHistoryItemInJson);

                if (deserializeResult.isSuccess)
                {
                    orderHistoryItems.add(deserializeResult.value);
                    orderHistoryItemViewAdapter.notifyItemInserted(index);
                }
            }

            if (orderHistoryItems.isEmpty())
            {
                emptySection.setVisibility(View.VISIBLE);
                orderHistorySection.setVisibility(View.GONE);
            }
            else
            {
                emptySection.setVisibility(View.GONE);
                orderHistorySection.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception exception)
        {
            popupToast("Something wrong when deserialized json to order history");
        }
    }

    private void handleGetAllOrdersFailed(VolleyError error)
    {
        popupToast("Failed to get all orders.");
    }

    private void popupToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}