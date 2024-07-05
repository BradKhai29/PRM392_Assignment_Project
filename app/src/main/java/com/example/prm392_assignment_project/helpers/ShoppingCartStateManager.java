package com.example.prm392_assignment_project.helpers;

import android.content.Context;

import com.example.prm392_assignment_project.models.shoppingcarts.ShoppingCartDto;

public class ShoppingCartStateManager {
    private static SharedPreferenceHelper sharedPreferenceHelper;
    private static final String SHOPPING_CART_ID_PREFERENCE_KEY = "shoppingCartId";
    private static String currentShoppingCartId;
    private static boolean loadShoppingCartSuccessFromApi = false;
    private static ShoppingCartDto shoppingCart;
    private static boolean hasChangesInState = false;

    public static void setupSharedPreferenceHelper(Context context) {
        if (sharedPreferenceHelper != null) {
            return;
        }

        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    public static void loadShoppingCartIdFromPreference() {
        if (sharedPreferenceHelper.contains(SHOPPING_CART_ID_PREFERENCE_KEY)) {
            currentShoppingCartId = sharedPreferenceHelper.getString(SHOPPING_CART_ID_PREFERENCE_KEY);
        }
    }

    public static boolean isShoppingCartPreferenceExisted() {
        return sharedPreferenceHelper.contains(SHOPPING_CART_ID_PREFERENCE_KEY);
    }

    /**
     * Set the value of input cartId into this application shared preference
     * for next time retrieval.
     * @param cartId CartId that used to get the shopping cart detail from API.
     */
    public static void setShoppingCartPreferenceValue(String cartId) throws Exception {
        if (sharedPreferenceHelper == null) {
            throw new Exception("The shopping cart helper wasn't setup yet.");
        }

        sharedPreferenceHelper.putString(SHOPPING_CART_ID_PREFERENCE_KEY, cartId);
    }
    public static void setCurrentShoppingCartId(String cartId) {
        currentShoppingCartId = cartId;
    }

    public static void clearShoppingCart() {
        shoppingCart.clear();
    }

    public static String getCurrentShoppingCartId() {
        return currentShoppingCartId;
    }

    public static void loadShoppingCartSuccess() {
        loadShoppingCartSuccessFromApi = true;
    }

    public static boolean isShoppingCartLoadSuccess() {
        return loadShoppingCartSuccessFromApi;
    }

    public static void setShoppingCart(ShoppingCartDto shoppingCartDto) {
        shoppingCart = shoppingCartDto;
    }

    public static ShoppingCartDto getShoppingCart() {
        return shoppingCart;
    }

    public static int getTotalItemsInCart() {
        return shoppingCart.getTotalItems();
    }

    public static int getTotalPrice() {
        return shoppingCart.getTotalPrice();
    }

    public static boolean hasChangesInState() {
        return hasChangesInState;
    }

    public static void addChanges() {
        hasChangesInState = true;
    }

    public static void confirmChangeInState() {
        hasChangesInState = false;
    }
}
