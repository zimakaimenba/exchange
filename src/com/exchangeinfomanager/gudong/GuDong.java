package com.exchangeinfomanager.gudong;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Interval;

import com.exchangeinfomanager.nodes.Stock;

public class GuDong 
{
	private JiGou gudong;
	private List<Interval> chigushijian;
	private Stock stock;
	private Double chigubili;
	
	public GuDong (Stock stock1, JiGou gd)
	{
		this.stock = stock1;
		this.gudong = gd;
		
		this.gudong.addChiCangGuPiao(this.stock);
	}
	public void addGuDongChiGuShiJianDuan (Interval chigu)
	{
		if(this.chigushijian == null)
			chigushijian = new ArrayList<> ();
		
		if(!chigushijian.contains(chigu))
			chigushijian.add(chigu);
	}
	
	public Boolean isGuDongAtSpecificPeriod (LocalDate requiredtime)
	{
		
	}
	
	public void setCurrentChiGuBiLi (Double bili)
	{
		this.chigubili = bili;
	}
	
}
