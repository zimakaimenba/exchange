package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.Color;
import java.time.LocalDate;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.commonlib.Season;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.ServicesOfNodeStock;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
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
//		String nodename = node.getMyOwnName();
//     	String curbknodecode = node.getMyOwnCode();
     	
       	if( node.getType() != BkChanYeLianTreeNode.TDXGG) 
       		return null;
       	
       		Object[][] dgObjects = this.node.getNodeJiBenMian().getGuDongInfo();
       		if(!ArrayUtils.isNotEmpty( dgObjects )) {
       			if(this.requiredstart == null) {
           			this.requiredstart = Season.getSeasonStartDate( LocalDate.now() );
//           			this.requiredstart = Season.getLastSeasonStartDate( this.requiredstart ); // find 2 season in row
           		}
//           		if(this.requiredend == null)	this.requiredend = Season.getSeasonEndDate( LocalDate.now() );
           		
       			Boolean has = true;
       			LocalDate maxcbrq = this.node.getNodeJiBenMian().getLastestCaiBaoDate();
				try {
					long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(this.requiredstart,maxcbrq);
					if( java.lang.Math.abs(daysBetween) >= 280 ) //有些股票机构股东数据是很久以前的，没意义
						has = false;
				} catch (java.lang.NullPointerException e) {has = false;}
       			
       			if(!has)
       				return null;
       			
       			ServicesOfNodeStock svsstk = new SvsForNodeOfStock ();
    	    	svsstk.getStockGuDong(node, "LiuTong",Season.getSeasonStartDate( maxcbrq ), Season.getSeasonEndDate( maxcbrq ) );
    	    	
//    	    	if(!ArrayUtils.isNotEmpty( node.getNodeJiBenMian().getGuDongInfo() )) {  // if not data, find last season data
//    	    		this.requiredstart = Season.getLastSeasonStartDate(this.requiredstart);
//    	    		requiredstart = Season.getLastSeasonStartDate(requiredstart);
//    	    		this.requiredend = Season.getLastSeasonEndDate(this.requiredend);
//    	    		svsstk.getStockGuDong(node, "LiuTong", requiredstart, this.requiredend);
//    	    	}	

    	    	svsstk = null;
       		}
        
       		dgObjects = node.getNodeJiBenMian().getGuDongInfo();
    	    if(dgObjects == null) 
    	    	return null;
    	    
        String htmlstring = ""; Boolean hasinfo = false;
	    org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
	    
	    Elements head = doc.select("head");
	    org.jsoup.select.Elements content = doc.select("body"); 
	    for (Element ct : content) {
	    	org.jsoup.nodes.Element table = ct.appendElement("table");
	    	table.attr("border", "1");
	    	table.attr("cellpadding", "0");  
	    	table.attr("cellspacing", "0");
	    	
	    	org.jsoup.nodes.Element trheader = table.appendElement("tr");
	    	org.jsoup.nodes.Element thgdname = trheader.appendElement("th");
	    	org.jsoup.nodes.Element fontgdname = thgdname.appendElement("font");
	    	fontgdname.attr("size", "3");
	    	fontgdname.appendText("股东名称");
	    	
	    	org.jsoup.nodes.Element thgddate = trheader.appendElement("th");
	    	org.jsoup.nodes.Element fontgddate = thgddate.appendElement("font");
	    	fontgddate.attr("size", "3");
	    	fontgddate.appendText("股东日期");
	    	
	    	org.jsoup.nodes.Element thgdnum = trheader.appendElement("th");
	    	org.jsoup.nodes.Element fontgdnum = thgdnum.appendElement("font");
	    	fontgdnum.attr("size", "3");
	    	fontgdnum.appendText("持股数量");
	    	
	    	for(int i=0;i<dgObjects.length;i++) {
	    		hasinfo = true;
	    		
	    		String output1 = dgObjects[i][0].toString();
	    		String output2 = dgObjects[i][1].toString();
	    		String output3 = dgObjects[i][2].toString();
	    		Boolean hqgq = (Boolean) dgObjects[i][3];
	    		Boolean mx = (Boolean) dgObjects[i][4];
	    		   
	    		org.jsoup.nodes.Element trdata = table.appendElement("tr");
		    	org.jsoup.nodes.Element tmpthgdname = trdata.appendElement("th");
		    	org.jsoup.nodes.Element tmpfontgdname = tmpthgdname.appendElement("font");
		    	tmpfontgdname.attr("size", "3");
		    	tmpfontgdname.attr("face", "verdana");
		    	if(hqgq)
		    		tmpfontgdname.attr("color", "red");
		    	if(mx)
		    		tmpfontgdname.attr("color", "Fuchsia");
		    	tmpfontgdname.appendText(output1);
		    	
		    	org.jsoup.nodes.Element tmpthgddate = trdata.appendElement("th");
		    	org.jsoup.nodes.Element tmpfontgddate = tmpthgddate.appendElement("font");
		    	tmpfontgddate.attr("size", "2");
		    	tmpfontgddate.attr("face", "verdana");
		    	Color seasoncolor = Season.getSeasonColor( (LocalDate)dgObjects[i][1] );
		    	String seasonresult = String.format("#%02x%02x%02x", seasoncolor.getRed(), seasoncolor.getGreen(), seasoncolor.getBlue());
		    	tmpfontgddate.attr("color", seasonresult);
		    	tmpfontgddate.appendText(output2);
		    	
		    	org.jsoup.nodes.Element tmpthgdnum = trdata.appendElement("th");
		    	org.jsoup.nodes.Element tmpfontgdnum = tmpthgdnum.appendElement("font");
		    	tmpfontgdnum.attr("size", "1");
		    	tmpfontgdnum.attr("face", "verdana");
		    	tmpfontgdnum.appendText(output3);
	    		
	    	}
	    }
	     
	    if(hasinfo) {
	    	htmlstring = doc.toString();
	 	   return htmlstring;
	    } else
	    	return null;
	}
	
	
}
