package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
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
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.google.common.base.Strings;

public class DisplayBkGgInfoEditorPane extends JEditorPane
{

	private BanKuaiDbOperation bkdbopt;
	private Boolean clearContentsBeforeDisplayNewInfo = false;
	private StockCalendarAndNewDbOperation newsdbopt;
	private static Logger logger = Logger.getLogger(DisplayBkGgInfoEditorPane.class);

	public DisplayBkGgInfoEditorPane()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		this.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		this.setEditable(false);
		creatEvents ();
	}
	
	public void displayNodeAllInfo (BkChanYeLianTreeNode curselectedbknodecode)
	{
		if(this.clearContentsBeforeDisplayNewInfo)
			this.setText("");
		
		this.displayChanYeLianNewsHtml(curselectedbknodecode);
		this.displayNodeBasicInfo(curselectedbknodecode);
		this.displayNodeZdgzMrMcZdgzYingKuiInfo (curselectedbknodecode);
		this.displayChanYeLianInfo (curselectedbknodecode);
	}
	/*
	 * 
	 */
	public void displayChanYeLianInfo (BkChanYeLianTreeNode curselectedbknodecode)
	{
		List<BkChanYeLianTreeNode> cylinfo = curselectedbknodecode.getNodeJiBenMian().getChanYeLianInfo();
		if(cylinfo == null  || cylinfo.isEmpty() )
			return;
		
		String htmlstring = this.getText();
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		content.append( "<h4>产业链信息</h4>");
		String result ="";
		for(BkChanYeLianTreeNode tmpinfo : cylinfo) {
			if(tmpinfo == null)
				continue;
			
			result =  result + tmpinfo.getMyOwnName() + "->";
		}
		content.append( "<p>" + result + "</p> ");
		
		htmlstring = doc.toString();
	    this.setText(htmlstring);
	}
	/*
	 * 
	 */
	public void displayAllNewsOfSpecificWeek (LocalDate displayeddate)
	{
		LocalDate monday = displayeddate.with( DayOfWeek.MONDAY );
		LocalDate sunday = displayeddate.with( DayOfWeek.SUNDAY );
		
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
		
		
		String htmlstring = this.getText();
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		       
		content.append( "<h4>周新闻:" + monday + "到" + sunday + "</h4>");
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
	   	this.setText(htmlstring);
	   	
	   	
	}
	 /*
     * 显示板块新闻连接
     */
    public void displayChanYeLianNewsHtml(BkChanYeLianTreeNode node)
    {
    	String curselectedbknodename = node.getMyOwnName();
	    String curbknodecode = node.getMyOwnCode();
	    
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
		
		ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache (node,svsnews,svslabel,LocalDate.now().minusMonths(3),LocalDate.now());
    	svsnews.setCache(newcache);
    	
    	ServicesForNews svscqgz = new ChangQiGuanZhuServices ();
    	NewsCache cqgzcache = new NewsCache (node,svscqgz,svslabel,LocalDate.now().minusMonths(3),LocalDate.now());
    	svscqgz.setCache(cqgzcache);
    	
    	ServicesForNews svsqs = new QiangShiServices ();
    	NewsCache qiangshicache = new NewsCache (node,svsqs,svslabel,LocalDate.now().minusMonths(3),LocalDate.now());
    	svsqs.setCache(qiangshicache);
    	
    	ServicesForNews svsrs = new RuoShiServices ();
    	NewsCache ruoshicache = new NewsCache (node,svsrs,svslabel,LocalDate.now().minusMonths(3),LocalDate.now());
    	svsrs.setCache(ruoshicache);
    	
    	ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
    	NewsCache dqgzcache = new NewsCache (node,svsdqgz,svslabel,LocalDate.now().minusMonths(3),LocalDate.now());
    	svsdqgz.setCache(dqgzcache);
    	
    	ServicesForNews svsbolang = new ZhiShuBoLangServices ();
    	NewsCache bolangcache = new NewsCache (node,svsbolang,svslabel,LocalDate.now().minusMonths(3),LocalDate.now());
    	svsbolang.setCache(dqgzcache);
    	
		
		Collection<News> curnewlist = new ArrayList<> ();
		curnewlist.addAll(newcache.produceNews());
		curnewlist.addAll(cqgzcache.produceNews());
		curnewlist.addAll(qiangshicache.produceNews());
		curnewlist.addAll(ruoshicache.produceNews());
		curnewlist.addAll(dqgzcache.produceNews());
		curnewlist.addAll(bolangcache.produceNews());
	    
	     String htmlstring = this.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		 logger.debug(doc.toString());
		 org.jsoup.select.Elements content = doc.select("body");
		       
		content.append( "<h4>\""+ node.getMyOwnCode() + node.getMyOwnName() + curselectedbknodename + "\"相关新闻</h4>");
    	for(News cylnew : curnewlist ) {
    		String title = cylnew.getTitle();
    		String newdate = cylnew.getStart().toString(); 
    		String slackurl = cylnew.getNewsUrl();
    		String keywords = cylnew.getKeyWords();
    		if(slackurl != null && !slackurl.isEmpty() )	    		
    			content.append( "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ");
    		else
    			content.append( "<p>" + newdate  + title + "</p> ");
    		//notesPane.setText("<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>");
    	}
    	
    	htmlstring = doc.toString();
    	this.setText(htmlstring);
    	
	   	curnewlist = null;
	}
    /*
     * 显示节点的基本信息
     */
    public void  displayNodeBasicInfo(BkChanYeLianTreeNode curselectedbknode)
    {
    		String curselectedbknodename = curselectedbknode.getMyOwnName();
	     	String curbknodecode = curselectedbknode.getMyOwnCode();
	       	int type = curselectedbknode.getType();

	       	String htmlstring = this.getText();
	       org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//	       logger.debug(doc.toString());
	       org.jsoup.select.Elements content = doc.select("body"); 
	       
	       String header = "<h4>\""+ curbknodecode + curselectedbknodename + "\"基本信息</h4>"; 
	       content.append(header);
	       
	       		if(!Strings.isNullOrEmpty( curselectedbknode.getNodeJiBenMian().getGainiantishi() ) ) 
				try {
					content.append( "<p>"+ "概念提示:" + Strings.nullToEmpty( curselectedbknode.getNodeJiBenMian().getGainiantishidate().toString()) + curselectedbknode.getNodeJiBenMian().getGainiantishi() + "</p>" );
				} catch (java.lang.NullPointerException e) {
					content.append("<p>"+ "概念提示:" +  Strings.nullToEmpty( "") + curselectedbknode.getNodeJiBenMian().getGainiantishi() + "</p>" );
				}
	       		if(!Strings.isNullOrEmpty(curselectedbknode.getNodeJiBenMian().getFumianxiaoxi() )  )
 	       		try {
					content.append( "<p>"+ "负面消息:" + Strings.nullToEmpty( curselectedbknode.getNodeJiBenMian().getFumianxiaoxidate().toString()) + curselectedbknode.getNodeJiBenMian().getFumianxiaoxi() + "</p>" );
				} catch (java.lang.NullPointerException e) {
					content.append("<p>"+ "负面消息:" +  Strings.nullToEmpty( "") + curselectedbknode.getNodeJiBenMian().getFumianxiaoxi() + "</p>" );
				}
	       		
	      		if(!Strings.isNullOrEmpty(curselectedbknode.getNodeJiBenMian().getQuanshangpingji() ) ) 
			try {
				content.append( "<p>"+  "券商评级" + Strings.nullToEmpty(curselectedbknode.getNodeJiBenMian().getQuanshangpingjidate().toString()) + " " + curselectedbknode.getNodeJiBenMian().getQuanshangpingji() + "</p>" );;
			} catch (java.lang.NullPointerException ex) {
				content.append( "<p>"+  "券商评级" + Strings.nullToEmpty("") + " " + curselectedbknode.getNodeJiBenMian().getQuanshangpingji() + "</p>" );;
			}
	      		if(!Strings.isNullOrEmpty(curselectedbknode.getNodeJiBenMian().getZhengxiangguan() ) )
 	      		content.append( "<p>"+  "正相关及客户(#" + Strings.nullToEmpty(curselectedbknode.getNodeJiBenMian().getZhengxiangguan() )
											 + " " + Strings.nullToEmpty(curselectedbknode.getNodeJiBenMian().getKeHuCustom() ) +  "#)" + "</p>" );
	      		if(!Strings.isNullOrEmpty(curselectedbknode.getNodeJiBenMian().getFuxiangguan() ) )
 	      		content.append( "<p>"+  "负相关及竞争对手(#" + Strings.nullToEmpty(curselectedbknode.getNodeJiBenMian().getFuxiangguan() )
 	      									 + " " + Strings.nullToEmpty(curselectedbknode.getNodeJiBenMian().getJingZhengDuiShou() ) + "#)" + "</p>" );

	     htmlstring = doc.toString();
	     this.setText(htmlstring);
//	     this.setCaretPosition(0);   
    }
    /*
     * 显示买卖关注等信息
     * 
     */
    public void displayNodeZdgzMrMcZdgzYingKuiInfo(BkChanYeLianTreeNode curselectedbknode)
    {
    	String curselectedbknodename = curselectedbknode.getMyOwnName();
       	String curbknodecode = curselectedbknode.getMyOwnCode();
       	int type = curselectedbknode.getType();

  		curselectedbknode =  bkdbopt.getZdgzMrmcZdgzYingKuiFromDB(curselectedbknode);
  		
	       String htmlstring = this.getText();
	       org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//	       logger.debug(doc.toString());
	       org.jsoup.select.Elements content = doc.select("body"); 
	       
	       content.append("<h4>\""+ curbknodecode + curselectedbknodename + "\"关注分析信息</h4>");
	       Object[][] sellbuyObjects = (curselectedbknode).getNodeJiBenMian().getZdgzMrmcZdgzYingKuiRecords();
	       for(int i=0;i<sellbuyObjects.length;i++) {
	    	   String output = "";
	    	   try {
	    		   output = sellbuyObjects[i][0].toString() + " " + sellbuyObjects[i][1].toString() + " " + sellbuyObjects[i][2].toString();
	    	   } catch (java.lang.NullPointerException e) {
//    			   e.printStackTrace();
    		   }
	    	   
	    	   content.append( "<p>"+ output + "</p>" );
	       }
	       
	       htmlstring = doc.toString();
		   this.setText(htmlstring);
//		   this.setCaretPosition(0); 
    }
    
    public void setClearContentsBeforeDisplayNewInfo (Boolean onoff)
    {
    	this.clearContentsBeforeDisplayNewInfo = onoff;
    }
    private void creatEvents()
	{
		this.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if(Desktop.isDesktopSupported()) {
					    try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
			        }
			}
		});
		
	}


}
