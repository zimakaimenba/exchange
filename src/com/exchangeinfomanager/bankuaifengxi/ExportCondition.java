package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import java.util.Set;

import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Strings;


public class ExportCondition 
{

	public ExportCondition ()
	{
		extracon = new ExtraExportConditions ();
	}
	private  ExtraExportConditions extracon;
	
	//������Щ��������2���ط�ʹ�ã�����ͻ����ʾ�͵�������׼��һ��ֱ�ӷ���ԭʼ���ݣ��ɿͻ��Լ�����
	private Double seetingltszmax;
	private Double seetingltszmin;
	private Double settingcjemax;
	private Double settingcjemin;
	private Integer settindpgmaxwk;
	private Integer settingcjemaxwk;
	private Double settinghsl;
	private String settingbk;
	
	private String tooltips = "";

	private boolean huibudownquekou;

	private Integer settindpgminwk;

	private String settingmaformula;
	
	/*
	 * ������е�node�Ƿ���ϵ�ǰ����
	 */
	public Set<TDXNodes> checkTDXNodeMatchedCurSettingConditons (BanKuaiAndStockTree bkcyltree,LocalDate exportdate, String period)
	{
		AllCurrentTdxBKAndStoksTree allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		
		Set<String> testcheck = new HashSet<> ();
//		createSet(testcheck);
		
		TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
		int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
		
		Set<TDXNodes> matchednodeset = new HashSet<TDXNodes> ();
		Set<String> checkednodesset = new HashSet<String> ();
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");
		
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
			if(childnode.getType() == BkChanYeLianTreeNode.TDXBK) {
				
				if( ! ((BanKuai)childnode).isImportdailytradingdata() )
					continue;
				if( ! ((BanKuai)childnode).isExportTowWlyFile() )
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //�������и��ɵİ��
					continue;
				
				childnode = allbksks.getBanKuai( (BanKuai)childnode, requirestart, exportdate,period,true);
				if( this.shouldOnlyExportBanKuaiWithYangXian () ) //��ҪOHLC���ݲ�ͬ��K�����ݣ���Լʱ��
					allbksks.syncBanKuaiData((BanKuai)childnode);
				
				String checkresult = this.checkBanKuaiMatchedCurSettingConditons((BanKuai)childnode, exportdate, period);
				
				if(checkresult.toUpperCase().contains("MATCHED") ) {
					if( !this.shouldOnlyExportStocksNotBanKuai() ) //ֻ����������
						if(!matchednodeset.contains(childnode)) 
							matchednodeset.add((TDXNodes) childnode);
					
				}  else
					continue;
					
				if(this.shouldOnlyExportBanKuaisNotStock())
					continue;
								
				if( !checkresult.toUpperCase().contains("WITHCHECKGEGU") )
					continue;
				
				childnode = allbksks.getAllGeGuOfBanKuai ( (BanKuai)childnode,period);
				Collection<BkChanYeLianTreeNode> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(exportdate,0);
				for (BkChanYeLianTreeNode ggstock : nowbkallgg) {
//					 if( testcheck.contains(ggstock.getMyOwnCode()))
//							System.out.print("checkTDXNodeMatchedCurSettingConditons");
					 
					 if(matchednodeset.contains( (TDXNodes)ggstock ) )
						 continue;
					 if( checkednodesset.contains(ggstock.getMyOwnCode() ) ) //�Ѿ�������stock�Ͳ����ˣ��ӿ��ٶ�
						 continue;
					 
					 ggstock = allbksks.getStock((Stock)ggstock,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
//					 if(!Strings.isNullOrEmpty(this.getSettingMAFormula() ) || this.shouldOnlyExportStocksWithWkYangXian() != null )
//						 allbksks.syncStockData(ggstock);

					 Boolean stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA((Stock)ggstock, exportdate, period);
					 if(stkcheckresult == null) {//ͣ�ƹ�
						 checkednodesset.add( ggstock.getMyOwnCode() );
						 continue;
					 }
					 
					 if(stkcheckresult && (!Strings.isNullOrEmpty(this.getSettingMAFormula() ) || this.shouldOnlyExportStocksWithWkYangXian() != null ) ) {
						 ggstock = allbksks.getStockKXian((Stock)ggstock, requirestart, exportdate, NodeGivenPeriodDataItem.DAY);
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
			
			if(childnode.getType() == BkChanYeLianTreeNode.TDXGG && this.shouldExportAllBanKuai() ) { //ֻ���ڵ������а����ɵ�ʱ��Ż��õ����������ɶ���ǰ������
				
				if(matchednodeset.contains( (TDXNodes)childnode ) )
					 continue;
				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //�Ѿ�������stock�Ͳ����ˣ��ӿ��ٶ�
					 continue;
				
				childnode = allbksks.getStock((Stock)childnode,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
				Boolean stkcheckresult = null;
				try{
				stkcheckresult = this.checkStockMatchedCurSettingConditonsWithoutCheckMA( (Stock)childnode, exportdate, period);
				if(stkcheckresult == null) {//ͣ�ƹ�
					 checkednodesset.add( childnode.getMyOwnCode() );
					 continue;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(stkcheckresult && !Strings.isNullOrEmpty(this.getSettingMAFormula() )) {
					
					childnode = allbksks.getStockKXian((Stock)childnode, requirestart, exportdate, NodeGivenPeriodDataItem.DAY);
					try{
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
	 * ������Ƿ�����趨
	 */
	public String checkBanKuaiMatchedCurSettingConditons (BanKuai node, LocalDate exportdate, String period)
	{
			String settingbk = this.getSettingBanKuai();
			if( this.shouldOnlyExportCurrentBankuai()  && !Strings.isNullOrEmpty( settingbk ) )  {//���޵�ǰ���
				if(!node.getMyOwnCode().equals(settingbk))
					return "UNMATCH";
			}
			
			if( this.shouldOnlyExportBanKuaiAllowedInSetting ()) //ֻ����������������
				if( !((BanKuai)node).isExportTowWlyFile() )
					return "UNMATCH";
			
			NodeXPeriodData nodexdata = ((BanKuai)node).getNodeXPeroidData(period);
			if(nodexdata == null) //��ʱ�仹û�����ݣ��԰����˵���ǻ�û�е���
				return "UNMATCH";
			
//			if(nodexdata.getIndexOfSpecificDateOHLCData(exportdate,0) == null) 
//				return "UNMATCH";
			
			if( this.shouldOnlyExportBanKuaiOfZhanBiUp() ) { //�������������İ��
					Double cjezbchangerate = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(exportdate, 0);
					if(cjezbchangerate == null)
						return "UNMATCH";
					Double cjlzbchangerate = nodexdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(exportdate, 0);
					if(cjezbchangerate <0 && cjlzbchangerate <0)
						return "UNMATCH";
			}
			if( this.shouldOnlyExportBanKuaiWithYangXian () ) {
				Double zhangfu = nodexdata.getSpecificOHLCZhangDieFu(exportdate, 0);
				if(zhangfu == null || zhangfu <0)
					return "UNMATCH";
			}
			String checkresult = ""; 
			if(!this.shouldExportAllBanKuai())
				checkresult = "WITHCHECKGEGU";
			
			Integer settindpgmaxwk = this.getSettinDpmaxwk();
			if(settindpgmaxwk == null)
				settindpgmaxwk = -1;
			Integer seetingcjemaxwk = this.getSettingCjemaxwk();
			if(seetingcjemaxwk == null)
				seetingcjemaxwk = -1;
			if(nodexdata.getChengJiaoEr(exportdate,0) != null ) { //��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У�����ǰ���ڳɽ����Ѿ����ٴ������ݿ�

					Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(exportdate,0);
					Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(exportdate,0);
					if( recordmaxbkwk >= settindpgmaxwk && recordmaxcjewk >= seetingcjemaxwk ) { //�������������� ; ���͸��ɲ�һ����ֻ��һ��ռ��
							checkresult = checkresult + "MATCHED";
					}
					
			}
			
		return checkresult;
	}
	/*
	 * MA��ҪK�����ݣ��ܺ�ʱ�������������������������ټ��MA
	 */
	public Boolean checkStockMatchedCurSettingConditonsOfCheckMA(Stock stock, LocalDate exportdate, String period) 
	{
		Boolean checkresult = null; 
		if(this.shouldOnlyExportStocksWithWkYangXian() != null) {
			NodeXPeriodData nodexdata = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			double zhangfu = nodexdata.getSpecificOHLCZhangDieFu(exportdate,0);
			
			double settinglevel = this.shouldOnlyExportStocksWithWkYangXian();
			if(zhangfu >  settinglevel)
				checkresult =  true;
			else
				return false;
		}
		
		String maf = this.getSettingMAFormula();
		if(maf != null) {
			NodeXPeriodData nodexdataday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			checkresult = nodexdataday.checkCloseComparingToMAFormula(maf, exportdate, 0);
		}

		return checkresult;
	}
	/*
	 * MA��ҪK�����ݣ��ܺ�ʱ�������������������������ټ��MA
	 */
	public Boolean checkStockMatchedCurSettingConditonsWithoutCheckMA (Stock stock,LocalDate exportdate, String period) 
	{
		try {
			 if(stock.getMyOwnName().toUpperCase().contains("ST") && this.shouldNotExportSTStocks() )
					return false;
		 } catch (java.lang.NullPointerException e) {

		 }
		
		if( stock.isVeryVeryNewXinStock(exportdate) ) // �����е��¹�Ҳ������
			 return false;
		
		NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
		Double recordcje = nodexdata.getChengJiaoEr(exportdate, 0);
		if(recordcje == null) //ͣ�ƹ�
			return null;
		
		Double settingcjemin = this.getSettingCjemin() ;
		if( settingcjemin == null)
			settingcjemin = -1.0;
		Double settingcjemax = this.getSettingCjeMax() ;
		if(  settingcjemax == null)
			settingcjemax = 1000000000000.0;
		Integer settindpgmaxwk = this.getSettinDpmaxwk();
		if( settindpgmaxwk == null)
			settindpgmaxwk = -1;
		Integer seetingcjemaxwk = this.getSettingCjemaxwk();
		if( seetingcjemaxwk == null)
			seetingcjemaxwk = -1;
		Double settinghsl = this.getSettingHsl();
		if( settinghsl == null)
			settinghsl = -1.0;
		
		Boolean shouldhavedayangxian = this.shouldHaveDaYangXianUnderCertainChenJiaoEr();
		Double cjelevelofyangxian = this.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian();
		Double yangxianlevelofyangxian = this.getDaYangXianUnderCertainChenJiaoEr();
		
		Boolean shouldhavelianxufl = this.shouldHaveLianXuFangLangUnderCertainChenJiaoEr();
		Double cjeleveloflianxufl = this.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang();
		

		Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(exportdate,0);
		Integer recordmaxcjewk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(exportdate,0);
		Double recordhsl = ((StockNodesXPeriodData)nodexdata).getSpecificTimeHuanShouLv(exportdate, 0);
		
		Boolean cjematch;
		if(recordcje != null  && recordcje >= settingcjemin && recordcje <= settingcjemax )
			cjematch = true;
		else if(recordcje != null  && ( recordcje < settingcjemin || recordcje > settingcjemax ))
			cjematch = false;
		else
			cjematch = true;
		
		Boolean dpmaxwkmatch;
		if(recordmaxbkwk != null  && recordmaxbkwk >= settindpgmaxwk)
			dpmaxwkmatch = true;
		else if(recordmaxbkwk != null  && recordmaxbkwk < settindpgmaxwk)
			dpmaxwkmatch = false;
		else
			dpmaxwkmatch = true;
		
		Boolean cjewkmatch;
		if(recordmaxcjewk != null && recordmaxcjewk >= seetingcjemaxwk)
			cjewkmatch = true;
		else if(recordmaxcjewk != null && recordmaxcjewk < seetingcjemaxwk)
			cjewkmatch = false;
		else
			cjewkmatch = true;
		
		Boolean hslmatch;
		if(recordhsl != null && recordhsl >= settinghsl)
			hslmatch = true;
		else if(recordhsl != null && recordhsl < settinghsl)
			hslmatch = false;
		else
			hslmatch = true;
		
		if(cjematch && dpmaxwkmatch && cjewkmatch && hslmatch) {	
			Boolean notskiptonextstock = true;
			
			if( shouldhavedayangxian && recordcje < cjelevelofyangxian  ) { //����ɽ���С����һ��������ǰ3�ܱ����д����߻�������2����������
					Integer searchyangxianrange = -3;//�趨��ǰ���ݼ����Ҵ����ߣ����ڶ�Ϊ3��
					
					for(int wkindex = 0;wkindex > searchyangxianrange;wkindex--) { //��ǰ3�ܵ����ݣ�������û�д�����
						Double hzdf = ((StockXPeriodDataForJFC)nodexdata).getSpecificTimeHighestZhangDieFu(exportdate, wkindex);
						if(hzdf != null && hzdf >= yangxianlevelofyangxian) { //�������Ƿ�
							notskiptonextstock = true;
							break;
						} else 
						if(hzdf == null) { //˵������ͣ�ƻ����Ǵ������У���Ҫ��ǰ�����
							searchyangxianrange = searchyangxianrange -1;
						} else
							notskiptonextstock = false;
						
					}
			} 
			
			if( shouldhavelianxufl && recordcje < cjeleveloflianxufl && notskiptonextstock == false) { //����ɽ���С����һ��������ǰ3�ܱ����д����߻�������2����������
				Integer lianxunum = nodexdata.getCjlLianXuFangLiangPeriodNumber (exportdate,0,settindpgmaxwk);
				Integer lianxuleveloflianxufl = this.getFangLiangLevelUnderCertainChenJiaoEr();
				if(lianxunum >= lianxuleveloflianxufl)
					notskiptonextstock = true;
				else
					notskiptonextstock = false;
			}
			
			return notskiptonextstock;
		}
		
		return false;
	}
	/*
	 * 
	 */
	public String getConditionsDescriptions ()
	{
		String outputfilehead = "[��������:";
		if(this.shouldExportAllBanKuai())
			outputfilehead = outputfilehead + "�������а�顣";
		if(this.shouldOnlyExportCurrentBankuai())
			outputfilehead = outputfilehead + "�����޶����Ϊ" + this.shouldOnlyExportCurrentBankuai() + "��";
		if(this.shouldOnlyExportBanKuaiAllowedInSetting())
			outputfilehead = outputfilehead + "�����������������İ�顣" ; 
		if(this.shouldOnlyExportBanKuaiOfZhanBiUp())
			outputfilehead = outputfilehead +"�����������̳ɽ����ɽ���ռ�Ȼ��������İ�顣";
		outputfilehead = outputfilehead + "���������趨Ϊ:";
		outputfilehead = outputfilehead + "�ɽ������" + this.getSettingCjemin() + "�ڣ�";
		outputfilehead = outputfilehead + "�ɽ���С��" + this.getSettingCjeMax() + "�ڡ�";
		outputfilehead = outputfilehead + "�ɽ����ɽ���DPMAX����" + this.getSettinDpmaxwk() + "��";
		outputfilehead = outputfilehead + "�ɽ������" + this.getSettingCjemaxwk() + "�ܡ�";
		outputfilehead = outputfilehead + "������ST" + this.shouldNotExportSTStocks() + "��";
		outputfilehead = outputfilehead + "����������K���Ƿ�������" + this.shouldOnlyExportStocksWithWkYangXian () + "��";
		outputfilehead = outputfilehead + "���������̼�������߹�ʽΪ" + this.getSettingMAFormula() + "��";
		outputfilehead = outputfilehead + "�����ʱ���>=" + this.getSettingHsl() + "��";
		outputfilehead = outputfilehead  +"]";
		
		return outputfilehead;
	}
	/*
	 * 
	 */
	public ExtraExportConditions getExtraExportConditions ()
	{
		return this.extracon;
	}
	/*
	 * 
	 */
	public Boolean shouldHaveDaYangXianUnderCertainChenJiaoEr ()
	{
		Boolean should = extracon.shouldHaveDaYangXianUnderCertainChenJiaoEr();
		if(should) {
			this.tooltips = this.tooltips + "�ɽ���С��" + this.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian() +  ",������" + this.getDaYangXianUnderCertainChenJiaoEr () + "%�����ߡ�";
			return true;
		}
		else
			return false;
	}
	//
	public Double getCjeLevelUnderCertaincChenJiaoErOfBigYangXian ()
	{
		Double cje = extracon.getCjeLevelUnderCertainChenJiaoErOfDaYangXian() ;
		if(cje == null)
			return -1.0;
		else
			return cje * 100000000;
	}
	//
	public Double getDaYangXianUnderCertainChenJiaoEr ()
	{
		Double dyxlevel = extracon.getYangXianLevelUnderCertainChenJiaoErofDaYangXian();
		
		if(dyxlevel == null)
			return -1.0;
		else
			return dyxlevel;
	}
	/*
	 * 
	 */
	public Boolean shouldHaveLianXuFangLangUnderCertainChenJiaoEr ()
	{
		Boolean should = extracon.shouldHaveFangLiangUnderCertainChenJiaoEr(); 
		if( should ) {
			this.tooltips = this.tooltips + "��ɽ���С��" + this.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang() +  "��������" 
							+ this.getFangLiangLevelUnderCertainChenJiaoEr()  + "�ܷ�����";
			return true;
		} else 
			return false;
		
	}
	//
	public Double getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang ()
	{
		Double cje = extracon.getCjeLevelUnderCertainChenJiaoErOfLianXuFangLiang();
		if(cje == null)
			return -1.0;
		else
			return cje * 100000000;
	}
	//
	public Integer getFangLiangLevelUnderCertainChenJiaoEr ()
	{
		Integer flwklevel = extracon.getLianXuFangLianLevelUnderCertainChenJiaoErOfFangLiang();
		if(flwklevel == null)
			return -1;
		else
			return flwklevel;
	}
	//
//	public void setExportSTStocks(boolean shouldNotExportSTStocks) 
//	{
//		this.shouldnotexportSTstocks = shouldNotExportSTStocks;
//		if(this.shouldnotexportSTstocks)
//			this.tooltips = this.tooltips + "������ST���ɡ�";
//	}
	public Boolean shouldOnlyExportBanKuaiOfZhanBiUp ()
	{
		boolean should = extracon.shouldOnlyExportBanKuaiOfZhanBiUp();
		return should;
	}
	public Boolean shouldOnlyExportBanKuaiWithYangXian()
	{
		boolean should = extracon.shouldOnlyExportBanKuaiWithYangXian ();
		return should;
	}
	public Boolean shouldNotExportSTStocks()
	{
		boolean should = extracon.shouldExportSTStocks();
		if(should ) {
			this.tooltips = this.tooltips + "������ST���ɡ�";
			return true;
		} else
			return false;
	}
	/*
	 * 
	 */
	public boolean shouldOnlyExportCurrentBankuai ()
	{
		if(extracon.shouldOnlyExportCurrentBankuai() ) 
			return true;
		else
			return false;
	}
	public void setSettingBanKuai(String exportbk) 
	{
		if(extracon.shouldOnlyExportCurrentBankuai() ) {
			if(exportbk != null ) {
				this.settingbk = exportbk;
				this.tooltips = this.tooltips + "�޶��ڰ��" + settingbk + "�ڡ�";
			}
		}
	}
	/*
	 * 
	 */
	public String getSettingBanKuai()
	{
		if(settingbk == null)
			return null;
		else
			return this.settingbk;
	}
	//
	public String getTooltips ()
	{
		return this.tooltips + "</br>";
	}
	//
	public Integer getSettinDpmaxwk() 
	{
		return settindpgmaxwk;
	}
	public void setSettinDpminwk(String exportdpminwklevel) 
	{
		if(exportdpminwklevel != null) {
			this.settindpgminwk = Integer.parseInt(exportdpminwklevel );
		} else
			this.settindpgminwk = null;
	}
	public Integer getSettingDpminwk ()
	{
		return this.settindpgminwk;
	}
	public void setSettinDpmaxwk(String exportdpmaxwklevel) 
	{
		if(exportdpmaxwklevel != null) {
			this.settindpgmaxwk = Integer.parseInt( exportdpmaxwklevel );
			this.tooltips = this.tooltips + "����MAXWK>=" +  settindpgmaxwk + "��";
		}
		else
			this.settindpgmaxwk = null;
	}
//	//
//	private Integer getSettinBkmaxwk() 
//	{
//		return settinbkgmaxwk;
//	}
//	private void setSettinBkmaxwk(String exportbkmaxwklevel) {
//		if(exportbkmaxwklevel != null) {
//			this.settinbkgmaxwk = Integer.parseInt( exportbkmaxwklevel );
//			this.tooltips = this.tooltips + "���MAXWK>=" + settinbkgmaxwk + "��";
//		}
//		else
//			this.settinbkgmaxwk = null;
//	}
	//
	public Integer getSettingCjemaxwk()
	{
		return settingcjemaxwk;
	}
	public void setSettingCjemaxwk(String exportcjemaxwklevel) {
		if(exportcjemaxwklevel != null) {
			this.settingcjemaxwk = Integer.parseInt( exportcjemaxwklevel );
			this.tooltips = this.tooltips + "�ɽ���MAXWK>=" + settingcjemaxwk + "��";
		}
		else
			this.settingcjemaxwk = null;
	}
	//�ɽ���ķ�Χ
	public Double getSettingCjemin()
	{
		try{
			return settingcjemin * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	//
	public Double getSettingCjeMax() 
	{
		try{
			return settingcjemax * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	//
	public void setChenJiaoEr (String exportcjelevelmin, String exportcjelevelmax)
	{
		if(exportcjelevelmin != null) {
			this.settingcjemin = Double.parseDouble( exportcjelevelmin );
			this.tooltips = this.tooltips + "�ɽ���>=" + settingcjemin + "��;";
		}
		else
			this.settingcjemin = null;
		
		if(exportcjelevelmax != null) {
			this.settingcjemax = Double.parseDouble( exportcjelevelmax );
			this.tooltips = this.tooltips + "�ɽ���<=" + settingcjemax + "�ڡ�";
		}
		else 
			this.settingcjemax = null;
		
	}
	/*
	 * 
	 */
	public void setSettingMAFormula (String maformula) //>250 && <60 &&>10
	{
		if(maformula == null ) {
			this.settingmaformula = maformula;
			return;
		}
			
		//Ӧ����������ʽ��飬���ڲ���
//		if( Pattern.matches(">=?$",maformula)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",maformula) )
//			return ;
		
		maformula = maformula.replace(">", "x>");
		maformula = maformula.replace("<", "x<");
		
		if(maformula.contains(">250") || maformula.contains(">=250") || maformula.contains("<250") )//����250��Ӧ����60
			maformula = maformula.replace("250", "\'250\'"  ) ;
	    if(maformula.contains(">120") || maformula.contains(">=120") || maformula.contains("<120") )//����250��Ӧ����60
	    	maformula = maformula.replace("120", "\'120\'" ) ;
	    if(maformula.contains(">60") || maformula.contains(">=60") || maformula.contains("<60") ) //����250��Ӧ����60
	    	maformula = maformula.replace("60", "\'60\'"   ) ;
	    if(maformula.contains(">30") || maformula.contains(">=30") || maformula.contains("<30") ) //����250��Ӧ����60
	    	maformula = maformula.replace("30", "\'30\'"  ) ;
	    if(maformula.contains(">20") || maformula.contains(">=20") || maformula.contains("<20") ) //����250��Ӧ����60
	    	maformula = maformula.replace("20", "\'20\'"  ) ;
	    if(maformula.contains(">10") || maformula.contains(">=10") || maformula.contains("<10") ) //����250��Ӧ����60
	    	maformula = maformula.replace("10", "\'10\'"  ) ;
	    if(maformula.contains(">5") || maformula.contains(">=5") || maformula.contains("<5") ) //����250��Ӧ����60
	    	maformula = maformula.replace("5","\'5\'"   ) ;
		
		this.settingmaformula = maformula;
		
	}
	public String getSettingMAFormula ()
	{
		return this.settingmaformula;
	}
	//������
	public Double getSettingHsl ()
	{
		return this.settinghsl;
	}
	
	public void setSettingHsl(String exporthsl) 
	{
		if(exporthsl != null) {
			this.settinghsl = Double.parseDouble(exporthsl);
			this.tooltips = this.tooltips + "������>=" + exporthsl + "%";
		} else
			this.settinghsl = null;
	}
	//
	public void setLiuTongShiZhi (String exportltszlevelmin, String exportltszlevelmax)
	{
		if(exportltszlevelmin != null) {
			this.seetingltszmin = Double.parseDouble( exportltszlevelmin );
			this.tooltips = this.tooltips + "��ͨ��ֵ>=" + seetingltszmin + "��;";
		}
		else
			this.seetingltszmin = null;
		
		if(exportltszlevelmax != null) {
			this.seetingltszmax = Double.parseDouble( exportltszlevelmax );
			this.tooltips = this.tooltips + "��ͨ��ֵ<=" + seetingltszmax + "�ڡ�";
		}
		else
			this.seetingltszmax = null ;
	}
	public Double getLiuTongShiZhiMin ()
	{
		try {
			return this.seetingltszmin * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	public Double getLiuTongShiZhiMax ()
	{
		try {
			return this.seetingltszmax * 100000000;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
		
	}
	//
	public void setDisplayHuiBuDownQueKou(boolean huibudownquekou1)
	{
		this.huibudownquekou = huibudownquekou1;
	}
	public Boolean shouldHighLightHuiBuDownQueKou  ()
	{
		return this.huibudownquekou ;
	}
	/*
	 * 
	 */
	public boolean shouldExportAllBanKuai ()
	{
		return extracon.shouldExportAllBanKuai ();
	}
	/*
	 * 
	 */
	public boolean shouldOnlyExportStocksNotBanKuai ()
	{
		return extracon.shouldOnlyExportStocksNotBanKuai();
	}
	/*
	 * 
	 */
	public Boolean shouldOnlyExportBanKuaiAllowedInSetting ()
	{
		return extracon.shouldOnlyExportBanKuaiAllowedInSetting ();
	}
	
	public Double shouldOnlyExportStocksWithWkYangXian ()
	{
		return extracon.shouldOnlyExportStocksWithWkYangXian ();
	}
	public Boolean shouldOnlyExportBanKuaisNotStock ()
	{
		return extracon.shouldOnlyExportBanKuaisNotStock ();
	}
}
