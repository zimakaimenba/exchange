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

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
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
		
		EventService eventDbService = new DBMeetingService ();

		Integer[] wantednewstypeforall = {Integer.valueOf(Meeting.NODESNEWS), Integer.valueOf(Meeting.CHANGQIJILU), Integer.valueOf(Meeting.JINQIGUANZHU),
				Integer.valueOf(Meeting.QIANSHI),Integer.valueOf(Meeting.RUOSHI)   };
        Cache cache = new Cache("ALL",eventDbService, null,monday,sunday,wantednewstypeforall);
		
		Collection<InsertedMeeting> curnewlist = cache.produceMeetings();
		
		String htmlstring = this.getText();
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		       
		content.append( "<h4>周新闻:" + monday + "到" + sunday + "</h4>");
		for(InsertedMeeting cylnew : curnewlist ) {
	   		String title = cylnew.getTitle();
	   		String newdate = cylnew.getStart().toString(); 
	   		String slackurl = cylnew.getSlackUrl();
	   		if(slackurl != null && !slackurl.isEmpty() )	    		
	   			content.append( "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ");
	   		else
	   			content.append( "<p>" + newdate  + title + "</p> ");
		}
		
		//说明该周的指数关键日期
		Integer[]  wantednewstypeforall2 = { Integer.valueOf(Meeting.ZHISHUDATE)  };
        Cache cachezhishu = new Cache("ALL",eventDbService, null,monday,sunday,wantednewstypeforall2);
        
		Collection<InsertedMeeting> curzhishuguanjianriqi = cachezhishu.produceMeetings();
		if(curzhishuguanjianriqi.size() >=1) {
			content.append( "<h4>周指数关键日期:" + monday + "到" + sunday + "</h4>");
			for(InsertedMeeting zsgjrq : curzhishuguanjianriqi ) {
		   		String title = zsgjrq.getTitle();
		   		String newstartdate = zsgjrq.getStart().toString();
		   		try {
		   			String newenddate = zsgjrq.getEnd().toString();
		   			String detail =zsgjrq.getDescription();
		   			content.append( "<p>" + newstartdate + "~" + newenddate +":" + title + ",\"" + detail + "\"</p> ");
		   		} catch (java.lang.NullPointerException e ) {
		   			String detail =zsgjrq.getDescription();
		   			content.append( "<p>" + newstartdate  +":" + title + ",\"" + detail + "\"</p> ");
		   		}
		   		
			}
		}
		
		
	   	htmlstring = doc.toString();
	   	this.setText(htmlstring);
	   	
	   	eventDbService = null;
	   	wantednewstypeforall = null;
	   	cache = null;
	   	curnewlist = null;
	   	
	   	wantednewstypeforall2 = null;
	   	cachezhishu = null;
	   	curzhishuguanjianriqi = null;
	   	
	}
	 /*
     * 显示板块新闻连接
     */
    public void displayChanYeLianNewsHtml(BkChanYeLianTreeNode curselectedbknodecode)
    {
    	String curselectedbknodename = curselectedbknodecode.getMyOwnName();
	    String curbknodecode = curselectedbknodecode.getMyOwnCode();
	    
	    EventService eventDbService = new DBMeetingService ();
//    	LabelService labelService = new DBLabelService ();
		Integer[] wantednewstype = {Integer.valueOf(Meeting.NODESNEWS) , Integer.valueOf(Meeting.QIANSHI), Integer.valueOf(Meeting.RUOSHI)  };
        Cache cache = new Cache(curbknodecode,eventDbService, null,LocalDate.now().minusMonths(3),LocalDate.now(),wantednewstype);
	    Collection<InsertedMeeting> curnewlist = cache.produceMeetings();
	    
	     String htmlstring = this.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		 logger.debug(doc.toString());
		 org.jsoup.select.Elements content = doc.select("body");
		       
		content.append( "<h4>\""+ curselectedbknodecode + curselectedbknodename + "\"相关新闻</h4>");
    	for(InsertedMeeting cylnew : curnewlist ) {
    		String title = cylnew.getTitle();
    		String newdate = cylnew.getStart().toString(); 
    		String slackurl = cylnew.getSlackUrl();
    		String keywords = cylnew.getKeyWords();
    		if(slackurl != null && !slackurl.isEmpty() )	    		
    			content.append( "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ");
    		else
    			content.append( "<p>" + newdate  + title + "</p> ");
    		//notesPane.setText("<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>");
    	}
    	
    	htmlstring = doc.toString();
    	this.setText(htmlstring);
    	
    	eventDbService = null;
	   	wantednewstype = null;
	   	cache = null;
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
