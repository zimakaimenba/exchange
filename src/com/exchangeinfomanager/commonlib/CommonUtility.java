package com.exchangeinfomanager.commonlib;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class CommonUtility {

	public static Double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    try {
	    	BigDecimal bd = BigDecimal.valueOf(value);
	    	bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
	    } catch ( java.lang.NumberFormatException e) {
	    	return null;
	    }
	}
	/*
	 * 
	 */
	public static LocalDate getSettingRangeDate (LocalDate curselectdate, String rangelevel)
	{
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		SystemConfigration sysconfig = SystemConfigration.getInstance();
		LocalDate requirestart = null ;
		if(width == 2560) {
			if(rangelevel.toLowerCase().equals("basic"))
				requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 4,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
			else if(rangelevel.toLowerCase().equals("middle"))
				requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(2*sysconfig.banKuaiFengXiMonthRange() + 4,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
			else if(rangelevel.toLowerCase().equals("large"))
				requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(3,ChronoUnit.YEARS).with(DayOfWeek.MONDAY);			
		} else {
			if(rangelevel.toLowerCase().equals("basic"))
				requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() ,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
			else if(rangelevel.toLowerCase().equals("middle"))
				requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(2*sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
			else if(rangelevel.toLowerCase().equals("large"))
				requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(3,ChronoUnit.YEARS).with(DayOfWeek.MONDAY);
		}
		
		return requirestart;
	}
	
	public static String formatDateYYYY_MM_DD_HHMMSS(LocalDateTime tmpdate)
	{
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return tmpdate.format(formatter);
		} catch (java.lang.NullPointerException e) {
			return null;
		}
		
	}
	public static String formatDateYYYY_MM_DD(LocalDate tmpdate)
	{
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return tmpdate.format(formatter);
		} catch (java.lang.NullPointerException e) {
			return null;
		}
		
	}
	
	public static String formatDateYYMMDD(LocalDate tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
	}
//	public static Date formateDateYYMMDD(String datestring) 
//	{
//		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
//		Date date = null ;
//		try {
//			date = formatterhwy.parse(datestring);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return date;
//	}
	public static LocalDate formateStringToDate(String date) 
	{
		DateTimeFormatter formatter = null;
		if(date.length() > 10)
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		else if(date.length() == 10)
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		else if(date.length() == 8 && !date.contains("-") )
			formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		else if(date.length() == 8 && date.contains("-") )
			formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
		
		LocalDate localdate = null;
		try {
			localdate = LocalDate.parse(date, formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return localdate;
	}
	/*
	 * 此方法返回结果是1(2011年第1周)。如果加上一句calendar.setMinimalDaysInFirstWeek(7);返回结果是52（2010年第52周）
	 * 
	 *  */
//	public static int getWeekNumber(Date testdate)
//	{
//		Calendar calendar = Calendar.getInstance();
//		calendar.setFirstDayOfWeek(Calendar.MONDAY);
//
//		calendar.setTime(testdate);
//		return calendar.get(Calendar.WEEK_OF_YEAR);
//	}
//	public static int getYearNumber(Date testdate)
//	{
//		Calendar calendar = Calendar.getInstance();
//		calendar.setFirstDayOfWeek(Calendar.MONDAY);
//
//		calendar.setTime(testdate);
//		return calendar.get(Calendar.YEAR);
//	} 
	public static Boolean isInSameWeek (LocalDate date1,LocalDate date2)
	{
		int year1 = date1.getYear();
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		int weeknumber1 = date1.get(weekFields.weekOfWeekBasedYear());
		
		int year2 = date2.getYear();
		int weeknumber2 = date2.get(weekFields.weekOfWeekBasedYear());
		
		if(year1 == year2 && weeknumber1 == weeknumber2)
			return true;
		else 
			return false;

	}
	
//	public static Date getDateFromYearAndWeek (int weeknum)
//	{
//		SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.WEEK_OF_YEAR, weeknum);        
//		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//		return cal.getTime(); 
//	}
//	
//	/*
//	 * 
//	 */
//	public static LocalDate getDateFromWeekAndYear(final int day,final int year,final int weekday) {
////	    int y = Integer.parseInt(j);
//	    LocalDate date = LocalDate.of(year, 7, 1); // safer than choosing current date
//	    // date = date.with(WeekFields.ISO.weekBasedYear(), y); // no longer necessary
//	    date = date.with(WeekFields.ISO.weekOfWeekBasedYear(), day);
//	    date = date.with(WeekFields.ISO.dayOfWeek(), weekday);
//
//	    return date;
//	}
	/*
	 * 
	 */
//	public static Calendar getDateOfSpecificMonthAgo (Date daylast,int monthnumber)
//	{
//    	
//    	Calendar calendar =  Calendar.getInstance();
//    	calendar.setTime(daylast);
//    	calendar.add(calendar.MONTH,0-monthnumber);//把日期往后增加一天.整数往后推,负数往前移动
//    	return calendar;
//	}
	/*
	 * 
//	 */
//	public static Date getDateOfSpecificMonthAfter (Date daylast,int monthnumber)
//	{
//    	
//    	Calendar calendar =  Calendar.getInstance();
//    	calendar.setTime(daylast);
//    	calendar.add(calendar.MONTH,monthnumber);//把日期往后增加一天.整数往后推,负数往前移动
//    	return calendar.getTime();
//	}
	/** 
	* 取得指定日期所在周的第一天
	* */ 
//	public static Calendar getFirstDayOfWeek(Date date)
//	{
//		Calendar c = new GregorianCalendar();
//		c.setFirstDayOfWeek(Calendar.MONDAY);
//		c.setTime(date);
//		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
//		return c;
//	}

	/** 
	* 取得指定日期所在周的最后一天,目前最后一天设置为星期六
	* 	*/ 
//	public static Calendar getLastDayOfWeek(Date date) 
//	{
//		Calendar c = new GregorianCalendar();
//		c.setFirstDayOfWeek(Calendar.MONDAY);
//		c.setTime(date);
//		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 5); // Saterday
//		return c;
//	}
	

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
