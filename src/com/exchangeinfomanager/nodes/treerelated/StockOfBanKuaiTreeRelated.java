package com.exchangeinfomanager.nodes.treerelated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/*
 * 
 */
public class StockOfBanKuaiTreeRelated implements NodesTreeRelated
{
	public StockOfBanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
	{
		this.node = treenode1;
	}
	
	private BkChanYeLianTreeNode node;
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
	@Override
	public void setStocksNumInParsedFile(LocalDate parsefiledate, Integer stocksnum) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Integer getStocksNumInParsedFileForSpecificDate(LocalDate requiredate) {
		// TODO Auto-generated method stub
		return null;
	}
	
}