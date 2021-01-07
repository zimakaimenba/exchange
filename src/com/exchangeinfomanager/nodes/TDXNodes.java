package com.exchangeinfomanager.nodes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.Interval;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.collect.Range;

public abstract class TDXNodes extends BkChanYeLianTreeNode
{
	public TDXNodes (String myowncode, String myownname)
	{
		super (myowncode,myownname);
	}
	
	protected NodeXPeriodData nodewkdata;
	protected NodeXPeriodData nodedaydata;
	protected NodeXPeriodData nodemonthdata;
	
	protected Collection<Tag> nodetags;
	
	private String suoshujiaoyisuo;
	private LocalDate lastdayofbxfx;
	
	private List<Range<LocalDate>> qiangshirange;
	private List<Range<LocalDate>> ruoshirange;
	private List<Range<LocalDate>> dqguangzhurange;
	
	private Boolean hasreviewedtoday;
	public void setHasReviewedToday (Boolean reviewedornot)
	{
		this.hasreviewedtoday = reviewedornot;
	}
	public Boolean wetherHasReiewedToday ()
	{
		if(this.hasreviewedtoday == null)
			return false;
		else
			return this.hasreviewedtoday;
	}
	public void addNewDuanQiGuanZhuRange (Range<LocalDate> inter) 
	{
//		for(int i=0; i<qiangshiinterval.size();i++) {
//			Interval tmpin = qiangshiinterval.get(i);
//			
//		}
		if(dqguangzhurange == null)
			dqguangzhurange = new ArrayList<> ();
		
		if( !dqguangzhurange.contains(inter) )
			dqguangzhurange.add(inter);
	}
	public Range<LocalDate> isInDuanQiGuanZhuRange (LocalDate date) 
	{
		if(dqguangzhurange == null)
			return null;
		
		for(int i=0; i<dqguangzhurange.size();i++) {
			Range<LocalDate> tmpin = dqguangzhurange.get(i);
			if( tmpin.contains(date) ) {
				return tmpin;
			}
		}
		
		return null;
	}
	public Boolean isEverBeingDuanQiGuanZhu ()
	{
		if(this.dqguangzhurange != null && dqguangzhurange.size() >0 )
			return true;
		else
			return false;
	}
	public void addNewQiangShiRange (Range<LocalDate> inter) 
	{
//		for(int i=0; i<qiangshiinterval.size();i++) {
//			Interval tmpin = qiangshiinterval.get(i);
//			
//		}
		if(qiangshirange == null)
			qiangshirange = new ArrayList<> ();
		
		if( !qiangshirange.contains(inter) )
			qiangshirange.add(inter);
	}
	public Range<LocalDate> isInQiangShiBanKuaiRange (LocalDate date)
	{
		if(qiangshirange == null)
			return null;
		
		for(int i=0; i<qiangshirange.size();i++) {
			Range<LocalDate> tmpin = qiangshirange.get(i);
			if( tmpin.contains(date) ) {
				return tmpin;
			}
		}
		
		return null;
	}
	public void addNewRuoShiRange (Range<LocalDate> inter) 
	{
//		for(int i=0; i<qiangshiinterval.size();i++) {
//			Interval tmpin = qiangshiinterval.get(i);
//			
//		}
		if(ruoshirange == null)
			ruoshirange = new ArrayList<> ();
		
		if( !ruoshirange.contains(inter) )
			ruoshirange.add(inter);
	}
	public Range<LocalDate> isInRuoShiBanKuaiRange (LocalDate date) 
	{
		if(ruoshirange == null)
			return null;
		
		for(int i=0; i<ruoshirange.size();i++) {
			Range<LocalDate> tmpin = ruoshirange.get(i);
			if(tmpin.contains(date) )
				return tmpin;
		}
		
		return null;
	}
		
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
	
	public void setNodeTags (Collection<Tag> tags)
	{
		this.nodetags = tags;
	}
	public  Collection<Tag> getNodeTags () {
		return this.nodetags;
	}
	public boolean isTagInCurrentNodeTags (Tag tag)
	{
		if(this.nodetags.contains(tag))
			return true;
		else
			return false;
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
