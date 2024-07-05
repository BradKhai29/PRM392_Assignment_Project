package com.example.prm392_assignment_project.views.view_callbacks;

import com.example.prm392_assignment_project.views.view_callbacks.support_enums.UpdateCartType;
import com.example.prm392_assignment_project.views.view_callbacks.support_models.UpdateCartActionDetail;

public interface IOnUpdateCartCallback {
    void resolve(UpdateCartActionDetail updateDetail);
}
