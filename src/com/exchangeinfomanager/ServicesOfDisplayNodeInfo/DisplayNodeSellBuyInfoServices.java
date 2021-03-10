package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;
import java.util.List;

//import org.bouncycastle.util.Strings;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.ServicesOfNodeStock;
import com.exchangeinfomanager.nodes.nodejibenmian.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Splitter;

public class DisplayNodeSellBuyInfoServices implements ServicesOfNodeJiBenMianInfo
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

       	if( node.getType() != BkChanYeLianTreeNode.TDXGG) 
       		return null;
       	
   		SvsForNodeOfStock svsstk = new SvsForNodeOfStock ();
	   	svsstk.getNodeGzMrMcYkInfo(node,null,null,NodeGivenPeriodDataItem.WEEK);
	   	svsstk = null;
	
	   	String htmlstring = ""; Boolean hasinfo = false;
	    org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
	    org.jsoup.select.Elements content = doc.select("body"); 
	       
	    content.append("<h4>\""+ curbknodecode + "" + nodename + "\"个股交易信息</h4>");
	    List<Object[]> sellbuyObjects = node.getNodeJiBenMian().getZdgzmrmcykRecords();
	    if(sellbuyObjects == null) 
	    	return null;
	    
	    
	    for(Object[] tmprecords : sellbuyObjects) {
	    	hasinfo = true;
	    	   String output = "";
	    	   try {
	    		   String output1 = tmprecords[0].toString().substring(0, 10);
	    		   String output2 = tmprecords[1].toString() ;
	    		   String outpu3part = "";
	    		   try {
	    			   String output3 = tmprecords[2].toString();
	    			   List<String> tmpoutput3 = Splitter.on("|").omitEmptyStrings().splitToList(output3);
	    			   outpu3part = tmpoutput3.get(0);
	    		   } catch (java.lang.NullPointerException e) {   
	    		   } catch (java.lang.IndexOutOfBoundsException e) {
	    		   }
	    		   output =  output1 + " " + output2 + " " + outpu3part;
	    	   } catch (java.lang.Exception e) {
 			   e.printStackTrace();
	    	   }
	    	   
	    	   content.append( "<p style=\"font-size:9px\">" + output + "</p>" );
	   }
	     
	    if(hasinfo) {
	    	htmlstring = doc.toString();
	 	   return htmlstring;
	    } else
	    	return null;
	   
	}




}
