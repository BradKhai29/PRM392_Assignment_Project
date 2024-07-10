package com.example.prm392_assignment_project.helpers;

import android.os.ParcelUuid;

public class DateTimeHelper
{
    public static final String DATE_TIME_SEPARATOR = "T";
    public static final String DATE_SEPARATOR = "-";
    public static final String TIME_SEPARATOR = ":";
    public static String normalize(String dateTimeString)
    {
        if (String.valueOf(dateTimeString).isEmpty())
        {
            return null;
        }

        if (!dateTimeString.contains(DATE_TIME_SEPARATOR))
        {
            return null;
        }

        String[] dateAndTime = dateTimeString.split(DATE_TIME_SEPARATOR);
        String datePart = dateAndTime[0];
        String timePart = dateAndTime[1];

        StringBuilder normalizedDateTimeBuilder = new StringBuilder();
        normalizeTimePart(timePart, normalizedDateTimeBuilder);
        normalizeDatePart(datePart, normalizedDateTimeBuilder);

        return normalizedDateTimeBuilder.toString();
    }

    private static void normalizeDatePart(String datePart, StringBuilder builder)
    {
        datePart = datePart.replaceAll(DATE_SEPARATOR, "/");
        builder.append(datePart);
    }

    private static void normalizeTimePart(String timePart, StringBuilder builder)
    {
        int lastIndexOfTimeSeparator = timePart.lastIndexOf(TIME_SEPARATOR);
        timePart = timePart.substring(0, lastIndexOfTimeSeparator);
        builder.append(timePart).append(" - ");
    }
}
