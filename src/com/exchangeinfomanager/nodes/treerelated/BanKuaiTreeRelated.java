package com.exchangeinfomanager.nodes.treerelated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

/*
 * 
 */
public class BanKuaiTreeRelated implements NodesTreeRelated
{
	public BanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
	{
		this.node = treenode1;
	}
	
	private BkChanYeLianTreeNode node;
	private Map<LocalDate,Integer> stocknumsinparsedfile; //
	private Set<LocalDate> bkselfinparsedfile;
	/*
	 * 
	 */
	public void setSelfIsMatchModel (LocalDate selfinsetdate, Boolean inorout)
	{
		if(bkselfinparsedfile == null)
			this.bkselfinparsedfile = new HashSet<LocalDate> ();
		
		LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
		if(inorout && !this.bkselfinparsedfile.contains(friday) )
			this.bkselfinparsedfile.add(friday);
		else
		if(!inorout)
			this.bkselfinparsedfile.remove(friday);
	}
	public Boolean selfIsMatchModel (LocalDate selfinsetdate)
	{ 
		try{
			LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
			return this.bkselfinparsedfile.contains(friday);
		} catch (java.lang.NullPointerException e) {
			return false;
		}
	}
	/*
	 * 
	 */
	public void setStocksNumInParsedFile (LocalDate parsefiledate, Integer stocksnum)
	{
		if(stocknumsinparsedfile == null) {
			stocknumsinparsedfile = new HashMap<LocalDate,Integer> ();
			LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
			if(stocksnum >0)
				stocknumsinparsedfile.put(friday, stocksnum);
		} else {
			LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
			if(stocksnum >0)
				stocknumsinparsedfile.put(friday, stocksnum);
		}
	}
	public Integer getStocksNumInParsedFileForSpecificDate (LocalDate requiredate)
	{
		try {
			LocalDate friday = requiredate.with(DayOfWeek.FRIDAY);
			return stocknumsinparsedfile.get(friday);
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	
}
