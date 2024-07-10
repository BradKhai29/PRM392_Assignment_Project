package com.example.prm392_assignment_project.helpers.input_validations;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidationHelper
{
    public  static final String EMAIL_VALIDATION_REGEXP_PATTERN = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

    public static boolean isValid(String inputEmail)
    {
        if (String.valueOf(inputEmail).isEmpty())
        {
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_VALIDATION_REGEXP_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputEmail);

        return matcher.matches();
    }

    public static boolean isValid(EditText editText)
    {
        return isValid(editText.getText().toString());
    }
}
