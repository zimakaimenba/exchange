package com.exchangeinfomanager.nodes.treerelated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
	private Multimap<LocalDate, String> isinparsedfile; //表明个股在指定日期是否在分析文件中
	
	@Override
	public void setSelfIsMatchModel (LocalDate selfinsetdate, String colorcode,  Boolean inorout)
	{
		if(isinparsedfile == null)   isinparsedfile = HashMultimap.create();
		
		LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
		if(inorout) {
			Collection<String> fruits = isinparsedfile.get(friday);
			Boolean checkresult = fruits.contains(colorcode);
			if(!checkresult) isinparsedfile.put(friday, colorcode);
		} else  isinparsedfile.remove(friday, colorcode);
		
	}
	@Override
	public Boolean selfIsMatchModel (LocalDate selfinsetdate,String colorcode) 
	{
		if(isinparsedfile == null) return false;
		
		LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
		try {
			Collection<String> result = isinparsedfile.get(friday);
			Boolean checkresult = result.contains(colorcode);
			return checkresult;
		} catch(java.lang.NullPointerException e) {return null;}
	}
	@Override
	public Collection<String> selfIsMatchModelSet (LocalDate selfinsetdate) 
	{
		if(isinparsedfile == null) return null;
		
		LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
		try {
			Collection<String> fruits = isinparsedfile.get(friday);
			return fruits;
		} catch(java.lang.NullPointerException e) {	return null;}
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
