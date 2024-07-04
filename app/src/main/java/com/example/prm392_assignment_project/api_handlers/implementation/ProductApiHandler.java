package com.example.prm392_assignment_project.api_handlers.implementation;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm392_assignment_project.commons.requestbuilders.HttpMethod;
import com.example.prm392_assignment_project.commons.requestbuilders.RequestBuilder;
import com.example.prm392_assignment_project.api_handlers.base.ApiHandler;
import com.example.prm392_assignment_project.views.view_callbacks.IOnFailureCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnSuccessCallback;

public class ProductApiHandler extends ApiHandler {
    public static final String API_URL = BASE_URL + "/product";
    public static final String GET_ALL_PRODUCTS_ENDPOINT = API_URL + "/all";
    private final RequestQueue requestQueue;

    public ProductApiHandler(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getAllProducts(
        int totalProduct,
        IOnSuccessCallback successCallback,
        IOnFailureCallback failureCallback)
    {
        if (totalProduct <= 0) {
            totalProduct = 4;
        }

        RequestBuilder requestBuilder = RequestBuilder.getInstance(GET_ALL_PRODUCTS_ENDPOINT + "/" + totalProduct);

        requestBuilder.withMethod(HttpMethod.GET);
        requestBuilder.addOnFailureCallback(failureCallback);
        requestBuilder.addOnSuccessCallback(successCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        // Add the request to the RequestQueue to execute the request.
        requestQueue.add(request);
    }

    public void getProductDetailById(
        String productId,
        IOnSuccessCallback successCallback,
        IOnFailureCallback failureCallback)
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(API_URL + "/" + productId);

        requestBuilder.withMethod(HttpMethod.GET);
        requestBuilder.addOnFailureCallback(failureCallback);
        requestBuilder.addOnSuccessCallback(successCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        // Add the request to the RequestQueue to execute the request.
        requestQueue.add(request);
    }
}
