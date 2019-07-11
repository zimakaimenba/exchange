package com.exchangeinfomanager.nodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.nodes.nodexdata.DaPanNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;

public class DaPan extends TDXNodes
{

	public DaPan(String dapcode, String dapanname) 
	{
		super(dapcode,"╢Сел");
		super.nodetype = BkChanYeLianTreeNode.DAPAN;
		
		super.nodetreerelated = new NodesTreeRelated (this);
	}
//	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai shanghai; //
	private BanKuai shenzhen; //
	
	
	public void setDaPanContents (BanKuai sh,BanKuai sz)
	{
		this.shanghai = sh;
		this.shenzhen = sz;
		
		super.nodewkdata = new DaPanNodeXPeriodData (TDXNodeGivenPeriodDataItem.WEEK,shanghai,shenzhen) ;
		super.nodedaydata = new DaPanNodeXPeriodData (TDXNodeGivenPeriodDataItem.DAY,shanghai,shenzhen) ;
//		super.nodemonthdata = new DaPanNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
	}
	
	/*
	 * 
	 */
	public Boolean  isDaPanXiuShi (LocalDate date,int difference,String period)
	{
		NodeXPeriodDataBasic shnodexdata = this.shanghai.getNodeXPeroidData(period);
		if(shnodexdata.hasRecordInThePeriod(date,difference) )
			return false;
		else
			return true;
	}
	
}
