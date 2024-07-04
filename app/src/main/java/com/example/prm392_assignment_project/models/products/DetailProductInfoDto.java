package com.example.prm392_assignment_project.models.products;

import com.example.prm392_assignment_project.models.commons.ApiResponse;
import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailProductInfoDto {
    private String id;
    private String name;
    private String category;
    private String description;
    private int unitPrice;
    private List<String> imageUrls;

    public DetailProductInfoDto(String productId, String name, String category, String description, int unitPrice, List<String> imageUrls) {
        this.id = productId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.unitPrice = unitPrice;
        this.imageUrls = imageUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public static DeserializeResult<DetailProductInfoDto> DeserializeFromJson(JSONObject jsonObject)
    {
        try {
            String productId = jsonObject.getString("id");
            String productName = jsonObject.getString("name");
            String category = jsonObject.getJSONObject("category").toString();
            String description = jsonObject.getString("description");
            int unitPrice = jsonObject.getInt("unitPrice");

            JSONArray imageArray = jsonObject.getJSONArray("imageUrls");
            List<String> imageUrls = new ArrayList<>(imageArray.length());
            int imageArrayLength = imageArray.length();

            for (byte i = 0; i < imageArrayLength; i++) {
                String imageUrl = imageArray.getString(i);
                imageUrls.add(imageUrl);
            }

            DetailProductInfoDto productDetail = new DetailProductInfoDto(
                productId,
                productName,
                category,
                description,
                unitPrice,
                imageUrls);

            return DeserializeResult.success(productDetail);
        }
        catch (Exception exception) {
            return DeserializeResult.failed();
        }
    }
}
