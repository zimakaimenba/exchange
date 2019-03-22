package com.exchangeinfomanager.nodes;

import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;

public abstract class TDXNodes extends BkChanYeLianTreeNode
{
	public TDXNodes (String myowncode, String myownname)
	{
		super (myowncode,myownname);
	}
	
	protected NodeXPeriodDataBasic nodewkdata;
	protected NodeXPeriodDataBasic nodedaydata;
	protected NodeXPeriodDataBasic nodemonthdata;
	
	private String suoshujiaoyisuo;
		
	public String getSuoShuJiaoYiSuo ()
	{
		return this.suoshujiaoyisuo;
	}
	public void setSuoShuJiaoYiSuo (String jys)
	{
		this.suoshujiaoyisuo = jys;
	}

//	public void addNewXPeriodData (TDXNodeGivenPeriodDataItem kdata,String period)
//	{
//		NodeXPeriodDataBasic nodexdata = this.getNodeXPeroidData(period);
//		nodexdata.addNewXPeriodData(kdata);
//	}
	
	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
	{
		if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
			return nodewkdata;
		else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
			return nodemonthdata;
		else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
			return nodedaydata;
		else 
			return null;
	}
	
}
