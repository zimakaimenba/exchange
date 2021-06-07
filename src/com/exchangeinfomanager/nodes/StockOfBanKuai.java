package com.exchangeinfomanager.nodes;


import java.time.LocalDate;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.NodesServices.ServicesForNode;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockOfBanKuaiXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
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
		
		super.nodewkdata = new StockOfBanKuaiXPeriodDataForJFC (stock1.getMyOwnCode(),bankuai1.getMyOwnCode(),NodeGivenPeriodDataItem.WEEK);
		super.nodedaydata = null; //对绝大多数分析来说说，周线数据才有意义，日线毫无意义，特别是个股相对于板块的日线数据。
//		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
	}
	
//	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
//	private LocalDate joindate;
//	private LocalDate leftdate;
	private Stock stock;
	private Integer quanzhong;
	private Boolean isbklongtou;
	private Set<Interval> inAndOutBanKuaiInterval ; 
	
	public void addInAndOutBanKuaiInterval (Interval newitvl)
	{
		if(this.inAndOutBanKuaiInterval == null) 
			this.inAndOutBanKuaiInterval = new HashSet<Interval> ();

		this.inAndOutBanKuaiInterval.add(newitvl);
	}
	public Set<Interval> getInAndOutBanKuaiInterval ()
	{
		return this.inAndOutBanKuaiInterval;
	}
	public Boolean isInBanKuaiAtSpecificDate (LocalDate checkdate) 
	{
		if(inAndOutBanKuaiInterval == null)
			return false;
		
		Boolean inbk = false;
		for(Interval tmpintvl : this.inAndOutBanKuaiInterval) {
			DateTime checkdt= new DateTime(checkdate.getYear(), checkdate.getMonthValue(), checkdate.getDayOfMonth(), 0, 0, 0, 0);
			if (tmpintvl.contains(checkdt) || tmpintvl.getEnd().isEqual(checkdt)) {
				inbk = true;
				break;
			}
		}
		
		return inbk;
	}
	
//	public LocalDate getJoinBanKuaiDate() {
//		return joindate;
//	}
//	public void setJoinBanKuaiDate(LocalDate joindate) {
//		this.joindate = joindate;
//	}
//	public LocalDate getLeftBanKuaiDate() {
//		return leftdate;
//	}
//	public void setLeftBanKuaiDate(LocalDate leftdate) {
//		this.leftdate = leftdate;
//	}
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
	@Override
	public ServicesForNode getServicesForNode(Boolean getornot) {
		// TODO Auto-generated method stub
		return null;
	}
	
}