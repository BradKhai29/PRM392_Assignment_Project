package com.example.prm392_assignment_project.models.dtos.categories;

import com.example.prm392_assignment_project.models.commons.DeserializeResult;

import org.json.JSONObject;

public class CategoryDetailDto
{
    public final String id;
    public final String name;

    private CategoryDetailDto(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public static DeserializeResult<CategoryDetailDto> DeserializeFromJson(JSONObject jsonData)
    {
        try
        {
            String categoryId = jsonData.getString("id");
            String categoryName = jsonData.getString("name");

            CategoryDetailDto categoryDetailDto = new CategoryDetailDto(categoryId, categoryName);
            return DeserializeResult.success(categoryDetailDto);
        }
        catch (Exception exception)
        {
            return DeserializeResult.failed();
        }
    }
}
