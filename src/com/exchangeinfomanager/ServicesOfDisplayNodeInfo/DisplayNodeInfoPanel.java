package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.Core.nodejibenmian.ServicesOfNodeJiBenMianInfo;

public class DisplayNodeInfoPanel extends JEditorPane //extends JPanel 
{
	private ServicesOfNodeJiBenMianInfo svsshow;

	public DisplayNodeInfoPanel (ServicesOfNodeJiBenMianInfo svsdisplay)
	{
		this.svsshow = svsdisplay;
		
		this.setEditable(false);
        HTMLDocument htmlDoc = new HTMLDocument();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        this.setEditorKit(editorKit);
        this.setSize(420,Short.MAX_VALUE);
        
        this.setOpaque(true);
//        this.setText("<b><font face=\"Arial\" size=\"50\" align=\"center\" > Unfortunately when I display this string it is too long and doesn't wrap to new line!</font></b>");
        
		setHtmlInfo ();
		
		this.revalidate();
		this.repaint();
	}
	/*
	 * 
	 */
	public int getContentHeight()
	{
        return this.getPreferredSize().height + 10;
    }

	
	public void setHtmlInfo ()
	{
		 String htmlstring = "";
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 org.jsoup.select.Elements content = doc.select("body");
		 
		 String htmlstr =  svsshow.getRequiredHtmlInfo();
		 if(htmlstr != null) {
			 hasinfo = true;
			 content.append(htmlstr );
			 	
			 htmlstring = doc.toString();
			 this.setText(htmlstring);
		 } else 
			 this.setText("");
		 
	}
	
	private Boolean hasinfo = false;
	public Boolean isHtmlContainedUsefulInfo ()
	{
		return hasinfo;
	}
}
