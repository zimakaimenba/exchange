package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Strings;

public class ExportMatchedNode
{
	private BanKuaiGeGuMatchCondition cond;

	public ExportMatchedNode (BanKuaiGeGuMatchCondition cond) 
	{
		this.cond = cond;
	}
	
	public Set<TDXNodes> checkTDXNodeMatchedCurSettingConditons (LocalDate exportdate, String period)
	{
		BanKuaiAndStockTree bkcyltree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
		int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
		
		Set<TDXNodes> matchednodeset = new HashSet<TDXNodes> ();
		Set<String> checkednodesset = new HashSet<String> ();
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");
		
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai  ();
//		SvsForNodeOfStock svsstk = new SvsForNodeOfStock  ();
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
			if(childnode.getType() == BkChanYeLianTreeNode.TDXBK) {
				
				if( ! ((BanKuai)childnode).isImportdailytradingdata() )
					continue;
				if( ! ((BanKuai)childnode).isExportTowWlyFile() )
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
					continue;
				
				
				childnode = svsbk.getNodeData( childnode, requirestart, exportdate,period,true);
				if( this.cond.shouldExportYangXianBanKuai () ) //需要OHLC数据才同步K线数据，节约时间
					svsbk.syncNodeData( childnode);
				
				String checkresult = this.checkBanKuaiMatchedCurSettingConditons((BanKuai)childnode, exportdate, period);
				
				if(checkresult.toUpperCase().contains("MATCHED") ) {
					if( !this.cond.shouldExportOnlyGeGuNotBanKuai() ) //只导出板块个股
						if(!matchednodeset.contains(childnode)) 
							matchednodeset.add((TDXNodes) childnode);
				}  
					
				if( this.cond.shouldExportOnlyBankuaiNotGeGu() )
					continue;
								
				if( !checkresult.toUpperCase().contains("WITHCHECKGEGU") )
					continue;
				
				childnode = svsbk.getAllGeGuOfBanKuai((BanKuai) childnode);
				Collection<BkChanYeLianTreeNode> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(exportdate,0);
				for (BkChanYeLianTreeNode ggstock : nowbkallgg) {
					this.checkStockMatchedCurSettingConditions ((Stock)ggstock, checkednodesset, matchednodeset,requirestart,exportdate,period);
//					 if(matchednodeset.contains( (TDXNodes)ggstock ) )
//						 continue;
//					 if( checkednodesset.contains(ggstock.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
//						 continue;
//					 
//					 ggstock = svsstk.getNodeData(ggstock,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
//
//					 Boolean stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA( (Stock)ggstock, exportdate, period);
//					 if(stkcheckresult == null) {//停牌股
//						 checkednodesset.add( ggstock.getMyOwnCode() );
//						 continue;
//					 }
//					 
//					 if(stkcheckresult  &&  this.cond.getExportYangXianGeGu() != null  ) {
//						 svsstk.getNodeKXian( ggstock, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
//						 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfZhangFu((Stock)ggstock, exportdate, period);
//					 }
//					 
//					 if(stkcheckresult  && !Strings.isNullOrEmpty(this.cond.getSettingMaFormula() ) ) {
//						 svsstk.getNodeKXian( ggstock, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
//						 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfCheckMA((Stock)ggstock, exportdate, period);
//					 }
//					 
//					 try{
//					 if(stkcheckresult)
//						 matchednodeset.add((TDXNodes) ggstock);
//					 } catch(Exception e) {
//						 checkednodesset.add( ggstock.getMyOwnCode() );
//						 e.printStackTrace();
//					 }
//					 
//					 checkednodesset.add( ggstock.getMyOwnCode() );
				}
			}
			
			if(childnode.getType() == BkChanYeLianTreeNode.TDXGG  ) { //只有在导出所有板块个股的时候才会用到这里，否则个股都在前面检查了
				if( !this.cond.shouldExportAllBanKuai() && !this.cond.shouldExportOnlyYellowSignBkStk() ) 
					continue;
			
				this.checkStockMatchedCurSettingConditions ((Stock)childnode, checkednodesset, matchednodeset,requirestart,exportdate,period);
//				if(matchednodeset.contains( (TDXNodes)childnode ) )
//					 continue;
//				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
//					 continue;
//				
//				childnode = svsstk.getNodeData( (Stock)childnode,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
//				Boolean stkcheckresult = null;
//				try{
//				stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA( (Stock)childnode, exportdate, period);
//				if(stkcheckresult == null) {//停牌股
//					 checkednodesset.add( childnode.getMyOwnCode() );
//					 continue;
//				}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if(stkcheckresult  &&  this.cond.getExportYangXianGeGu() != null  ) {
//					 svsstk.getNodeKXian( childnode, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
//					 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfZhangFu((Stock)childnode, exportdate, period);
//				 }
//				 
//				 if(stkcheckresult  && !Strings.isNullOrEmpty(this.cond.getSettingMaFormula() ) ) {
//					 svsstk.getNodeKXian( childnode, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
//					 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfCheckMA((Stock)childnode, exportdate, period);
//				 }
//				try{
//				if(stkcheckresult)
//					matchednodeset.add(  (TDXNodes)childnode);
//				} catch(Exception e) {
//					checkednodesset.add( childnode.getMyOwnCode() );
//					e.printStackTrace();
//				 }
//					 
//				 checkednodesset.add( childnode.getMyOwnCode() );
				
			}
			
		}
		
		checkednodesset = null;
		return matchednodeset;
	}
	/*
	 * 对个股进行检查
	 */
	private Boolean checkStockMatchedCurSettingConditions (Stock childnode,
			Set<String> checkednodesset, Set<TDXNodes> matchednodeset,LocalDate requirestart,LocalDate exportdate,String period)
	{
		if(matchednodeset.contains( (TDXNodes)childnode ) )
			 return false;
		if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
			 return false;
		
		SvsForNodeOfStock svsstk = new SvsForNodeOfStock  ();
		childnode = (Stock) svsstk.getNodeData( (Stock)childnode,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
		
		//检查黄标
		Boolean stkcheckresult = null;
		stkcheckresult = this.checkStockMatchedCurSettingConditonsOfYellowSign( (Stock)childnode, exportdate, period);
		if( stkcheckresult != null && stkcheckresult == false) {
			 checkednodesset.add( childnode.getMyOwnCode() );
			 return false;
		} else if( stkcheckresult != null && stkcheckresult == true) {
			matchednodeset.add(  (TDXNodes)childnode);
			checkednodesset.add( childnode.getMyOwnCode() );
			return stkcheckresult;
		}
		
		//stkcheckresult == null 说明不是到处黄标个股,做下面的，这样提高效率
		try{
			stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA( (Stock)childnode, exportdate, period);
			if(stkcheckresult == null) {//停牌股
				 checkednodesset.add( childnode.getMyOwnCode() );
				 return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(stkcheckresult  &&  this.cond.getExportYangXianGeGu() != null  ) {
			 svsstk.getNodeKXian( childnode, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
			 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfZhangFu((Stock)childnode, exportdate, period);
		 }
		 
		 if(stkcheckresult  && !Strings.isNullOrEmpty(this.cond.getSettingMaFormula() ) ) {
			 svsstk.getNodeKXian( childnode, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
			 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfCheckMA((Stock)childnode, exportdate, period);
		 }
		try{
		if(stkcheckresult)
			matchednodeset.add(  (TDXNodes)childnode);
		} catch(Exception e) {
			checkednodesset.add( childnode.getMyOwnCode() );
//			e.printStackTrace();
		 }
			 
		 checkednodesset.add( childnode.getMyOwnCode() );
		 svsstk = null;
		 return stkcheckresult;
	}
	/*
	 * 检查黄标
	 */
	private Boolean checkStockMatchedCurSettingConditonsOfYellowSign(Stock childnode, LocalDate exportdate,
			String period) 
	{
		if(this.cond.shouldExportOnlyYellowSignBkStk() ) {
			NodesTreeRelated filetree = childnode.getNodeTreeRelated ();
			if(!filetree.selfIsMatchModel(exportdate) )
				return false;
			else
				return true;
		} 
		
		return null;
	}

	/*
	 * 检查板块是否符合设定
	 */
	public String checkBanKuaiMatchedCurSettingConditons (BanKuai node, LocalDate exportdate, String period)
	{
			String settingbk = this.cond.getSettingBanKuai();
			if(this.cond.shouldExportOnlyYellowSignBkStk() ) {
				NodesTreeRelated filetree = node.getNodeTreeRelated ();
    			if(!filetree.selfIsMatchModel(exportdate) )
    				return "UNMATCH";
			}
			if( this.cond.shouldExportOnlyCurrentBanKuai()  && !Strings.isNullOrEmpty( settingbk ) )  {//仅限当前板块
				if(!node.getMyOwnCode().equals(settingbk))
					return "UNMATCH";
			}
			
			if( this.cond.shouldExportBankuaiInConfig ()) //只导出板块设置允许的
				if( !((BanKuai)node).isExportTowWlyFile() )
					return "UNMATCH";
			
			NodeXPeriodData nodexdata = ((BanKuai)node).getNodeXPeroidData(period);
			if(nodexdata == null) //该时间还没有数据，对板块来说就是还没有诞生
				return "UNMATCH";
			
//			if(nodexdata.getIndexOfSpecificDateOHLCData(exportdate,0) == null) 
//				return "UNMATCH";
			
			if( this.cond.shouldExportChenJiaoErZhanbiUpBanKuai() ) { //导出环比上升的板块
					Double cjezbchangerate = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(exportdate, 0);
					if(cjezbchangerate == null)
						return "UNMATCH";
					Double cjlzbchangerate = nodexdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(exportdate, 0);
					if(cjezbchangerate <0 && cjlzbchangerate <0)
						return "UNMATCH";
			}
			if( this.cond.shouldExportYangXianBanKuai () ) {
				Double zhangfu = nodexdata.getSpecificOHLCZhangDieFu(exportdate, 0);
				if(zhangfu == null || zhangfu <0)
					return "UNMATCH";
			}
			String checkresult = ""; 
			if( ! this.cond.shouldExportAllBanKuai())
				checkresult = "WITHCHECKGEGU";
			
			Integer settindpgmaxwk = this.cond.getSettingDpMaxWk();
			if(settindpgmaxwk == null)
				settindpgmaxwk = -1;
			Integer seetingcjemaxwk = this.cond.getSettingChenJiaoErMaxWk();
			if(seetingcjemaxwk == null)
				seetingcjemaxwk = -1;
			if(nodexdata.getChengJiaoEr(exportdate,0) != null ) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，而当前现在成交量已经不再存入数据库

					Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(exportdate,0);
					Integer recordmaxcjewk =nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(exportdate,0);
					if( recordmaxbkwk >= settindpgmaxwk && recordmaxcjewk >= seetingcjemaxwk ) { //满足条件，导出 ; 板块和个股不一样，只有一个占比
							checkresult = checkresult + "MATCHED";
					}
			}
			
			return checkresult;
	}
	/*
	 * 涨幅需要K线数据，很耗时，
	 */
	public Boolean checkStockMatchedCurSettingConditonsOfZhangFu (Stock stock, LocalDate exportdate, String period)
	{
		Boolean checkresult = null; 
		if( this.cond.getExportYangXianGeGu() != null ) {
			NodeXPeriodData nodexdata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			if(nodexdata == null)
				return false;
			
			double zhangfu = nodexdata.getSpecificOHLCZhangDieFu(exportdate,0);
			
			double settinglevel = this.cond.getExportYangXianGeGu();
			if(zhangfu >  settinglevel)
				checkresult =  true;
			else
				return false;
		}
		
		return checkresult;
	}
	/*
	 * MA需要K线数据，很耗时，最好是其他条件如果都满足再检查MA
	 */
	public Boolean checkStockMatchedCurSettingConditonsOfCheckMA (Stock stock, LocalDate exportdate, String period) 
	{
		Boolean checkresult = null; 
		
		String maf = this.cond.getSettingMaFormula();
		if(maf != null) {
			NodeXPeriodData nodexdataday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			checkresult = nodexdataday.checkCloseComparingToMAFormula(maf, exportdate, 0);
		}

		return checkresult;
	}
	/*
	 * MA需要K线数据，很耗时，最好是其他条件如果都满足再检查MA
	 */
	public Boolean checkStockMatchedCurSettingConditonsWithoutCheckMA (Stock stock,LocalDate exportdate, String period) 
	{
//		if(this.cond.shouldExportOnlyYellowSignBkStk() ) {
//			NodesTreeRelated filetree = stock.getNodeTreeRelated ();
//			if(!filetree.selfIsMatchModel(exportdate) )
//				return false;
//		}
		try {
			Boolean shouldexportst = this.cond.shouldExportST();
			 if(stock.getMyOwnName().toUpperCase().contains("ST") &&  !shouldexportst)
					return false;
		} catch (java.lang.NullPointerException e) {}
		
		if( stock.isVeryVeryNewXinStock(exportdate) ) // 刚上市的新股也不考虑
			 return false;
		
		NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
		Double recordcje = nodexdata.getChengJiaoEr(exportdate, 0);
		if(recordcje == null) //停牌股
			return null;
		//条件设定
		Double settingcjemin = this.cond.getSettingChenJiaoErMin() ;
		if( settingcjemin == null)
			settingcjemin = -1.0;
		Double settingcjemax = this.cond.getSettingChenJiaoErMax() ;
		if(  settingcjemax == null)
			settingcjemax = 1000000000000.0;
		Integer settindpgmaxwk = this.cond.getSettingDpMaxWk();
		if( settindpgmaxwk == null)
			settindpgmaxwk = -1;
		Integer seetingcjemaxwk = this.cond.getSettingChenJiaoErMaxWk();
		if( seetingcjemaxwk == null)
			seetingcjemaxwk = -1;
		Double settinghsl = this.cond.getSettingHuanShouLv();
		if( settinghsl == null)
			settinghsl = -1.0;
		Double settinggeguyangxianuplevel = this.cond.getExportYangXianGeGu ();
		if(settinggeguyangxianuplevel == null)
			settinggeguyangxianuplevel = -200.0;
		
		Boolean shouldhavedayangxian = true;
		Double cjelevelofyangxian = this.cond.getChenJiaoErBottomForYangXianLevle();
		if(cjelevelofyangxian == null)
			shouldhavedayangxian = false;
		
		Boolean shouldhavelianxufl = true;
		Double cjeleveloflianxufl = this.cond.getChenJiaoErBottomForFangLiangLevle();
		if(cjeleveloflianxufl == null)
			shouldhavelianxufl = false;
		//个股本周数据
		Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(exportdate,0);
		Integer recordmaxcjewk = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(exportdate,0);
		Double recordhsl = ((StockNodesXPeriodData)nodexdata).getSpecificTimeHuanShouLv(exportdate, 0);
//		Double recordzhangfu = nodexdata.getSpecificOHLCZhangDieFu(exportdate,0);
		
		//逐步计较
		Boolean cjematch = null;
		if(recordcje != null  && recordcje >= settingcjemin && recordcje <= settingcjemax )
			cjematch = true;
		else if(recordcje != null  && ( recordcje < settingcjemin || recordcje > settingcjemax ))
			cjematch = false;
		else 
			cjematch = true;
		
		Boolean dpmaxwkmatch = null;
		if(recordmaxbkwk != null  && recordmaxbkwk >= settindpgmaxwk)
			dpmaxwkmatch = true;
		else if(recordmaxbkwk != null  && recordmaxbkwk < settindpgmaxwk)
			dpmaxwkmatch = false;
		else
			dpmaxwkmatch = true;
		
		Boolean cjewkmatch = null;
		if(recordmaxcjewk != null && recordmaxcjewk >= seetingcjemaxwk)
			cjewkmatch = true;
		else if(recordmaxcjewk != null && recordmaxcjewk < seetingcjemaxwk)
			cjewkmatch = false;
		else
			cjewkmatch = true;
		
		Boolean hslmatch = null;
		if(recordhsl != null && recordhsl >= settinghsl)
			hslmatch = true;
		else if(recordhsl != null && recordhsl < settinghsl)
			hslmatch = false;
		else
			hslmatch = true;
		
//		Boolean zhouzhangfumatch = null;
//		if(recordzhangfu != null &&  recordzhangfu > settinggeguyangxianuplevel)
//			zhouzhangfumatch = true;
//		else 
//			zhouzhangfumatch = false;
		
		if( cjematch && dpmaxwkmatch && cjewkmatch && hslmatch  ) {	
			Boolean notskiptonextstock = true;
			
			if( shouldhavedayangxian && recordcje < cjelevelofyangxian  ) { //如果成交量小于于一定量，就必须有大阳线或者连续2周满足条件
				Double hzdf = ((StockXPeriodDataForJFC)nodexdata).getSpecificTimeHighestZhangDieFu(exportdate, 0);
				Double yangxianlevelofyangxian = this.cond.getChenJiaoErBottomYangXianLevel();
				if(hzdf != null && hzdf >= yangxianlevelofyangxian) { //大阳线涨幅
					notskiptonextstock = true;
				} else
					notskiptonextstock = false;
				
				
//					Integer searchyangxianrange = -3;//设定往前回溯几周找大阳线，现在定为3周
//					
//					for(int wkindex = 0;wkindex > searchyangxianrange;wkindex--) { //找前3周的数据，看看有没有大阳线
//						Double hzdf = ((StockXPeriodDataForJFC)nodexdata).getSpecificTimeHighestZhangDieFu(exportdate, wkindex);
//						if(hzdf != null && hzdf >= yangxianlevelofyangxian) { //大阳线涨幅
//							notskiptonextstock = true;
//							break;
//						} else 
//						if(hzdf == null) { //说明改周停牌或者是大盘休市，那要往前多回溯
//							searchyangxianrange = searchyangxianrange -1;
//						} else
//							notskiptonextstock = false;
//				}
			} 
			
			if( shouldhavelianxufl && recordcje < cjeleveloflianxufl && notskiptonextstock == false) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件
				Integer lianxunum = nodexdata.getCjeDpMaxLianXuFangLiangPeriodNumber (exportdate,0,settindpgmaxwk);
				Integer lianxuleveloflianxufl = this.cond.getChenJiaoErFangLiangLevel();
				if(lianxunum >= lianxuleveloflianxufl)
					notskiptonextstock = true;
				else
					notskiptonextstock = false;
			}
			
			return notskiptonextstock;
		}
		
		return false;
	}

}
