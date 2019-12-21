package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.base.Strings;

public class DisplayNodeJiBenMianService implements ServicesOfDisplayNodeInfo
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
	       
	    String header = "<h4>\""+ curbknodecode + " " + nodename + "\"������Ϣ</h4>";
	    content.append(header);
       
       	if(!Strings.isNullOrEmpty( node.getNodeJiBenMian().getGainiantishi() ) ) 
		try {
				content.append( "<p>"+ "������ʾ:" + Strings.nullToEmpty( node.getNodeJiBenMian().getGainiantishidate().toString()) + node.getNodeJiBenMian().getGainiantishi() + "</p>" );
		} catch (java.lang.NullPointerException e) {
				content.append("<p>"+ "������ʾ:" +  Strings.nullToEmpty( "") + node.getNodeJiBenMian().getGainiantishi() + "</p>" );
		}
       	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getFumianxiaoxi() )  )
	       		try {
				content.append( "<p>"+ "������Ϣ:" + Strings.nullToEmpty( node.getNodeJiBenMian().getFumianxiaoxidate().toString()) + node.getNodeJiBenMian().getFumianxiaoxi() + "</p>" );
		} catch (java.lang.NullPointerException e) {
				content.append("<p>"+ "������Ϣ:" +  Strings.nullToEmpty( "") + node.getNodeJiBenMian().getFumianxiaoxi() + "</p>" );
		}
       		
      	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getQuanshangpingji() ) ) 
		try {
			content.append( "<p>"+  "ȯ������" + Strings.nullToEmpty(node.getNodeJiBenMian().getQuanshangpingjidate().toString()) + " " + node.getNodeJiBenMian().getQuanshangpingji() + "</p>" );;
		} catch (java.lang.NullPointerException ex) {
			content.append( "<p>"+  "ȯ������" + Strings.nullToEmpty("") + " " + node.getNodeJiBenMian().getQuanshangpingji() + "</p>" );;
		}
      	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getZhengxiangguan() ) )
	    		content.append( "<p>"+  "����ؼ��ͻ�(#" + Strings.nullToEmpty(node.getNodeJiBenMian().getZhengxiangguan() )
										 + " " + Strings.nullToEmpty(node.getNodeJiBenMian().getKeHuCustom() ) +  "#)" + "</p>" );
      	if(!Strings.isNullOrEmpty(node.getNodeJiBenMian().getFuxiangguan() ) )
	      		content.append( "<p>"+  "����ؼ���������(#" + Strings.nullToEmpty(node.getNodeJiBenMian().getFuxiangguan() )
	      									 + " " + Strings.nullToEmpty(node.getNodeJiBenMian().getJingZhengDuiShou() ) + "#)" + "</p>" );

	    htmlstring = doc.toString();
	    return htmlstring;

	}

}
