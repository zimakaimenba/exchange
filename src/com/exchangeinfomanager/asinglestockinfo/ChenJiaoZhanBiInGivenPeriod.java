package com.exchangeinfomanager.asinglestockinfo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.exchangeinfomanager.commonlib.CommonUtility;

public class ChenJiaoZhanBiInGivenPeriod 
{

	public ChenJiaoZhanBiInGivenPeriod(String nodecode,String datatype) 
	{
		
		this.myowncode = nodecode;
		if( datatype.toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.DAY)
				|| datatype.toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.WEEK)
				|| datatype.toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.SIXTYMINUTES))
			this.dataperiodtype = datatype;
		else
			this.dataperiodtype = null;
		
	}
	public static String  DAY = "DAY", WEEK = "WEEK", SIXTYMINUTES = "SIXTYMINUTES", MONTH= "MONTH";
	private String dataperiodtype; //日线，周线，月线，年线
	
	private String myuplevelcode;

	private Double openprice;
	private Double closeprice;
	private Double highprice;
	private Double lowprice;
	private LocalDate  dayofendoftheweek;
	
	private Double myownchengjiaoliang; //成交量
	private Double uplevelchengjiaoliang; //上级板块的成交量
	private Double myownchengjiaoer; //成交额
	private Double uplevelchengjiaoer; //上级板块的成交额
	private Double ggbkcjezhanbigrowthrate;  //个股板块占比增速
	private Integer ggbkcjezhanbimaxweek;  //个股板块占比最大周
	private Integer ggbkcjemaxweek; //个股板块成交额最大周
	private Double ggbkcjegrowthratetosuperbankuaicjegrowth;  //个股板块成交量贡献率
//	private Double ggdpcjezhanbi;  //个股成交额大盘占比
//	private Double ggdpcjegrowthzhanbi;  //个股大盘成交量贡献率
//	private Integer ggdpcjezhanbimaxweek;//个股大盘占比最大周
//	private Double ggdpcjezhanbigrowthrate; //个股大盘占比增速
	
	private Boolean hasfengxijieguo; //有加入关注，分析结果等等
	
	private String myowncode;
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
	public void setMyOwnChengJiaoEr(Double myownchengjiaoliang) {
		this.myownchengjiaoer = myownchengjiaoliang;
	}
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
		return this.myownchengjiaoer/this.uplevelchengjiaoer;
	}
	
	public Double getMyownchengjiaoliang() {
		return myownchengjiaoliang;
	}
	public void setMyownchengjiaoliang(Double myownchengjiaoliang) {
		this.myownchengjiaoliang = myownchengjiaoliang;
	}
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
		return this.myownchengjiaoliang/this.uplevelchengjiaoliang;
	}
	
	
//	/**
//	 * @return the ggdpcjegrowthzhanbi
//	 */
//	public Double getGgdpcjegrowthzhanbi() {
//		return ggdpcjegrowthzhanbi;
//	}
//	/**
//	 * @param ggdpcjegrowthzhanbi the ggdpcjegrowthzhanbi to set
//	 */
//	public void setGgdpcjegrowthzhanbi(Double ggdpcjegrowthzhanbi) {
//		this.ggdpcjegrowthzhanbi = ggdpcjegrowthzhanbi;
//	}
//	/**
//	 * @return the ggdpzhanbimaxweek
//	 */
//	public Integer getGgdpzhanbimaxweek() {
//		return ggdpcjezhanbimaxweek;
//	}
//	/**
//	 * @param ggdpzhanbimaxweek the ggdpzhanbimaxweek to set
//	 */
//	public void setGgdpzhanbimaxweek(Integer ggdpzhanbimaxweek) {
//		this.ggdpcjezhanbimaxweek = ggdpzhanbimaxweek;
//	}
//	/**
//	 * @return the ggdpzhanbi
//	 */
//	public Double getGgdpzhanbi() {
//		return ggdpcjezhanbi;
//	}
//	/**
//	 * @param ggdpzhanbi the ggdpzhanbi to set
//	 */
//	public void setGgdpzhanbi(Double ggdpzhanbi) {
//		this.ggdpcjezhanbi = ggdpzhanbi;
//	}
	
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
	public void setFengXiJIeGuo (Boolean fxjg)
	{
		this.hasfengxijieguo =  fxjg;
		
	}
	public Boolean hasFengXiJieGuo ()
	{
		if(this.hasfengxijieguo != null)
			return this.hasfengxijieguo;
		else
			return false;
	}
	
	public Double getOpenPrice() {
		return openprice;
	}
	public void setOpenPrice(Double openprice) {
		this.openprice = openprice;
	}
	public Double getClosePrice() {
		return closeprice;
	}
	public void setClosePrice(Double closeprice) {
		this.closeprice = closeprice;
	}
	public Double getHighPrice() {
		return highprice;
	}
	public void setHighPrice(Double highprice) {
		this.highprice = highprice;
	}
	public Double getLowPrice() {
		return lowprice;
	}
	public void setLowPrice(Double lowprice) {
		this.lowprice = lowprice;
	}
	/*
	 * 本周起成交额和合理的上周期(个股未停牌前提下)的差额
	 */
	public void setGgBkCjeDifferenceWithLastPeriod(double d) {
		this.ggbkcjedifference = d;
		
	}

	

}
