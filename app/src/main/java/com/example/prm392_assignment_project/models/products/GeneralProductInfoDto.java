package com.example.prm392_assignment_project.models.products;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeneralProductInfoDto {
    private String id;
    private String name;
    private String category;
    private int unitPrice;
    private List<String> imageUrls;

    public GeneralProductInfoDto(String productId, String name, String category, int unitPrice, List<String> imageUrls) {
        this.id = productId;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.imageUrls = imageUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public static DeserializeResult<GeneralProductInfoDto> DeserializeFromJson(JSONObject jsonObject)
    {
        try {
            String productId = jsonObject.getString("id");
            String productName = jsonObject.getString("name");
            String category = jsonObject.getString("category");
            int unitPrice = jsonObject.getInt("unitPrice");

            JSONArray imageJsonArray = jsonObject.getJSONArray("imageUrls");
            List<String> imageUrls = new ArrayList<>(imageJsonArray.length());
            int imageArrayLength = imageJsonArray.length();

            for (byte i = 0; i < imageArrayLength; i++) {
                String imageUrl = imageJsonArray.getString(i);
                imageUrls.add(imageUrl);
            }

            GeneralProductInfoDto response = new GeneralProductInfoDto(
                productId,
                productName,
                category,
                unitPrice,
                imageUrls);

            return DeserializeResult.success(response);
        }
        catch (Exception exception) {
            return DeserializeResult.failed();
        }
    }
}
