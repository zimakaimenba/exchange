package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.Color;

import java.time.LocalDate;

import java.util.Collection;
import java.util.HashSet;

import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.NodesDataServices.ServicesForNode;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Core.exportimportrelated.NodesTreeRelated;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.google.common.base.Strings;


public class ExportMatchedNode2
{
	private BanKuaiAndGeGuMatchingConditions cond;
	private LocalDate exportdate;

	public ExportMatchedNode2 (BanKuaiAndGeGuMatchingConditions cond) 
	{
		this.cond = cond;
	}
	
	public Set<TDXNodes> checkTDXNodeMatchedCurSettingConditons (LocalDate exportdate, String period)
	{
		this.exportdate = exportdate;
		Set<TDXNodes> matchednodeset = new HashSet<> ();
		Set<String> checkednodesset = new HashSet<> ();
		
		BanKuaiAndStockTree bkcyltree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
		int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
			//Yellow sign part
			Boolean yellowsignresult = checkNodeMatchedCurSettingConditonsOfYellowSign (childnode, exportdate, period);
			if(yellowsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && yellowsignresult ) matchednodeset.add((TDXNodes) childnode);
//				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
			} 
			// Red sign part
			Boolean redsignresult = checkNodeMatchedCurSettingConditonsOfRedSign (childnode, exportdate, period);
			if(redsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && redsignresult ) matchednodeset.add((TDXNodes) childnode);
//				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
			}
			
			if(this.cond.shouldExportOnlyRedSignBkStk() || this.cond.shouldExportOnlyYellowSignBkStk() ) continue;
			
			//normal part
			if(childnode.getType() == BkChanYeLianTreeNode.TDXBK ) {
				if( ! ((BanKuai)childnode).getBanKuaiOperationSetting().isImportdailytradingdata() )
					continue;
				if( ! ((BanKuai)childnode).getBanKuaiOperationSetting().isExportTowWlyFile() )
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
					continue;
				
				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) 
					continue;
				
				String checkresult = this.checkBanKuaiMatchedCurSettingConditons((BanKuai)childnode, exportdate, period);
				checkednodesset.add( childnode.getMyOwnCode() );
				
				if( checkresult.toUpperCase().contains("UNMATCH") ) continue;
				
				if(checkresult.toUpperCase().contains("MATCHED") ) {
					if( !this.cond.shouldExportOnlyGeGuNotBanKuai() ) //只导出板块个股
						if(!matchednodeset.contains(childnode)) 
							matchednodeset.add((TDXNodes) childnode);
				}  
					
				if( this.cond.shouldExportOnlyBankuaiNotGeGu() )		continue;
								
				if( !checkresult.toUpperCase().contains("WITHCHECKGEGU") )			continue;
				
				childnode = ((SvsForNodeOfBanKuai)((BanKuai) childnode).getServicesForNode(true)).getAllGeGuOfBanKuai((BanKuai) childnode);
				((BanKuai) childnode).getServicesForNode(false);
//				
				Collection<BkChanYeLianTreeNode> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(exportdate,0);
				if(nowbkallgg == null) continue;

				for (BkChanYeLianTreeNode ggstock : nowbkallgg) {
						if( checkednodesset.contains(ggstock.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
							continue;
						
						Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)ggstock);
						if(result && !matchednodeset.contains(ggstock))	matchednodeset.add((TDXNodes) ggstock);
						if(!checkednodesset.contains(ggstock.getMyOwnCode()))  checkednodesset.add( ggstock.getMyOwnCode() );
				}
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
				
				if( this.cond.getExportGeGuOfRedSignBanKuai() )  
					continue;
				
				if( this.cond.getExportGeGuOfYellowSignBanKuai() )  
					continue;
				
				if( ((Stock)childnode).isVeryVeryNewXinStock(this.exportdate) )
					continue;
			
				Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)childnode);
				if(result != null && result && !matchednodeset.contains(childnode))	matchednodeset.add((TDXNodes) childnode);
				if(!checkednodesset.contains(childnode.getMyOwnCode() ) )  checkednodesset.add( childnode.getMyOwnCode() );
			}
		}
		//DZH part
		BanKuaiAndStockTree dzhbkcyltree = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks();
		TDXNodes dzhtreeroot = (TDXNodes)dzhbkcyltree.getModel().getRoot();
		bankuaicount = dzhbkcyltree.getModel().getChildCount(dzhtreeroot);
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)dzhbkcyltree.getModel().getChild(treeroot, i);
			//Yellow sign part
			Boolean yellowsignresult = checkNodeMatchedCurSettingConditonsOfYellowSign (childnode, exportdate, period);
			if(yellowsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && yellowsignresult ) matchednodeset.add((TDXNodes) childnode);
//				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
			} 
			// Red sign part
			Boolean redsignresult = checkNodeMatchedCurSettingConditonsOfRedSign (childnode, exportdate, period);
			if(redsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && redsignresult ) matchednodeset.add((TDXNodes) childnode);
//				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
			}
			
			if(this.cond.shouldExportOnlyRedSignBkStk() || this.cond.shouldExportOnlyYellowSignBkStk() ) continue;
			
			//normal part
			if(childnode.getType() == BkChanYeLianTreeNode.DZHBK ) {
				if( ! ((BanKuai)childnode).getBanKuaiOperationSetting().isImportdailytradingdata() )
					continue;
				if( ! ((BanKuai)childnode).getBanKuaiOperationSetting().isExportTowWlyFile() )
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
					continue;
				
				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) 
					continue;
				
				String checkresult = this.checkBanKuaiMatchedCurSettingConditons((BanKuai)childnode, exportdate, period);
				checkednodesset.add( childnode.getMyOwnCode() );
				
				if( checkresult.toUpperCase().contains("UNMATCH") ) continue;
				
				if(checkresult.toUpperCase().contains("MATCHED") ) {
					if( !this.cond.shouldExportOnlyGeGuNotBanKuai() ) //只导出板块个股
						if(!matchednodeset.contains(childnode)) 
							matchednodeset.add((TDXNodes) childnode);
				}  
					
				if( this.cond.shouldExportOnlyBankuaiNotGeGu() )		continue;
								
				if( !checkresult.toUpperCase().contains("WITHCHECKGEGU") )			continue;
				
				childnode = ((SvsForNodeOfBanKuai)((BanKuai) childnode).getServicesForNode(true)).getAllGeGuOfBanKuai((BanKuai) childnode);
				((BanKuai) childnode).getServicesForNode(false);
//				
				Collection<BkChanYeLianTreeNode> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(exportdate,0);
				if(nowbkallgg == null) continue;

				for (BkChanYeLianTreeNode ggstock : nowbkallgg) {
						if( checkednodesset.contains(ggstock.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
							continue;
						
						Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)ggstock);
						if(result && !matchednodeset.contains(ggstock))	matchednodeset.add((TDXNodes) ggstock);
						if(!checkednodesset.contains(ggstock.getMyOwnCode()))  checkednodesset.add( ggstock.getMyOwnCode() );
				}
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
//			String colorcode = String.format("#%02x%02x%02x", Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getGreen() );
			if(!filetree.selfIsMatchModel(exportdate,Color.YELLOW) )		return false;
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
//			String colorcode = String.format("#%02x%02x%02x", Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getGreen() );
			if(!filetree.selfIsMatchModel(exportdate,Color.RED) )		return false;
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
			
//			String checkresult = ""; 
			if( ! this.cond.shouldExportAllBanKuai()  )
				return "WITHCHECKGEGU";
			
			if( this.cond.getExportGeGuOfRedSignBanKuai() && node.getNodeTreeRelated().selfIsMatchModel(exportdate, Color.RED))  
				return  "WITHCHECKGEGU";
			
			if( this.cond.getExportGeGuOfYellowSignBanKuai() && node.getNodeTreeRelated().selfIsMatchModel(exportdate, Color.YELLOW))  
				return  "WITHCHECKGEGU";
			
			if(this.cond.shouldExportOnlyGeGuNotBanKuai() )
				return "UNMATCH";
			
			return "UNMATCH";
	}
}
