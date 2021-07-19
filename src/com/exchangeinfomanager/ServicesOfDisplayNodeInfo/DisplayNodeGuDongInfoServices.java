package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.Color;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.ServicesOfNodeStock;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Core.nodejibenmian.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.commonlib.Season;

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
      	
       	Boolean has = true;
       	if(this.requiredstart == null) { //NULL即默认问显示最新数据，要做一些处理
       		LocalDate maxcbrq = this.node.getNodeJiBenMian().getLastestCaiBaoDate();
    		try {
    			long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(Season.getSeasonStartDate( LocalDate.now() ),maxcbrq);
    			if( java.lang.Math.abs(daysBetween) >= 280 ) //有些股票机构股东数据是很久以前的，没意义
    				has = false;
    		} catch (java.lang.NullPointerException e) {has = false; return null;}
    		
    		if(this.requiredstart == null) 
       			this.requiredstart = Season.getSeasonStartDate( maxcbrq );
    		if(this.requiredend == null)
    			this.requiredend = Season.getSeasonEndDate( maxcbrq );
       	}
		
		if(!has) return null;
       	
       		List<Object[]> dgObjects = this.node.getNodeJiBenMian().getGuDongInfo();
       		if(dgObjects == null || dgObjects.size() == 0) {
       			ServicesOfNodeStock svsstk = new SvsForNodeOfStock ();
    	    	svsstk.getStockGuDong(node, "LiuTong",this.requiredstart, this.requiredend);
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
	    	
	    	for(Object[] gudong : dgObjects) {
	    		hasinfo = true;
	    		try {
	    		if(  ((LocalDate)gudong[1]).isBefore(this.requiredstart)  || ((LocalDate)gudong[1]).isAfter(this.requiredend) )
	    			continue;
	    		} catch(java.lang.NullPointerException e) 		{e.printStackTrace();}
	    		
	    		String output1 = gudong[0].toString();
	    		String output2 = gudong[1].toString();
	    		String output3 = gudong[2].toString();
	    		Boolean hqgq = (Boolean) gudong[3];
	    		Boolean mx = (Boolean) gudong[4];
	    		   
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
		    	Color seasoncolor = Season.getSeasonColor( (LocalDate)gudong[1] );
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
