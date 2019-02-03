package com.exchangeinfomanager.commonlib.JLocalDataChooser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.exchangeinfomanager.StockCalendar.StockCalendar;
import com.toedter.calendar.IDateEditor;
import com.toedter.calendar.JDateChooser;

public class JLocalDateChooser extends JDateChooser
{

	public JLocalDateChooser() 
	{
		super ();
	}
	public JLocalDateChooser(StockCalendar jcal, Date date, String dateFormatString,
	IDateEditor dateEditor) {
		super(jcal,date,dateFormatString,dateEditor);
	}
	public void setLocalDate (LocalDate date)
	{
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = date.atStartOfDay().atZone(zone).toInstant();
		super.setDate(Date.from(instant));
	}
	public LocalDate getLocalDate()
	{
		Date date = super.getDate();
		try {
			return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
}
