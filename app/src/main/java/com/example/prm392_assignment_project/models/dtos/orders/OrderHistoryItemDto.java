package com.example.prm392_assignment_project.models.dtos.orders;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONObject;

public class OrderHistoryItemDto
{
    public final String orderId;
    public final String orderCode;
    public final int totalPrice;
    public final String createdAt;

    private OrderHistoryItemDto(
        String orderId,
        String orderCode,
        int totalPrice,
        String createdAt)
    {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public static DeserializeResult<OrderHistoryItemDto> DeserializeFromJson(
        JSONObject jsonData)
    {
        try
        {
            String orderId = jsonData.getString("id");
            String orderCode = String.valueOf(jsonData.getLong("orderCode"));
            int totalPrice = jsonData.getInt("totalPrice");
            String createdAt = jsonData.getString("createdAt");

            OrderHistoryItemDto orderHistoryItemDto = new OrderHistoryItemDto(
                orderId,
                orderCode,
                totalPrice,
                createdAt);

            return DeserializeResult.success(orderHistoryItemDto);
        }
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
