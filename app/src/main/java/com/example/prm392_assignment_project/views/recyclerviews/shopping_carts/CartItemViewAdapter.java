package com.example.prm392_assignment_project.views.recyclerviews.shopping_carts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnUpdateCartCallback;
import com.example.prm392_assignment_project.views.view_callbacks.support_enums.UpdateCartType;
import com.example.prm392_assignment_project.views.view_callbacks.support_models.UpdateCartActionDetail;

import java.util.List;

public class CartItemViewAdapter extends RecyclerView.Adapter<CartItemViewHolder> {
    private final Context context;
    private final List<CartItemDto> cartItems;
    private final IOnUpdateCartCallback onUpdateCartCallback;
    private final IOnUpdateCartCallback onUpdateCartFromViewHolderCallback;

    public CartItemViewAdapter(
        Context context,
        List<CartItemDto> cartItems,
        IOnUpdateCartCallback onUpdateCartCallback)
    {
        this.context = context;
        this.cartItems = cartItems;
        this.onUpdateCartCallback = onUpdateCartCallback;
        this.onUpdateCartFromViewHolderCallback = this::handleOnUpdateCartFromViewHolder;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);

        return new CartItemViewHolder(view, context, onUpdateCartFromViewHolderCallback);
    }

    @Override
    public void onBindViewHolder(
        @NonNull CartItemViewHolder holder,
        int position)
    {
        CartItemDto cartItem = cartItems.get(position);

        if (cartItem == null) {
            return;
        }

        // Populate cart item information.
        holder.setProductId(cartItem.productId);
        holder.setProductName(cartItem.productName);
        holder.setProductPrice(cartItem.unitPrice);
        holder.setProductImage(cartItem.imageUrl);
        holder.setCurrentQuantity(cartItem.quantity);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void handleOnUpdateCartFromViewHolder(UpdateCartActionDetail updateDetail) {
        if (updateDetail.updateCartType == UpdateCartType.REMOVE_CART_ITEM) {
            int removePosition = -1;

            for (byte currentPosition = 0; currentPosition < cartItems.size(); currentPosition++) {
                CartItemDto cartItem = cartItems.get(currentPosition);

                if (cartItem.productId.equals(updateDetail.updatedCartItemId)) {
                    removePosition = currentPosition;
                    break;
                }
            }

            if (removePosition == -1) {
                Toast.makeText(context, "Invalid product id ???.", Toast.LENGTH_SHORT).show();
                return;
            }

            cartItems.remove(removePosition);
            notifyItemRemoved(removePosition);
        }

        onUpdateCartCallback.resolve(updateDetail);
    }
}
