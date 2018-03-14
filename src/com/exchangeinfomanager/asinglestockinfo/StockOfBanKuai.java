package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeriesDataItem;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;

public class StockOfBanKuai extends Stock
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		super.nodetype = BanKuaiAndStockBasic.BKGEGU;
		
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = null; //个股对板块，周线数据才有意义，日线毫无意义
		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
	}
	
	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private Stock stock;
	private Integer quanzhong;
	
	public void setStockQuanZhong (Integer quanzhong)
	{
		this.quanzhong = quanzhong; 
	}
	public Stock getStock ()
	{
		return this.stock;
	}
	public BanKuai getBanKuai ()
	{
		return this.bankuai;
	}
	public Integer getQuanZhong ()
	{
		return this.quanzhong;
	}
	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) {
		NodeXPeriodDataBasic nodexdata = getStockXPeriodDataForBanKuai (period);
		return nodexdata;
	}
	
	public NodeXPeriodDataBasic getStockXPeriodDataForBanKuai (String period)
	{
		if(period.equals(StockGivenPeriodDataItem.MONTH))
			return (StockOfBanKuaiNodeXPeriodData) this.nodemonthdata;
		else if(period.equals(StockGivenPeriodDataItem.WEEK))
			return this.nodewkdata;
		else 
			return null;
	}
	
	public class StockOfBanKuaiNodeXPeriodData extends StockNodeXPeriodData
	{
		public StockOfBanKuaiNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
		}
	//		@Override
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
		 * 和其他该函数不同的地方就是这里是板块的成交量而不是大盘的成交量
		 */
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
		{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,0));
				if( curcjlrecord == null) 
					return null;
				
				//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
				String nodept = getNodeperiodtype();
				NodeXPeriodDataBasic bkxdata = bankuai.getNodeXPeroidData(nodept);
				Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate);
				if( bkcjediff<0 || bkcjediff == null ) {//板块缩量，
					return -100.0;
				}
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null) { //休市前还是空，说明是停牌后复牌了
					Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
					return curggcje/bkcjediff;
				}
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				Double cjechange = curcje - lastcje; //个股成交量的变化
				
				return cjechange/bkcjediff;
		}
	}
}