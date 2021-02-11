package com.exchangeinfomanager.gudong;

import java.util.List;

import com.exchangeinfomanager.nodes.Stock;

public class JiGou 
{
	private String jigouname;
	private Boolean ismingxin;
	private int jgtype;
	private List<Stock> stocklist;
	
	public static int  GUOJIADUI = 3, GONGMUJIJIN = 4,  SIMUJIJIN = 7, GEREN = 6; 

	public JiGou (String name)
	{
		this.jigouname = name;
	}
	public void addChiCangGuPiao (Stock chicang)
	{
		this.stocklist.add(chicang);
	}
	public void setMingXing (Boolean mx) {
		this.ismingxin = mx;
	}
	public Boolean isMingXing ()
	{
		if( this.ismingxin == null)
			return false;
		else
			return this.ismingxin;
	}
	public void setJiGouType (int type)
	{
		this.jgtype = type;
	}
	public int getJiGouType ()
	{
		return this.jgtype;
	}
}
