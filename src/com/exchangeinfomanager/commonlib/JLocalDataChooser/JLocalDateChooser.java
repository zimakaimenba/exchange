package com.exchangeinfomanager.commonlib.JLocalDataChooser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.toedter.calendar.JDateChooser;

public class JLocalDateChooser extends JDateChooser
{

	public JLocalDateChooser() 
	{
		super ();
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
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
