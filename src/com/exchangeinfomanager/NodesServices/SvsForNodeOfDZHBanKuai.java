package com.exchangeinfomanager.NodesServices;

import java.awt.Color;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.JiGouGuDongDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfDZHBanKuai extends SvsForNodeOfBanKuai
{
	private BanKuaiDZHDbOperation dzhbkdbopt;
	
	public SvsForNodeOfDZHBanKuai ()
	{
		this.dzhbkdbopt = new BanKuaiDZHDbOperation ();
	}
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException
	{
//		this.getNodeData(bk, requiredstartday, requiredendday, period,calwholeweek); //û�б�Ҫ�ټ���һ�鱾������ݣ���Ϊ����ǰ�϶��Ѿ��л�����������
		this.syncNodeData(bk);
		//�������ͬ���󣬰����ɵ�ʱ��������Ȱ��̣�Ҫ�Ͱ���ʱ����һ�¡�����Ȱ��ʱ�䳤������Ҫͬ��ʱ����
		NodeXPeriodData bknodexdata = bk.getNodeXPeroidData(period);
		LocalDate bkdatastartday = bknodexdata.getAmoRecordsStartDate();
		if(bkdatastartday != null  && bkdatastartday.isBefore(requiredstartday) )
			requiredstartday = bkdatastartday;

		LocalDate notcalwholewkmodedate = bknodexdata.isInNotCalWholeWeekMode();
		if(notcalwholewkmodedate == null) { //����Ǽ������ܣ� ���������Сû��ϵ��ֻҪ�����ܶ�����
			LocalDate bkdataendday = bknodexdata.getAmoRecordsEndDate();
			if(bkdataendday != null  && bkdataendday.isAfter(requiredendday) )
				requiredendday = bkdataendday;
		}	else { //����Ǽ��㲻��ȫ�ܣ������һ�ܱ�����Ҫ��������
			requiredendday = notcalwholewkmodedate; 
//			calwholeweek = false;
		}

		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) {
//			int bkggbefore = 0;
//			try {	bkggbefore = bk.getAllGeGuOfBanKuaiInHistory().size();
//			} catch (java.lang.NullPointerException e) {}
			bk = this.getAllGeGuOfBanKuai (bk);
//			int bkggafter = 0;
//			try {	bkggafter = bk.getAllGeGuOfBanKuaiInHistory().size();
//			} catch (java.lang.NullPointerException e) {}
//			
//			if( bkggafter != bkggbefore ) { //˵�����������˱仯����Ҫ���¼��㣬���������ٴ˼��㣬�˷�ʱ��,��ʵ����Ӧ���Ǽ�����������Ƿ��б仯���Ƚ��ѣ�����ʱ����������������ظ�����
//				bk = bkdbopt.getBanKuaiGeGuZhanBi (bk,requiredstartday,requiredendday,period);
//				bkdbopt.getTDXBanKuaiSetForBanKuaiGeGu (bk);
//				bkdbopt.getBanKuaiGeGuGzMrMcYkInfo (bk);
//				gddbopt.checkBanKuaiGeGuHasHuangQinGuoQieAndMingXin(bk, requiredendday); //��ѯ�������Ƿ��л��׹���
//			}

			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			if(allbkgg != null) {
				SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
				for(BkChanYeLianTreeNode stockofbk : allbkgg)   
			    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) {
			    		Stock stock = ((StockOfBanKuai)stockofbk).getStock();
//			    		if(!calwholeweek) {
//			    			stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).setNotCalWholeWeekMode (notcalwholewkmodedate);
//			    			stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).setNotCalWholeWeekMode (notcalwholewkmodedate);
//			    		}
			    		stock = (Stock) svstock.getNodeData( stock, requiredstartday, requiredendday, period,calwholeweek); 
		    			svstock.syncNodeData(stock);
			    	 }
			}
		}
		
		return;
	}
	/*
	 * 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai) 
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getOHLCRecordsEndDate();
		
		if(bkstartday == null || bkendday == null)  {
			bkstartday = CommonUtility.getSettingRangeDate(LocalDate.now(), "middle");
			bkendday = LocalDate.now();
			
			bankuai = dzhbkdbopt.getDZHBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		Interval alreadyinggtimerange = bankuai.getBanKuaiGeGuTimeRange();
		LocalDate alreadystartday; LocalDate alreadyendday ;
		if(alreadyinggtimerange == null) {
			alreadystartday = bkstartday;
			alreadyendday = bkendday;
			
			bankuai = dzhbkdbopt.getDZHBanKuaiGeGuOfHyGnFg (bankuai,alreadystartday,alreadyendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		DateTime tmpnewstartdt = alreadyinggtimerange.getStart();
		DateTime tmpnewenddt = alreadyinggtimerange.getEnd();
		alreadystartday = LocalDate.of(tmpnewstartdt.getYear(), tmpnewstartdt.getMonthOfYear(), tmpnewstartdt.getDayOfMonth());
		alreadyendday = LocalDate.of(tmpnewenddt.getYear(), tmpnewenddt.getMonthOfYear(), tmpnewenddt.getDayOfMonth());

		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval(bkstartday,bkendday,alreadystartday,alreadyendday);
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = dzhbkdbopt.getDZHBanKuaiGeGuOfHyGnFg (bankuai,requiredstartday,requiredendday);
		}

		return bankuai;
	}
	
	public BkChanYeLianTreeNode updateBanKuaiBasicOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg, Color bkcolor,  boolean corezhishu)
	{
		dzhbkdbopt.updateBanKuaiOperationsSettings (node,importdailydata,exporttogephi,showinbkfx,showincyltree,exporttowkfile,importbkgg,bkcolor,corezhishu);
		((BanKuai)node).getBanKuaiOperationSetting().setImportdailytradingdata(importdailydata);
		((BanKuai)node).getBanKuaiOperationSetting().setExporttogehpi(exporttogephi);
		((BanKuai)node).getBanKuaiOperationSetting().setShowinbkfxgui(showinbkfx);
		((BanKuai)node).getBanKuaiOperationSetting().setShowincyltree(showincyltree);
		((BanKuai)node).getBanKuaiOperationSetting().setExportTowWlyFile(exporttowkfile);
		((BanKuai)node).getBanKuaiOperationSetting().setImportBKGeGu(importbkgg);
		((BanKuai)node).getBanKuaiOperationSetting().setBanKuaiLabelColor (bkcolor);
		((BanKuai)node).getNodeJiBenMian().setIsCoreZhiShu(corezhishu);
		
		return node;
	}

}
