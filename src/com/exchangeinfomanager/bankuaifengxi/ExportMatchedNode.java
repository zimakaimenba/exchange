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
		SvsForNodeOfStock svsstk = new SvsForNodeOfStock  ();
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
					 if(matchednodeset.contains( (TDXNodes)ggstock ) )
						 continue;
					 if( checkednodesset.contains(ggstock.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
						 continue;
					 
					 ggstock = svsstk.getNodeData(ggstock,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);

					 Boolean stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA( (Stock)ggstock, exportdate, period);
					 if(stkcheckresult == null) {//停牌股
						 checkednodesset.add( ggstock.getMyOwnCode() );
						 continue;
					 }
					 
					 if(stkcheckresult  &&  this.cond.getExportYangXianGeGu() != null  ) {
						 svsstk.getNodeKXian( ggstock, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
						 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfZhangFu((Stock)ggstock, exportdate, period);
					 }
					 
					 if(stkcheckresult  && !Strings.isNullOrEmpty(this.cond.getSettingMaFormula() ) ) {
						 svsstk.getNodeKXian( ggstock, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
						 stkcheckresult = this.checkStockMatchedCurSettingConditonsOfCheckMA((Stock)ggstock, exportdate, period);
					 }
					 
					 try{
					 if(stkcheckresult)
						 matchednodeset.add((TDXNodes) ggstock);
					 } catch(Exception e) {
						 checkednodesset.add( ggstock.getMyOwnCode() );
						 e.printStackTrace();
					 }
					 
					 checkednodesset.add( ggstock.getMyOwnCode() );
				}
			}
			
			if(childnode.getType() == BkChanYeLianTreeNode.TDXGG && this.cond.shouldExportAllBanKuai() ) { //只有在导出所有板块个股的时候才会用到这里，否则个股都在前面检查了
				
				if(matchednodeset.contains( (TDXNodes)childnode ) )
					 continue;
				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
					 continue;
				
				childnode = svsstk.getNodeData( (Stock)childnode,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
				Boolean stkcheckresult = null;
				try{
				stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA( (Stock)childnode, exportdate, period);
				if(stkcheckresult == null) {//停牌股
					 checkednodesset.add( childnode.getMyOwnCode() );
					 continue;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(stkcheckresult && !Strings.isNullOrEmpty(this.cond.getSettingMaFormula() )) {
					
					childnode = svsstk.getNodeKXian( (Stock)childnode, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
					try{
					stkcheckresult = this.checkStockMatchedCurSettingConditonsOfZhangFu((Stock)childnode, exportdate, period);
					stkcheckresult = this.checkStockMatchedCurSettingConditonsOfCheckMA((Stock)childnode, exportdate, period);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try{
				if(stkcheckresult)
					matchednodeset.add(  (TDXNodes)childnode);
				} catch(Exception e) {
					checkednodesset.add( childnode.getMyOwnCode() );
					e.printStackTrace();
				 }
					 
				 checkednodesset.add( childnode.getMyOwnCode() );
				
			}
			
		}
		
		checkednodesset = null;
		return matchednodeset;
	}
//	private void createSet(Set<String> testcheck) 
//	{
//		testcheck.add("399001");testcheck.add("399006");testcheck.add("880815");testcheck.add("880538");testcheck.add("880868");testcheck.add("880548");testcheck.add("880824");testcheck.add("880951");testcheck.add("880852");testcheck.add("880817");testcheck.add("880803");testcheck.add("880942");testcheck.add("880958");testcheck.add("880592");testcheck.add("880911");testcheck.add("880577");testcheck.add("880909");testcheck.add("880944");testcheck.add("880702");testcheck.add("880547");testcheck.add("880957");testcheck.add("880336");testcheck.add("880816");testcheck.add("880949");testcheck.add("880598");testcheck.add("880502");testcheck.add("880530");testcheck.add("880576");testcheck.add("880912");testcheck.add("880705");testcheck.add("880580");testcheck.add("880551");testcheck.add("880947");testcheck.add("880941");testcheck.add("880452");testcheck.add("880809");testcheck.add("880597");testcheck.add("880908");testcheck.add("880565");testcheck.add("880558");testcheck.add("880959");testcheck.add("880484");testcheck.add("880573");testcheck.add("880926");testcheck.add("880807");testcheck.add("880563");testcheck.add("880955");testcheck.add("880581");testcheck.add("880896");testcheck.add("880962");testcheck.add("880707");testcheck.add("880340");testcheck.add("880350");testcheck.add("880338");testcheck.add("880706");testcheck.add("880497");testcheck.add("300017");testcheck.add("300750");testcheck.add("880339");testcheck.add("880438");testcheck.add("880444");testcheck.add("880466");testcheck.add("880585");testcheck.add("880443");testcheck.add("002466");testcheck.add("880442");testcheck.add("000799");testcheck.add("000503");testcheck.add("000034");testcheck.add("600218");testcheck.add("880409");testcheck.add("002639");testcheck.add("300474");testcheck.add("600273");testcheck.add("002134");testcheck.add("300333");testcheck.add("002191");testcheck.add("600383");testcheck.add("603739");testcheck.add("600702");testcheck.add("600291");testcheck.add("300755");testcheck.add("002479");testcheck.add("000958");testcheck.add("300054");testcheck.add("300492");testcheck.add("300408");testcheck.add("600903");testcheck.add("002099");testcheck.add("603308");testcheck.add("000996");testcheck.add("002002");testcheck.add("601600");testcheck.add("300471");testcheck.add("603222");testcheck.add("603863");testcheck.add("002294");testcheck.add("600821");testcheck.add("300072");testcheck.add("600360");testcheck.add("601137");testcheck.add("002531");testcheck.add("000589");testcheck.add("002078");testcheck.add("000932");testcheck.add("002189");testcheck.add("002199");testcheck.add("002166");testcheck.add("600875");testcheck.add("300045");testcheck.add("880363");testcheck.add("601216");testcheck.add("000630");testcheck.add("002171");testcheck.add("600587");testcheck.add("300009");testcheck.add("000983");testcheck.add("002222");testcheck.add("600368");testcheck.add("600729");testcheck.add("000581");testcheck.add("300409");testcheck.add("600501");testcheck.add("000622");testcheck.add("002920");testcheck.add("603012");testcheck.add("600567");testcheck.add("002581");testcheck.add("600233");testcheck.add("300470");testcheck.add("001896");testcheck.add("600777");testcheck.add("300767");testcheck.add("000031");testcheck.add("600129");testcheck.add("300497");testcheck.add("603458");testcheck.add("300457");testcheck.add("002930");testcheck.add("603886");testcheck.add("002956");testcheck.add("601311");testcheck.add("300029");testcheck.add("000708");testcheck.add("002192");testcheck.add("002608");testcheck.add("000544");testcheck.add("002625");testcheck.add("000677");testcheck.add("601098");testcheck.add("002951");testcheck.add("002819");testcheck.add("603113");testcheck.add("002140");testcheck.add("002221");testcheck.add("300723");testcheck.add("002808");testcheck.add("300273");testcheck.add("000910");testcheck.add("300199");testcheck.add("002929");testcheck.add("002584");testcheck.add("002226");testcheck.add("300430");testcheck.add("603181");testcheck.add("603305");testcheck.add("300649");testcheck.add("603700");testcheck.add("600299");testcheck.add("603603");testcheck.add("600122");testcheck.add("603586");testcheck.add("300245");testcheck.add("002661");testcheck.add("002085");testcheck.add("300129");testcheck.add("603789");testcheck.add("002423");testcheck.add("603908");testcheck.add("600039");testcheck.add("603595");testcheck.add("300332");testcheck.add("002662");testcheck.add("603002");testcheck.add("600328");testcheck.add("300464");testcheck.add("600897");testcheck.add("300620");testcheck.add("002590");testcheck.add("002087");testcheck.add("000905");testcheck.add("002549");testcheck.add("603337");testcheck.add("002228");testcheck.add("603607");testcheck.add("603507");testcheck.add("002305");testcheck.add("300654");testcheck.add("002827");testcheck.add("300192");testcheck.add("600818");testcheck.add("603416");testcheck.add("600876");testcheck.add("300415");testcheck.add("603729");testcheck.add("300204");testcheck.add("000633");testcheck.add("300737");testcheck.add("603278");testcheck.add("603021");testcheck.add("603223");testcheck.add("300388");testcheck.add("603203");testcheck.add("300360");testcheck.add("603577");testcheck.add("002580");testcheck.add("603897");testcheck.add("300720");testcheck.add("300626");testcheck.add("603677");testcheck.add("002003");testcheck.add("002802");testcheck.add("002779");testcheck.add("600719");testcheck.add("603200");testcheck.add("603519");testcheck.add("000502");testcheck.add("603033");testcheck.add("002291");testcheck.add("603123");testcheck.add("601007");testcheck.add("002026");testcheck.add("600358");testcheck.add("600302");testcheck.add("000695");testcheck.add("603041");testcheck.add("603656");
//		
//	}
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
					Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(exportdate,0);
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
		try {
			 if(stock.getMyOwnName().toUpperCase().contains("ST") && this.cond.shouldExportST() )
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
		Integer recordmaxcjewk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(exportdate,0);
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
				Integer lianxunum = nodexdata.getCjlLianXuFangLiangPeriodNumber (exportdate,0,settindpgmaxwk);
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
