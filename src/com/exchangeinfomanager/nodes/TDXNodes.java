package com.exchangeinfomanager.nodes;

import java.time.LocalDate;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;

public abstract class TDXNodes extends BkChanYeLianTreeNode
{
	public TDXNodes (String myowncode, String myownname)
	{
		super (myowncode,myownname);
	}
	
	protected NodeXPeriodDataBasic nodewkdata;
	protected NodeXPeriodDataBasic nodedaydata;
	protected NodeXPeriodDataBasic nodemonthdata;
	
	private String suoshujiaoyisuo;
		
	public String getSuoShuJiaoYiSuo ()
	{
		return this.suoshujiaoyisuo;
	}
	public void setSuoShuJiaoYiSuo (String jys)
	{
		this.suoshujiaoyisuo = jys;
	}
	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
	{
		if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
			return nodewkdata;
		else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
			return nodemonthdata;
		else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
			return nodedaydata;
		else 
			return null;
	}
	
	public String getNodeXPeroidDataInHtml (LocalDate requireddate, String period)
	{
		String html;
		NodeXPeriodDataBasic nodexdata = this.getNodeXPeroidData(period);
		if(super.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = ( (StockNodeXPeriodData) nodexdata).getNodeXDataInHtml((DaPan)this.getRoot(),requireddate, 0);
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
	
}
