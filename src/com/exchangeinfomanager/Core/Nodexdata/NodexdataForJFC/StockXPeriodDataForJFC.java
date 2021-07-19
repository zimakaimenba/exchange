package com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.google.common.collect.ObjectArrays;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class StockXPeriodDataForJFC extends TDXNodesXPeriodDataForJFC implements StockNodesXPeriodData
{
	public StockXPeriodDataForJFC (String nodecode, String nodeperiodtype1) 
	{
		super(nodecode , nodeperiodtype1);
		
		stockhuanshoulv = new TimeSeries(nodeperiodtype1);
		stockhuanshoulvfree = new TimeSeries(nodeperiodtype1);
		stockliutongshizhi = new TimeSeries(nodeperiodtype1);
		stockzongshizhi = new TimeSeries(nodeperiodtype1);
//		periodhighestzhangdiefu = new TimeSeries(nodeperiodtype1);
//		periodlowestzhangdiefu = new TimeSeries(nodeperiodtype1);
	}
	
	private Logger logger = Logger.getLogger(StockXPeriodDataForJFC.class);
	
	private  TimeSeries stockhuanshoulv; //换手�?
	private  TimeSeries stockhuanshoulvfree; //自由流�?�股份换手率
	private  TimeSeries stockliutongshizhi; //流�?�市�?
	private  TimeSeries stockzongshizhi; //总市�?
//	private  TimeSeries periodhighestzhangdiefu; //�?高涨�?
//	private  TimeSeries periodlowestzhangdiefu; //�?高涨�?
	private  TimeSeries stockgzjl; //关注记录
	private  TimeSeries stockmairujl; //买入记录
	private  TimeSeries stockmaichuujl; //卖出记录
	
	
	/*
	 * 
	 */
	public void addMaiRuJiLu (RegularTimePeriod period,Integer fxjg) 
	{
		if(this.stockmairujl == null)
			stockmairujl = new TimeSeries(super.getNodeperiodtype() );
		
		try {
			stockmairujl.add(period,fxjg);
		} catch (org.jfree.data.general.SeriesException e) {
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Integer hasMaiRuJiLuInPeriod (LocalDate requireddate)
	{
		if(this.stockmairujl == null)
			return null;
		RegularTimePeriod period = super.getJFreeChartFormateTimePeriod(requireddate);
		if(period == null)
			return null;
		TimeSeriesDataItem gzjlitem = stockmairujl.getDataItem(period );
		if(gzjlitem == null)
			return null;
		
		Integer value = (Integer)gzjlitem.getValue();
		return value;
	}
	/*
	 * 
	 */
	public void addMaiChuJiLu (RegularTimePeriod period,Integer fxjg) 
	{
		if(this.stockmaichuujl == null)
			stockmaichuujl = new TimeSeries(super.getNodeperiodtype() );
		
		try {
			stockmaichuujl.add(period,fxjg);
		} catch (org.jfree.data.general.SeriesException e) {	}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Integer hasMaiChuJiLuInPeriod (LocalDate requireddate)
	{
		if(this.stockmaichuujl == null)
			return null;
		
		RegularTimePeriod period = super.getJFreeChartFormateTimePeriod(requireddate);
		if(period == null)
			return null;
		TimeSeriesDataItem gzjlitem = stockmaichuujl.getDataItem( period);
		if(gzjlitem == null)
			return null;
		
		Integer value = (Integer)gzjlitem.getValue();
		return value;
	}
	/*
	 * 
	 */
	public Double getAverageDailyHuanShouLvOfWeek (LocalDate requireddate)
	{
		Double hsl = this.getSpecificTimeHuanShouLv(requireddate);
		if(hsl != null) {
			Integer daynum = super.getExchangeDaysNumberForthePeriod(requireddate);
			if(daynum != null)
				return hsl/daynum;
			else
				return hsl/5;
		} else
			return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Integer hasGzjlInPeriod (LocalDate requireddate)
	{
		if(this.stockgzjl == null)
			return null;
		
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem gzjlitem = stockgzjl.getDataItem(curperiod );
		if(gzjlitem == null)
			return null;
		
		Integer value = (Integer)gzjlitem.getValue();
		return value;
	}
	/*
	 * 
	 */
	public void addGzjlToPeriod (RegularTimePeriod period,Integer fxjg) 
	{
		if(this.stockgzjl == null)
			stockgzjl = new TimeSeries(super.getNodeperiodtype() );
		
		try {
			stockgzjl.add(period,fxjg);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.NodeGivenPeriodDataItem)
	 */
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		super.addNewXPeriodData(kdata);
		
		try {
			stockhuanshoulv.setNotify(false);
			if(kdata.getHuanShouLv() != null && kdata.getHuanShouLv() !=0.0 )
				stockhuanshoulv.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getHuanShouLv(),false);
		} catch (org.jfree.data.general.SeriesException e) {}
		try {
			stockhuanshoulvfree.setNotify(false);
			if(kdata.getHuanShouLvFree() != null && kdata.getHuanShouLvFree() !=0.0 )
				stockhuanshoulvfree.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getHuanShouLvFree(),false);
		} catch (org.jfree.data.general.SeriesException e) {}
		try {
			stockliutongshizhi.setNotify(false);
			if(kdata.getLiuTongShiZhi() != null && kdata.getLiuTongShiZhi() !=  0.0 )
				stockliutongshizhi.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getLiuTongShiZhi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在�?" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		try {	
			stockzongshizhi.setNotify(false);
			if( kdata.getZongShiZhi() != null && kdata.getZongShiZhi() != 0)
				stockzongshizhi.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getZongShiZhi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在�?" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
//		try {	
//			periodhighestzhangdiefu.setNotify(false);
//			if( kdata.getPeriodHighestZhangDieFu() != null && kdata.getPeriodHighestZhangDieFu() != 0)
//				periodhighestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getPeriodHighestZhangDieFu(),false);
//		} catch (org.jfree.data.general.SeriesException e) {}
//		try {	
//			periodlowestzhangdiefu.setNotify(false);
//			if( kdata.getPeriodLowestZhangDieFu() != null && kdata.getPeriodLowestZhangDieFu() != 0)
//				periodlowestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()), kdata.getPeriodLowestZhangDieFu(),false);
//		} catch (org.jfree.data.general.SeriesException e) {}
	}
	/*
	 * 
	 */
	public void resetAllData ()
	{
		super.resetAllData();
		try {stockhuanshoulv.clear();} catch (java.lang.NullPointerException e) {}
		try {stockhuanshoulvfree.clear();} catch (java.lang.NullPointerException e) {}
		try {stockliutongshizhi.clear();} catch (java.lang.NullPointerException e) {}
		try {stockzongshizhi.clear();} catch (java.lang.NullPointerException e) {}
//		try {periodhighestzhangdiefu.clear();} catch (java.lang.NullPointerException e) {}
//		try {periodlowestzhangdiefu.clear();} catch (java.lang.NullPointerException e) {}
		try { stockgzjl.clear();	} catch (java.lang.NullPointerException e) {}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeHuanShouLv (LocalDate requireddate)
	{
		try{
			RegularTimePeriod period = super.getJFreeChartFormateTimePeriod(requireddate);
			if(period == null)
				return null;
			TimeSeriesDataItem curhslrecord = stockhuanshoulv.getDataItem(period );
			Double curhsl = curhslrecord.getValue().doubleValue();
			
			return curhsl;
		} catch (java.lang.NullPointerException e) {return null;}
		
	}
	public Double getSpecificTimeHuanShouLvFree (LocalDate requireddate)
	{
		try{
			RegularTimePeriod period = super.getJFreeChartFormateTimePeriod(requireddate);
			if(period == null)
				return null;
			TimeSeriesDataItem curhslrecord = stockhuanshoulvfree.getDataItem(period );
			Double curhslfree = curhslrecord.getValue().doubleValue();
			
			return curhslfree;
		} catch (java.lang.NullPointerException e) {return null;}
		
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeZongShiZhi (LocalDate requireddate)
	{
		RegularTimePeriod period = super.getJFreeChartFormateTimePeriod(requireddate);
		if(period == null)
			return null;
		TimeSeriesDataItem curzszrecord = stockzongshizhi.getDataItem(period);
		Double curzsz = null ;
		try { curzsz = curzszrecord.getValue().doubleValue();
		} catch (Exception e) {}
		
		return curzsz;
	}

	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate)
	{
		RegularTimePeriod curperiod = super.getJFreeChartFormateTimePeriod(requireddate);
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curltszrecord = stockliutongshizhi.getDataItem(curperiod );
		try{
			Double curltsz = curltszrecord.getValue().doubleValue();
			return curltsz;
		} catch (java.lang.NullPointerException e ) {return null;}
	}
	/*
	 * 
	 */
	public Object getNodeDataByKeyWord( String keyword, LocalDate date, String... maformula)
	 {
		 Object value = null;
		 value = super.getNodeDataByKeyWord (keyword,date,maformula[0]);
		 if(value != null)
			 return value;
		 
		 switch(keyword) {
		 case "HuanShouLv" :
		      	 Double hsl  = this.getSpecificTimeHuanShouLv(date) ;
		   	     value = hsl;
		   	     break;
		 case "HuanShouLvFree" :
			 Double hslf = this.getSpecificTimeHuanShouLvFree (date);
	   	     value = hslf;
	   	     break;
		 case "LiuTongShiZhi" :
			 Double ltsz = this.getSpecificTimeLiuTongShiZhi(date);//显示成交额是多少周最�?,成交额多少周�?小没有意义，因为如果不是完整周成交量就是会很�?
	   	     value = ltsz;
	   	     break;
		 case "ZongShiZhi" :
			 Double zsz = this.getSpecificTimeZongShiZhi(date);
	   	     value = zsz;
	   	     break;
		 }
		 return value;
	 }
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC#getNodeXDataCsvData(com.exchangeinfomanager.nodes.TDXNodes, java.time.LocalDate, int)
	 */
	 public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate)
	 {
		 String[] supcsv = super.getNodeXDataCsvData(superbk, requireddate);
		 if(supcsv == null)
			 return null;
		 
		 Double hsl = this.getSpecificTimeHuanShouLv(requireddate);
		 Double hslf = this.getSpecificTimeHuanShouLvFree(requireddate);
		 Double liutongshizhi = this.getSpecificTimeLiuTongShiZhi(requireddate);
		 Double zongshizhi = this.getSpecificTimeZongShiZhi(requireddate);
		 
		 String strhsl = null; String strhslf = null; String strliutongshizhi = null;String strzongshizhi = null;
		 try { strhsl = hsl.toString();	 } catch (java.lang.NullPointerException e) { strhsl = String.valueOf("0"); }
		 try { strhslf = hslf.toString();	 } catch (java.lang.NullPointerException e) { strhslf = String.valueOf("0"); }
		 try { strliutongshizhi	 =   liutongshizhi.toString(); } catch (java.lang.NullPointerException e) {	 strliutongshizhi	= String.valueOf("0"); }
		 try { strzongshizhi	 =   zongshizhi.toString(); } catch (java.lang.NullPointerException e) {	 strzongshizhi	= String.valueOf("0"); }
		 String[] curcsvline = {strhsl,strhslf, strliutongshizhi,strzongshizhi };
		 
		 String [] joined = ObjectArrays.concat(supcsv, curcsvline, String.class);
			
		 return joined;
	 }
	 /*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getNodeXDataInHtml(java.time.LocalDate, int)
		 */
		public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate) 
		{
			String datahtml = super.getNodeXDataInHtml(superbk,requireddate);
			
			org.jsoup.nodes.Document doc = Jsoup.parse(datahtml);
			org.jsoup.select.Elements content = doc.select("body");
			org.jsoup.select.Elements dl = content.select("dl");
			
			String[] csvdataresult = getNodeXDataCsvData (superbk,requireddate);
			
				 for(int i=0;i<StockNodesXPeriodData.NODEXDATACSVDATAHEADLINE.length;i++) {
					 String value = csvdataresult[i];
					 if(value != null && !value.equals("0") && StockNodesXPeriodData.ouputToHtmlToolTipControl[i] == 1) {
						 org.jsoup.nodes.Element li = dl.get(0).appendElement("li");
						 org.jsoup.nodes.Element font = li.appendElement("font");
						 font.appendText(StockNodesXPeriodData.NODEXDATACSVDATAHEADLINE[i] + ":" + value  );
						 font.attr("color", StockNodesXPeriodData.OuputColorControl[i]);
					 } 
				 }
			return doc.toString();
		}
		@Override
		public Boolean hasFxjgInPeriod(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}

}