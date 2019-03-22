package com.exchangeinfomanager.nodes.treerelated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

/*
 * 
 */
public class StockOfBanKuaiTreeRelated extends NodesTreeRelated
{
	public StockOfBanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
	{
		super(treenode1);
	}
	
	private HashMap<LocalDate,Boolean> isinparsedfile;
	public void setStocksNumInParsedFile (LocalDate parsefiledate, Boolean isin)
	{
		if(isinparsedfile == null) {
			isinparsedfile = new HashMap<LocalDate,Boolean> ();
			LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
			if(isin)
				isinparsedfile.put(friday, isin);
		} else {
			LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
			if(isin)
				isinparsedfile.put(friday, isin);
		}
	}
	public Boolean isInBanKuaiFengXiResultFileForSpecificDate (LocalDate requiredate)
	{
		LocalDate friday = requiredate.with(DayOfWeek.FRIDAY);
		try {
			Boolean result = isinparsedfile.get(friday);
			return result;
		} catch(java.lang.NullPointerException e) {
			return null;
		}
	}
	
}