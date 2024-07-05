package com.example.prm392_assignment_project.views.recyclerviews.products;

import android.content.Context;
import android.content.Intent;
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
import com.example.prm392_assignment_project.models.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.models.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.screens.products.ProductDetailActivity;
import com.example.prm392_assignment_project.views.view_callbacks.IOnAddToCartCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductItemViewHolder extends RecyclerView.ViewHolder {
    private String productId;
    private String productName;
    private int unitPrice;
    private String imageUrl;

    private final Context context;
    private final ImageView productImage;
    private final TextView tvProductName;
    private final TextView tvProductPrice;
    private final Button btnAddToCart;
    private final ShoppingCartApiHandler shoppingCartApiHandler;

    // Callbacks.
    private final IOnAddToCartCallback onAddToCartCallback;
    private final IOnCallApiSuccessCallback onAddToCartSuccessCallback;
    private final IOnCallApiFailedCallback onAddToCartFailureCallback;

    public ProductItemViewHolder(@NonNull View itemView, Context context, IOnAddToCartCallback addToCartCallback) {
        super(itemView);
        // Set up dependencies.
        this.context = context;
        shoppingCartApiHandler = new ShoppingCartApiHandler(context);

        // Init UI components.
        productImage = itemView.findViewById(R.id.product_image);
        tvProductName = itemView.findViewById(R.id.product_name);
        tvProductPrice = itemView.findViewById(R.id.product_price);
        btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);

        // Set up callbacks.
        onAddToCartCallback = addToCartCallback;
        onAddToCartSuccessCallback = this::handleOnAddToCartSuccess;
        onAddToCartFailureCallback = this::handlerOnAddToCartFailed;

        // Set up on click listeners.
        setUpOnClickListeners();
    }

    private void setUpOnClickListeners() {
        itemView.setOnClickListener(this::handleClickOnItemView);
        btnAddToCart.setOnClickListener(this::handleAddToCartListener);
    }

    private void handleClickOnItemView(View view) {
        Intent goToProductDetailIntent = new Intent(context, ProductDetailActivity.class);
        goToProductDetailIntent.putExtra("productId", productId);

        context.startActivity(goToProductDetailIntent);
    }

    private void handleAddToCartListener(View view) {
        CartItemDto cartItem = CartItemDto.getInstanceToCallApi(ShoppingCartStateManager.getCurrentShoppingCartId(), productId, 1);

        try {
            shoppingCartApiHandler.addToCart(cartItem, onAddToCartSuccessCallback, onAddToCartFailureCallback);
        }
        catch (JSONException jsonException) {
            Toast.makeText(context, "Have error with JSON body when add to cart", Toast.LENGTH_LONG).show();
        }
    }

    ///region Getters and Setters
    public void setProductId(String id) {
        productId = id;
    }

    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
        Picasso.get().load(imageUrl).into(productImage);
    }

    public void setProductName(String productName) {
        this.productName = productName;
        tvProductName.setText(productName);
    }

    public void setProductPrice(int price) {
        unitPrice = price;
        tvProductPrice.setText(price + " VND");
    }
    ///endregion

    ///region Callback handlers
    private void handleOnAddToCartSuccess(JSONObject response) {
        ShoppingCartDto shoppingCart = ShoppingCartStateManager.getShoppingCart();

        String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();

        CartItemDto cartItem = CartItemDto.getInstanceToAdd(
                cartId,
                productId,
                productName,
                unitPrice,
                imageUrl,
                1);

        shoppingCart.addCartItem(cartItem);

        Toast.makeText(context, "Thêm vào giỏ hàng thành công", Toast.LENGTH_LONG).show();
        onAddToCartCallback.resolve();
    }

    private void handlerOnAddToCartFailed(VolleyError error) {

    }
    ///endregion
}
