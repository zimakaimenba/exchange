package com.exchangeinfomanager.nodes;

import java.time.LocalDate;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public abstract class TDXNodes extends BkChanYeLianTreeNode
{
	public TDXNodes (String myowncode, String myownname)
	{
		super (myowncode,myownname);
	}
	
	protected NodeXPeriodData nodewkdata;
	protected NodeXPeriodData nodedaydata;
	protected NodeXPeriodData nodemonthdata;
	
	private String suoshujiaoyisuo;
	private LocalDate lastdayofbxfx;
		
	public String getSuoShuJiaoYiSuo ()
	{
		return this.suoshujiaoyisuo;
	}
	public void setSuoShuJiaoYiSuo (String jys)
	{
		this.suoshujiaoyisuo = jys;
	}
	public NodeXPeriodData getNodeXPeroidData (String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.WEEK))
			return nodewkdata;
		else if(period.equals(NodeGivenPeriodDataItem.MONTH))
			return nodemonthdata;
		else if(period.equals(NodeGivenPeriodDataItem.DAY))
			return nodedaydata;
		else 
			return null;
	}
	
	public String getNodeXPeroidDataInHtml (LocalDate requireddate, String period)
	{
		String html;
		NodeXPeriodData nodexdata = this.getNodeXPeroidData(period);
		if(super.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html =  nodexdata.getNodeXDataInHtml((DaPan)this.getRoot(),requireddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml((DaPan)this.getRoot(),requireddate, 0);
		}
		
		org.jsoup.nodes.Document htmldoc = Jsoup.parse(html);
		org.jsoup.select.Elements htmlcontent = htmldoc.select("body");
		for(org.jsoup.nodes.Element htmlbody : htmlcontent) {
			org.jsoup.nodes.Element htmldiv = htmlbody.prependElement("div");
			htmldiv.append( super.getMyOwnCode() + super.getMyOwnName() );
		}	
		
		return htmldoc.toString();
	}
	
	/*
	 * 
	 */
	public Boolean isNodeDataAtNotCalWholeWeekMode ()
	{
		if(this.lastdayofbxfx == null)
			return false;
		else
			return true;
	}
	public void setNodeDataAtNotCalWholeWeekMode (LocalDate lastdayofbxfx)
	{
		this.lastdayofbxfx = lastdayofbxfx;
	}
	public LocalDate getNodeDataAtNotCalWholeWeekModeLastDate ()
	{
		return this.lastdayofbxfx;
	}
	
	
}
