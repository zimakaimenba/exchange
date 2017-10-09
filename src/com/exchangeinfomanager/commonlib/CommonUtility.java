package com.exchangeinfomanager.commonlib;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CommonUtility {

	public CommonUtility() 
	{
		
	}

	public static String formatDateYYYY_MM_DD(Date tmpdate)
	{
		try {
			SimpleDateFormat formatterhwy=new SimpleDateFormat("yyyy-MM-dd");
			return formatterhwy.format(tmpdate);
		} catch (java.lang.NullPointerException e) {
			return null;
		}
		
	}
	
	public static String formatDateYYMMDD(Date tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
	}
	public static Date formateDateYYMMDD(String datestring) 
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		Date date = null ;
		try {
			date = formatterhwy.parse(datestring);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
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
	public static int getYearNumber(Date testdate)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		calendar.setTime(testdate);
		return calendar.get(Calendar.YEAR);
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
	
	public static Date getDateFromYearAndWeek (int weeknum)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.WEEK_OF_YEAR, weeknum);        
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime(); 
	}
	
	/*
	 * 
	 */
	public static LocalDate getDateFromWeekAndYear(final int day,final int year,final int weekday) {
//	    int y = Integer.parseInt(j);
	    LocalDate date = LocalDate.of(year, 7, 1); // safer than choosing current date
	    // date = date.with(WeekFields.ISO.weekBasedYear(), y); // no longer necessary
	    date = date.with(WeekFields.ISO.weekOfWeekBasedYear(), day);
	    date = date.with(WeekFields.ISO.dayOfWeek(), weekday);

	    return date;
	}
//	public static LocalDate getMondayOfGivenWeekAndYear(int week, int year) {
//
//	    LocalDate firstMonOfFirstWeek = LocalDate.now()
//	            .with(IsoFields.WEEK_BASED_YEAR, year) // year
//	            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, 1) // First week of the year
//	            .with(ChronoField.DAY_OF_WEEK, 1); // Monday
//
//	    // Plus multi of 7
//	    return firstMonOfFirstWeek.plusDays( (week - 1) * 7);
//	}
	public static Date getDateOfSpecificMonthAgo (Date daylast,int monthnumber)
	{
    	
    	Calendar calendar =  Calendar.getInstance();
    	calendar.setTime(daylast);
    	calendar.add(calendar.MONTH,0-monthnumber);//把日期往后增加一天.整数往后推,负数往前移动
    	return calendar.getTime();
	}
	/** 
	* 取得指定日期所在周的第一天
	* */ 
	public static Date getFirstDayOfWeek(Date date)
	{
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
//		c.add(Calendar.DAY_OF_YEAR,-10);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		return c.getTime ();
	}

	/** 
	* 取得指定日期所在周的最后一天,目前最后一天设置为星期六
	* 	*/ 
	public static Date getLastDayOfWeek(Date date) 
	{
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
//		c.add(Calendar.DAY_OF_YEAR,-10);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 5); // Saterday
		return c.getTime();
	}
	
	public static Date formateStringToDate(String tmpdate) 
	{
		DateFormat format = null;
		System.out.println(tmpdate);
		if(tmpdate.length() > 10)
			 format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		else if(tmpdate.length() == 10)
			format = new SimpleDateFormat("yyyy-MM-dd");
		else if(tmpdate.length() == 8)
			format = new SimpleDateFormat("yyyyMMdd");
		
		Date date = null;
		try {
//			System.out.println("Data need to be parsed is" + tmpdate);
			date = format.parse(tmpdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return date;
	}
	/*
	 * 
	 */
//	public Date getMondayOfGivenWeekAndYear(int week, int year) 
//	{
//		Calendar cal = Calendar.getInstance();
//
//		int weekday = cal.get(Calendar.DAY_OF_WEEK);
//		int days = (Calendar.SATURDAY - weekday + 2) % 7;
//
//		cal.add(Calendar.DAY_OF_YEAR, days);
//
//		cal.add(Calendar.DAY_OF_MONTH, -7);
//		cal.add(Calendar.DAY_OF_MONTH, -7);
//	}
}
