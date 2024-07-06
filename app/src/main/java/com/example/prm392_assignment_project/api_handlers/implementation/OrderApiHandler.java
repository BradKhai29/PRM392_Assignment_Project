package com.example.prm392_assignment_project.api_handlers.implementation;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.prm392_assignment_project.api_handlers.base.ApiHandler;
import com.example.prm392_assignment_project.commons.requestbuilders.HttpMethod;
import com.example.prm392_assignment_project.commons.requestbuilders.RequestBuilder;
import com.example.prm392_assignment_project.models.dtos.checkouts.CheckoutDetailDto;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiFailedCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnCallApiSuccessCallback;

import org.json.JSONException;

public class OrderApiHandler extends ApiHandler {
    public static final String API_URL = BASE_URL + "/order";
    public static final String ORDER_CHECKOUT_ENDPOINT = API_URL;
    public static final String GET_ALL_ORDERS_BY_GUEST_ID_ENDPOINT = API_URL + "/guest";
    public static final String GET_ORDER_DETAIL_BY_ID_ENDPOINT = API_URL;

    private final Context context;
    private final RequestQueue requestQueue;

    public OrderApiHandler(Context context)
    {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void checkout(
        CheckoutDetailDto checkoutDetail,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback) throws JSONException
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(ORDER_CHECKOUT_ENDPOINT);

        requestBuilder.withMethod(HttpMethod.POST);
        requestBuilder.addJsonBody(checkoutDetail.toJsonObject());
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void getAllOrdersByGuestId(
        String guestId,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback)
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(GET_ALL_ORDERS_BY_GUEST_ID_ENDPOINT + "/" + guestId);

        requestBuilder.withMethod(HttpMethod.GET);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }

    public void getOrderDetailById(
        String orderId,
        IOnCallApiSuccessCallback successCallback,
        IOnCallApiFailedCallback failedCallback)
    {
        RequestBuilder requestBuilder = RequestBuilder.getInstance(GET_ORDER_DETAIL_BY_ID_ENDPOINT + "/" + orderId);

        requestBuilder.withMethod(HttpMethod.GET);
        requestBuilder.addOnSuccessCallback(successCallback);
        requestBuilder.addOnFailureCallback(failedCallback);

        JsonObjectRequest request = requestBuilder.buildJsonRequest();

        requestQueue.add(request);
    }
}
