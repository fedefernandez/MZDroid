package com.projectsexception.mzdroid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.projectsexception.mzdroid.R;

public class CalendarUtil {
    
    private static final Map<Integer, Integer> CALENDAR_MZ_DAYS;
    private static final Map<Integer, Integer> MZ_DAYS_TITLES;
    private static final Map<Integer, Integer> CALENDAR_MONTHS;
    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("yyyy_MM_dd");
    
    static {
        CALENDAR_MZ_DAYS = new HashMap<Integer, Integer>();
        CALENDAR_MZ_DAYS.put(Calendar.MONDAY, 1);
        CALENDAR_MZ_DAYS.put(Calendar.TUESDAY, 2);
        CALENDAR_MZ_DAYS.put(Calendar.WEDNESDAY, 3);
        CALENDAR_MZ_DAYS.put(Calendar.THURSDAY, 4);
        CALENDAR_MZ_DAYS.put(Calendar.FRIDAY, 5);
        CALENDAR_MZ_DAYS.put(Calendar.SATURDAY, 6);
        CALENDAR_MZ_DAYS.put(Calendar.SUNDAY, 7);
        CALENDAR_MONTHS = new HashMap<Integer, Integer>();
        CALENDAR_MONTHS.put(Calendar.JANUARY, 1);
        CALENDAR_MONTHS.put(Calendar.FEBRUARY, 2);
        CALENDAR_MONTHS.put(Calendar.MARCH, 3);
        CALENDAR_MONTHS.put(Calendar.APRIL, 4);
        CALENDAR_MONTHS.put(Calendar.MAY, 5);
        CALENDAR_MONTHS.put(Calendar.JUNE, 6);
        CALENDAR_MONTHS.put(Calendar.JULY, 7);
        CALENDAR_MONTHS.put(Calendar.AUGUST, 8);
        CALENDAR_MONTHS.put(Calendar.SEPTEMBER, 9);
        CALENDAR_MONTHS.put(Calendar.OCTOBER, 10);
        CALENDAR_MONTHS.put(Calendar.NOVEMBER, 11);
        CALENDAR_MONTHS.put(Calendar.DECEMBER, 12);
        MZ_DAYS_TITLES = new HashMap<Integer, Integer>();
        MZ_DAYS_TITLES.put(1, R.string.day_1);
        MZ_DAYS_TITLES.put(2, R.string.day_2);
        MZ_DAYS_TITLES.put(3, R.string.day_3);
        MZ_DAYS_TITLES.put(4, R.string.day_4);
        MZ_DAYS_TITLES.put(5, R.string.day_5);
        MZ_DAYS_TITLES.put(6, R.string.day_6);
        MZ_DAYS_TITLES.put(7, R.string.day_7);
    }
    
    public static int calendarToMZDay(int calendarWeekDay) {
        return CALENDAR_MZ_DAYS.get(calendarWeekDay);
    }
    
    public static int mzToCalendarDay(int mzDay) {
        for (Integer calendarWeekDay : CALENDAR_MZ_DAYS.keySet()) {
            if (CALENDAR_MZ_DAYS.get(calendarWeekDay) == mzDay) {
                return calendarWeekDay;
            }
        }
        return Calendar.SUNDAY;
    }
    
    public static int calendarToMonth(int calendarMonth) {
        return CALENDAR_MONTHS.get(calendarMonth);
    }
    
    public static int mzDayTitle(int mzDay) {
        return MZ_DAYS_TITLES.get(mzDay);
    }
    
    public static String formatWeekDate(Date weekDate) {
        return WEEK_FORMAT.format(weekDate);
    }
    
    public static Date parseWeekDate(String weekDate) {
        try {
            return WEEK_FORMAT.parse(weekDate);
        } catch (ParseException e) {
            return new Date();
        }
    }
    
    public static String calculateWeek() {
        Calendar c = GregorianCalendar.getInstance();
        while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        return formatWeekDate(c.getTime());
    }
    
    public static Date parseWeekAndDay(String week, int day) {
        Calendar calendar = GregorianCalendar.getInstance();
        try {
            calendar.setTime(WEEK_FORMAT.parse(week));
            while (day > 1) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                day--;
            }
            return calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
