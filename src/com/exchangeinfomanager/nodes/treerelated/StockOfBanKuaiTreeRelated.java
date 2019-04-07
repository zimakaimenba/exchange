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
public class StockOfBanKuaiTreeRelated extends NodesTreeRelated
{
	public StockOfBanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
	{
		super(treenode1);
	}
	
	private Set<LocalDate> isinparsedfile; //����������ָ�������Ƿ��ڷ����ļ���
	public void setStocksNumInParsedFile (LocalDate parsefiledate, Boolean isin)
	{
		if(isinparsedfile == null) {
			isinparsedfile = new HashSet<LocalDate> ();
			LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
			if(isin)
				isinparsedfile.add(friday);
		} else {
			LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
			if(isin)
				isinparsedfile.add(friday);
		}
	}
	public Boolean isInBanKuaiFengXiResultFileForSpecificDate (LocalDate requiredate)
	{
		LocalDate friday = requiredate.with(DayOfWeek.FRIDAY);
		try {
			 if( isinparsedfile.contains(friday) )
				 return true;
			 else
				  return false;
		} catch(java.lang.NullPointerException e) {
			return null;
		}
	}
	
}