package com.example.prm392_assignment_project.models.commons;

public class DeserializeResult<TValue>
{
    public boolean isSuccess;
    public TValue value;

    public static DeserializeResult failed()
    {
        DeserializeResult failedResult = new DeserializeResult();
        failedResult.isSuccess = false;

        return failedResult;
    }

    public static <TValue> DeserializeResult<TValue> success(TValue value)
    {
        DeserializeResult<TValue> successResult = new DeserializeResult<>();
        successResult.isSuccess = true;
        successResult.value = value;

        return successResult;
    }
}
