package com.exchangeinfomanager.NodesServices;

import java.awt.Color;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfDZHBanKuai implements ServicesForNode ,ServicesForNodeBanKuai
{
	private BanKuaiDZHDbOperation dzhbkdbopt;
	
	public SvsForNodeOfDZHBanKuai ()
	{
		this.dzhbkdbopt = new BanKuaiDZHDbOperation ();
	}
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException
	{
		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) {
			SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
			bk = this.getAllGeGuOfBanKuai (bk); 
			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			if(allbkgg != null) {
				for(BkChanYeLianTreeNode stockofbk : allbkgg)   
			    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) { 
			    		 Stock stock = (Stock) svstock.getNodeData( ((StockOfBanKuai)stockofbk).getStock(), requiredstartday, requiredendday, period,calwholeweek);
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

		return bankuai;
	}
	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificGntxString(String requiredgntxstring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificFmxxString(String requiredfmxxstring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		requiredendday   = requiredendday.with(DayOfWeek.FRIDAY);
		
		NodeXPeriodData nodedayperioddata = ((BanKuai)bankuai).getNodeXPeroidData(period);
		
		if(!calwholeweek) {
			LocalDate curdate = ((BanKuai)bankuai).getNodeDataAtNotCalWholeWeekModeLastDate();
			if(curdate == null) {
				((BanKuai)bankuai).setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else if(!curdate.isEqual(requiredendday)) {
				((BanKuai)bankuai).setNodeDataAtNotCalWholeWeekMode(requiredendday);
				nodedayperioddata.resetAllData();
			} else
				return bankuai;
		}
		if(calwholeweek) {
			if(((BanKuai)bankuai).isNodeDataAtNotCalWholeWeekMode() ) {
				((BanKuai)bankuai).setNodeDataAtNotCalWholeWeekMode(null);
				nodedayperioddata.resetAllData();
			}
		}

		if(nodedayperioddata.getAmoRecordsStartDate() == null) {
			bankuai = dzhbkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		if(timeintervallist == null) 
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate tmprequiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				LocalDate tmprequiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
				bankuai = dzhbkdbopt.getBanKuaiZhanBi ((BanKuai) bankuai,tmprequiredstartday,tmprequiredendday,period);
		}
		return bankuai;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		NodeXPeriodData nodedayperioddata = ((BanKuai) bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bankuai = dzhbkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
			
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getOHLCRecordsStartDate(),nodedayperioddata.getOHLCRecordsEndDate() );
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = dzhbkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
		}
		
		return bankuai;
	
	}

	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(String bkcode, LocalDate requiredrecordsday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(BkChanYeLianTreeNode bk, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void syncNodeData(BkChanYeLianTreeNode bankuai) {

		BanKuai bk = (BanKuai)bankuai;
		
		LocalDate bkohlcstartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsStartDate();
		LocalDate bkohlcendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsEndDate();
		
		LocalDate bkamostartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getAmoRecordsStartDate();
		LocalDate bkamoendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
		
		if(bkohlcstartday == null && bkamostartday == null) //¶¼Îª¿Õ£¬
			return ;
		
		if(bkohlcstartday == null) {
			bkohlcstartday = bkamostartday;
			bkohlcendday = bkamoendday;
			bankuai = this.getNodeKXian(bankuai, bkohlcstartday,bkohlcendday, NodeGivenPeriodDataItem.DAY,true);
//			this.getNodeCjeCjlZhanbiExtremeUpDownLevel (bankuai);
			return;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(bkamostartday,bkamoendday,bkohlcstartday,bkohlcendday );
		
		if(timeintervallist == null)
			return ;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = this.getNodeKXian(bankuai, requiredstartday,requiredendday, NodeGivenPeriodDataItem.DAY,true);
//				this.getNodeCjeCjlZhanbiExtremeUpDownLevel (bankuai);
		}
		
	
		
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChanYeLianInfo(String nodecode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo(String nodecode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo(String nodecode) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk, String period) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode, String period) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode, String period) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BkChanYeLianTreeNode getNodeCjeCjlZhanbiExtremeUpDownLevel(BkChanYeLianTreeNode node) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setNodeCjeExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setNodeCjlExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		// TODO Auto-generated method stub
		
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
	@Override
	public Collection<BkChanYeLianTreeNode> getNodesWithKeyWords(String keywords) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo(BkChanYeLianTreeNode node, LocalDate selecteddatestart,
			LocalDate selecteddateend, String period) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<Interval> getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
	{
		if(nodestart == null)
		return null;
		
		List<Interval> result = new ArrayList<Interval> ();
		
		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
		if(overlapinterval != null) {
		DateTime overlapstart = overlapinterval.getStart();
		DateTime overlapend = overlapinterval.getEnd();
		
		Interval resultintervalpart1 = null ;
		if(requiredstartday.isBefore(nodestart)) {
			resultintervalpart1 = new Interval(requiredstartdt,overlapstart);
		} 
		
		Interval resultintervalpart2 = null;
		if (requiredendday.isAfter(nodeend)) {
			resultintervalpart2 = new Interval(overlapend,requiredenddt);
		}
		if(resultintervalpart1 != null)
			result.add(resultintervalpart1);
		if(resultintervalpart2 != null)
			result.add(resultintervalpart2);
		//return result;
		}
		if(requiredinterval.abuts(nodeinterval)) {
		result.add(requiredinterval);
		// return result;
		}
		Interval gapinterval = requiredinterval.gap(nodeinterval);
		if(gapinterval != null) {
		result.add(requiredinterval);
		result.add(gapinterval);
		}
		
		return result;
	}

}
