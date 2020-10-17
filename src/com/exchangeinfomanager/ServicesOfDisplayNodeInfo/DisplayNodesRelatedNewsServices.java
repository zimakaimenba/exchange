package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.Services.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;

public class DisplayNodesRelatedNewsServices implements ServicesOfNodeJiBenMianInfo
{
	private BkChanYeLianTreeNode node;
	private LocalDate requiredstart;
	private LocalDate requiredend;

	public DisplayNodesRelatedNewsServices (BkChanYeLianTreeNode node)
	{
		this.node = node;
	}
	public void setTimeRangeForInfoRange (LocalDate start , LocalDate end)
	{
		this.requiredstart = start;
		this.requiredend = end;
	}

	@Override
	public String getRequiredHtmlInfo() 
	{
		if(requiredstart == null)
			requiredstart = LocalDate.now();
		
		if(requiredend == null)
			requiredend = LocalDate.now().minusMonths(12);
		
		if(node instanceof DaPan)
			return this.getDaPanRequiredHtmlInfo( requiredstart,  requiredend);
		else 
			return this.getNodeRequiredHtmlInfo( requiredstart,  requiredend);
	}

	
	private String getDaPanRequiredHtmlInfo(LocalDate start, LocalDate end) 
	{
		LocalDate monday = start.with( DayOfWeek.MONDAY );
		LocalDate sunday = end.with( DayOfWeek.SUNDAY );
		
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
		
		ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache ("ALL",svsnews,svslabel,monday,sunday);
    	svsnews.setCache(newcache);
    	
    	ServicesForNews svscqgz = new ChangQiGuanZhuServices ();
    	NewsCache cqgzcache = new NewsCache ("ALL",svscqgz,svslabel,monday,sunday);
    	svscqgz.setCache(cqgzcache);
    	
    	ServicesForNews svsqs = new QiangShiServices ();
    	NewsCache qiangshicache = new NewsCache ("ALL",svsqs,svslabel,monday,sunday);
    	svsqs.setCache(qiangshicache);
    	
    	ServicesForNews svsrs = new RuoShiServices ();
    	NewsCache ruoshicache = new NewsCache ("ALL",svsrs,svslabel,monday,sunday);
    	svsrs.setCache(ruoshicache);
    	
    	ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,svslabel,monday,sunday);
    	svsdqgz.setCache(dqgzcache);
    	
    	ServicesForNews svsbolang = new ZhiShuBoLangServices ();
    	NewsCache bolangcache = new NewsCache ("ALL",svsbolang,svslabel,monday,sunday);
    	svsbolang.setCache(dqgzcache);
    	
		
		Collection<News> curnewlist = new ArrayList<> ();
		curnewlist.addAll(newcache.produceNews());
		curnewlist.addAll(cqgzcache.produceNews());
		curnewlist.addAll(qiangshicache.produceNews());
		curnewlist.addAll(ruoshicache.produceNews());
		curnewlist.addAll(dqgzcache.produceNews());
		curnewlist.addAll(bolangcache.produceNews());
		
		return this.createHtmlInfo (curnewlist,monday, sunday);
	}
	private String createHtmlInfo(Collection<News> curnewlist, LocalDate monday, LocalDate sunday) 
	{
		String htmlstring = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		       
		content.append( "<h4>" + node.getMyOwnCode() + " " + node.getMyOwnName() + "的新闻:" + monday + "到" + sunday + "</h4>");
		for(News cylnew : curnewlist ) {
	   		String title = cylnew.getTitle();
	   		String newdate = cylnew.getStart().toString(); 
	   		String slackurl = cylnew.getNewsUrl();
	   		if(slackurl != null && !slackurl.isEmpty() )	    		
	   			content.append( "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ");
	   		else
	   			content.append( "<p>" + newdate  + title + "</p> ");
		}
		
	   	htmlstring = doc.toString();

	   	return htmlstring;

	}
	private String getNodeRequiredHtmlInfo(LocalDate start, LocalDate end) 
	{
		LocalDate monday = start.with( DayOfWeek.MONDAY );
		LocalDate sunday = end.with( DayOfWeek.SUNDAY );
		
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
		
		ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache (node,svsnews,svslabel,monday,sunday);
    	svsnews.setCache(newcache);
    	
    	ServicesForNews svscqgz = new ChangQiGuanZhuServices ();
    	NewsCache cqgzcache = new NewsCache (node,svscqgz,svslabel,monday,sunday);
    	svscqgz.setCache(cqgzcache);
    	
    	ServicesForNews svsqs = new QiangShiServices ();
    	NewsCache qiangshicache = new NewsCache (node,svsqs,svslabel,monday,sunday);
    	svsqs.setCache(qiangshicache);
    	
    	ServicesForNews svsrs = new RuoShiServices ();
    	NewsCache ruoshicache = new NewsCache (node,svsrs,svslabel,monday,sunday);
    	svsrs.setCache(ruoshicache);
    	
    	ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
    	NewsCache dqgzcache = new NewsCache (node,svsdqgz,svslabel,monday,sunday);
    	svsdqgz.setCache(dqgzcache);
    	
    	ServicesForNews svsbolang = new ZhiShuBoLangServices ();
    	NewsCache bolangcache = new NewsCache (node,svsbolang,svslabel,monday,sunday);
    	svsbolang.setCache(dqgzcache);
    	
		
		Collection<News> curnewlist = new ArrayList<> ();
		curnewlist.addAll(newcache.produceNews());
		curnewlist.addAll(cqgzcache.produceNews());
		curnewlist.addAll(qiangshicache.produceNews());
		curnewlist.addAll(ruoshicache.produceNews());
		curnewlist.addAll(dqgzcache.produceNews());
		curnewlist.addAll(bolangcache.produceNews());
		
		return this.createHtmlInfo(curnewlist,monday, sunday);
		
	}
}
