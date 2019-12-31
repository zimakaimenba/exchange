package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesOfDisplayNodeInfo;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;

public class DisPlayNodeSuoShuBanKuaiListServices implements ServicesOfDisplayNodeInfo
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

		String htmlstring = "";
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 
		 org.jsoup.select.Elements content = doc.select("body");
		 
		 content.append( "<html> "
		 		+ "<body>"
		 		);
		 
		 boolean shuyuruoshibankuai = false;
		 for ( java.util.Iterator<BkChanYeLianTreeNode> it = suosusysbankuai.iterator(); it.hasNext(); ) {
			 BkChanYeLianTreeNode f = it.next();
			 BanKuai bk = (BanKuai)f;
			 Integer bkquanzhong = bk.getGeGuSuoShuBanKuaiWeight (node.getMyOwnCode() );
		      
	    	 String displayedbkformate = "\"" + f.getMyOwnCode() + f.getMyOwnName() + "(QZ:" + bkquanzhong.toString() + ")" + "\"";
	    	 try {
	    		 boolean inrsbk = false; 
    			 if(inrsbk ) {
    				 content.append("<a style=\"color:green\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + "</a>   " );
	    			 shuyuruoshibankuai = true ;
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
