package com.example.prm392_assignment_project.models.dtos.shoppingcarts;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.base.IApiInputDto;

import org.json.JSONException;
import org.json.JSONObject;

public class CartItemDto implements IApiInputDto
{
    public String cartId;
    public String productId;
    public String productName;
    public int unitPrice;
    public String imageUrl;
    public int quantity;

    public int subTotal() {
        return quantity * unitPrice;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject cartItemInJson = new JSONObject();

        cartItemInJson.put("cartId", cartId);
        cartItemInJson.put("productId", productId);
        cartItemInJson.put("quantity", quantity);

        return cartItemInJson;
    }

    public static CartItemDto getInstanceToCallApi(String cartId, String productId, int quantity)
    {
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

    public static DeserializeResult<CartItemDto> DeserializeFromJson(JSONObject jsonData)
    {
        try
        {
            String productId = jsonData.getString("productId");
            String productName = jsonData.getString("productName");
            int unitPrice = jsonData.getInt("unitPrice");
            int quantity = jsonData.getInt("quantity");
            String imageUrl = jsonData.getString("imageUrl");

            // Init the cart item.
            CartItemDto cartItemDto = new CartItemDto();
            cartItemDto.productId = productId;
            cartItemDto.productName = productName;
            cartItemDto.unitPrice = unitPrice;
            cartItemDto.quantity = quantity;
            cartItemDto.imageUrl = imageUrl;

            return DeserializeResult.success(cartItemDto);
        }
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
