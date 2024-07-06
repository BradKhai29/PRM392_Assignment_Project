package com.example.prm392_assignment_project.models.input_validations;

public class InputValidationResult {
    public boolean isValid;
    public String errorMessage;

    private InputValidationResult(boolean isValid) {
        this.isValid = isValid;
        errorMessage = null;
    }

    private InputValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public static InputValidationResult failed(String errorMessage) {
        return new InputValidationResult(false, errorMessage);
    }

    public static InputValidationResult failed() {
        return new InputValidationResult(false);
    }

    public static InputValidationResult success() {
        return new InputValidationResult(true);
    }
}
