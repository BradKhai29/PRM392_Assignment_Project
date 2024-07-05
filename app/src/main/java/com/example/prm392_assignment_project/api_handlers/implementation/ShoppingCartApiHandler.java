package com.example.prm392_assignment_project.api_handlers.implementation;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm392_assignment_project.commons.requestbuilders.HttpMethod;
import com.example.prm392_assignment_project.commons.requestbuilders.HttpRequestHeader;
import com.example.prm392_assignment_project.commons.requestbuilders.RequestBuilder;
import com.example.prm392_assignment_project.api_handlers.base.ApiHandler;
import com.example.prm392_assignment_project.models.shoppingcarts.CartItemDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ShoppingCartApiHandler extends ApiHandler {
    public static final String API_URL = BASE_URL + "/cart";
    public static final String INIT_SHOPPING_CART_ENDPOINT = API_URL + "/init";
    public static final String LOAD_SHOPPING_CART_BY_ENDPOINT = API_URL;
    public static final String ADD_ITEM_TO_CART_ENDPOINT = API_URL + "/add";
    public static final String DECREASE_CART_ITEM_QUANTITY_ENDPOINT = API_URL + "/decrease";
    public static final String REMOVE_CART_ITEM_QUANTITY_ENDPOINT = API_URL;

    private final RequestQueue requestQueue;
    private final Context context;

    public ShoppingCartApiHandler(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void initShoppingCart(IOnCallApiSuccessCallback successCallback, IOnCallApiFailedCallback failureCallback) {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(INIT_SHOPPING_CART_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void loadShoppingCartById(String cartId, IOnCallApiSuccessCallback successCallback, IOnCallApiFailedCallback failureCallback) {
        final String apiUrl = LOAD_SHOPPING_CART_BY_ENDPOINT + "/" + cartId;
        RequestBuilder requestBuilder = RequestBuilder.getInstance(apiUrl);

        requestBuilder.withMethod(HttpMethod.GET);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void addToCart(
        CartItemDto cartItem,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failureCallback) throws JSONException
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(ADD_ITEM_TO_CART_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);

        // Init the request body.
        JSONObject requestBody = cartItem.toJsonObject();
        requestBuilder.addJsonBody(requestBody);

        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void decreaseCartItemQuantity(
        CartItemDto cartItemToDecrease,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failureCallback) throws JSONException
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(DECREASE_CART_ITEM_QUANTITY_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.PUT);

        // Init the request body.
        JSONObject requestBody = cartItemToDecrease.toJsonObject();
        requestBuilder.addJsonBody(requestBody);

        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void removeCartItem(
        String cartId,
        String productId,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failureCallback)
    {
        final String apiUrl = REMOVE_CART_ITEM_QUANTITY_ENDPOINT + "/" + cartId + "/" + productId;
        RequestBuilder requestBuilder = RequestBuilder.getInstance(apiUrl);

        requestBuilder.withMethod(HttpMethod.DELETE);
        requestBuilder.addRequestHeader(HttpRequestHeader.ContentTypeJson());
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failureCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }
}
