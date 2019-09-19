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

import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForTA4J.DaPanXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
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
		
		super.nodewkdata = new DaPanXPeriodData (NodeGivenPeriodDataItem.WEEK,shanghai,shenzhen) ;
		super.nodedaydata = new DaPanXPeriodData (NodeGivenPeriodDataItem.DAY,shanghai,shenzhen) ;
//		super.nodemonthdata = new DaPanNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
	}
	
	/*
	 * 
	 */
	public Boolean  isDaPanXiuShi (LocalDate date,int difference,String period)
	{
		NodeXPeriodData shnodexdata = this.shanghai.getNodeXPeroidData(period);
		if(shnodexdata.getIndexOfSpecificDateOHLCData(date,difference) != null )
			return false;
		else
			return true;
	}
	
}
