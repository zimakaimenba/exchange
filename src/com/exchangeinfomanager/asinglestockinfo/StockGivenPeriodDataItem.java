package com.exchangeinfomanager.asinglestockinfo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.commonlib.CommonUtility;

public class StockGivenPeriodDataItem extends OHLCItem
{											  

	
	public StockGivenPeriodDataItem(String nodecode,String datatype,RegularTimePeriod period, double open, double high, double low, double close,double myamount,double myvolumne) 
	{
		super (period,open,high,low,close);
		
		this.myowncode = nodecode;
		this.myownchengjiaoer = myamount;
		this.myownchengjiaoliang = myvolumne;
		this.regulartimeperiod = period;
		
		if( datatype.toUpperCase().equals(StockGivenPeriodDataItem.DAY)
				|| datatype.toUpperCase().equals(StockGivenPeriodDataItem.WEEK)
				|| datatype.toUpperCase().equals(StockGivenPeriodDataItem.SIXTYMINUTES))
			this.dataperiodtype = datatype;
		else
			this.dataperiodtype = null;
	}
	
	public static String  DAY = "DAY", WEEK = "WEEK", SIXTYMINUTES = "SIXTYMINUTES", MONTH= "MONTH";
	private String dataperiodtype; //日线，周线，月线，年线
	private RegularTimePeriod regulartimeperiod;
	
	private String myowncode;
	private String myuplevelcode;

	private LocalDate  dayofendoftheweek;
	
	private Double myownchengjiaoliang; //成交量
	private Double uplevelchengjiaoliang; //上级板块的成交量
	private Double myownchengjiaoer; //成交额
	private Double uplevelchengjiaoer; //上级板块的成交额
	private Double ggbkcjezhanbigrowthrate;  //个股板块占比增速
	private Integer ggbkcjezhanbimaxweek;  //个股板块占比最大周
	private Integer ggbkcjemaxweek; //个股板块成交额最大周
	private Double ggbkcjegrowthratetosuperbankuaicjegrowth;  //个股板块成交量贡献率
	
	private Integer hasfengxijieguo; //有加入关注，分析结果等等
	
	
	private double ggbkcjedifference; //个股的成交额本周期和合理的上周期的差额
	/**
	 * @return the myowncode
	 */
	public String getMyOwnCode() {
		return myowncode;
	}
	/**
	 * @param myowncode the myowncode to set
	 */
//	public void setMyOwnCode(String myowncode) {
//		this.myowncode = myowncode;
//	}
	public String getRecordsType () {
		return this.dataperiodtype;
	}
//	/**
//	 * @return the myuplevelcode
//	 */
//	public String getMyUpLevelCode() {
//		return myuplevelcode;
//	}
//	/**
//	 * @param myuplevelcode the myuplevelcode to set
//	 */
//	public void setMyUpLevelCode(String myuplevelcode) {
//		this.myuplevelcode = myuplevelcode;
//	}
	/**
	 * @return the year
	 */
	public int getRecordsYear() 
	{
		return this.dayofendoftheweek.getYear();
	}
	public int getRecordsMonth () 
	{
		return this.dayofendoftheweek.getMonthValue();
	}
	/**
	 * @return the week
	 */
	public int getRecordsWeek() 
	{
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		int weekNumber = this.dayofendoftheweek.get(weekFields.weekOfWeekBasedYear());
		return weekNumber;
	}
	/**
	 * @return the dayofendoftheweek
	 */
	public LocalDate getRecordsDayofEndofWeek() {
		
		return dayofendoftheweek;
	}
	/**
	 * @param dayofendoftheweek the dayofendoftheweek to set
	 */
	public void setRecordsDayofEndofWeek(java.sql.Date dayofendoftheweek) 
	{
		try {
			this.dayofendoftheweek = dayofendoftheweek.toLocalDate();
//			this.dayofendoftheweek = dayofendoftheweek.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return the myownchengjiaoliang
	 */
	public Double getMyOwnChengJiaoEr() {
		return myownchengjiaoer;
	}
	/**
	 * @param myownchengjiaoliang the myownchengjiaoliang to set
	 */
//	public void setMyOwnChengJiaoEr(Double myownchengjiaoliang) {
//		this.myownchengjiaoer = myownchengjiaoliang;
//	}
	/**
	 * @return the uplevelchengjiaoliang
	 */
	public Double getUpLevelChengJiaoEr() {
		return uplevelchengjiaoer;
	}
	/**
	 * @param uplevelchengjiaoliang the uplevelchengjiaoliang to set
	 */
	public void setUpLevelChengJiaoEr(Double uplevelchengjiaoliang) {
		this.uplevelchengjiaoer = uplevelchengjiaoliang;
	}

	/**
	 * @板块或个股和他上级的大盘或板块的成交额的比值
	 */
	public Double getCjeZhanBi()  
	{
		try {
			return this.myownchengjiaoer/this.uplevelchengjiaoer;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	
	public Double getMyownchengjiaoliang() {
		return myownchengjiaoliang;
	}
//	public void setMyownchengjiaoliang(Double myownchengjiaoliang) {
//		this.myownchengjiaoliang = myownchengjiaoliang;
//	}
	public Double getUplevelchengjiaoliang() {
		return uplevelchengjiaoliang;
	}
	public void setUplevelchengjiaoliang(Double uplevelchengjiaoliang) {
		this.uplevelchengjiaoliang = uplevelchengjiaoliang;
	}
	/*
	 * 个股/板块的成交量占比
	 */
	public Double getCjlZhanBi()  
	{
		try {
			return this.myownchengjiaoliang/this.uplevelchengjiaoliang;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
			
	}
	

	/**
	 * @return the ggbkzhanbigrowthrate
	 */
	public Double getGgBkCjeZhanbiGrowthRate() {
		return ggbkcjezhanbigrowthrate;
	}
	/**
	 * @param ggbkzhanbigrowthrate the ggbkzhanbigrowthrate to set
	 */
	public void setGgBkCjeZhanbiGrowthRate(Double ggbkzhanbigrowthrate) {
		this.ggbkcjezhanbigrowthrate = ggbkzhanbigrowthrate;
	}
	/**
	 * @return the ggbkzhanbimaxweek
	 */
	public Integer getGgBkCjeZhanbiMaxweek() {
		return ggbkcjezhanbimaxweek;
	}
	/**
	 * @param ggbkzhanbimaxweek the ggbkzhanbimaxweek to set
	 */
	public void setGgBkCjeZhanbiMaxweek(Integer ggbkzhanbimaxweek) {
		this.ggbkcjezhanbimaxweek = ggbkzhanbimaxweek;
	}
	/**
	 * @return the ggbkcjegrowthzhanbi
	 */
	public Double getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth() {
		return ggbkcjegrowthratetosuperbankuaicjegrowth;
	}
	/**
	 * @param ggbkcjegrowthzhanbi the ggbkcjegrowthzhanbi to set
	 */
	public void setGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth(Double ggbkcjegrowthzhanbi) {
		this.ggbkcjegrowthratetosuperbankuaicjegrowth = ggbkcjegrowthzhanbi;
	}
//	/**
//	 * @return the ggdpzhanbigrowthrate
//	 */
//	public Double getGgdpzhanbigrowthrate() {
//		return ggdpcjezhanbigrowthrate;
//	}
//	/**
//	 * @param ggdpzhanbigrowthrate the ggdpzhanbigrowthrate to set
//	 */
//	public void setGgdpzhanbigrowthrate(Double ggdpzhanbigrowthrate) {
//		this.ggdpcjezhanbigrowthrate = ggdpzhanbigrowthrate;
//	}
	/**
	 * @return the ggbkcjemaxweek
	 */
	public Integer getGgBkCjeMaxweek() {
		return ggbkcjemaxweek;
	}
	/**
	 * @param ggbkcjemaxweek the ggbkcjemaxweek to set
	 */
	public void setGgBkCjeMaxweek(Integer ggbkcjemaxweek) {
		this.ggbkcjemaxweek = ggbkcjemaxweek;
	}
	public void setFengXiJIeGuo (Integer fxjg)
	{
		this.hasfengxijieguo =  fxjg;
		
	}
	public Integer getFengXiJieGuo ()
	{
		return this.hasfengxijieguo;
	}
	
	/*
	 * 本周起成交额和合理的上周期(个股未停牌前提下)的差额
	 */
	public void setGgBkCjeDifferenceWithLastPeriod(double d) {
		this.ggbkcjedifference = d;
		
	}

	

}
