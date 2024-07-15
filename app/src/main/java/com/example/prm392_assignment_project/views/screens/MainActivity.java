package com.example.prm392_assignment_project.views.screens;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.prm392_assignment_project.R;
import com.example.prm392_assignment_project.api_handlers.implementation.ProductApiHandler;
import com.example.prm392_assignment_project.helpers.ShoppingCartStateManager;
import com.example.prm392_assignment_project.helpers.UserAuthStateManager;
import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.products.GeneralProductInfoDto;
import com.example.prm392_assignment_project.receivers.WifiReceiver;
import com.example.prm392_assignment_project.views.fragments.BottomNavigationBar;
import com.example.prm392_assignment_project.views.recyclerviews.products.ProductItemViewAdapter;
import com.example.prm392_assignment_project.views.screens.auths.AuthActivity;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView leftSidebar;
    private Toolbar toolbar;
    private BottomNavigationBar bottomNavigationBar;
    private ProductItemViewAdapter productItemViewAdapter;
    private final List<GeneralProductInfoDto> products;
    private boolean loadProductSuccess = false;

    public MainActivity() {
        products = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserAuthStateManager.setUp(this);

        // Register WifiReceiver.
        IntentFilter checkWifiStateIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        WifiReceiver wifiReceiver = new WifiReceiver(
            this::getDataWhenHasInternetConnection,
            this::notifyUserToEnableInternet);

        registerReceiver(wifiReceiver, checkWifiStateIntentFilter);

        // Set up view section.
        setUpDrawerLayoutAndLeftSidebar();
        setUpRecyclerView();
        setUpShoppingCartManager();
        setUpBottomNavigationBarFragment();

        if (!checkInternetPermission())
        {
            requestInternetPermission();
            Toast.makeText(this, "Vui lòng cho phép app truy cập internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataWhenHasInternetConnection()
    {
        if (loadProductSuccess)
        {
            return;
        }

        // Calling api to retrieve data section.
        UserAuthStateManager.getInstance().verifyCurrentAccessToken(this::reloadLeftSidebar, this::reloadLeftSidebar);

        loadAllProductsFromApi();
    }

    private void notifyUserToEnableInternet()
    {
        Toast.makeText(this, "Vui lòng kết nối Internet để sử dụng app", Toast.LENGTH_SHORT).show();
    }

    private boolean checkInternetPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestInternetPermission() {
        final int INTERNET_PERMISSION_CODE = 1;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
    }

    private void setUpDrawerLayoutAndLeftSidebar()
    {
        // Bind the components from the layout.
        drawerLayout = findViewById(R.id.main);
        leftSidebar = findViewById(R.id.left_sidebar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nut Shop");

        // Set up on click listeners for action tool bar.
        setSupportActionBar(toolbar);
        leftSidebar.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_left_sidebar_description,
            R.string.close_left_sidebar_description);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        showNotSignedInMenuGroup();
        setOnMenuItemClickListenerForLeftSidebar();
    }

    private void reloadLeftSidebar()
    {
        View headerView = leftSidebar.getHeaderView(0);
        if (headerView == null)
        {
            return;
        }

        UserAuthStateManager userAuthStateManager = UserAuthStateManager.getInstance();
        if (!userAuthStateManager.isAccessTokenStillValid())
        {
            LinearLayout userFullNameSection = headerView.findViewById(R.id.user_full_name_section);
            userFullNameSection.setVisibility(View.INVISIBLE);
            ImageView userAvatar = headerView.findViewById(R.id.userAvatar);
            userAvatar.setImageResource(R.drawable.ic_user_2);

            showNotSignedInMenuGroup();
            return;
        }

        // Get components to update UI.
        ImageView userAvatar = headerView.findViewById(R.id.userAvatar);
        TextView tvUserFullName = headerView.findViewById(R.id.userFullName);
        LinearLayout userFullNameSection = headerView.findViewById(R.id.user_full_name_section);

        // Populate info into components.
        String avatarUrl = userAuthStateManager.getUserAvatarUrl();
        String fullName = userAuthStateManager.getUserFullName();

        Picasso.get().load(avatarUrl).into(userAvatar);
        tvUserFullName.setText(fullName);
        userFullNameSection.setVisibility(View.VISIBLE);
        showSignedInMenuGroup();
    }

    private void showNotSignedInMenuGroup()
    {
        Menu leftSidebarMenu = leftSidebar.getMenu();

        leftSidebarMenu.setGroupVisible(R.id.signed_in_group, false);
        leftSidebarMenu.setGroupVisible(R.id.not_signed_in_group, true);
    }

    private void showSignedInMenuGroup()
    {
        Menu leftSidebarMenu = leftSidebar.getMenu();

        leftSidebarMenu.setGroupVisible(R.id.signed_in_group, true);
        leftSidebarMenu.setGroupVisible(R.id.not_signed_in_group, false);
    }

    private void setOnMenuItemClickListenerForLeftSidebar()
    {
        leftSidebar.setNavigationItemSelectedListener(menuItem ->
        {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.login)
            {
                Intent goToAuthIntent = new Intent(this, AuthActivity.class);
                this.startActivity(goToAuthIntent);
            }

            if (itemId == R.id.logout)
            {
                UserAuthStateManager.getInstance().logoutUser(this::reloadLeftSidebar);
                drawerLayout.close();
            }

            return false;
        });
    }

    private void setUpBottomNavigationBarFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        bottomNavigationBar = new BottomNavigationBar(this);

        // Constants that used in app.
        String BOTTOM_NAVIGATION_BAR_FRAGMENT_TAG = "bottom_nav_bar";
        fragmentTransaction.add(R.id.fragment_container_view, bottomNavigationBar, BOTTOM_NAVIGATION_BAR_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (ShoppingCartStateManager.hasChangesInState())
        {
            bottomNavigationBar.loadShoppingCartBadge();
            ShoppingCartStateManager.confirmChangeInState();
        }

        if (UserAuthStateManager.getInstance().isAccessTokenStillValid())
        {
            reloadLeftSidebar();
        }
    }

    ///region View components and dependency setup section.
    private void setUpShoppingCartManager()
    {
        // Set up the shopping cart helper for later operation.
        ShoppingCartStateManager.setupSharedPreferenceHelper(MainActivity.this);
    }

    private void setUpRecyclerView()
    {
        // Private fields and components section.
        RecyclerView productItemRecyclerView = findViewById(R.id.productListRecyclerView);

        // Configure the layout manager for recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productItemRecyclerView.setLayoutManager(linearLayoutManager);

        productItemViewAdapter = new ProductItemViewAdapter(MainActivity.this, products, this::handleOnAddToCart);
        productItemRecyclerView.setAdapter(productItemViewAdapter);
    }
    ///endregion

    ///region Private methods.
    private void loadAllProductsFromApi()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        Thread loadAllProductsThread = new Thread(() ->
        {
            try {
                handler.post(() ->
                {
                    // Calling api using product controller.
                    ProductApiHandler productApiHandler = new ProductApiHandler(this);
                    productApiHandler.getAllProducts(
                    4,
                        this::handleLoadAllProductsSuccess,
                        this::handleLoadAllProductsFailed);
                });
            } catch (Exception e) {

            }
        });

        loadAllProductsThread.start();
    }
    ///endregion

    private void handleOnAddToCart()
    {
        bottomNavigationBar.loadShoppingCartBadge();
    }

    // Load all product handler section.
    private void handleLoadAllProductsSuccess(JSONObject response)
    {
        DeserializeResult<ApiResponse> result = ApiResponse.DeserializeFromJson(response);

        if (!result.isSuccess)
        {
            return;
        }

        ApiResponse apiResponse = result.value;

        try
        {
            JSONArray productListInJson = apiResponse.getBodyAsJsonArray();
            int productListLength = productListInJson.length();

            for (byte index = 0; index < productListLength; index++)
            {
                JSONObject productJson = productListInJson.getJSONObject(index);

                DeserializeResult<GeneralProductInfoDto> deserializeResult
                    = GeneralProductInfoDto.DeserializeFromJson(productJson);

                if (deserializeResult.isSuccess)
                {
                    GeneralProductInfoDto productItem = deserializeResult.value;

                    products.add(productItem);
                    productItemViewAdapter.notifyItemInserted(index);
                }
            }

            loadProductSuccess = true;
        }
        catch (Exception exception)
        {
            Toast.makeText(this, "Có lỗi xảy ra khi lấy sản phẩm", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoadAllProductsFailed(VolleyError error)
    {
        Toast.makeText(this, "Không gọi được API để lấy sản phẩm", Toast.LENGTH_LONG).show();
    }
}