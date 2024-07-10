package com.example.prm392_assignment_project.models.dtos.shoppingcarts;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartDto
{
    private String cartId;
    private int totalPrice;
    private int totalItems;
    private final Map<String, CartItemDto> cartItemMap;

    private ShoppingCartDto()
    {
        cartItemMap = new HashMap<>();
        totalItems = 0;
    }

    private ShoppingCartDto(List<CartItemDto> cartItems)
    {
        totalItems = 0;
        this.cartItemMap = new HashMap<>(cartItems.size());

        for (CartItemDto cartItem : cartItems)
        {
            cartItemMap.put(cartItem.productId, cartItem);
            totalItems += cartItem.quantity;
        }
    }

    public static ShoppingCartDto empty(String cartId)
    {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.cartId = cartId;

        return shoppingCartDto;
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

    public void addCartItem(CartItemDto cartItem)
    {
        if (cartItemMap.containsKey(cartItem.productId)) {
            CartItemDto foundCartItem = cartItemMap.get(cartItem.productId);
            foundCartItem.quantity += cartItem.quantity;
        }
        else {
            this.cartItemMap.put(cartItem.productId, cartItem);
        }

        totalItems += cartItem.quantity;
        totalPrice += cartItem.quantity * cartItem.unitPrice;
    }

    public void increaseQuantity(String itemId, int increaseQuantity)
    {
        if (cartItemMap.containsKey(itemId)) {
            CartItemDto foundCartItem = cartItemMap.get(itemId);
            foundCartItem.quantity += increaseQuantity;

            totalItems += increaseQuantity;
            totalPrice += foundCartItem.unitPrice * increaseQuantity;
        }
    }

    public void decreaseQuantity(String itemId, int decreaseQuantity)
    {
        if (cartItemMap.containsKey(itemId)) {
            CartItemDto foundCartItem = cartItemMap.get(itemId);
            foundCartItem.quantity -= decreaseQuantity;

            totalItems -= decreaseQuantity;
            totalPrice -= foundCartItem.unitPrice * decreaseQuantity;
        }
    }

    public void removeCartItem(String itemId)
    {
        if (cartItemMap.containsKey(itemId)) {
            CartItemDto foundCartItem = cartItemMap.get(itemId);

            cartItemMap.remove(itemId);
            totalItems -= foundCartItem.quantity;
            totalPrice -= foundCartItem.subTotal();
        }
    }

    public int getTotalItems() {
        return totalItems;
    }

    public List<CartItemDto> getCartItems()
    {
        List<CartItemDto> cartItems = new ArrayList<>(cartItemMap.size());

        for (Map.Entry<String, CartItemDto> cartItemEntry : cartItemMap.entrySet()) {
            cartItems.add(cartItemEntry.getValue());
        }

        return cartItems;
    }

    public void clear()
    {
        cartItemMap.clear();
        totalItems = 0;
        totalPrice = 0;
    }

    public static DeserializeResult<ShoppingCartDto> DeserializeFromJson(JSONObject jsonData)
    {
        try
        {
            String cartId = jsonData.getString("cartId");
            int totalPrice = jsonData.getInt("totalPrice");

            // Deserialize the cart item list from json.
            JSONArray cartItemListInJson = jsonData.getJSONArray("cartItems");

            int cartItemsLength = cartItemListInJson.length();
            int totalItems = 0;
            List<CartItemDto> cartItems = new ArrayList<>(cartItemsLength);

            for (byte index = 0; index < cartItemsLength; index++)
            {
                JSONObject cartItemInJson = cartItemListInJson.getJSONObject(index);

                DeserializeResult<CartItemDto> deserializeCartItemResult = CartItemDto.DeserializeFromJson(cartItemInJson);

                if (!deserializeCartItemResult.isSuccess)
                {
                    return DeserializeResult.failed();
                }

                CartItemDto cartItem = deserializeCartItemResult.value;
                cartItems.add(cartItem);

                totalItems += cartItem.quantity;
            }

            ShoppingCartDto shoppingCartDto = new ShoppingCartDto(cartItems);

            shoppingCartDto.cartId = cartId;
            shoppingCartDto.totalPrice = totalPrice;
            shoppingCartDto.totalItems = totalItems;

            return DeserializeResult.success(shoppingCartDto);
        }
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
