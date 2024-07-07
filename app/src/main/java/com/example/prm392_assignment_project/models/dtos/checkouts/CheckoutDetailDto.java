package com.example.prm392_assignment_project.models.dtos.checkouts;

import com.example.prm392_assignment_project.models.dtos.base.IApiInputDto;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckoutDetailDto implements IApiInputDto {
    public String cartId;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String deliveryAddress;
    public String orderNote;
    public final boolean cashOnDelivery = true;

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject checkoutDetailInJson = new JSONObject();

        checkoutDetailInJson.put("cartId", cartId);
        checkoutDetailInJson.put("firstName", firstName);
        checkoutDetailInJson.put("lastName", lastName);
        checkoutDetailInJson.put("email", email);
        checkoutDetailInJson.put("phoneNumber", phoneNumber);
        checkoutDetailInJson.put("deliveryAddress", deliveryAddress);
        checkoutDetailInJson.put("orderNote", orderNote);
        checkoutDetailInJson.put("cashOnDelivery", cashOnDelivery);

        return checkoutDetailInJson;
    }
}
