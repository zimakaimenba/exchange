package com.exchangeinfomanager.nodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeriesDataItem;

import com.exchangeinfomanager.nodes.nodexdata.StockOfBanKuaiNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
/*
 * 板块的个股和普通大盘的个股有少数相似，但不同就在成交量，一个是对板块的成交量，一个是对大盘的成交量。
 * StockOfBanKuai 不应该有如流通市值，总市值这些属性，应该只有成交量和成交额属性，还有权重，
 */
public class StockOfBanKuai extends TDXNodes
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		super.nodetype = BkChanYeLianTreeNode.BKGEGU;
		
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (stock1.getMyOwnCode(),bankuai1.getMyOwnCode(),TDXNodeGivenPeriodDataItem.WEEK);
		super.nodedaydata = null; //对绝大多数分析来说说，周线数据才有意义，日线毫无意义，特别是个股相对于板块的日线数据。
//		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		
		super.nodetreerelated = null;
		super.nodetreerelated = new StockOfBanKuaiTreeRelated(this);
	}
	
//	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private LocalDate joindate;
	private LocalDate leftdate;
	private Stock stock;
	private Integer quanzhong;
	private Boolean isbklongtou;
	
	public LocalDate getJoinBanKuaiDate() {
		return joindate;
	}
	public void setJoinBanKuaiDate(LocalDate joindate) {
		this.joindate = joindate;
	}
	public LocalDate getLeftBanKuaiDate() {
		return leftdate;
	}
	public void setLeftBanKuaiDate(LocalDate leftdate) {
		this.leftdate = leftdate;
	}
	public Boolean isBkLongTou ()
	{
		if(this.isbklongtou == null)
			return false;
		else
			return this.isbklongtou;
	}
	public void setBkLongTou (Boolean isbklongtou1)
	{
		this.isbklongtou = isbklongtou1; 
	}
	
	public void setStockQuanZhong (Integer quanzhong)
	{
		this.quanzhong = quanzhong; 
		
	}
	public Stock getStock ()
	{
		return this.stock;
	}
	public BanKuai getBanKuai ()
	{
		return this.bankuai;
	}
	public Integer getQuanZhong ()
	{
		return this.quanzhong;
	}
	
}