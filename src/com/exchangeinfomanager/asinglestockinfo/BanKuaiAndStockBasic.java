package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

public interface BanKuaiAndStockBasic 
{
	public static int  DAPAN = 3, TDXBK = 4, SUBBK = 5, BKGEGU = 7, TDXGG = 6, GPC = 8, SUBGPC = 9;
	
	public int getType ();
//	public void setSuoSuTdxBanKuai (String tdxbkcode);
//	public String getSuoSuTdxBanKuai ();
	
	/**
	 * @return the bankuainame
	 */
	public String getMyOwnName() ;
	/**
	 * @param bankuainame the bankuainame to set
	 */
	public void setMyOwnName(String bankuainame) ;
	/**
	 * @return the bankuaicode
	 */
	public String getMyOwnCode() ;
	/**
	 * @return the bankuaicreatedtime
	 */
	public String getCreatedTime() ;
	/**
	 * @param bankuaicreatedtime the bankuaicreatedtime to set
	 */
	public void setCreatedTime(String bankuaicreatedtime) ;
	public boolean checktHanYuPingYin (String nameorhypy);
	
//	/**
//	 * @return the jingZhengDuiShou
//	 */
//	public String getJingZhengDuiShou() ;
//	/**
//	 * @param jingZhengDuiShou the jingZhengDuiShou to set
//	 */
//	public void setJingZhengDuiShou(String jingZhengDuiShou);
//	/**
//	 * @return the keHuCustom
//	 */
//	public String getKeHuCustom() ;
//	/**
//	 * @param keHuCustom the keHuCustom to set
//	 */
//	public void setKeHuCustom(String keHuCustom) ;
//
//	public LocalDate getGainiantishidate() ;
//	public void setGainiantishidate(LocalDate gainiantishidate);
//	public String getGainiantishi();
//	public void setGainiantishi(String gainiantishi) ;
//	public LocalDate getQuanshangpingjidate() ;
//	public void setQuanshangpingjidate(LocalDate quanshangpingjidate);
//	public String getQuanshangpingji() ;
//	public void setQuanshangpingji(String quanshangpingji) ;
//	public LocalDate getFumianxiaoxidate() ;
//	public void setFumianxiaoxidate(LocalDate fumianxiaoxidate) ;
//	public String getFumianxiaoxi() ;
//	public void setFumianxiaoxi(String fumianxiaoxi) ;
//	public String getZhengxiangguan() ;
//	public void setZhengxiangguan(String zhengxiangguan);
//	public String getFuxiangguan() ;
//	public void setFuxiangguan(String fuxiangguan) ;
	
	public interface NodeXPeriodDataBasic 
	{
		public String getNodeperiodtype();
		
		/*
		 * 获取数据
		 */
//		public ArrayList<ChenJiaoZhanBiInGivenPeriod> getNodePeriodRecords ();
		
		Double getChengJiaoEr (LocalDate requireddate,int difference);
		Double getChenJiaoErZhanBi (LocalDate requireddate,int difference);
		
		void addNewXPeriodData (StockGivenPeriodDataItem kdata);

		LocalDate getRecordsStartDate ();
		/*
		 * 
		 */
		LocalDate getRecordsEndDate ();
		
		Boolean hasRecordInThePeriod (LocalDate requireddate, int difference); 
		/*
		 * 在交易记录中找到对应周/日的位置,difference是偏移量，
		 */
//		protected Integer getRequiredRecordsPostion (LocalDate requireddate,int difference)

		/*
		 * 获得指定周的记录
		 */
//		StockGivenPeriodDataItem getSpecficRecord(LocalDate requireddate, int difference);
		/*
		 * 计算指定周期和上周期的成交额差额，适合stock/bankuai，dapan有自己的计算方法
		 */
		Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate);
		/*
		 * 对上级板块的成交额占比增速
		 */
		Double  getChenJiaoErZhanBiGrowthRateOfSuperBanKuai (LocalDate requireddate,int difference);
		/*
		 * 对上级板块的成交额占比是多少周内的最大值
		 */
		Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
		/*
		 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率
		 */
		Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (LocalDate requireddate,int difference);
		/*
		 * 成交额是多少周内的最大
		 * 
		 */
		Integer getChenJiaoErMaxWeekOfSuperBanKuai (LocalDate requireddate,int difference);
		/*
		 * 
		 */
		TimeSeries getRangeChengJiaoEr (LocalDate requiredstart,LocalDate requiredend);
		/*
		 * 
		 */
		TimeSeries getRangeChengJiaoErZhanBi (LocalDate requiredstart,LocalDate requiredend);
		/*
		 * 
		 */
		public OHLCSeries getRangeOHLCData (LocalDate requiredstart,LocalDate requiredend);
		/*
		 * 
		 */
		public OHLCItem getSpecificDateOHLCData (LocalDate requireddate,int difference);
		/*
		 * 
		 */
		public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference);
		/*
		 * 
		 */
		public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg);
		/*
		 * 一次性计算所有数据
		 */
//		ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate);
	}
	
}
