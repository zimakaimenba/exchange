/*
 * 为板块列表专门设计的类，
 */
package com.exchangeinfomanager.gui.subgui;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;


public class BanKuaiListEditorPane extends JEditorPane 
{

	public BanKuaiListEditorPane() 
	{
		super ();
		this.setEditable(false);
		this.setContentType("text/html");
		createEvents ();
	}
	
	private String selectstring;
	
	public String getSelectedBanKuai ()
	{
		return this.selectstring;
	}
	private void createEvents() 
	{
		//为个股板块信息的hyperlink注册时间  http://www.javalobby.org/java/forums/t19716.html
		 ActionMap actionMap = new ActionMap(); 
	     actionMap.put("openBanKuaiAndChanYeLianDialog", new AbstractAction (){
			public void actionPerformed(ActionEvent e) {
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
