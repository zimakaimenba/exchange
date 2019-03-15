package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import com.exchangeinfomanager.commonlib.DayCounter;

public class QueKou 
{
	private Integer dbid;
	private String nodecode;
	private LocalDate quekoudate;
	private LocalDate huibudate;
	private Double quekoudown;
	private Double quekouup;
	private Boolean quekouleixing ;
	private Boolean shouldstoreindb;
	private Double shoupanjia;
	
	
	public QueKou (String nodecode,LocalDate quekoudate, Double down, Double up,Boolean qkleixing)
	{
		this.nodecode = nodecode;
		this.quekoudate = quekoudate;
		this.quekoudown = down;
		this.quekouup = up;
		this.quekouleixing = qkleixing;
		this.shouldstoreindb = true;
	}
	public String getNodeCode ()
	{
		return this.nodecode;
	}
	public void setDbId (int id) 
	{
		this.dbid = id;
		this.shouldstoreindb = false; //有dbid，默认为一般不会变化，不需要重新存储到数据库
	}
	public Integer getDbId ()
	{
		if(this.dbid == null)
			return null;
		else
			return this.dbid;
	}
	public void setShouPanJia (Double spj)
	{
		this.shoupanjia = spj;
	}
	public Double getShouPanJia ()
	{
		return this.shoupanjia;
	}
	public LocalDate getQueKouDate() {
		return quekoudate;
	}
	public void setQueKouHuiBuDate(LocalDate hbdate) {
		this.huibudate = hbdate;
	}
	public LocalDate getQueKouHuiBuDate() {
		return huibudate;
	}
	public Double getQueKouDown() {
		return quekoudown;
	}

	public Double getQueKouUp() {
		return quekouup;
	}
	/*
	 * 计算回补缺口的日期数
	 */
	public Integer getQueKouHuiBuDaysNumber ()
	{
		int range = DayCounter.bestDaysBetweenIngoreWeekEnd(quekoudate, huibudate);
		return range;
	}
	public Boolean getQueKouLeiXing ()
	{
		if(quekouleixing != null)
			return this.quekouleixing;
		else
			return null;
	}
	
	public String checkQueKouHuiBu (LocalDate checkdate,Double down, Double up)
	{
		if(this.getQueKouLeiXing() == null)
			return null;
		
		if(this.quekouleixing) { //向上跳空
			if(down <= this.quekoudown) {
				setHuiBuInfo (checkdate);				
				return "FULL";
			}
			else 
			if(down >= this.quekoudown && down <= this.quekouup) {//部分回补
				this.quekouup = down;
				this.shouldstoreindb = true;
				return "PART";
			}
			else
			if(down >= this.quekouup)
				return "NO";
		} else { //向下跳空
			if(up >= this.quekouup ) {
				setHuiBuInfo (checkdate);
				return "FULL";
			}
			else 
			if(up >= this.quekoudown && up <= this.quekouup) {
				this.quekoudown = up;
				this.shouldstoreindb = true;
				return "PART";
			}
			else
			if(up <= this.quekoudown)
				return "NO";
		}
		
		return null;
	}
	
	private void setHuiBuInfo(LocalDate checkdate) 
	{
		huibudate = checkdate;
		this.shouldstoreindb = true;
	}
	
	public Boolean isQueKouHuiBu ()
	{
		if(this.huibudate != null)
			return true;
		else
			return false;
	}
	public Boolean shouldUpdatedToDb ()
	{
		if(this.shouldstoreindb == null)
			return false;
		else
			return this.shouldstoreindb;
	}
	
}

