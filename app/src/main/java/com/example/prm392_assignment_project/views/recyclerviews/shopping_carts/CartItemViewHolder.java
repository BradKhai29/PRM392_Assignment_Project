package com.example.prm392_assignment_project.views.recyclerviews.shopping_carts;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnUpdateCartCallback;
import com.example.prm392_assignment_project.views.view_callbacks.support_enums.UpdateCartType;
import com.example.prm392_assignment_project.views.view_callbacks.support_models.UpdateCartActionDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class CartItemViewHolder extends RecyclerView.ViewHolder {
    private final Context context;
    private final ShoppingCartApiHandler shoppingCartApiHandler;
    private final IOnUpdateCartCallback onUpdateCartCallback;

    private String productId;
    private int currentQuantity;

    // UI Components.
    private final TextView tvProductName;
    private final TextView tvProductPrice;
    private final TextView tvCurrentQuantity;
    private final ImageView productImage;
    private final Button btnIncreaseQuantity;
    private final Button btnDecreaseQuantity;
    private final Button btnRemoveItem;

    public CartItemViewHolder(
        @NonNull View itemView,
        Context context,
        IOnUpdateCartCallback onUpdateCartCallback)
    {
        super(itemView);

        this.context = context;
        shoppingCartApiHandler = new ShoppingCartApiHandler(context);
        this.onUpdateCartCallback = onUpdateCartCallback;

        tvProductName = itemView.findViewById(R.id.product_name);
        tvProductPrice = itemView.findViewById(R.id.product_price);
        tvCurrentQuantity = itemView.findViewById(R.id.currentQuantity);
        productImage = itemView.findViewById(R.id.product_image);

        btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
        btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
        btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);

        setUpOnClickListener();
    }

    private void setUpOnClickListener() {
        btnIncreaseQuantity.setOnClickListener(this::increaseQuantity);
        btnDecreaseQuantity.setOnClickListener(this::decreaseQuantity);
        btnRemoveItem.setOnClickListener(this::removeItem);
    }

    ///region Public methods.
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductPrice(int productPrice) {
        tvProductPrice.setText(productPrice + " VND");
    }

    public void setProductImage(String imageUrl) {
        Picasso.get().load(imageUrl).into(productImage);
    }

    public void setProductName(String productName) {
        tvProductName.setText(productName);
    }

    public void setCurrentQuantity(int quantity) {
        currentQuantity = quantity;
        tvCurrentQuantity.setText(String.valueOf(quantity));
    }
    ///endregion

    private void decreaseQuantity(View view) {
        final int MIN_QUANTITY = 1;

        if (currentQuantity == MIN_QUANTITY) {
            return;
        }

        try {
            String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();
            CartItemDto cartItemToDecrease = CartItemDto.getInstanceToCallApi(cartId, productId, 1);

            shoppingCartApiHandler.decreaseCartItemQuantity(
                cartItemToDecrease,
                this::handleDecreaseQuantitySuccess,
                this::handleDecreaseQuantityFailed);
        }
        catch (Exception exception) {
            Toast.makeText(context, "Has error when decrease quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDecreaseQuantitySuccess(JSONObject response) {
        currentQuantity--;
        tvCurrentQuantity.setText(String.valueOf(currentQuantity));

        ShoppingCartStateManager.getShoppingCart().decreaseCartItem(productId, 1);
        ShoppingCartStateManager.addChanges();

        UpdateCartActionDetail updateDetail = UpdateCartActionDetail.getInstance(UpdateCartType.DECREASE_CART_ITEM_QUANTITY, productId);
        onUpdateCartCallback.resolve(updateDetail);
    }

    private void handleDecreaseQuantityFailed(VolleyError error) {
        Toast.makeText(context, "Decrease quantity api call failed", Toast.LENGTH_SHORT).show();
    }

    private void increaseQuantity(View view) {
        try {
            String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();
            CartItemDto cartItemToIncrease = CartItemDto.getInstanceToCallApi(cartId, productId, 1);

            shoppingCartApiHandler.addToCart(
                cartItemToIncrease,
                this::handleIncreaseQuantitySuccess,
                this::handleIncreaseQuantityFailed);
        }
        catch (Exception exception) {
            Toast.makeText(context, "Has error when decrease quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleIncreaseQuantitySuccess(JSONObject response) {
        currentQuantity++;
        tvCurrentQuantity.setText(String.valueOf(currentQuantity));

        ShoppingCartStateManager.getShoppingCart().increaseCartItem(productId, 1);
        ShoppingCartStateManager.addChanges();

        UpdateCartActionDetail updateDetail = UpdateCartActionDetail.getInstance(UpdateCartType.INCREASE_CART_ITEM_QUANTITY, productId);
        onUpdateCartCallback.resolve(updateDetail);
    }

    private void handleIncreaseQuantityFailed(VolleyError error) {
        Toast.makeText(context, "Api error when increase quantity", Toast.LENGTH_SHORT).show();
    }

    private void removeItem(View view) {
        String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();

        shoppingCartApiHandler.removeCartItem(
            cartId,
            productId,
            this::handleRemoveCartItemSuccess,
            this::handleRemoveCartItemFailed);
    }

    private void handleRemoveCartItemSuccess(JSONObject response) {
        ShoppingCartStateManager.getShoppingCart().removeCartItem(productId);
        ShoppingCartStateManager.addChanges();

        UpdateCartActionDetail updateDetail = UpdateCartActionDetail.getInstance(UpdateCartType.REMOVE_CART_ITEM, productId);
        onUpdateCartCallback.resolve(updateDetail);
    }

    private void handleRemoveCartItemFailed(VolleyError error) {
        Toast.makeText(context, "Remove cart item failed from api", Toast.LENGTH_SHORT).show();
    }
}
