package com.exchangeinfomanager.nodes.nodejibenmian;

import java.time.LocalDate;

public class ShuJuJiLuInfo 
{
	LocalDate jyjlmaxdate; //交易数据
	LocalDate jyjlmindate;
	LocalDate neteasejlmaxdate; //网易数据
	LocalDate neteasejlmindate;
	LocalDate tushareextradatajlmaxdate; //tushare数据
	LocalDate tushareextradatajlmindate;
	String gupiaobankuaiduiyingbiao; //和数据库表相关，对应哪个表
	
	private Boolean hasreviewedtoday;
	public LocalDate getTushareextradatajlmindate() {
		return tushareextradatajlmindate;
	}
	public void setTushareextradatajlmindate(LocalDate tushareextradatajlmaxdate) {
		this.tushareextradatajlmindate = tushareextradatajlmaxdate;
	}
	public LocalDate getTushareextradatajlmaxdate() {
		return tushareextradatajlmaxdate;
	}
	public void setTushareextradatajlmaxdate(LocalDate tushareextradatajlmaxdate) {
		this.tushareextradatajlmaxdate = tushareextradatajlmaxdate;
	}
	public void setHasReviewedToday (Boolean reviewedornot)
	{
		this.hasreviewedtoday = reviewedornot;
	}
	public Boolean wetherHasReiewedToday ()
	{
		if(this.hasreviewedtoday == null)
			return false;
		else
			return this.hasreviewedtoday;
	}
	public String getGuPiaoBanKuaiDuiYingBiao() {
		return gupiaobankuaiduiyingbiao;
	}
	public void setGuPiaoBanKuaiDuiYingBiao(String gupiaobankuaiduiyingbiao) {
		this.gupiaobankuaiduiyingbiao = gupiaobankuaiduiyingbiao;
	}
	public LocalDate getJyjlmaxdate() {
		return jyjlmaxdate;
	}
	public void setJyjlmaxdate(LocalDate jyjlmaxdate) {
		this.jyjlmaxdate = jyjlmaxdate;
	}
	public LocalDate getJyjlmindate() {
		return jyjlmindate;
	}
	public void setJyjlmindate(LocalDate jyjlmindate) {
		this.jyjlmindate = jyjlmindate;
	}
	public LocalDate getNeteasejlmaxdate() {
		return neteasejlmaxdate;
	}
	public void setNeteasejlmaxdate(LocalDate neteasejlmaxdate) {
		this.neteasejlmaxdate = neteasejlmaxdate;
	}
	public LocalDate getNeteasejlmindate() {
		return neteasejlmindate;
	}
	public void setNeteasejlmindate(LocalDate neteasejlmindate) {
		this.neteasejlmindate = neteasejlmindate;
	}
	
}
