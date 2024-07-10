package com.example.prm392_assignment_project.views.screens.products;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ProductApiHandler;
import com.example.prm392_assignment_project.api_handlers.implementation.ShoppingCartApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.products.DetailProductInfoDto;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.models.dtos.shoppingcarts.ShoppingCartDto;
import com.example.prm392_assignment_project.views.screens.shopping_carts.ShoppingCartDetailActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    // Private fields.
    private String productId;
    private String imageUrl;
    private String productName;
    private int unitPrice;
    private int addToCartQuantity;

    private ShoppingCartApiHandler shoppingCartApiHandler;

    public ProductDetailActivity()
    {
        addToCartQuantity = 1;
    }

    // UI Components
    private ImageView productImage;
    private TextView tvProductName;
    private Button productCategory;
    private TextView tvProductPrice;
    private TextView tvProductDescription;

    // Add to cart section.
    /**
     * This field will control the add-to-cart action from user.
     */
    private boolean allowAddToCart = false;
    private TextView tvTotalItems;
    private TextView tvAddToCartQuantity;
    private ImageButton btnIncreaseQuantity;
    private ImageButton btnDecreaseQuantity;
    private Button btnAddToCart;
    private ImageButton btnBackHome;
    private ImageButton btnViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productId = getIntent().getStringExtra("productId");

        if (String.valueOf(productId).isEmpty())
        {
            finish();
            return;
        }

        // Bind the components from layout.
        productImage = findViewById(R.id.product_image);
        tvProductName = findViewById(R.id.product_name);
        productCategory = findViewById(R.id.product_category);
        tvProductPrice = findViewById(R.id.product_price);
        tvProductDescription = findViewById(R.id.product_description);
        btnBackHome = findViewById(R.id.btnBackHome);

        // Add to cart components section.
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvAddToCartQuantity = findViewById(R.id.addToCartQuantity);
        btnDecreaseQuantity = findViewById(R.id.btnMinus);
        btnIncreaseQuantity = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnViewCart = findViewById(R.id.btnViewCart);

        loadCartTotalItems();
        shoppingCartApiHandler = new ShoppingCartApiHandler(this);

        setUpOnClickListener();
        getProductDetail();
    }

    private void loadCartTotalItems()
    {
        tvTotalItems.setText(String.valueOf(ShoppingCartStateManager.getTotalItemsInCart()));
    }

    private void getProductDetail()
    {
        ProductApiHandler productApiHandler = new ProductApiHandler(this);
        productApiHandler.getProductDetailById(
            productId,
            this::handleGetProductDetailSuccess,
            this::handleGetProductDetailFailed);
    }

    private void setUpOnClickListener()
    {
        btnIncreaseQuantity.setOnClickListener(this::increaseAddToCartQuantity);
        btnDecreaseQuantity.setOnClickListener(this::decreaseAddToCartQuantity);
        btnAddToCart.setOnClickListener(this::handleAddToCart);
        btnBackHome.setOnClickListener(this::backToHome);
        btnViewCart.setOnClickListener(this::viewCartDetail);
    }

    private void backToHome(View view)
    {
        finish();
    }

    private void viewCartDetail(View view)
    {
        Intent viewCartDetailIntent = new Intent(this, ShoppingCartDetailActivity.class);

        this.startActivity(viewCartDetailIntent);
    }

    private void increaseAddToCartQuantity(View view)
    {
        addToCartQuantity++;
        tvAddToCartQuantity.setText(String.valueOf(addToCartQuantity));
    }

    private void decreaseAddToCartQuantity(View view)
    {
        final int MIN_QUANTITY = 1;

        if (addToCartQuantity == MIN_QUANTITY) {
            return;
        }

        addToCartQuantity--;
        tvAddToCartQuantity.setText(String.valueOf(addToCartQuantity));
    }

    private void handleAddToCart(View view)
    {
        if (!allowAddToCart)
        {
            return;
        }

        // Set add to cart flag to false to prevent user spamming add to cart button.
        allowAddToCart = false;

        CartItemDto cartItem = CartItemDto.getInstanceToCallApi(
            ShoppingCartStateManager.getCurrentShoppingCartId(),
            productId,
            addToCartQuantity);

        try {
            shoppingCartApiHandler.addToCart(cartItem, this::handleAddToCartSuccess, this::handleAddToCartFailed);
        }
        catch (JSONException jsonException)
        {
            Toast.makeText(this, "Have error with JSON body when add to cart", Toast.LENGTH_LONG).show();
        }
    }

    ///region Callback handlers
    private void handleGetProductDetailSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
            return;
        }

        ApiResponse apiResponse = result.value;

        try {
            JSONObject productDetailInJson = apiResponse.getBodyAsJsonObject();
            DeserializeResult<DetailProductInfoDto> deserializeResult = DetailProductInfoDto.DeserializeFromJson(productDetailInJson);

            if (!deserializeResult.isSuccess) {
                Toast.makeText(this, "Có lỗi hiển thị sản phẩm", Toast.LENGTH_LONG).show();
            }

            DetailProductInfoDto productDetail = deserializeResult.value;
            productName = productDetail.getName();
            imageUrl = productDetail.getImageUrls().get(0);
            unitPrice = productDetail.getUnitPrice();

            // Populate data into view component.
            Picasso.get().load(imageUrl).into(productImage);
            tvProductName.setText(productName);
            productCategory.setText(productDetail.getCategory());
            tvProductPrice.setText(String.valueOf(unitPrice));
            tvProductDescription.setText(productDetail.getDescription());

            // Set allow add to cart flag to true to let user add product to cart.
            allowAddToCart = true;
        }
        catch (Exception exception) {
            Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
        }
    }

    private void handleGetProductDetailFailed(VolleyError error)
    {
        Toast.makeText(this, "Lấy sản phẩm thất bại", Toast.LENGTH_LONG).show();
    }

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
            addToCartQuantity);

        // Update the shopping cart state and update again UI.
        shoppingCart.addCartItem(cartItem);
        ShoppingCartStateManager.addChanges();
        loadCartTotalItems();

        allowAddToCart = true;
        Toast.makeText(this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_LONG).show();
    }

    private void handleAddToCartFailed(VolleyError error)
    {
        allowAddToCart = true;
        Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_LONG).show();
    }
    ///endregion
}