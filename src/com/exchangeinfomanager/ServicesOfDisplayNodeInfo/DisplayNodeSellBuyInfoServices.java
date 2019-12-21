package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class DisplayNodeSellBuyInfoServices implements ServicesOfDisplayNodeInfo
{
	private BkChanYeLianTreeNode node;

	public DisplayNodeSellBuyInfoServices (BkChanYeLianTreeNode node)
	{
		this.node = node;
	}

	@Override
	public String getRequiredHtmlInfo() 
	{
		String nodename = node.getMyOwnName();
     	String curbknodecode = node.getMyOwnCode();
     	
       	int type = node.getType();
       	if( type == BkChanYeLianTreeNode.TDXBK) {
//       		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai (); 
//       		svsbk.getNodeJiBenMian(node);
//       		svsbk = null;
       	} else
       	if( type == BkChanYeLianTreeNode.TDXGG) {
	    	SvsForNodeOfStock svsstk = new SvsForNodeOfStock ();
	    	svsstk.getNodeMrmcYingKuiInfo(node);
	    	svsstk = null;
	    }
       	
        String htmlstring = "";
	    org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
	    org.jsoup.select.Elements content = doc.select("body"); 
	       
	    content.append("<h4>\""+ curbknodecode + "" + nodename + "\"个股交易信息</h4>");
	    Object[][] sellbuyObjects = node.getNodeJiBenMian().getZdgzMrmcZdgzYingKuiRecords();
	    if(sellbuyObjects == null)
	    	return doc.toString();
	    
	    for(int i=0;i<sellbuyObjects.length;i++) {
	    	   String output = "";
	    	   try {
	    		   output = sellbuyObjects[i][0].toString() + " " + sellbuyObjects[i][1].toString() + " " + sellbuyObjects[i][2].toString();
	    	   } catch (java.lang.NullPointerException e) {
// 			   e.printStackTrace();
	    	   }
	    	   
	    	   content.append( "<p>"+ output + "</p>" );
	   }
	       
	   htmlstring = doc.toString();
	   return htmlstring;
	}




}
