package com.example.prm392_assignment_project.views.recyclerviews.orders.order_item;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;

public class OrderItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvProductName;
    private final TextView tvSellingPrice;
    private final TextView tvSellingQuantity;
    private final TextView tvSubtotal;

    public OrderItemViewHolder(@NonNull View itemView)
    {
        super(itemView);

        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvSellingPrice = itemView.findViewById(R.id.tvSellingPrice);
        tvSellingQuantity = itemView.findViewById(R.id.tvSellingQuantity);
        tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
    }

    public void setProductName(String productName)
    {
        tvProductName.setText(productName);
    }

    public void setSellingPrice(int sellingPrice)
    {
        tvSellingPrice.setText(String.valueOf(sellingPrice));
    }

    public void setSellingQuantity(int sellingQuantity)
    {
        tvSellingQuantity.setText(String.valueOf(sellingQuantity));
    }

    public void setSubTotal(int subTotal)
    {
        tvSubtotal.setText(String.valueOf(subTotal));
    }
}
