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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;


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
	public static List<Interval> getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
	{
		if(nodestart == null)
		return null;
		
		List<Interval> result = new ArrayList<Interval> ();
		
		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
		if(overlapinterval != null) {
		DateTime overlapstart = overlapinterval.getStart();
		DateTime overlapend = overlapinterval.getEnd();
		
		Interval resultintervalpart1 = null ;
		if(requiredstartday.isBefore(nodestart)) {
			resultintervalpart1 = new Interval(requiredstartdt,overlapstart);
		} 
		
		Interval resultintervalpart2 = null;
		if (requiredendday.isAfter(nodeend)) {
			resultintervalpart2 = new Interval(overlapend,requiredenddt);
		}
		if(resultintervalpart1 != null)
			result.add(resultintervalpart1);
		if(resultintervalpart2 != null)
			result.add(resultintervalpart2);
		//return result;
		}
		if(requiredinterval.abuts(nodeinterval)) {
		result.add(requiredinterval);
		// return result;
		}
		Interval gapinterval = requiredinterval.gap(nodeinterval);
		if(gapinterval != null) {
		result.add(requiredinterval);
		result.add(gapinterval);
		}
		
		return result;
	}
	
	/*
	 * 
	 */
	public static LocalDate getSettingRangeDate (LocalDate curselectdate, String rangelevel)
	{
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		
		SetupSystemConfiguration sysconfig = new SetupSystemConfiguration();
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
	 * �˷������ؽ����1(2011���1��)���������һ��calendar.setMinimalDaysInFirstWeek(7);���ؽ����52��2010���52�ܣ�
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
//    	calendar.add(calendar.MONTH,0-monthnumber);//��������������һ��.����������,������ǰ�ƶ�
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
//    	calendar.add(calendar.MONTH,monthnumber);//��������������һ��.����������,������ǰ�ƶ�
//    	return calendar.getTime();
//	}
	/** 
	* ȡ��ָ�����������ܵĵ�һ��
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
	* ȡ��ָ�����������ܵ����һ��,Ŀǰ���һ������Ϊ������
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
