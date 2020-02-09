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

import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;

public class DisplayNodeInfoPanel extends JEditorPane //extends JPanel 
{
//	protected JEditorPane infoeditor;
	private ServicesOfDisplayNodeInfo svsshow;

	public DisplayNodeInfoPanel (ServicesOfDisplayNodeInfo svsdisplay)
	{
		this.svsshow = svsdisplay;
		
		this.setEditable(false);
        HTMLDocument htmlDoc = new HTMLDocument();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        this.setEditorKit(editorKit);
        this.setSize(420,Short.MAX_VALUE);
        
        this.setOpaque(true);
        this.setText("<b><font face=\"Arial\" size=\"50\" align=\"center\" > Unfortunately when I display this string it is too long and doesn't wrap to new line!</font></b>");
        
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
		 
//		 String htmlstr = "<p>" + svsshow.getRequiredHtmlInfo() + "</p> "; 
		 String htmlstr =  svsshow.getRequiredHtmlInfo();
		 content.append(htmlstr );
 	
		 htmlstring = doc.toString();
		 this.setText(htmlstring);
//		 this.setCaretPosition(this.getDocument().getLength());
	}
}
