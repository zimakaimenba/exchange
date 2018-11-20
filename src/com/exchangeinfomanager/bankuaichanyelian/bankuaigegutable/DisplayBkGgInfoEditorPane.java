package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
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
	}
	
	 /*
     * 显示板块新闻连接
     */
    public void displayChanYeLianNewsHtml(BkChanYeLianTreeNode curselectedbknodecode)
    {
    	String curselectedbknodename = curselectedbknodecode.getMyOwnName();
	    String curbknodecode = curselectedbknodecode.getMyOwnCode();
	    Collection<InsertedMeeting> curnewlist = newsdbopt.getBanKuaiRelatedNews(curbknodecode,LocalDate.now().minusMonths(3),LocalDate.now());
	    
	     String htmlstring = this.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		 logger.debug(doc.toString());
		 org.jsoup.select.Elements content = doc.select("body");
		       
		content.append( "<h4>板块"+ curselectedbknodecode + curselectedbknodename + "相关新闻</h4>");
    	for(InsertedMeeting cylnew : curnewlist ) {
    		String title = cylnew.getTitle();
    		String newdate = cylnew.getStart().toString(); 
    		String slackurl = cylnew.getSlackUrl();
    		String keywords = cylnew.getLocation();
    		if(slackurl != null && !slackurl.isEmpty() )	    		
    			content.append( "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ");
    		else
    			content.append( "<p>" + newdate  + title + "</p> ");
    		//notesPane.setText("<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>");
    	}
    	
    	htmlstring = doc.toString();
    	this.setText(htmlstring);
//    	this.setCaretPosition(0);
	}
    /*
     * 显示节点的基本信息
     */
    public void  displayNodeBasicInfo(BkChanYeLianTreeNode curselectedbknode)
    {
    	String curselectedbknodename = curselectedbknode.getMyOwnName();
	       	String curbknodecode = curselectedbknode.getMyOwnCode();
	       	int type = curselectedbknode.getType();
	       	
	       	if(type == 4 ) {
	       		curselectedbknode = bkdbopt.getBanKuaiBasicInfo((BanKuai)curselectedbknode) ;
	       	} else if(type == 6) {
	       		curselectedbknode = bkdbopt.getStockBasicInfo((Stock)curselectedbknode) ;
	       	}
	       	
	       String htmlstring = this.getText();
	       org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//	       logger.debug(doc.toString());
	       org.jsoup.select.Elements content = doc.select("body"); 
	       
	       content.append("<h4>板块基本信息</h4>");
	       
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
	       
	       content.append("<h4>板块关注分析信息</h4>");
	       Object[][] sellbuyObjects = (curselectedbknode).getNodeJiBenMian().getZdgzMrmcZdgzYingKuiRecords();
	       for(int i=0;i<sellbuyObjects.length;i++) {
	    	   String output = "";
	    	   try {
	    		   output = sellbuyObjects[i][0].toString() + " " + sellbuyObjects[i][1].toString() + " " + sellbuyObjects[i][2].toString();
	    	   } catch (java.lang.NullPointerException e) {
    			   e.printStackTrace();
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
