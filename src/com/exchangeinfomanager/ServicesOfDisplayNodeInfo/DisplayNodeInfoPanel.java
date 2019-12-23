package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;

public class DisplayNodeInfoPanel extends JEditorPane //extends JPanel 
{
//	protected JEditorPane infoeditor;
	private ServicesOfDisplayNodeInfo svsshow;

	public DisplayNodeInfoPanel (ServicesOfDisplayNodeInfo svsdisplay)
	{
		this.svsshow = svsdisplay;
		this.setContentType("text/html;charset=utf-8");
		this.setEditable(false);
//		this.setLayout(new BorderLayout() );
//		
//		this.infoeditor = new JEditorPane ();
//		this.infoeditor.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
//		this.infoeditor.setContentType("text/html;charset=utf-8");
//		this.infoeditor.setEditable(false);
//		this.add(infoeditor, BorderLayout.CENTER);
		
		setHtmlInfo ();
	}
	
	public void setHtmlInfo ()
	{
		String htmlstring = this.getText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 org.jsoup.select.Elements content = doc.select("body");

		 content.append( "<p>" + svsshow.getRequiredHtmlInfo() + "</p> ");
 	
		 htmlstring = doc.toString();
		 this.setText(htmlstring);
//		 this.setCaretPosition(this.getDocument().getLength());
	}
}
