package com.example.prm392_assignment_project.helpers.input_validations;

import android.widget.EditText;

public class InputValidationHelper {
    static final char WHITE_SPACE = ' ';

    public static boolean isEmpty(String input) {
        return String.valueOf(input).isEmpty();
    }

    public static boolean containsLetterAndWhiteSpace(String input) {
        for (int i = 0; i < input.length(); i++) {
            char currentCharacter = input.charAt(i);

            if(!Character.isLetter(currentCharacter) && currentCharacter != WHITE_SPACE) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEmpty(EditText editText) {
        return isEmpty(editText.getText().toString());
    }

    public static boolean containsLetterAndWhiteSpace(EditText editText) {
        if (isEmpty(editText)) {
            return false;
        }

        return containsLetterAndWhiteSpace(editText.getText().toString());
    }
}
