package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;
import java.util.List;

import org.bouncycastle.util.Strings;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.base.Splitter;

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
	    		   String output1 = sellbuyObjects[i][0].toString().substring(0, 10);
	    		   String output2 = sellbuyObjects[i][1].toString() ;
	    		   String outpu3part = "";
	    		   try {
	    			   String output3 = sellbuyObjects[i][2].toString();
	    			   List<String> tmpoutput3 = Splitter.on("|").omitEmptyStrings().splitToList(output3);
	    			   outpu3part = tmpoutput3.get(0);
	    		   } catch (java.lang.NullPointerException e) {
	    			   
	    		   } catch (java.lang.IndexOutOfBoundsException e) {
	    			   
	    		   }
	    		   output =  output1 + " " + output2 + " " + outpu3part;
	    	   } catch (java.lang.Exception e) {
 			   e.printStackTrace();
	    	   }
	    	   
	    	   content.append( "<p>"+ output + "</p>" );
	   }
	       
	   htmlstring = doc.toString();
	   return htmlstring;
	}




}
