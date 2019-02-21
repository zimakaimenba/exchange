package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeriesDataItem;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
/*
 * 板块的个股和普通大盘的个股有少数相似，但不同就在成交量，一个是对板块的成交量，一个是对大盘的成交量。
 * StockOfBanKuai 不应该有如流通市值，总市值这些属性，应该只有成交量和成交额属性，还有权重，
 */
public class StockOfBanKuai extends BkChanYeLianTreeNode
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		super.nodetype = BanKuaiAndStockBasic.BKGEGU;
		
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = null;
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = null; //对绝大多数分析来说说，周线数据才有意义，日线毫无意义，特别是个股相对于板块的日线数据。
//		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		
		super.nodetreerelated = null;
		super.nodetreerelated = new StockOfBanKuai.StockOfBanKuaiTreeRelated(this);
	}
	
//	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private Stock stock;
	private Integer quanzhong;
	private Boolean isbklongtou;
	
	public Boolean isBkLongTou ()
	{
		if(this.isbklongtou == null)
			return false;
		else
			return this.isbklongtou;
	}
	public void setBkLongTou (Boolean isbklongtou1)
	{
		this.isbklongtou = isbklongtou1; 
	}
	
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
//	@Override
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.Stock#getNodeXPeroidData(java.lang.String)
	 */
//	public NodeXPeriodDataBasic getNodeXPeroidData(String period) {
//		
//		if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return (StockOfBanKuaiNodeXPeriodData) super.nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return (StockOfBanKuaiNodeXPeriodData)super.nodewkdata;
//		else 
//			return null;
//		
////		NodeXPeriodDataBasic nodexdata = getStockXPeriodDataForBanKuai (period);
////		return nodexdata;
//	}
	
//	private NodeXPeriodDataBasic getStockXPeriodDataForBanKuai (String period)
//	{
//		if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return (StockOfBanKuaiNodeXPeriodData) super.nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return super.nodewkdata;
//		else 
//			return null;
//	}
	
	/*
	 * 
	 */
	public class StockOfBanKuaiTreeRelated extends TreeRelated
	{
		public StockOfBanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
		{
			super(treenode1);
		}
		
		private HashMap<LocalDate,Boolean> isinparsedfile;
		public void setStocksNumInParsedFile (LocalDate parsefiledate, Boolean isin)
		{
			if(isinparsedfile == null) {
				isinparsedfile = new HashMap<LocalDate,Boolean> ();
				LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
				if(isin)
					isinparsedfile.put(friday, isin);
			} else {
				LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
				if(isin)
					isinparsedfile.put(friday, isin);
			}
		}
		public Boolean isInBanKuaiFengXiResultFileForSpecificDate (LocalDate requiredate)
		{
			LocalDate friday = requiredate.with(DayOfWeek.FRIDAY);
			try {
				Boolean result = isinparsedfile.get(friday);
				return result;
			} catch(java.lang.NullPointerException e) {
				return null;
			}
		}
		
	}
	
	public class StockOfBanKuaiNodeXPeriodData extends NodeXPeriodData
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
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
		{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				if( curcjlrecord == null) 
					return null;
				
				//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
				String nodept = getNodeperiodtype();
				NodeXPeriodDataBasic bkxdata = bankuai.getNodeXPeroidData(nodept);
				Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
				if( bkcjediff == null || bkcjediff < 0   ) {//板块缩量，
					return -100.0;
				}
				
				int index = difference-1;
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
		@Override
		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}