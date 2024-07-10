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
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.screens.products.ProductDetailActivity;
import com.example.prm392_assignment_project.views.view_callbacks.IOnAddToCartCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductItemViewHolder extends RecyclerView.ViewHolder
{
    // Private fields.
    private String productId;
    private String productName;
    private int unitPrice;
    private String imageUrl;

    private final Context context;
    private final ImageView productImage;
    private final TextView tvProductName;
    private final TextView tvProductCategory;
    private final TextView tvProductPrice;
    private final Button btnAddToCart;
    private final ShoppingCartApiHandler shoppingCartApiHandler;

    // Callbacks.
    private final IOnAddToCartCallback onAddToCartCallback;

    public ProductItemViewHolder(
        @NonNull View itemView,
        Context context,
        IOnAddToCartCallback addToCartCallback)
    {
        super(itemView);
        // Set up dependencies.
        this.context = context;
        shoppingCartApiHandler = new ShoppingCartApiHandler(context);

        // Bind UI components from view.
        productImage = itemView.findViewById(R.id.product_image);
        tvProductName = itemView.findViewById(R.id.product_name);
        tvProductCategory = itemView.findViewById(R.id.product_category);
        tvProductPrice = itemView.findViewById(R.id.product_price);
        btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);

        // Set up callbacks.
        onAddToCartCallback = addToCartCallback;

        // Set up on click listeners.
        itemView.setOnClickListener(this::viewProductDetail);
        btnAddToCart.setOnClickListener(this::addToCart);
    }

    ///region Getters and Setters
    public void setProductId(String id) {
        productId = id;
    }

    public void setImage(String imageUrl)
    {
        this.imageUrl = imageUrl;
        Picasso.get().load(imageUrl).into(productImage);
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
        tvProductName.setText(productName);
    }

    public void setProductCategory(String category)
    {
        tvProductCategory.setText(category);
    }

    public void setProductPrice(int price)
    {
        unitPrice = price;
        tvProductPrice.setText(price + " VND");
    }
    ///endregion

    private void viewProductDetail(View view)
    {
        Intent viewProductDetailIntent = new Intent(context, ProductDetailActivity.class);
        viewProductDetailIntent.putExtra("productId", productId);

        context.startActivity(viewProductDetailIntent);
    }

    private void addToCart(View view)
    {
        final String cartId = ShoppingCartStateManager.getCurrentShoppingCartId();
        CartItemDto cartItem = CartItemDto.getInstanceToCallApi(
            cartId,
            productId,
            1);

        try
        {
            shoppingCartApiHandler.addToCart(
                cartItem,
                this::handleAddToCartSuccess,
                this::handleAddToCartFailed);
        }
        catch (JSONException jsonException) {
            Toast.makeText(context, "Have error with JSON body when add to cart", Toast.LENGTH_LONG).show();
        }
    }

    ///region Callback handlers
    private void handleAddToCartSuccess(JSONObject response)
    {
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

    private void handleAddToCartFailed(VolleyError error)
    {
        Toast.makeText(context, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_LONG).show();
    }
    ///endregion
}
