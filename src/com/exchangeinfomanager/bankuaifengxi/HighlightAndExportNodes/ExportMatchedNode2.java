package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.ServicesForNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.udojava.evalex.Expression;

public class ExportMatchedNode2
{
	private BanKuaiAndGeGuMatchingConditions cond;
//	private SvsForNodeOfBanKuai svsbk;
//	private SvsForNodeOfStock svsstk;
	private LocalDate exportdate;

	public ExportMatchedNode2 (BanKuaiAndGeGuMatchingConditions cond) 
	{
		this.cond = cond;
	}
	
	public Set<TDXNodes> checkTDXNodeMatchedCurSettingConditons (LocalDate exportdate, String period)
	{
		this.exportdate = exportdate;
		BanKuaiAndStockTree bkcyltree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
		int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
		
		Set<TDXNodes> matchednodeset = new HashSet<> ();
		Set<String> checkednodesset = new HashSet<String> ();
		
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
			//Yellow sign part
			Boolean yellowsignresult = checkNodeMatchedCurSettingConditonsOfYellowSign (childnode, exportdate, period);
			if(yellowsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && yellowsignresult ) matchednodeset.add((TDXNodes) childnode);
//				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
			} 
			Boolean redsignresult = checkNodeMatchedCurSettingConditonsOfRedSign (childnode, exportdate, period);
			if(redsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && redsignresult ) matchednodeset.add((TDXNodes) childnode);
//				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
			}
			
			if(this.cond.shouldExportOnlyRedSignBkStk() || this.cond.shouldExportOnlyYellowSignBkStk() ) continue;
			
			//normal part
			if(childnode.getType() == BkChanYeLianTreeNode.TDXBK ) {
//				if( ! ((BanKuai)childnode).isImportdailytradingdata() )
//					continue;
//				if( ! ((BanKuai)childnode).isExportTowWlyFile() )
//					continue;
//				
//				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
//						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
//						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
//					continue;
//				
//				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) 
//					continue;
//				
////				String checkresult = this.checkBanKuaiMatchedCurSettingConditons((BanKuai)childnode, exportdate, period);
//				checkednodesset.add( childnode.getMyOwnCode() );
//				
//				if(checkresult.toUpperCase().contains("MATCHED") ) {
//					if( !this.cond.shouldExportOnlyGeGuNotBanKuai() ) //只导出板块个股
//						if(!matchednodeset.contains(childnode)) 
//							matchednodeset.add((TDXNodes) childnode);
//				}  
//					
//				if( this.cond.shouldExportOnlyBankuaiNotGeGu() )		continue;
//								
//				if( !checkresult.toUpperCase().contains("WITHCHECKGEGU") )			continue;
//				
//				childnode = ((SvsForNodeOfBanKuai)((BanKuai) childnode).getServicesForNode(true)).getAllGeGuOfBanKuai((BanKuai) childnode);
//				((BanKuai) childnode).getServicesForNode(false);
//				
//				Collection<BkChanYeLianTreeNode> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(exportdate,0);
//				if(nowbkallgg != null)
//					for (BkChanYeLianTreeNode ggstock : nowbkallgg) {
//						if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
//							continue;
//						
//						Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)ggstock);
//						if(result && !matchednodeset.contains(childnode))	matchednodeset.add((TDXNodes) childnode);
//						if(!checkednodesset.contains(childnode))  checkednodesset.add( childnode.getMyOwnCode() );
//					}
			}
			
			if(childnode.getType() == BkChanYeLianTreeNode.TDXGG  ) { //只有在导出所有板块个股的时候才会用到这里，否则个股都在前面检查了
				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
					continue;
				
				if(!this.cond.shouldExportST() && childnode.getMyOwnName() != null && childnode.getMyOwnName().toUpperCase().contains("ST"))
					continue;
				if(childnode.getMyOwnName()!= null &&  childnode.getMyOwnName().contains("退"))
					continue;
				
				if( this.cond.shouldExportOnlyYellowSignBkStk() ) // 
					continue;
				
				if( ((Stock)childnode).isVeryVeryNewXinStock(this.exportdate) )
					continue;
			
				Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)childnode);
				if(result != null && result && !matchednodeset.contains(childnode))	matchednodeset.add((TDXNodes) childnode);
				if(!checkednodesset.contains(childnode.getMyOwnCode() ) )  checkednodesset.add( childnode.getMyOwnCode() );
			}
		}
		
		checkednodesset = null;
		return matchednodeset;
	}
	/*
	 * 检查黄标
	 */
	private Boolean checkNodeMatchedCurSettingConditonsOfYellowSign(BkChanYeLianTreeNode childnode, LocalDate exportdate,	String period) 
	{
		if(this.cond.shouldExportOnlyYellowSignBkStk() ) {
			NodesTreeRelated filetree = childnode.getNodeTreeRelated ();
			String colorcode = String.format("#%02x%02x%02x", Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getGreen() );
			if(!filetree.selfIsMatchModel(exportdate,colorcode) )		return false;
			else	return true;
		} 
		
		return null;
	}
	/*
	 * 检查红标
	 */
	private Boolean checkNodeMatchedCurSettingConditonsOfRedSign(BkChanYeLianTreeNode childnode, LocalDate exportdate,	String period) 
	{
		if(this.cond.shouldExportOnlyRedSignBkStk() ) {
			NodesTreeRelated filetree = childnode.getNodeTreeRelated ();
			String colorcode = String.format("#%02x%02x%02x", Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getGreen() );
			if(!filetree.selfIsMatchModel(exportdate,colorcode) )		return false;
			else	return true;
		} 
		
		return null;
	}
	/*
	 * 对个股进行检查
	 */
	private Boolean checkNodesMatchedCurSettingConditions (TDXNodes node)
	{
		Boolean checkresult = null;
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");

		ServicesForNode svsnode = node.getServicesForNode(true);
		try {	node =  (TDXNodes) svsnode.getNodeData( node,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
		} catch (java.lang.NullPointerException e) {	e.printStackTrace();}
		
		String formula = this.cond.getExportConditionFormula();
		if(formula != null && formula.contains("CLOSEVSMA") || formula.contains("ZhangDieFu") || formula.contains("DAY")) 
			svsnode.getNodeKXian( node, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
		
		checkresult = node.checkNodeDataMatchedWithFormula(formula);
		

		return checkresult;
	}
	private String getKeywordValue(TDXNodes node, String kw, LocalDate selectdate, String curperiod,String factor)
	{
		  Object value = null;
		  factor = factor.replaceAll(" ", "");
		  NodeXPeriodData nodexdata = node.getNodeXPeroidData(curperiod);
          
		  if(kw.equals("CLOSEVSMA")) {
	          Pattern p = Pattern.compile("\'.*?\'", Pattern.CASE_INSENSITIVE);
	          Matcher m = p.matcher(factor);
	          int indexofeq ;  int indexofend = 0;
	          while (m.find()) {
	        	  indexofeq = m.start();
	        	  indexofend = m.end();
	          }
	          String maformula = factor.substring(indexofend + 1, factor.length()-1);
			  value =  nodexdata.getNodeDataByKeyWord( kw, selectdate,  maformula);
		  }
		  else try {  value = nodexdata.getNodeDataByKeyWord(kw,selectdate,"");
		  } catch (java.lang.NullPointerException e) {  e.printStackTrace();
			  System.out.println(node.getMyOwnCode() + node.getMyOwnName() + "Get keyword failed. keyword is" + kw + selectdate.toString() + ". FACTOR is" + factor + "PERIOD is " + curperiod);
		  }
		  
		  if(value != null)		  return value.toString();
		  else	  return null;
	}
	/*
	 * 检查板块是否符合设定
	 */
	public String checkBanKuaiMatchedCurSettingConditons (BanKuai node, LocalDate exportdate, String period)
	{
			String settingbk = this.cond.getSettingBanKuai();

			if( this.cond.shouldExportOnlyCurrentBanKuai()  && !Strings.isNullOrEmpty( settingbk ) )  {//仅限当前板块
				if(!node.getMyOwnCode().equals(settingbk))
					return "UNMATCH";
			}
			
			if( this.cond.shouldExportBankuaiInConfig ()) //只导出板块设置允许的
				if( !((BanKuai)node).getBanKuaiOperationSetting().isExportTowWlyFile() )
					return "UNMATCH";
			
			if(this.cond.shouldExportOnlyGeGuNotBanKuai() && ! this.cond.shouldExportAllBanKuai() )
				return  "WITHCHECKGEGU";
			
			if(this.cond.shouldExportOnlyGeGuNotBanKuai() )
				return "UNMATCH";
			
			String checkresult = ""; 
			if( ! this.cond.shouldExportAllBanKuai()  )
				checkresult = "WITHCHECKGEGU";
			
			return checkresult;
	}
}
