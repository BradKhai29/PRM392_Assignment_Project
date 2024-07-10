package com.example.prm392_assignment_project.helpers.input_validations;

import android.widget.EditText;

public class PhoneNumberValidationHelper {
    public static final int MIN_LENGTH = 10;
    public static final int MAX_LENGTH = 11;

    public static boolean isValid(String inputPhoneNumber)
    {
        if (String.valueOf(inputPhoneNumber).isEmpty())
        {
            return false;
        }

        boolean isValidLength = inputPhoneNumber.length() >= MIN_LENGTH
            && inputPhoneNumber.length() <= MAX_LENGTH;

        if (!isValidLength)
        {
            return false;
        }

        boolean firstDigitIsNotZero = inputPhoneNumber.charAt(0) != '0';
        if (firstDigitIsNotZero)
        {
            return false;
        }

        for (byte i = 1; i < inputPhoneNumber.length(); i++)
        {
            char currentCharacter = inputPhoneNumber.charAt(i);

            if (!Character.isDigit(currentCharacter))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isValid(EditText editText)
    {
        return isValid(editText.getText().toString());
    }
}
