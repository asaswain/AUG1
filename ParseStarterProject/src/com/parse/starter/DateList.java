package com.parse.starter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// simple utility class to store method to generate a list of dates
public class DateList {

    // build a list of dates centered around the current date'
    public static String[] getDateList(int listSize) {

        String dateList[] = new String[listSize];
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -((listSize - 1) / 2));
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        for (int i = 0; i < listSize; i++)
        {
            dateList[i] = df.format(c.getTime());
            c.add(Calendar.DATE, 1);
        }

        return dateList;
    }
}
