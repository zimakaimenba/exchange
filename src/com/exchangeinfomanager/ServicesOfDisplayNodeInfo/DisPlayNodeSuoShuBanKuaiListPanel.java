package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;

public class DisPlayNodeSuoShuBanKuaiListPanel  extends DisplayNodeInfoPanel 
{
	private String selectstring;
	public static final String URLSELECTED_PROPERTY = "urlselected";
	
	public DisPlayNodeSuoShuBanKuaiListPanel(ServicesOfDisplayNodeInfo svsdisplay)
	{
		super(svsdisplay);
		
		selectstring = "";
		
		//为个股板块信息的hyperlink注册时间  http://www.javalobby.org/java/forums/t19716.html
		 ActionMap actionMap = new ActionMap(); 
	     actionMap.put("openBanKuaiAndChanYeLianDialog", new AbstractAction (){
			public void actionPerformed(ActionEvent e) {
				 	HyperlinkEvent hle = (HyperlinkEvent)e.getSource();
				 	formateHyperLink (hle);
			}
	     }); 
	     
	     super.infoeditor.addHyperlinkListener(new ActionBasedBanKuaiAndChanYeLianHyperlinkListener(actionMap)); 
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

}
