package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;

public class DisPlayNodeSuoShuBanKuaiListServices implements ServicesOfNodeJiBenMianInfo
{
	private BkChanYeLianTreeNode node;
	
	public DisPlayNodeSuoShuBanKuaiListServices (BkChanYeLianTreeNode node)
	{
		this.node = node;
		
		SvsForNodeOfStock svsstk = new SvsForNodeOfStock ();
		node = svsstk.getNodeSuoShuBanKuaiList(node);
		svsstk = null;
	}

	@Override
	public String getRequiredHtmlInfo() 
	{
		Set<BkChanYeLianTreeNode> suosusysbankuai = ((Stock)node).getGeGuCurSuoShuTDXSysBanKuaiList();
		
		DuanQiGuanZhuServices svsdqgz = new DuanQiGuanZhuServices ();
		Collection<News> curdqgz = svsdqgz.getCurrentNews( LocalDate.now() );
		Set<String> gzbkcodeset = new HashSet<> ();
		for (Iterator<News> lit = curdqgz.iterator(); lit.hasNext(); ) {
    		News f = lit.next();
    		String bkcode = f.getNewsOwnerCodes();
    		gzbkcodeset.add(bkcode);
    	}

		String htmlstring = "";
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 
		 org.jsoup.select.Elements content = doc.select("body");
		 
		 content.append( "<html> "
		 		+ "<body>"
		 		);
		 
//		 boolean shuyuruoshibankuai = false;
		 for ( java.util.Iterator<BkChanYeLianTreeNode> it = suosusysbankuai.iterator(); it.hasNext(); ) {
			 BkChanYeLianTreeNode f = it.next();
			 BanKuai bk = (BanKuai)f;
			 Integer bkquanzhong = bk.getGeGuSuoShuBanKuaiWeight (node.getMyOwnCode() );
		      
	    	 String displayedbkformate = "\"" + bk.getMyOwnCode() + bk.getMyOwnName() + "(QZ:" + bkquanzhong.toString() + ")" + "\"";
	    	 try {
	    		 boolean inrsbk = false; 
    			 if(  gzbkcodeset.contains(bk.getMyOwnCode() ) && bkquanzhong >=5 ) { //是当前关注的板块，并且权重大于5用红色标记，权重太小，说明没有什么业务
    				 
    				 content.append("<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + "</a>   " );
//	    			 shuyuruoshibankuai = true ;
	    		 } else
	    			 content.append("<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");
	    	 } catch (java.lang.NullPointerException e) {
	    		 content.append("<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");
	    	 }
	     } 
	     
	     content.append( "</body>"
					+ "</html>");
	    
	     htmlstring = doc.toString();

	     return htmlstring;
	}


}
