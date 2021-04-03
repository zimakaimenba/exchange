package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
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
		periodhighestzhangdiefu = new TimeSeries(nodeperiodtype1);
		periodlowestzhangdiefu = new TimeSeries(nodeperiodtype1);
	}
	
	private Logger logger = Logger.getLogger(StockXPeriodDataForJFC.class);
	
	private  TimeSeries stockhuanshoulv; //换手率
	private  TimeSeries stockhuanshoulvfree; //自由流通股份换手率
	private  TimeSeries stockliutongshizhi; //流通市值
	private  TimeSeries stockzongshizhi; //总市值
	private  TimeSeries periodhighestzhangdiefu; //最高涨幅
	private  TimeSeries periodlowestzhangdiefu; //最高涨幅
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
	public Integer hasMaiRuJiLuInPeriod (LocalDate requireddate,int difference)
	{
		if(this.stockmairujl == null)
			return null;
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
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
	public Integer hasMaiChuJiLuInPeriod (LocalDate requireddate,int difference)
	{
		if(this.stockmaichuujl == null)
			return null;
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
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
	public Double getAverageDailyHuanShouLvOfWeek (LocalDate requireddate,int difference)
	{
		Double hsl = this.getSpecificTimeHuanShouLv(requireddate, 0);
		if(hsl != null) {
			Integer daynum = super.getExchangeDaysNumberForthePeriod(requireddate, 0);
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
	public Integer hasGzjlInPeriod (LocalDate requireddate,int difference)
	{
		if(this.stockgzjl == null)
			return null;
		
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem gzjlitem = stockgzjl.getDataItem(curperiod );
		if(gzjlitem == null)
			return null;
		
		Integer value = (Integer)gzjlitem.getValue();
		return value;
	}
	public TimeSeries getPeriodHighestZhangDieFuData ()
	{
		return this.periodhighestzhangdiefu;
	}
	public TimeSeries getPeriodLowestZhangDieFuData ()
	{
		return this.periodlowestzhangdiefu;
	} 
	public void addPeriodHighestZhangDieFu (LocalDate requireddate,Double zhangfu)
	{
		try {	
			periodhighestzhangdiefu.setNotify(false);
			
			org.jfree.data.time.Week recordwk = null;
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
				java.sql.Date sqldate = new java.sql.Date(format.parse(requireddate.toString()).getTime());
				recordwk = new org.jfree.data.time.Week (sqldate);
			} catch (ParseException e) {
				e.printStackTrace();
				return ;
			}
			
			Double curzhangfu = this.getSpecificTimeHighestZhangDieFu(requireddate, 0);
			if(curzhangfu == null  ){
				try{
					periodhighestzhangdiefu.add(recordwk, zhangfu, false );
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}
				return;
			}
			
			if( CommonUtility.round(curzhangfu,6) != CommonUtility.round(zhangfu,6)) {
				try{
					this.periodhighestzhangdiefu.delete(recordwk);
					periodhighestzhangdiefu.add(recordwk, zhangfu, false );
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}
				return;
			}
		} catch (org.jfree.data.general.SeriesException e) {}
	}
	public void addPeriodLowestZhangDieFu (LocalDate requireddate,Double diefu)
	{
		try {	
			periodlowestzhangdiefu.setNotify(false);
			
			org.jfree.data.time.Week recordwk = null;
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
				java.sql.Date sqldate = new java.sql.Date(format.parse(requireddate.toString()).getTime());
				recordwk = new org.jfree.data.time.Week (sqldate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Double curzhangfu = this.getSpecificTimeLowestZhangDieFu(requireddate, 0);
			if(curzhangfu == null  ){
				try{
					periodlowestzhangdiefu.add(recordwk, diefu, false );
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}
				return;
			}
			
			if( CommonUtility.round(curzhangfu,6) != CommonUtility.round(diefu,6)) {
				try{
					this.periodlowestzhangdiefu.delete(recordwk);
					periodlowestzhangdiefu.add(recordwk, diefu, false );
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}
				return;
			}
		} catch (org.jfree.data.general.SeriesException e) {}
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
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		try {	
			stockzongshizhi.setNotify(false);
			if( kdata.getZongShiZhi() != null && kdata.getZongShiZhi() != 0)
				stockzongshizhi.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getZongShiZhi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
//			System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//			e.printStackTrace();
		}
		try {	
			periodhighestzhangdiefu.setNotify(false);
			if( kdata.getPeriodHighestZhangDieFu() != null && kdata.getPeriodHighestZhangDieFu() != 0)
				periodhighestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getPeriodHighestZhangDieFu(),false);
		} catch (org.jfree.data.general.SeriesException e) {}
		try {	
			periodlowestzhangdiefu.setNotify(false);
			if( kdata.getPeriodLowestZhangDieFu() != null && kdata.getPeriodLowestZhangDieFu() != 0)
				periodlowestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()), kdata.getPeriodLowestZhangDieFu(),false);
		} catch (org.jfree.data.general.SeriesException e) {}
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
		try {periodhighestzhangdiefu.clear();} catch (java.lang.NullPointerException e) {}
		try {periodlowestzhangdiefu.clear();} catch (java.lang.NullPointerException e) {}
		try { stockgzjl.clear();	} catch (java.lang.NullPointerException e) {}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference)
	{
		try{
			RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
			if(period == null)
				return null;
			TimeSeriesDataItem curhslrecord = stockhuanshoulv.getDataItem(period );
			Double curhsl = curhslrecord.getValue().doubleValue();
			
			return curhsl;
		} catch (java.lang.NullPointerException e) {return null;}
		
	}
	public Double getSpecificTimeHuanShouLvFree (LocalDate requireddate,int difference)
	{
		try{
			RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
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
	public Double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference)
	{
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(period == null)
			return null;
		TimeSeriesDataItem curzszrecord = stockzongshizhi.getDataItem(period);
		Double curzsz = null ;
		try { curzsz = curzszrecord.getValue().doubleValue();
		} catch (Exception e) {}
		
		return curzsz;
	}
	/*
	 * 得到某个时期的涨跌幅
	 */
	public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate,int difference)
	{
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(period == null)
			return null;
		
		TimeSeriesDataItem curhighzdfrecord = periodhighestzhangdiefu.getDataItem(period);
		Double curhzdf = null ;
		try {curhzdf = curhighzdfrecord.getValue().doubleValue();
		} catch (Exception e) {return null;}
		
		return curhzdf;
	}
	/*
	 * 
	 */
	public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate,int difference)
	{
		RegularTimePeriod period = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(period == null)
			return null;
		TimeSeriesDataItem curlowzdfrecord = periodlowestzhangdiefu.getDataItem(period);
		Double curlzdf = null ;
		try { curlzdf = curlowzdfrecord.getValue().doubleValue();
		} catch (Exception e) {return null;}
		
		return curlzdf;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
	 */
	public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference)
	{
		RegularTimePeriod curperiod = getJFreeChartFormateTimePeriodForAMO(requireddate,difference);
		if(curperiod == null)
			return null;
		
		TimeSeriesDataItem curltszrecord = stockliutongshizhi.getDataItem(curperiod );
		try{
			Double curltsz = curltszrecord.getValue().doubleValue();
			return curltsz;
		} catch (java.lang.NullPointerException e ) {return null;}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC#getNodeXDataCsvData(com.exchangeinfomanager.nodes.TDXNodes, java.time.LocalDate, int)
	 */
	 public String[] getNodeXDataCsvData (TDXNodes superbk, LocalDate requireddate, int difference)
	 {
		 String[] supcsv = super.getNodeXDataCsvData(superbk, requireddate, 0);
		 if(supcsv == null)
			 return null;
		 
		 Double hsl = this.getSpecificTimeHuanShouLv(requireddate, 0);
		 Double hslf = this.getSpecificTimeHuanShouLvFree(requireddate, 0);
		 Double liutongshizhi = this.getSpecificTimeLiuTongShiZhi(requireddate, 0);
		 
		 String strhsl = null; String strhslf = null; String strliutongshizhi = null;
		 try { strhsl = hsl.toString();	 } catch (java.lang.NullPointerException e) { strhsl = String.valueOf("0"); }
		 try { strhslf = hslf.toString();	 } catch (java.lang.NullPointerException e) { strhslf = String.valueOf("0"); }
		 try { strliutongshizhi	 =   liutongshizhi.toString(); } catch (java.lang.NullPointerException e) {	 strliutongshizhi	= String.valueOf("0"); }
		 String[] curcsvline = {strhsl,strhslf, strliutongshizhi };
		 
		 String [] joined = ObjectArrays.concat(supcsv, curcsvline, String.class);
			
		 return joined;
	 }
	 /*
	  * 
	  */
		public String getNodeXDataInHtml2(TDXNodes superbk,LocalDate requireddate, int difference) 
		{
			String[] csvresult = this.getNodeXDataCsvData(superbk,requireddate,difference);
			
			org.jsoup.nodes.Document doc = Jsoup.parse("");
			org.jsoup.select.Elements content = doc.select("body");
			content.get(0).appendElement("dl");
			org.jsoup.select.Elements dl = content.select("dl");
			
//			"成交额占比",
//		    "成交额占比MaxWk",
//			"成交额占比MinWk",
//			"成交额占比增长率",
//			
//			"成交量占比",
//		    "成交量占比MaxWk",
//			"成交量占比MinWk",
//			"成交量占比增长率",
//
//			"OpenUpQueKou",
//			 "HuiBuDownQueKou",
//			 "OpenDownQueKou",
//			 "HuiBuUpQueKou",
//			 
//			 "涨停",
//			 "跌停",
//			 
//			 "成交额",
//			 "周平均成交额",
//			 "周平均成交额MaxWK",
//			 "成交额大盘贡献率",
//			 
//			 "成交量",
//			 "周平均成交量",
//			 "周平均成交量MaxWK",
//			 
//			 "涨跌幅",
//			 "开盘价",
//			 "最高价",
//			 "最低价",
//			 "收盘价",
//			 
//			 "周平均成交额增长率"
//			 
//			 "换手率",
//			 "自由流通换手率",
			
//			 "周平均流通市值",
			
			String [] header = ObjectArrays.concat(NodeXPeriodData.NODEXDATACSVDATAHEADLINE, StockNodesXPeriodData.NODEXDATACSVDATAHEADLINE, String.class);
			Object [] ouputcontrol = ObjectArrays.concat(NodeXPeriodData.ouputcontrol, StockNodesXPeriodData.ouputcontrol,Object.class);
			String [] color = ObjectArrays.concat(NodeXPeriodData.ouputcolor, StockNodesXPeriodData.ouputcolor,String.class);;
			for(int i=0;i<csvresult.length;i++) {
				if(ouputcontrol[i] == null)
					continue;
				
				try {
		    		org.jsoup.nodes.Element li =  dl.get(0).appendElement("li");
		    		if(ouputcontrol[i] instanceof Integer) {
		    			if(Integer.parseInt(csvresult[i]) == 0)
		    				continue;
		    			
		    			org.jsoup.nodes.Element font = li.appendElement("font");
						font.appendText(header[i] + ": " + csvresult[i] );
						font.attr("color", color[i]);
		    		}
		    		else if(ouputcontrol[i] instanceof DecimalFormat) {
		    			if(Double.parseDouble( csvresult[i]) == 0.0)
		    				continue;
		    			
		    			DecimalFormat formate = (DecimalFormat)ouputcontrol[i];
		    			org.jsoup.nodes.Element font = li.appendElement("font");
						font.appendText(header[i] + ": " + formate.format( Double.parseDouble( csvresult[i]) ) );
						font.attr("color", color[i]);
		    		}
				} catch (java.lang.IllegalArgumentException e)  { e.printStackTrace();
				} catch (java.lang.IndexOutOfBoundsException e) { e.printStackTrace();}
			}
			
			return doc.toString();
		}
	 
	 /*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getNodeXDataInHtml(java.time.LocalDate, int)
		 */
		public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference) 
		{
			String datahtml = super.getNodeXDataInHtml(superbk,requireddate,difference);
			
			org.jsoup.nodes.Document doc = Jsoup.parse(datahtml);
			org.jsoup.select.Elements content = doc.select("body");
			org.jsoup.select.Elements dl = content.select("dl");
			
			
			Double liutongshizhi = null; String liutongshizhidanwei = null;
			liutongshizhi = this.getSpecificTimeLiuTongShiZhi(requireddate, 0);
			if(liutongshizhi != null) {
					liutongshizhidanwei = FormatDoubleToShort.getNumberChineseDanWei(liutongshizhi);
					liutongshizhi = FormatDoubleToShort.formateDoubleToShort( liutongshizhi);
					
					try {
			    		DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
			        	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
			        	percentFormat.setMinimumFractionDigits(4);
						org.jsoup.nodes.Element li5 =  dl.get(0).appendElement("li");
						li5.appendText( "流通周平均市值" + decimalformate.format(liutongshizhi) + liutongshizhidanwei );
					} catch (java.lang.IllegalArgumentException e)  {
					} catch (java.lang.IndexOutOfBoundsException e) { e.printStackTrace();}
			}
	    	
				 
			Double hsl = null ;
			hsl = this.getSpecificTimeHuanShouLv(requireddate, 0);	 
			try {
					DecimalFormat decimalformate2 = new DecimalFormat("%.3f");
					if(hsl != null) {
						org.jsoup.nodes.Element li4 = dl.get(0).appendElement("li");
						li4.appendText( "换手率" + String.format("%.3f", hsl) );
					}
				} catch (java.lang.IllegalArgumentException e ) {
				} catch (java.lang.NullPointerException ex) {}
			
			Double hslf = null ;
			hslf = this.getSpecificTimeHuanShouLvFree(requireddate, 0);	 
			try {
					DecimalFormat decimalformate2 = new DecimalFormat("%.3f");
					if(hsl != null) {
						org.jsoup.nodes.Element li4 = dl.get(0).appendElement("li");
						li4.appendText( "自由流通换手率" + String.format("%.3f", hslf) );
					}
				} catch (java.lang.IllegalArgumentException e ) {
				} catch (java.lang.NullPointerException ex) {}
			
			return doc.toString();
		}

	 
		@Override
	public Boolean hasFxjgInPeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}
	
}