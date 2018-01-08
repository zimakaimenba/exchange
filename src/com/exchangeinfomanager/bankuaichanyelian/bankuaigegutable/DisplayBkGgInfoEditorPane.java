package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Strings;

public class DisplayBkGgInfoEditorPane extends JEditorPane
{

	private BanKuaiDbOperation bkdbopt;
	private Boolean clearContentsBeforeDisplayNewInfo = false;

	public DisplayBkGgInfoEditorPane()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
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
     * ��ʾ�����������
     */
    public void displayChanYeLianNewsHtml(BkChanYeLianTreeNode curselectedbknodecode)
    {
    	String curselectedbknodename = curselectedbknodecode.getMyOwnName();
	       	String curbknodecode = curselectedbknodecode.getMyOwnCode();
	       	ArrayList<ChanYeLianNews> curnewlist = bkdbopt.getBanKuaiRelatedNews (curbknodecode);
	       	 
	     String htmlstring = this.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		 System.out.println(doc.toString());
		 org.jsoup.select.Elements content = doc.select("body");
		       
		 content.append( "<h4>���"+ curselectedbknodecode + curselectedbknodename + "�������</h4>");
    	for(ChanYeLianNews cylnew : curnewlist ) {
    		String title = cylnew.getNewsTitle();
    		String newdate = cylnew.getGenerateDate().toString(); 
    		String slackurl = cylnew.getNewsSlackUrl();
    		String keywords = cylnew.getKeyWords ();
    		if(slackurl != null && !slackurl.isEmpty() )	    		
    			content.append( "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ");
    		else
    			content.append( "<p>" + newdate  + title + "</p> ");
    		//notesPane.setText("<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>");
    	}
    	
    	htmlstring = doc.toString();
    	this.setText(htmlstring);
    	this.setCaretPosition(0);
	}
    /*
     * ��ʾ�ڵ�Ļ�����Ϣ
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
//	       System.out.println(doc.toString());
	       org.jsoup.select.Elements content = doc.select("body"); 
	       
	       content.append("<h4>��������Ϣ</h4>");
	       
	       		if(!Strings.isNullOrEmpty( curselectedbknode.getGainiantishi() ) ) 
				try {
					content.append( "<p>"+ "������ʾ:" + Strings.nullToEmpty( curselectedbknode.getGainiantishidate().toString()) + curselectedbknode.getGainiantishi() + "</p>" );
				} catch (java.lang.NullPointerException e) {
					content.append("<p>"+ "������ʾ:" +  Strings.nullToEmpty( "") + curselectedbknode.getGainiantishi() + "</p>" );
				}
	       		if(!Strings.isNullOrEmpty(curselectedbknode.getFumianxiaoxi() )  )
 	       		try {
					content.append( "<p>"+ "������Ϣ:" + Strings.nullToEmpty( curselectedbknode.getFumianxiaoxidate().toString()) + curselectedbknode.getFumianxiaoxi() + "</p>" );
				} catch (java.lang.NullPointerException e) {
					content.append("<p>"+ "������Ϣ:" +  Strings.nullToEmpty( "") + curselectedbknode.getFumianxiaoxi() + "</p>" );
				}
	       		
	      		if(!Strings.isNullOrEmpty(curselectedbknode.getQuanshangpingji() ) ) 
			try {
				content.append( "<p>"+  "ȯ������" + Strings.nullToEmpty(curselectedbknode.getQuanshangpingjidate().toString()) + " " + curselectedbknode.getQuanshangpingji() + "</p>" );;
			} catch (java.lang.NullPointerException ex) {
				content.append( "<p>"+  "ȯ������" + Strings.nullToEmpty("") + " " + curselectedbknode.getQuanshangpingji() + "</p>" );;
			}
	      		if(!Strings.isNullOrEmpty(curselectedbknode.getZhengxiangguan() ) )
 	      		content.append( "<p>"+  "����ؼ��ͻ�(#" + Strings.nullToEmpty(curselectedbknode.getZhengxiangguan() )
											 + " " + Strings.nullToEmpty(curselectedbknode.getKeHuCustom() ) +  "#)" + "</p>" );
	      		if(!Strings.isNullOrEmpty(curselectedbknode.getFuxiangguan() ) )
 	      		content.append( "<p>"+  "����ؼ���������(#" + Strings.nullToEmpty(curselectedbknode.getFuxiangguan() )
 	      									 + " " + Strings.nullToEmpty(curselectedbknode.getJingZhengDuiShou() ) + "#)" + "</p>" );

	     htmlstring = doc.toString();
	     this.setText(htmlstring);
	     this.setCaretPosition(0);   
    }
    /*
     * ��ʾ������ע����Ϣ
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
//	       System.out.println(doc.toString());
	       org.jsoup.select.Elements content = doc.select("body"); 
	       
	       content.append("<h4>����ע������Ϣ</h4>");
	       Object[][] sellbuyObjects = (curselectedbknode).getZdgzMrmcZdgzYingKuiRecords();
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
		   this.setCaretPosition(0); 
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
