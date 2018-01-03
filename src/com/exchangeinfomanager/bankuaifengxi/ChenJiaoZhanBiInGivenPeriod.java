package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.exchangeinfomanager.commonlib.CommonUtility;

public class ChenJiaoZhanBiInGivenPeriod {

	public ChenJiaoZhanBiInGivenPeriod() 
	{
		// TODO Auto-generated constructor stub
	}
	
	private String myuplevelcode;

	private Double openprice;
	private Double closeprice;
	private Double highprice;
	private Double lowprice;
	private LocalDate  dayofendoftheweek;
	
	private Double myownchengjiaoer;
	private Double myownchengjiaoliang;
	private Double uplevelchengjiaoer; //上级板块的成交额
	private Double ggbkzhanbigrowthrate;  //个股板块占比增速
	private Integer ggbkzhanbimaxweek;  //个股板块占比最大周
	private Integer ggbkcjemaxweek; //个股板块成交额最大周
	private Double ggbkcjegrowthzhanbi;  //个股板块成交量贡献率
	private Double ggdpzhanbi;  //个股大盘占比
	private Double ggdpcjegrowthzhanbi;  //个股大盘成交量贡献率
	private Integer ggdpzhanbimaxweek;//个股大盘占比最大周
	private Double ggdpzhanbigrowthrate; //个股大盘占比增速
	private Boolean hasfengxijieguo; //有加入关注，分析结果等等
	
	private String myowncode;
	/**
	 * @return the myowncode
	 */
	public String getMyOwnCode() {
		return myowncode;
	}
	/**
	 * @param myowncode the myowncode to set
	 */
	public void setMyOwnCode(String myowncode) {
		this.myowncode = myowncode;
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
	public Double getCjlZhanBi()  
	{
		return this.myownchengjiaoer/this.uplevelchengjiaoer;
	}
	
	/**
	 * @return the ggdpcjegrowthzhanbi
	 */
	public Double getGgdpcjegrowthzhanbi() {
		return ggdpcjegrowthzhanbi;
	}
	/**
	 * @param ggdpcjegrowthzhanbi the ggdpcjegrowthzhanbi to set
	 */
	public void setGgdpcjegrowthzhanbi(Double ggdpcjegrowthzhanbi) {
		this.ggdpcjegrowthzhanbi = ggdpcjegrowthzhanbi;
	}
	/**
	 * @return the ggdpzhanbimaxweek
	 */
	public Integer getGgdpzhanbimaxweek() {
		return ggdpzhanbimaxweek;
	}
	/**
	 * @param ggdpzhanbimaxweek the ggdpzhanbimaxweek to set
	 */
	public void setGgdpzhanbimaxweek(Integer ggdpzhanbimaxweek) {
		this.ggdpzhanbimaxweek = ggdpzhanbimaxweek;
	}
	/**
	 * @return the ggdpzhanbi
	 */
	public Double getGgdpzhanbi() {
		return ggdpzhanbi;
	}
	/**
	 * @param ggdpzhanbi the ggdpzhanbi to set
	 */
	public void setGgdpzhanbi(Double ggdpzhanbi) {
		this.ggdpzhanbi = ggdpzhanbi;
	}
	
	/**
	 * @return the ggbkzhanbigrowthrate
	 */
	public Double getGgbkzhanbigrowthrate() {
		return ggbkzhanbigrowthrate;
	}
	/**
	 * @param ggbkzhanbigrowthrate the ggbkzhanbigrowthrate to set
	 */
	public void setGgbkzhanbigrowthrate(Double ggbkzhanbigrowthrate) {
		this.ggbkzhanbigrowthrate = ggbkzhanbigrowthrate;
	}
	/**
	 * @return the ggbkzhanbimaxweek
	 */
	public Integer getGgbkzhanbimaxweek() {
		return ggbkzhanbimaxweek;
	}
	/**
	 * @param ggbkzhanbimaxweek the ggbkzhanbimaxweek to set
	 */
	public void setGgbkzhanbimaxweek(Integer ggbkzhanbimaxweek) {
		this.ggbkzhanbimaxweek = ggbkzhanbimaxweek;
	}
	/**
	 * @return the ggbkcjegrowthzhanbi
	 */
	public Double getGgbkcjegrowthzhanbi() {
		return ggbkcjegrowthzhanbi;
	}
	/**
	 * @param ggbkcjegrowthzhanbi the ggbkcjegrowthzhanbi to set
	 */
	public void setGgbkcjegrowthzhanbi(Double ggbkcjegrowthzhanbi) {
		this.ggbkcjegrowthzhanbi = ggbkcjegrowthzhanbi;
	}
	/**
	 * @return the ggdpzhanbigrowthrate
	 */
	public Double getGgdpzhanbigrowthrate() {
		return ggdpzhanbigrowthrate;
	}
	/**
	 * @param ggdpzhanbigrowthrate the ggdpzhanbigrowthrate to set
	 */
	public void setGgdpzhanbigrowthrate(Double ggdpzhanbigrowthrate) {
		this.ggdpzhanbigrowthrate = ggdpzhanbigrowthrate;
	}
	/**
	 * @return the ggbkcjemaxweek
	 */
	public Integer getGgbkcjemaxweek() {
		return ggbkcjemaxweek;
	}
	/**
	 * @param ggbkcjemaxweek the ggbkcjemaxweek to set
	 */
	public void setGgbkcjemaxweek(Integer ggbkcjemaxweek) {
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

	

}
