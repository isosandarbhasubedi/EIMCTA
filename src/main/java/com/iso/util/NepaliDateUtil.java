package com.iso.util;



import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NepaliDateUtil {

    private static final LocalDate AD_REFERENCE = LocalDate.of(1943, 4, 14);

    private static final int[][] BS_CALENDAR = {
        // Each row = one BS year starting from 2000
        // Format: {year, month1_days, month2_days, ... month12_days}
        {2000,30,32,31,32,31,30,30,30,29,30,29,31},
        {2001,31,31,32,31,31,31,30,29,30,29,30,30},
        {2002,31,31,32,32,31,30,30,29,30,29,30,30},
        {2003,31,32,31,32,31,30,30,30,29,29,30,31},
        {2004,30,32,31,32,31,30,30,30,29,30,29,31},
        {2005,31,31,32,31,31,31,30,29,30,29,30,30},
        {2006,31,31,32,32,31,30,30,29,30,29,30,30},
        {2007,31,32,31,32,31,30,30,30,29,29,30,31},
        {2008,31,31,31,32,31,31,29,30,30,29,29,31},
        {2009,31,31,32,31,31,31,30,29,30,29,30,30},
        {2010,31,31,32,32,31,30,30,29,30,29,30,30},
        // 🔥 Continue dataset until 2090
    };

    public static String convertAdToBs(LocalDate adDate) {

        long totalDays = ChronoUnit.DAYS.between(AD_REFERENCE, adDate);

        int bsYear = 2000;
        int month = 1;
        int day = 1;

        for (int i = 0; i < BS_CALENDAR.length; i++) {

            int[] yearData = BS_CALENDAR[i];

            for (int m = 1; m <= 12; m++) {

                int daysInMonth = yearData[m];

                if (totalDays < daysInMonth) {
                    day += totalDays;
                    return format(bsYear, m, day);
                } else {
                    totalDays -= daysInMonth;
                }
            }

            bsYear++;
        }

        throw new RuntimeException("Date out of supported BS range.");
    }

    private static String format(int year, int month, int day) {
        return year + "-" +
                String.format("%02d", month) + "-" +
                String.format("%02d", day);
    }
}
