package com.example.prm392_assignment_project.models.shoppingcarts;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShoppingCartDto {
    private String cartId;
    private int totalPrice;
    private int totalItems = 0;
    private final Map<String, CartItemDto> cartItemMap;

    private ShoppingCartDto() {
        cartItemMap = new HashMap<>();
    }

    private ShoppingCartDto(List<CartItemDto> cartItems) {
        this.cartItemMap = new HashMap<>(cartItems.size());

        for (CartItemDto cartItem : cartItems) {
            cartItemMap.put(cartItem.productId, cartItem);
        }
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addCartItem(CartItemDto cartItem) {
        if (cartItemMap.containsKey(cartItem.productId)) {
            CartItemDto foundCartItem = cartItemMap.get(cartItem.productId);

            Objects.requireNonNull(foundCartItem).quantity += cartItem.quantity;
            totalItems += cartItem.quantity;

            return;
        }

        this.cartItemMap.put(cartItem.productId, cartItem);
        totalItems += cartItem.quantity;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public static DeserializeResult<ShoppingCartDto> DeserializeFromJson(JSONObject shoppingCartJson) {
        try {
            String cartId = shoppingCartJson.getString("cartId");
            int totalPrice = shoppingCartJson.getInt("totalPrice");

            // Deserialize the cart item list from json.
            JSONArray cartItemListInJson = shoppingCartJson.getJSONArray("cartItems");

            int cartItemsLength = cartItemListInJson.length();
            List<CartItemDto> cartItems = new ArrayList<>(cartItemsLength);
            int totalItems = 0;

            for (byte index = 0; index < cartItemsLength; index++) {
                JSONObject cartItemInJson = cartItemListInJson.getJSONObject(index);

                DeserializeResult<CartItemDto> deserializeCartItemResult = CartItemDto.DeserializeFromJson(cartItemInJson);

                if (!deserializeCartItemResult.isSuccess) {
                    return DeserializeResult.failed();
                }

                CartItemDto cartItem = deserializeCartItemResult.value;
                totalItems += cartItem.quantity;

                cartItems.add(cartItem);
            }

            ShoppingCartDto shoppingCartDto = new ShoppingCartDto(cartItems);
            shoppingCartDto.cartId = cartId;
            shoppingCartDto.totalPrice = totalPrice;
            shoppingCartDto.totalItems = totalItems;

            return DeserializeResult.success(shoppingCartDto);
        }
        catch (Exception exception) {
            return DeserializeResult.failed();
        }
    }
}
