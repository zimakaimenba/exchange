/*
 * 为板块列表专门设计的类，
 */
package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

//import org.bouncycastle.util.Arrays.Iterator;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


class BanKuaiListEditorPane extends JEditorPane 
{
	
	
	public BanKuaiListEditorPane() 
	{
		super ();
		this.setEditable(false);
		this.setContentType("text/html;charset=utf-8");
		
//		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		selectstring = "";
		displayedBankuaiinfomap = ArrayListMultimap.create();
		displayedStockinfomap = ArrayListMultimap.create();
		
		menuItemclear = new JMenuItem("清空内容"); 
		menuItemgcsv = new JMenuItem("导出到CSV");
		jPopupMenue.add(menuItemgcsv);
		jPopupMenue.add(menuItemclear);
		
		createEvents ();
//		this.cyltreedb = cyltreedb1;
	}
	
//	private CylTreeDbOperation cyltreedb;
	private String selectstring;
	public static final String URLSELECTED_PROPERTY = "urlselected";
	public static final String EXPORTCSV_PROPERTY = "exporttocsv";
	private Multimap<String,LocalDate> displayedBankuaiinfomap;
	private Multimap<String,LocalDate> displayedStockinfomap;
	
	JPopupMenu jPopupMenue = new JPopupMenu();
	JMenuItem menuItemclear; 
	JMenuItem menuItemgcsv;
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
	public void displayNodeSpecificDateInfo (TDXNodes node, LocalDate selectdate, String period)
	{
		String htmlstring = node.getNodeXPeroidDataInHtml(selectdate,period);
		this.displayNodeSelectedInfo (htmlstring);
		
		if(node.getType() == BkChanYeLianTreeNode.TDXBK)
			this.displayedBankuaiinfomap.put(node.getMyOwnCode(), selectdate);
		else
			displayedStockinfomap.put(node.getMyOwnCode(), selectdate);
	}
	public Multimap<String,LocalDate> getDisplayBanKuaiSpecificDateInfo ()
	{
		return this.displayedBankuaiinfomap;
	}
	public Multimap<String,LocalDate> getDisplayStockSpecificDateInfo ()
	{
		return this.displayedStockinfomap;
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
			    org.jsoup.nodes.Element font = new org.jsoup.nodes.Element("Font");
			    font.attr("size", "3");
			    dl.appendChild(font);
			}
		}
		String head = selectbody.get(0).text();
		
		org.jsoup.nodes.Document tflddoc = Jsoup.parse(this.getText());
		org.jsoup.select.Elements content = tflddoc.select("body");
		content.append("<font size=\"3\">" + selectbody + "</font>");
		
		String htmlstring = tflddoc.toString();
		this.setText(htmlstring);
	}
		
	private void createEvents() 
	{
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
		this.displayedBankuaiinfomap.clear();
		this.displayedStockinfomap.clear();
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
