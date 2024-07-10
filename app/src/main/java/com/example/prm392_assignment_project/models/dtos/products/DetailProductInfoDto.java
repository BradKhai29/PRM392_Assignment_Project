package com.example.prm392_assignment_project.models.dtos.products;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;
import com.example.prm392_assignment_project.models.dtos.categories.CategoryDetailDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailProductInfoDto
{
    private String id;
    private String name;
    private String category;
    private String description;
    private int unitPrice;
    private List<String> imageUrls;

    public DetailProductInfoDto(
        String productId,
        String name,
        String category,
        String description,
        int unitPrice,
        List<String> imageUrls)
    {
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

    public static DeserializeResult<DetailProductInfoDto> DeserializeFromJson(JSONObject jsonData)
    {
        try
        {
            String productId = jsonData.getString("id");
            String productName = jsonData.getString("name");
            JSONObject categoryInJson = jsonData.getJSONObject("category");
            String description = jsonData.getString("description");
            int unitPrice = jsonData.getInt("unitPrice");
            String category = CategoryDetailDto.DeserializeFromJson(categoryInJson).value.name;

            // Deserialize an array of imageUrl from json.
            JSONArray imageArray = jsonData.getJSONArray("imageUrls");
            List<String> imageUrls = new ArrayList<>(imageArray.length());
            int imageArrayLength = imageArray.length();

            for (byte i = 0; i < imageArrayLength; i++)
            {
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
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
