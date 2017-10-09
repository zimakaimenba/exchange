package com.exchangeinfomanager.bankuaifengxi;

import java.util.Date;

public class ChenJiaoZhanBiInGivenPeriod {

	public ChenJiaoZhanBiInGivenPeriod() 
	{
		// TODO Auto-generated constructor stub
	}
	
	private String myuplevelcode;
	private int year;
	private int week;
	private Date dayofendoftheweek;
	private Double myownchengjiaoer;
	private Double uplevelchengjiaoer;
	private Double cjlratio;
	
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
	/**
	 * @return the myuplevelcode
	 */
	public String getMyUpLevelCode() {
		return myuplevelcode;
	}
	/**
	 * @param myuplevelcode the myuplevelcode to set
	 */
	public void setMyUpLevelCode(String myuplevelcode) {
		this.myuplevelcode = myuplevelcode;
	}
	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * @return the week
	 */
	public int getWeek() {
		return week;
	}
	/**
	 * @param week the week to set
	 */
	public void setWeek(int week) {
		this.week = week;
	}
	/**
	 * @return the dayofendoftheweek
	 */
	public Date getDayofEndofWeek() {
		return dayofendoftheweek;
	}
	/**
	 * @param dayofendoftheweek the dayofendoftheweek to set
	 */
	public void setDayofEndofWeek(Date dayofendoftheweek) {
		this.dayofendoftheweek = dayofendoftheweek;
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
	 * @return the cjlration
	 */
	public Double getCjlZhanBi() {
		return this.myownchengjiaoer/this.uplevelchengjiaoer;
	}

	

}
