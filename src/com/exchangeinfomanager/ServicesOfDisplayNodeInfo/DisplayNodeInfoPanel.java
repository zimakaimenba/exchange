package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;

public class DisplayNodeInfoPanel extends JPanel 
{
	protected JEditorPane infoeditor;
	private ServicesOfDisplayNodeInfo svsshow;

	public DisplayNodeInfoPanel (ServicesOfDisplayNodeInfo svsdisplay)
	{
		this.svsshow = svsdisplay;
		
		this.setLayout(new BorderLayout() );
		
//		JScrollPane infopane = new JScrollPane(); 
		
		this.infoeditor = new JEditorPane ();
		this.infoeditor.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		this.infoeditor.setContentType("text/html;charset=utf-8");
		this.infoeditor.setEditable(false);
//		infopane.setViewportView(this.infoeditor);
		
//		this.add(infopane, BorderLayout.CENTER);
		this.add(infoeditor, BorderLayout.CENTER);
		
		setHtmlInfo ();
	}
	
	public void setHtmlInfo ()
	{
		String htmlstring = this.infoeditor.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		 logger.debug(doc.toString());
		 org.jsoup.select.Elements content = doc.select("body");
//		 String jbm =
		 content.append( "<p>" + svsshow.getRequiredHtmlInfo() + "</p> ");
 	
		 htmlstring = doc.toString();
		 this.infoeditor.setText(htmlstring);
		 this.infoeditor.setCaretPosition(this.infoeditor.getDocument().getLength());
	}
}
