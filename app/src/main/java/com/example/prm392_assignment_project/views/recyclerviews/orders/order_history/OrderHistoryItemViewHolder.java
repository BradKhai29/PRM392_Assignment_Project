package com.example.prm392_assignment_project.views.recyclerviews.orders.order_history;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.views.screens.orders.OrderDetailActivity;

public class OrderHistoryItemViewHolder extends RecyclerView.ViewHolder {
    private final Context context;
    private final TextView tvOrderCode;
    private final TextView tvOrderTotalPrice;
    private final TextView tvCheckoutDate;

    private String orderId;

    public OrderHistoryItemViewHolder(
        @NonNull View itemView,
        Context context)
    {
        super(itemView);
        this.context = context;

        tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
        tvOrderTotalPrice = itemView.findViewById(R.id.tvOrderTotalPrice);
        tvCheckoutDate = itemView.findViewById(R.id.tvCheckoutDate);

        // Set up on-click listener.
        itemView.setOnClickListener(this::viewOrderDetail);
    }

    private void viewOrderDetail(View view)
    {
        Intent viewOrderDetailIntent = new Intent(context, OrderDetailActivity.class);

        viewOrderDetailIntent.putExtra("orderId", orderId);

        context.startActivity(viewOrderDetailIntent);
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setOrderCode(String orderCode)
    {
        String orderCodeText = "Đơn hàng: " + orderCode;
        tvOrderCode.setText(orderCodeText);
    }

    public void setOrderTotalPrice(int orderTotalPrice)
    {
        String orderTotalPriceText = orderTotalPrice + " VND";
        tvOrderTotalPrice.setText(orderTotalPriceText);
    }

    public void setCheckoutDate(String checkoutDate)
    {
        String checkoutDateText = "Thanh toán lúc: " + checkoutDate;
        tvCheckoutDate.setText(checkoutDateText);
    }
}
