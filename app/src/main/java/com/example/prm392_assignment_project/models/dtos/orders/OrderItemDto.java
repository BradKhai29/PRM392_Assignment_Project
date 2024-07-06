package com.example.prm392_assignment_project.models.dtos.orders;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONObject;

public class OrderItemDto
{
    public final String productName;
    public final int sellingQuantity;
    public final int sellingPrice;
    public final int subTotal;

    private OrderItemDto(
        String productName,
        int sellingQuantity,
        int sellingPrice,
        int subTotal)
    {
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.sellingQuantity = sellingQuantity;
        this.subTotal = subTotal;
    }

    public static DeserializeResult<OrderItemDto> DeserializeFromJson(JSONObject jsonData)
    {
        try
        {
            String productName = jsonData.getString("productName");
            int sellingPrice = jsonData.getInt("sellingPrice");
            int sellingQuantity = jsonData.getInt("sellingQuantity");
            int subTotal = jsonData.getInt("subTotal");

            OrderItemDto orderItemDto = new OrderItemDto(
                productName,
                sellingQuantity,
                sellingPrice,
                subTotal);

            return DeserializeResult.success(orderItemDto);
        }
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
