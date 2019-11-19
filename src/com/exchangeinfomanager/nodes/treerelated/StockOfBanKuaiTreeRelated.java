package com.exchangeinfomanager.nodes.treerelated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

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
	private Set<LocalDate> isinparsedfile; //表明个股在指定日期是否在分析文件中
	
	@Override
	public void setSelfIsMatchModel(LocalDate selfinsetdate)
	{
		if(isinparsedfile == null) 
			isinparsedfile = new HashSet<LocalDate> ();
		
		LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
		if(!isinparsedfile.contains(friday))
			isinparsedfile.add(friday);
		
	}
	@Override
	public Boolean selfIsMatchModel(LocalDate selfinsetdate) 
	{
		LocalDate friday = selfinsetdate.with(DayOfWeek.FRIDAY);
		try {
			 if( isinparsedfile.contains(friday) )
				 return true;
			 else
				  return false;
		} catch(java.lang.NullPointerException e) {
			return null;
		}
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