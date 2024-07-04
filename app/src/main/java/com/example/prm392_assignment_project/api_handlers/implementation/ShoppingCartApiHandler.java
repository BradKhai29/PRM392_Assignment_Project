package com.example.prm392_assignment_project.api_handlers.implementation;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm392_assignment_project.commons.requestbuilders.HttpMethod;
import com.example.prm392_assignment_project.commons.requestbuilders.RequestBuilder;
import com.example.prm392_assignment_project.api_handlers.base.ApiHandler;
import com.example.prm392_assignment_project.models.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnFailureCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnSuccessCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ShoppingCartApiHandler extends ApiHandler {
    public static final String API_URL = BASE_URL + "/cart";
    public static final String INIT_SHOPPING_CART_ENDPOINT = API_URL + "/init";
    public static final String LOAD_SHOPPING_CART_BY_ENDPOINT = API_URL;
    public static final String ADD_ITEM_TO_CART_ENDPOINT = API_URL + "/add";

    private final RequestQueue requestQueue;
    private final Context context;

    public ShoppingCartApiHandler(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void initShoppingCart(IOnSuccessCallback successCallback, IOnFailureCallback failureCallback) {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(INIT_SHOPPING_CART_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void loadShoppingCartById(String cartId, IOnSuccessCallback successCallback, IOnFailureCallback failureCallback) {
        final String apiUrl = LOAD_SHOPPING_CART_BY_ENDPOINT + "/" + cartId;
        RequestBuilder requestBuilder = RequestBuilder.getInstance(apiUrl);

        requestBuilder.withMethod(HttpMethod.GET);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void addToCart(CartItemDto cartItem, IOnSuccessCallback successCallback, IOnFailureCallback failureCallback)
            throws JSONException
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(ADD_ITEM_TO_CART_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);

        // Init the request body.
        JSONObject requestBody = cartItem.toJsonObject();
        requestBuilder.addBody(requestBody);

        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }
}
