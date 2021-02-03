package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.base.Strings;

public class DisplayNodeJiBenMianService implements ServicesOfNodeJiBenMianInfo
{
	private BkChanYeLianTreeNode node;

	public DisplayNodeJiBenMianService (BkChanYeLianTreeNode node)
	{
		this.node = node;
	}
	
	public String getRequiredHtmlInfo ()
	{
		String nodename = node.getMyOwnName();
     	String curbknodecode = node.getMyOwnCode();
     	
       	int type = node.getType();
       	if( type == BkChanYeLianTreeNode.TDXBK) {
       		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai (); 
       		svsbk.getNodeJiBenMian(node);
       		svsbk = null;
       	} else
       	if( type == BkChanYeLianTreeNode.TDXGG) {
	    	SvsForNodeOfStock svsstk = new SvsForNodeOfStock ();
	    	svsstk.getNodeJiBenMian(node);
	    	svsstk = null;
	    }

    	String htmlstring = "";
	    org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
	    org.jsoup.select.Elements content = doc.select("body"); 
	       
	    String header = "<h4>\""+ curbknodecode + " " + nodename + "\"基本信息</h4>";
	    content.append(header);
       
	    Boolean hasinfo = false;
       	if(!Strings.isNullOrEmpty( node.getNodeJiBenMian().getGainiantishi() ) ) 
			try {
				hasinfo = true;
				content.append( "<p>"+ "概念提示:" + Strings.nullToEmpty( node.getNodeJiBenMian().getGainiantishidate().toString()) + node.getNodeJiBenMian().getGainiantishi() + "</p>" );
			} catch (java.lang.NullPointerException e) {
					content.append("<p>"+ "概念提示:" +  Strings.nullToEmpty( "") + node.getNodeJiBenMian().getGainiantishi() + "</p>" );
		}
       	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getFumianxiaoxi() )  )
	       	try {
	       		hasinfo = true;
				content.append( "<p>"+ "负面消息:" + Strings.nullToEmpty( node.getNodeJiBenMian().getFumianxiaoxidate().toString()) + node.getNodeJiBenMian().getFumianxiaoxi() + "</p>" );
			} catch (java.lang.NullPointerException e) {
					content.append("<p>"+ "负面消息:" +  Strings.nullToEmpty( "") + node.getNodeJiBenMian().getFumianxiaoxi() + "</p>" );
			}
       		
      	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getQuanshangpingji() ) ) 
			try {
				hasinfo = true;
				content.append( "<p>"+  "券商评级" + Strings.nullToEmpty(node.getNodeJiBenMian().getQuanshangpingjidate().toString()) + " " + node.getNodeJiBenMian().getQuanshangpingji() + "</p>" );;
			} catch (java.lang.NullPointerException ex) {
				content.append( "<p>"+  "券商评级" + Strings.nullToEmpty("") + " " + node.getNodeJiBenMian().getQuanshangpingji() + "</p>" );;
			}
      	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getZhengxiangguan() ) ) {
      		hasinfo = true;
	    	content.append( "<p>"+  "正相关及客户(#" + Strings.nullToEmpty(node.getNodeJiBenMian().getZhengxiangguan() )
										 + " " + Strings.nullToEmpty(node.getNodeJiBenMian().getKeHuCustom() ) +  "#)" + "</p>" );
      	}
      	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getFuxiangguan() ) ) {
      		hasinfo = true;
      		content.append( "<p>"+  "负相关及竞争对手(#" + Strings.nullToEmpty(node.getNodeJiBenMian().getFuxiangguan() )
      		+ " " + Strings.nullToEmpty(node.getNodeJiBenMian().getJingZhengDuiShou() ) + "#)" + "</p>" );
      	}
      	
	    htmlstring = doc.toString();
	    if(hasinfo)
	    	return htmlstring;
	    else
	    	return null;
	}

}
