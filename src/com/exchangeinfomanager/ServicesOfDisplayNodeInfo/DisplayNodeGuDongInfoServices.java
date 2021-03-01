package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.commonlib.Season;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.ServicesOfNodeStock;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.nodejibenmian.ServicesOfNodeJiBenMianInfo;

public class DisplayNodeGuDongInfoServices implements ServicesOfNodeJiBenMianInfo
{
	private Stock node;
	private LocalDate requiredstart;
	private LocalDate requiredend;

	public DisplayNodeGuDongInfoServices (BkChanYeLianTreeNode node)
	{
		this.node = (Stock)node;
	}
	
	public void setDisplayGuDongTimeRange (LocalDate requiredstart1, LocalDate requiredend1)
	{
		this.requiredstart = requiredstart1;
		this.requiredend = requiredend1;
	}

	@Override
	public String getRequiredHtmlInfo() 
	{
		String nodename = node.getMyOwnName();
     	String curbknodecode = node.getMyOwnCode();
     	
       	if( node.getType() != BkChanYeLianTreeNode.TDXGG) 
       		return null;
       	
       		Object[][] dgObjects = this.node.getNodeJiBenMian().getGuDongInfo();
       		if(!ArrayUtils.isNotEmpty( dgObjects )) {
       			ServicesOfNodeStock svsstk = new SvsForNodeOfStock ();
           		if(this.requiredstart == null) {
           			this.requiredstart = Season.getSeasonStartDate( LocalDate.now() );
           			this.requiredstart = Season.getLastSeasonStartDate( this.requiredstart ); // find 2 season in row
           		}
           		if(this.requiredend == null)	this.requiredend = Season.getSeasonEndDate( LocalDate.now() );
    	    	svsstk.getStockGuDong(node, "LiuTong", this.requiredstart, this.requiredend);
    	    	
    	    	if(!ArrayUtils.isNotEmpty( node.getNodeJiBenMian().getGuDongInfo() )) {  // if not data, find last season data
    	    		this.requiredstart = Season.getLastSeasonStartDate(this.requiredstart);
    	    		requiredstart = Season.getLastSeasonStartDate(requiredstart);
    	    		this.requiredend = Season.getLastSeasonEndDate(this.requiredend);
    	    		svsstk.getStockGuDong(node, "LiuTong", requiredstart, this.requiredend);
    	    	}	

    	    	svsstk = null;
       		}
        
       		dgObjects = node.getNodeJiBenMian().getGuDongInfo();
    	    if(dgObjects == null) 
    	    	return null;
    	    
        String htmlstring = ""; Boolean hasinfo = false;
	    org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
	    org.jsoup.select.Elements content = doc.select("body"); 
	    content.append("<h4 style=\"font-size:9px\">"+ curbknodecode + "" + nodename + "\"股东信息</h4>");
	
	    for(int i=0;i<dgObjects.length;i++) {
	    	hasinfo = true;
	    	   String output = "";
	    	   try {
	    		   String output1 = dgObjects[i][0].toString();
	    		   String output2 = dgObjects[i][1].toString();
	    		   String output3 = dgObjects[i][2].toString();
	    		   
	    		   output =  output1 + " " + output2 + " " + output3;
	    	   } catch (java.lang.Exception e) {e.printStackTrace();}
	    	   
	    	   content.append( "<p style=\"font-size:9px\">"+ output + "</p>" );
	   }
	     
	    if(hasinfo) {
	    	htmlstring = doc.toString();
	 	   return htmlstring;
	    } else
	    	return null;
	}
}
