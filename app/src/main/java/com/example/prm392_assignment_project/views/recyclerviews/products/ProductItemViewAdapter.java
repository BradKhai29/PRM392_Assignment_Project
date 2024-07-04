package com.example.prm392_assignment_project.views.recyclerviews.products;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.models.products.GeneralProductInfoDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnAddToCartCallback;

import java.util.List;

public class ProductItemViewAdapter extends RecyclerView.Adapter<ProductItemViewHolder> {
    private final Context context;
    private final List<GeneralProductInfoDto> products;
    private final IOnAddToCartCallback onAddToCartCallback;

    public ProductItemViewAdapter(Context context, List<GeneralProductInfoDto> products, IOnAddToCartCallback onAddToCartCallback) {
        this.context = context;
        this.products = products;
        this.onAddToCartCallback = onAddToCartCallback;
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);

        return new ProductItemViewHolder(view, context, onAddToCartCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        Log.d("Product Item", "Binding Position: " + position);
        GeneralProductInfoDto productInfo = products.get(position);

        if (productInfo == null) {
            return;
        }

        // Populate product information.
        holder.setProductId(productInfo.getId());
        holder.setImage(productInfo.getImageUrls().get(0));
        holder.setProductName(productInfo.getName());
        holder.setProductPrice(productInfo.getUnitPrice());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
