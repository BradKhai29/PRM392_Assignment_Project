package com.example.prm392_assignment_project.models.shoppingcarts;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONException;
import org.json.JSONObject;

public class CartItemDto {
    public String cartId;
    public String productId;
    public String productName;
    public int unitPrice;
    public String imageUrl;
    public int quantity;

    public int subTotal() {
        return quantity * unitPrice;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject cartItemInJson = new JSONObject();

        cartItemInJson.put("cartId", cartId);
        cartItemInJson.put("productId", productId);
        cartItemInJson.put("quantity", quantity);

        return cartItemInJson;
    }

    public static CartItemDto getInstanceToCallApi(String cartId, String productId, int quantity) {
        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.cartId = cartId;
        cartItemDto.productId = productId;
        cartItemDto.quantity = quantity;

        return cartItemDto;
    }

    public static CartItemDto getInstanceToAdd(
        String cartId,
        String productId,
        String productName,
        int unitPrice,
        String imageUrl,
        int quantity) {
        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.cartId = cartId;
        cartItemDto.productId = productId;
        cartItemDto.productName = productName;
        cartItemDto.unitPrice = unitPrice;
        cartItemDto.imageUrl = imageUrl;
        cartItemDto.quantity = quantity;

        return cartItemDto;
    }

    public static DeserializeResult<CartItemDto> DeserializeFromJson(JSONObject cartItemInJson) {
        try{
            String productId = cartItemInJson.getString("productId");
            String productName = cartItemInJson.getString("productName");
            int unitPrice = cartItemInJson.getInt("unitPrice");
            int quantity = cartItemInJson.getInt("quantity");
            String imageUrl = cartItemInJson.getString("imageUrl");

            // Init the cart item.
            CartItemDto cartItemDto = new CartItemDto();
            cartItemDto.productId = productId;
            cartItemDto.productName = productName;
            cartItemDto.unitPrice = unitPrice;
            cartItemDto.quantity = quantity;
            cartItemDto.imageUrl = imageUrl;

            return DeserializeResult.success(cartItemDto);
        }
        catch (Exception exception) {
            return DeserializeResult.failed();
        }
    }
}
