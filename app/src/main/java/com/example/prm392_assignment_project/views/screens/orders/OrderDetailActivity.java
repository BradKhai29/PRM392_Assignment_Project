package com.example.prm392_assignment_project.views.screens.orders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.orders.OrderItemDto;
import com.example.prm392_assignment_project.views.recyclerviews.orders.order_item.OrderItemViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderCode;
    private TextView tvOrderTotalPrice;
    private TextView tvOrderTotalItems;
    private OrderItemViewAdapter orderItemViewAdapter;
    private final List<OrderItemDto> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String orderId = getIntent().getStringExtra("orderId");
        if (orderId == null)
        {
            Toast.makeText(this, "OrderId is null", Toast.LENGTH_SHORT).show();
            return;
        }

        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderTotalPrice = findViewById(R.id.tvOrderTotalPrice);
        tvOrderTotalItems = findViewById(R.id.tvOrderTotalItems);

        RecyclerView orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);

        // Set up Layout manager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderItemsRecyclerView.setLayoutManager(layoutManager);

        // Set up view adapter.
        orderItemViewAdapter = new OrderItemViewAdapter(orderItems);
        orderItemsRecyclerView.setAdapter(orderItemViewAdapter);

        getOrderDetailById(orderId);
    }

    private void getOrderDetailById(String orderId)
    {
        OrderApiHandler orderApiHandler = new OrderApiHandler(this);
        orderApiHandler.getOrderDetailById(
            orderId,
            this::handleGetOrderDetailSuccess,
            this::handleGetOrderDetailFailed);
    }

    private void handleGetOrderDetailSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            return;
        }

        ApiResponse apiResponse = result.value;

        try
        {
            JSONObject orderDetailInJson = apiResponse.getBodyAsJsonObject();
            int orderCode = orderDetailInJson.getInt("orderCode");
            int orderTotalPrice = orderDetailInJson.getInt("totalPrice");
            int orderTotalItems = 0;

            JSONArray orderItemListInJson = orderDetailInJson.getJSONArray("orderItems");
            int listLength = orderDetailInJson.length();

            for (byte index = 0; index < listLength; index++)
            {
                JSONObject orderItemInJson = orderItemListInJson.getJSONObject(index);
                DeserializeResult<OrderItemDto> deserializeResult = OrderItemDto.DeserializeFromJson(orderItemInJson);

                if (deserializeResult.isSuccess)
                {
                    OrderItemDto orderItem = deserializeResult.value;
                    orderTotalItems += orderItem.sellingQuantity;

                    orderItems.add(orderItem);
                    orderItemViewAdapter.notifyItemInserted(index);
                }
            }

            // Populate into text view.
            tvOrderCode.setText(String.valueOf(orderCode));
            tvOrderTotalPrice.setText(String.valueOf(orderTotalPrice));
            tvOrderTotalItems.setText(String.valueOf(orderTotalItems));
        }
        catch (Exception exception)
        {
            Toast.makeText(this, "Something wrong when deserialize", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGetOrderDetailFailed(VolleyError error)
    {
        Toast.makeText(this, "Api call failed from order detail", Toast.LENGTH_SHORT).show();
    }
}