package com.example.prm392_assignment_project.views.view_callbacks.support_models;

import com.example.prm392_assignment_project.views.view_callbacks.support_enums.UpdateCartType;

public class UpdateCartActionDetail {
    public UpdateCartType updateCartType;
    public String updatedCartItemId;

    public static UpdateCartActionDetail getInstance(UpdateCartType updateCartType, String updatedCartItemId) {
        UpdateCartActionDetail updateCartActionDetail = new UpdateCartActionDetail();

        updateCartActionDetail.updateCartType = updateCartType;
        updateCartActionDetail.updatedCartItemId = updatedCartItemId;

        return updateCartActionDetail;
    }
}
