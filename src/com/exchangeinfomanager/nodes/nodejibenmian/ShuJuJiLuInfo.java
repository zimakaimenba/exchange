package com.exchangeinfomanager.nodes.nodejibenmian;

import java.time.LocalDate;

public class ShuJuJiLuInfo 
{
	LocalDate jyjlmaxdate;
	LocalDate jyjlmindate;
	LocalDate neteasejlmaxdate;
	LocalDate neteasejlmindate;
	String gupiaobankuaiduiyingbiao;
	
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
