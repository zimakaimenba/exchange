package com.exchangeinfomanager.commonlib;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CommonUtility {

	public CommonUtility() 
	{
		
	}
	/*
	 * 此方法返回结果是1(2011年第1周)。如果加上一句calendar.setMinimalDaysInFirstWeek(7);返回结果是52（2010年第52周）
	 * 
	 *  */
	public static int getWeekNumber(Date testdate)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		calendar.setTime(testdate);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	
	public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }
	
	/** 
	* 取得指定日期所在周的第一天
	* */ 
	public static Date getFirstDayOfWeek(Date date)
	{
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		return c.getTime ();
	}

	/** 
	* 取得指定日期所在周的最后一天
	* 	*/ 
	public static Date getLastDayOfWeek(Date date) 
	{
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
		return c.getTime();
	}
}
