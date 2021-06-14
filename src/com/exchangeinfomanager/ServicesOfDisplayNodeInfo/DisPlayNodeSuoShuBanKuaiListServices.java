package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;


import java.time.LocalDate;

import java.util.Set;
import org.jsoup.Jsoup;


import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.nodejibenmian.ServicesOfNodeJiBenMianInfo;
import com.google.common.base.Strings;

public class DisPlayNodeSuoShuBanKuaiListServices implements ServicesOfNodeJiBenMianInfo
{
	private BkChanYeLianTreeNode node;
	private LocalDate requireddate;
	private BanKuai samenamebk;
	
	
	public DisPlayNodeSuoShuBanKuaiListServices (BkChanYeLianTreeNode node)
	{
		this.node = node;
		
		SvsForNodeOfStock svsstk = new SvsForNodeOfStock ();
		node = svsstk.getNodeSuoShuBanKuaiList(node);
		svsstk = null;
	}
	public void setDateForInfoCurrentNode (LocalDate start )
	{
		this.requireddate= start;
	}
	public void setHightSameNameBanKuai (BanKuai bk) {
		this.samenamebk = bk;
	}
	@Override
	public String getRequiredHtmlInfo() 
	{
		Set<BkChanYeLianTreeNode> suosutdxbankuai = ((Stock)node).getGeGuCurSuoShuTDXSysBanKuaiList();
		Set<BkChanYeLianTreeNode> suosudzhbankuai = ((Stock)node).getGeGuCurSuoShuDZHSysBanKuaiList();
		
//		LocalDate monday = requiredstart.with( DayOfWeek.MONDAY );
//		LocalDate sunday = requiredend.with( DayOfWeek.SUNDAY );
//		ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
//    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,null,monday,sunday);
//    	svsdqgz.setCache(dqgzcache);
		 
		if(this.requireddate == null) this.requireddate = LocalDate.now();
		String samenamebkname;
		try {	samenamebkname = this.samenamebk.getMyOwnName().trim();
		} catch (Exception e) {samenamebkname = "";}
		
//		DuanQiGuanZhuServices svsdqgz = new DuanQiGuanZhuServices ();
//		Collection<News> curdqgz = svsdqgz.getCurrentNews( this.requireddate );
//		Set<String> gzbkcodeset = new HashSet<> ();
//		for (Iterator<News> lit = curdqgz.iterator(); lit.hasNext(); ) {
//    		News f = lit.next();
//    		String bkcode = f.getNewsOwnerCodes();
//    		gzbkcodeset.add(bkcode);
//    	}

		 String htmlstring = "";
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 
		 org.jsoup.select.Elements content = doc.select("body");
		 
		 content.append( "<html> "
		 		+ "<body>"
		 		);
		 //优先级: 当前关注板块，和当前板块同名， 自身颜色
		 for ( java.util.Iterator<BkChanYeLianTreeNode> it = suosutdxbankuai.iterator(); it.hasNext(); ) {
			 BkChanYeLianTreeNode f = it.next();
			 BanKuai bk = (BanKuai)f;
			 Integer bkquanzhong = bk.getGeGuSuoShuBanKuaiWeight (node.getMyOwnCode() );
		     String bkselfcolor = bk.getBanKuaiOperationSetting().getBanKuaiLabelColorStr();

		     String displayedbkformate = "\"" + bk.getMyOwnCode() + bk.getMyOwnName() + "(QZ:" + bkquanzhong.toString() + ")" + "\"";
	    	 try {
//	    		 boolean inrsbk = false; 
    			 if(  bk.isInDuanQiGuanZhuRange(this.requireddate) !=null && bkquanzhong >=5 ) { //是当前关注的板块，并且权重大于5用红色标记，权重太小，说明没有什么业务
    				 content.append("<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + "</a>   " );
	    		 } else if( !Strings.isNullOrEmpty(samenamebkname) && bk.getMyOwnName().equals(samenamebkname) )
	    			 content.append("<a style=\"color:#FF00FFF\" href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");
	    		 else
	    			 content.append("<a style=\"color:" + bkselfcolor + "\" href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");
	    	 } catch (java.lang.NullPointerException e) { content.append("<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   "); }
	     }
		 for ( java.util.Iterator<BkChanYeLianTreeNode> it = suosudzhbankuai.iterator(); it.hasNext(); ) {
			 BkChanYeLianTreeNode f = it.next();
			 BanKuai bk = (BanKuai)f;
			 Integer bkquanzhong = bk.getGeGuSuoShuBanKuaiWeight (node.getMyOwnCode() );
			 String bkselfcolor = bk.getBanKuaiOperationSetting().getBanKuaiLabelColorStr();
			 
	    	 String displayedbkformate = "\"" + bk.getMyOwnCode() + bk.getMyOwnName() + "(QZ:" + bkquanzhong.toString() + ")" + "\"";
	    	 try {
//	    		 boolean inrsbk = false; 
    			 if(  bk.isInDuanQiGuanZhuRange(this.requireddate) !=null && bkquanzhong >=5 ) { //是当前关注的板块，并且权重大于5用红色标记，权重太小，说明没有什么业务		 
    				 content.append("<a style=\"color:red font-family:STHeiti \" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + "</a>   " );
    			 } else if( !Strings.isNullOrEmpty(samenamebkname) && bk.getMyOwnName().equals(samenamebkname) )
	    			 content.append("<a style=\"color:#FF00FFF font-family:STHeiti \" href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");
    			 else
    				 content.append("<a style=\"color:" + bkselfcolor + "font-family:STHeiti \" href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");
	    	 } catch (java.lang.NullPointerException e) { content.append("<a style=\"color:SlateBlue font-family:STHeiti \" href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a>   ");}
	     } 
	     
	     content.append( "</body>"
					+ "</html>");
	    
	     htmlstring = doc.toString();

	     return htmlstring;
	}


}
