/*
 * 为板块列表专门设计的类，
 */
package com.exchangeinfomanager.gui.subgui;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.google.common.collect.Multimap;


public class BanKuaiListEditorPane extends JEditorPane 
{
	public BanKuaiListEditorPane() 
	{
		super ();
		this.setEditable(false);
		this.setContentType("text/html");
		
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		
		createEvents ();
		
	}
	
	private String selectstring;
	private BanKuaiAndChanYeLian2 bkcyl;
	/*
	 * 
	 */
	public String getSelectedBanKuai ()
	{
		return this.selectstring;
	}
	/*
	 * 
	 */
	public void displayBanKuaiListContents (Stock stock)
	{
		this.setText("");
		HashMap<String, String> suosusysbankuai = stock.getGeGuCurSuoShuTDXSysBanKuaiList();
		Set<String> union =  suosusysbankuai.keySet();
		Multimap<String,String> suoshudaleibank = bkcyl.checkBanKuaiSuoSuTwelveDaLei (  union ); //获得板块是否属于12个大类板块
		 
		 String htmlstring = this.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 
		 org.jsoup.select.Elements content = doc.select("body");
		 
		 content.append( "<html> "
		 		+ "<body>"
		 		+ " <p>所属板块:");
		 
		 boolean shuyuruoshibankuai = false;
	     for(String suoshubankcode : union ) {
	    	 String displayedbkformate = "\"" + suoshubankcode + suosusysbankuai.get(suoshubankcode) + "\"";
	    	 
	    	 try {
	    		 Collection<String> daleilist = suoshudaleibank.get(suoshubankcode);
	    		 if( daleilist.size() != 0) {
	    			 content.append("<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + daleilist.toString()  + "</a> " );
	    			 if(daleilist.contains("弱势远离") )
	    				 shuyuruoshibankuai = true ;
	    		 }
	    		 else
	    			 content.append("<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a> ");
	    	 } catch (java.lang.NullPointerException e) {
	    		 content.append("<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a> ");
	    	 }
	     } 
	     
	     if(shuyuruoshibankuai)
	    	 JOptionPane.showMessageDialog(this, "<html><font face='Calibri' size='8' color='red'>该个股属于近期弱势或要回避的板块！");
	     
	     content.append( "</p>");
	     
	     		
	     
//	     String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
//	     ArrayList<String> gegucyl = ((Stock)nodeshouldbedisplayed).getGeGuAllChanYeLianInfo();
//	     for(String cyl : gegucyl) {
//	    	 content.append( " <p>个股产业链:"
//	    	 		+ "<a  href=\"openBanKuaiAndChanYeLianDialog\">  " + cyl)
//	    	 		;
//	     }
//	     if(gegucyl.size()>0)
//	    	 content.append( "</p>");
	     
	     content.append( "</body>"
					+ "</html>");
	    
	     htmlstring = doc.toString();
	     this.setText(htmlstring);
	     this.setCaretPosition(this.getDocument().getLength());
	}
	
	private void createEvents() 
	{
		//为个股板块信息的hyperlink注册时间  http://www.javalobby.org/java/forums/t19716.html
		 ActionMap actionMap = new ActionMap(); 
	     actionMap.put("openBanKuaiAndChanYeLianDialog", new AbstractAction (){
			public void actionPerformed(ActionEvent e) {
					selectstring = null;
				 	HyperlinkEvent hle = (HyperlinkEvent)e.getSource();
				 	String link = null;
			        try{
			        	Element elem = hle.getSourceElement(); 
			            Document doc = elem.getDocument(); 
			            int start = elem.getStartOffset(); 
			            int end = elem.getEndOffset(); 
			            link = doc.getText(start, end-start);
//			            System.out.println(link);
//			            link = link.equals("contains people") ? "" : link.substring("contains ".length()); 
			            StringTokenizer stok = new StringTokenizer(link, ", "); 
			            while(stok.hasMoreTokens()){ 
			                String token = stok.nextToken(); 
			            }
			        } catch(BadLocationException ex){ 
			            ex.printStackTrace(); 
			        }
			        selectstring = link;
			}
	     }); 
	     this.addHyperlinkListener(new ActionBasedBanKuaiAndChanYeLianHyperlinkListener(actionMap)); 
	}
}

//http://www.javalobby.org/java/forums/t19716.html
class ActionBasedBanKuaiAndChanYeLianHyperlinkListener implements HyperlinkListener
{ 
	 ActionMap actionMap; 
	
	 public ActionBasedBanKuaiAndChanYeLianHyperlinkListener(ActionMap actionMap)
	 { 
	     this.actionMap = actionMap; 
	 } 
	
	 public void hyperlinkUpdate(HyperlinkEvent e)
	 { 
	     if(e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED) 
	         return; 
	     String href = e.getDescription(); 
	     Action action = actionMap.get(href); 
	     if(action!=null) 
	         action.actionPerformed(new ActionEvent(e, ActionEvent.ACTION_PERFORMED, href)); 
	 } 
}
