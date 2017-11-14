package com.exchangeinfomanager.commonlib;

import java.time.LocalDate;
import java.time.ZoneId;

public class StockLocalDate extends java.util.Date
{

	public StockLocalDate(java.util.Date localday) 
	{
		localdate = localday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); 
	}
	
	private LocalDate localdate;

}
