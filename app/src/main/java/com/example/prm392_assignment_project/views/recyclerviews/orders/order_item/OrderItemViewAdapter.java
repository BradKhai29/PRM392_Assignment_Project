package com.example.prm392_assignment_project.views.recyclerviews.orders.order_item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.models.dtos.orders.OrderHistoryItemDto;
import com.example.prm392_assignment_project.models.dtos.orders.OrderItemDto;

import java.util.List;

public class OrderItemViewAdapter
    extends RecyclerView.Adapter<OrderItemViewHolder>
{
    private final List<OrderItemDto> orderItems;

    public OrderItemViewAdapter(List<OrderItemDto> orderItemDtoList)
    {
        orderItems = orderItemDtoList;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType)
    {
        View view = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.order_item, parent, false);

        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
        @NonNull OrderItemViewHolder holder,
        int position)
    {
        OrderItemDto orderItem = orderItems.get(position);

        if (orderItem == null)
        {
            return;
        }

        // Populate order item information.
        holder.setProductName(orderItem.productName);
        holder.setSellingPrice(orderItem.sellingPrice);
        holder.setSellingQuantity(orderItem.sellingQuantity);
        holder.setSubTotal(orderItem.subTotal);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }
}
