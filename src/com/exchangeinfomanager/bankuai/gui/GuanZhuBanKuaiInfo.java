package com.exchangeinfomanager.bankuai.gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuanZhuBanKuaiInfo 
{

	public GuanZhuBanKuaiInfo() 
	{
		
	}
	private String tdxbk;
	private String bkchanyelian;
	private Date selectedtime;
	/**
	 * @return the tdxbk
	 */
	public String getTdxbk() {
		return tdxbk;
	}
	/**
	 * @param tdxbk the tdxbk to set
	 */
	public void setTdxbk(String tdxbk) {
		this.tdxbk = tdxbk;
	}
	/**
	 * @return the bkchanyelian
	 */
	public String getBkchanyelian() {
		return bkchanyelian;
	}
	/**
	 * @param bkchanyelian the bkchanyelian to set
	 */
	public void setBkchanyelian(String bkchanyelian) {
		this.bkchanyelian = bkchanyelian;
	}
	/**
	 * @return the selectedtime
	 */
	public String getSelectedtime() {
		return this.formatDate(selectedtime);
	}
	/**
	 * @param selectedtime the selectedtime to set
	 */
	public void setSelectedtime(Date selectedtime) {
		this.selectedtime = selectedtime;
	}
	public void setSelectedtime(String selectedtime2) {
		this.selectedtime = formatString(selectedtime2);
	}
	private String formatDate(Date tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
		//return formatterhwy;
	}
	private Date formatString(String selectedtime2)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		Date date = new Date() ;
		try {
			date = formatterhwy.parse(selectedtime2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	
	

}
