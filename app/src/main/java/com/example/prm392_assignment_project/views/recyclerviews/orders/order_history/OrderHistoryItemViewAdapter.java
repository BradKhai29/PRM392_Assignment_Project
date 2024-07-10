package com.example.prm392_assignment_project.views.recyclerviews.orders.order_history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.models.dtos.orders.OrderHistoryItemDto;

import java.util.List;

public class OrderHistoryItemViewAdapter
    extends RecyclerView.Adapter<OrderHistoryItemViewHolder>
{
    private final List<OrderHistoryItemDto> orderHistoryItems;
    private final Context context;

    public OrderHistoryItemViewAdapter(
        List<OrderHistoryItemDto> orderHistoryItemDtoList,
        Context context)
    {
        orderHistoryItems = orderHistoryItemDtoList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHistoryItemViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType)
    {
        View view = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.order_history_item, parent, false);

        return new OrderHistoryItemViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(
        @NonNull OrderHistoryItemViewHolder holder,
        int position)
    {
        OrderHistoryItemDto orderHistoryItem = orderHistoryItems.get(position);

        if (orderHistoryItem == null)
        {
            return;
        }

        // Populate order history item information.
        holder.setOrderId(orderHistoryItem.orderId);
        holder.setOrderCode(orderHistoryItem.orderCode);
        holder.setOrderTotalPrice(orderHistoryItem.totalPrice);
        holder.setCreatedAt(orderHistoryItem.createdAt);
    }

    @Override
    public int getItemCount() {
        return orderHistoryItems.size();
    }
}
