package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;

public class DaPan extends BkChanYeLianTreeNode
{

	public DaPan(String dpcode,String dpname) 
	{
		super(dpcode,dpname);
		super.nodetype = BanKuaiAndStockBasic.DAPAN;
		
		super.nodewkdata = new DaPanNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new DaPanNodeXPeriodData (StockGivenPeriodDataItem.DAY) ;
//		super.nodemonthdata = new DaPanNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		
		super.nodetreerelated = new TreeRelated (this);
	}
//	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai shanghai; //上证成交记录
	private BanKuai shenzhen; //深证成交记录
	
	public void setShangHai (BanKuai sh)
	{
		this.shanghai = sh;
	}
	public void setShenZhen(BanKuai sz)
	{
		this.shenzhen = sz;
	}
	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
	{
		if(period.equals(StockGivenPeriodDataItem.WEEK))
			return nodewkdata;
		else if(period.equals(StockGivenPeriodDataItem.MONTH))
			return nodemonthdata;
		else if(period.equals(StockGivenPeriodDataItem.DAY))
			return nodedaydata;
		else 
			return null;
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
	
	public class DaPanNodeXPeriodData implements NodeXPeriodDataBasic
	{
		private String nodeperiodtype;
		public DaPanNodeXPeriodData (String nodeperiodtype1)
		{
			this.nodeperiodtype = nodeperiodtype1;
		}
		
		@Override
		public String getNodeperiodtype() 
		{
			return this.nodeperiodtype;
		}

		/*
		 * 计算指定周期和上周期的成交额差额
		 */
		public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate)
		{
			Double dapanchaer = null;
			
			String recordsperiod = getNodeperiodtype();
			NodeXPeriodDataBasic shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
			Double shcurrecord = shanghaiperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate);
			
			NodeXPeriodDataBasic shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
			Double szcurrecord = shenzhenperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate);
			
			return szcurrecord + shcurrecord ;
		}

		@Override
		public void addNewXPeriodData(StockGivenPeriodDataItem kdata) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public LocalDate getRecordsStartDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LocalDate getRecordsEndDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean hasRecordInThePeriod(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Double getChengJiaoEr(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TimeSeries getRangeChengJiaoEr(LocalDate requiredstart, LocalDate requiredend) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TimeSeries getRangeChengJiaoErZhanBi(LocalDate requiredstart, LocalDate requiredend) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OHLCSeries getRangeOHLCData(LocalDate requiredstart, LocalDate requiredend) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OHLCItem getSpecificDateOHLCData(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean hasFxjgInPeriod(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addFxjgToPeriod(RegularTimePeriod period, Integer fxjg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Double getChenJiaoErZhanBi(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	

	
}
