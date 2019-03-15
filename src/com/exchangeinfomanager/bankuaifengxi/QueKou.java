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
		this.shouldstoreindb = false; //��dbid��Ĭ��Ϊһ�㲻��仯������Ҫ���´洢�����ݿ�
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
	 * ����ز�ȱ�ڵ�������
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
		
		if(this.quekouleixing) { //��������
			if(down <= this.quekoudown) {
				setHuiBuInfo (checkdate);				
				return "FULL";
			}
			else 
			if(down >= this.quekoudown && down <= this.quekouup) {//���ֻز�
				this.quekouup = down;
				this.shouldstoreindb = true;
				return "PART";
			}
			else
			if(down >= this.quekouup)
				return "NO";
		} else { //��������
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

