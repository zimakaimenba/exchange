/*
 * 为板块列表专门设计的类，
 */
package com.exchangeinfomanager.gui.subgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.jsoup.Jsoup;


import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.nodes.Stock;
import com.google.common.collect.Multimap;


public class BanKuaiListEditorPane extends JEditorPane 
{
	public BanKuaiListEditorPane() 
	{
		super ();
		this.setEditable(false);
		this.setContentType("text/html;charset=utf-8");
		
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		selectstring = "";
		createEvents ();
		
	}
	
	private String selectstring;
	private BanKuaiAndChanYeLian2 bkcyl;
	public static final String URLSELECTED_PROPERTY = "urlselected";
	public static final String EXPORTCSV_PROPERTY = "exporttocsv";
	JPopupMenu jPopupMenue = new JPopupMenu();
	JMenuItem menuItemclear = new JMenuItem("清空内容"); 
	JMenuItem menuItemgcsv = new JMenuItem("导出到CSV");
	/*
	 * 
	 */
	public void resetSelectedBanKuai ()
	{
		this.selectstring = "";
	}
	/*
	 * 
	 */
	public void displayNodeSelectedInfo (String selectinfo)
	{
		org.jsoup.nodes.Document selectdoc = Jsoup.parse(selectinfo);
		org.jsoup.select.Elements selectbody = selectdoc.select("body");
		String bodystr = null;
		for(org.jsoup.nodes.Element body : selectbody) {
			bodystr = body.text();
			org.jsoup.select.Elements dls = body.select("dl");
			for(org.jsoup.nodes.Element dl : dls) {
				
//				org.jsoup.nodes.Attributes attrs = new org.jsoup.nodes.Attributes();
//			    attrs.put("size", "5");
			    org.jsoup.nodes.Element font = new org.jsoup.nodes.Element("Font");
			    font.attr("size", "3");
			    
			    dl.appendChild(font);
			    
//			    atrs.put("id", "div1");
			}
//			String dltext = dl.text();
//			System.out.println(dltext);
		}
		String head = selectbody.get(0).text();
		
		
		org.jsoup.nodes.Document tflddoc = Jsoup.parse(this.getText());
		org.jsoup.select.Elements content = tflddoc.select("body");
		content.append("<font size=\"3\">" + selectbody + "</font>");
		
		String htmlstring = tflddoc.toString();
		this.setText(htmlstring);
//		try {
//			this.setPage(htmlstring);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	    			 if(daleilist.contains("GREEN") ) {
	    				 content.append("<a style=\"color:green\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + daleilist.toString().replace("GREEN,", "")  + "</a> " );
	    				 shuyuruoshibankuai = true ;
	    			 } else
	    				 content.append("<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + daleilist.toString()  + "</a> " );
	    			 
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
		
//		menuItemgcsv.setEnabled(false);
		jPopupMenue.add(menuItemgcsv);
		jPopupMenue.add(menuItemclear);
		
		menuItemgcsv.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	exportContentsToCsv ();
            }
        });
		
		menuItemclear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	clearPaneContents ();
            }
        });

 
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println("this is the test");
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
                  
              } else if (e.getButton() == MouseEvent.BUTTON3) {
            	  showpopupMenu (e);

              }
				
			}
			
		});

		
		//为个股板块信息的hyperlink注册时间  http://www.javalobby.org/java/forums/t19716.html
		 ActionMap actionMap = new ActionMap(); 
	     actionMap.put("openBanKuaiAndChanYeLianDialog", new AbstractAction (){
			public void actionPerformed(ActionEvent e) {
				 	HyperlinkEvent hle = (HyperlinkEvent)e.getSource();
				 	formateHyperLink (hle);
			}
	     }); 
	     
	     this.addHyperlinkListener(new ActionBasedBanKuaiAndChanYeLianHyperlinkListener(actionMap)); 
	}
	/*
	 * 
	 */
	protected void clearPaneContents()
	{
		this.setText("");
	}
	/*
	 * 
	 */
	protected void exportContentsToCsv() 
	{
		String htmlstring = this.getText();
		
//		PropertyChangeEvent evt = new PropertyChangeEvent(this, EXPORTCSV_PROPERTY, "",  htmlstring );
		this.firePropertyChange(EXPORTCSV_PROPERTY, "", htmlstring);
	}
	/*
	 * 
	 */
	protected void showpopupMenu(MouseEvent e) 
	{
		jPopupMenue.show(this, e.getX(),   e.getY());
		
	}
	protected void formateHyperLink(HyperlinkEvent hle) 
	{
		String link = null;
        try{
        	Element elem = hle.getSourceElement(); 
            Document doc = elem.getDocument(); 
            int start = elem.getStartOffset(); 
            int end = elem.getEndOffset(); 
            link = doc.getText(start, end-start);

            StringTokenizer stok = new StringTokenizer(link, ", "); 
            while(stok.hasMoreTokens()){ 
                String token = stok.nextToken(); 
            }
        } catch(BadLocationException ex){ 
            ex.printStackTrace(); 
        }
        if(!selectstring.equals(link.trim())) {
        	selectstring = link;
            firePropertyChange(URLSELECTED_PROPERTY, "", link);
        }
        
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
