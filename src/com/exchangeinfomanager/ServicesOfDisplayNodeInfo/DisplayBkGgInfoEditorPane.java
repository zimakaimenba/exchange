package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ServicesForNews;
import com.exchangeinfomanager.News.ServicesForNewsLabel;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.google.common.base.Strings;

class DisplayBkGgInfoEditorPane extends JEditorPane
{

	public void displayJSTestHtml ()
	{
		String jshtml = "<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n" + 
				"<style>\r\n" + 
				"#more {display: none;}\r\n" + 
				"</style>\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"\r\n" + 
				"<h2>Read More Read Less Button</h2>\r\n" + 
				"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus imperdiet, nulla et dictum interdum, nisi lorem egestas vitae scel<span id=\"dots\">...</span><span id=\"more\">erisque enim ligula venenatis dolor. Maecenas nisl est, ultrices nec congue eget, auctor vitae massa. Fusce luctus vestibulum augue ut aliquet. Nunc sagittis dictum nisi, sed ullamcorper ipsum dignissim ac. In at libero sed nunc venenatis imperdiet sed ornare turpis. Donec vitae dui eget tellus gravida venenatis. Integer fringilla congue eros non fermentum. Sed dapibus pulvinar nibh tempor porta.</span></p>\r\n" + 
				"<button onclick=\"myFunction()\" id=\"myBtn\">Read more</button>\r\n" + 
				"\r\n" + 
				"<script>\r\n" + 
				"function myFunction() {\r\n" + 
				"  var dots = document.getElementById(\"dots\");\r\n" + 
				"  var moreText = document.getElementById(\"more\");\r\n" + 
				"  var btnText = document.getElementById(\"myBtn\");\r\n" + 
				"\r\n" + 
				"  if (dots.style.display === \"none\") {\r\n" + 
				"    dots.style.display = \"inline\";\r\n" + 
				"    btnText.innerHTML = \"Read more\"; \r\n" + 
				"    moreText.style.display = \"none\";\r\n" + 
				"  } else {\r\n" + 
				"    dots.style.display = \"none\";\r\n" + 
				"    btnText.innerHTML = \"Read less\"; \r\n" + 
				"    moreText.style.display = \"inline\";\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				"</script>\r\n" + 
				"\r\n" + 
				"</body>\r\n" + 
				"</html>\r\n" + 
				""
				;
		
		String htmlstring = this.getText();
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		content.append(jshtml );
		htmlstring = doc.toString();
	    this.setText(htmlstring);
	}
	/*
	 * 
	 */
	public void displayChanYeLianInfo (BkChanYeLianTreeNode curselectedbknodecode)
	{
		List<BkChanYeLianTreeNode> cylinfo = curselectedbknodecode.getNodeJiBenMian().getChanYeLianInfo();
		if(cylinfo == null  || cylinfo.isEmpty() )
			return;
		
		String htmlstring = this.getText();
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		org.jsoup.select.Elements content = doc.select("body");
		content.append( "<h4>产业链信息</h4>");
		String result ="";
		for(BkChanYeLianTreeNode tmpinfo : cylinfo) {
			if(tmpinfo == null)
				continue;
			
			result =  result + tmpinfo.getMyOwnName() + "->";
		}
		content.append( "<p>" + result + "</p> ");
		
		htmlstring = doc.toString();
	    this.setText(htmlstring);
	}
	
}
