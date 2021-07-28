package com.exchangeinfomanager.Core.NodesDataServices;

import java.awt.Color;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

import java.util.Collection;

import java.util.List;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.TimeIntervalUnion;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;

public class SvsForNodeOfBanKuai implements ServicesForNode, ServicesForNodeBanKuai
{
	private BanKuaiDbOperation bkdbopt;
	private CylTreeDbOperation dboptforcyltree;
//	private JiGouGuDongDbOperation gddbopt;

	public SvsForNodeOfBanKuai ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
	}
	private static Logger logger = Logger.getLogger(BanKuaiDbOperation.class);
	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) 
	{
		node = bkdbopt.getBanKuaiBasicInfo ((BanKuai) node);
		return node;
	}
	@Override
	public void syncNodeData(BkChanYeLianTreeNode bankuai)
	{
		BanKuai bk = (BanKuai)bankuai;
		
		LocalDate bkohlcstartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsStartDate();
		LocalDate bkohlcendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsEndDate();
		
		NodeXPeriodData nodexdatawk = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate bkamostartday =	nodexdatawk.getAmoRecordsStartDate();
		LocalDate bkamoendday = nodexdatawk.getAmoRecordsEndDate();
		
		Boolean calwholeweek = true;
		LocalDate notcalwholewkmodedate = nodexdatawk.isInNotCalWholeWeekMode() ;
		if(notcalwholewkmodedate != null) 	
			calwholeweek = false;
				
		if(bkohlcstartday == null && bkamostartday == null) return ;
		LocalDate requiredstartday; LocalDate requiredendday = null;
		if(bkohlcstartday == null) {
			requiredstartday = bkamostartday;
			requiredendday = bkamoendday;
		} else {
			if(bkohlcstartday.isBefore(bkamostartday)) requiredstartday = bkohlcstartday;
			else	requiredstartday = bkamostartday;
			
			if(bkohlcendday.isAfter(bkamoendday)) requiredendday = bkohlcendday;
			else	requiredendday = bkamoendday;
		}
		
		bankuai = this.getNodeKXian(bankuai, requiredstartday,requiredendday, NodeGivenPeriodDataItem.DAY,calwholeweek);
		extraActionsForBanKuai ((BanKuai) bankuai);
		return;
	}
	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		requiredendday = ServicesForNode.getNodeCaculateEndDateAndRelatedActions(bankuai, period, requiredendday, calwholeweek);
		
		LocalDate jlmaxdate = ((BanKuai)bankuai).getShuJuJiLuInfo().getJyjlmaxdate();
		LocalDate jlmindate = ((BanKuai)bankuai).getShuJuJiLuInfo().getJyjlmindate();
		if(jlmaxdate == null && jlmindate == null)
			return bankuai; //bankuai without data records should not be displayed
		
		if(requiredendday.isBefore(jlmindate))
			return bankuai;
		if(requiredstartday.isBefore(jlmindate) )
			requiredstartday = jlmindate;

		NodeXPeriodData nodexdatawk = ((BanKuai)bankuai).getNodeXPeroidData(period);
		if(nodexdatawk.getAmoRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		LocalDate amostart = nodexdatawk.getAmoRecordsStartDate();
		LocalDate amoend = nodexdatawk.getAmoRecordsEndDate();
		List<Interval> timeintervallist = TimeIntervalUnion.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
			(requiredstartday,requiredendday,nodexdatawk.getAmoRecordsStartDate(),nodexdatawk.getAmoRecordsEndDate()	); 
		
		if(timeintervallist == null) 
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				//����Ƚϸ��ӣ�����Ƿ������ܣ��Ǿͼ��㵽�������ڣ�������Ƿ������ܣ��ǹ�ȥʱ�䣬��ȥ�ǻ���Ҫ��������һ�ܵ�����
				LocalDate isnotcalwholewkdate = nodexdatawk.isInNotCalWholeWeekMode();
				if( isnotcalwholewkdate == null ) //�����ܣ�����ĩ
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				else if( CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) ) //��������ģʽ����ָ������������ܣ��ǵ�ָ������ 
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				else if( isnotcalwholewkdate != null && !CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) )	//��������ģʽ���ڹ�ȥ���ڣ�����Ҫ����������������
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai) bankuai,requiredstartday,requiredendday,period);
		}
		
		return bankuai;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period, Boolean calwholeweek) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(String bkcode, LocalDate requiredstartday, LocalDate requiredendday,
			String period,Boolean calwholeweek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeKXian(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period,Boolean calwholeweek) 
	{
		requiredstartday = requiredstartday.with(DayOfWeek.MONDAY);
		
		NodeXPeriodData nodexdatawk = ((BanKuai) bankuai).getNodeXPeroidData(period);
		if(nodexdatawk.isInNotCalWholeWeekMode() == null || calwholeweek) 
			requiredendday   = requiredendday.with(DayOfWeek.FRIDAY);

		if(nodexdatawk.getOHLCRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist = TimeIntervalUnion.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodexdatawk.getOHLCRecordsStartDate(),nodexdatawk.getOHLCRecordsEndDate() );
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				//����Ƚϸ��ӣ�����Ƿ������ܣ��Ǿͼ��㵽�������ڣ�������Ƿ������ܣ��ǹ�ȥʱ�䣬��ȥ�ǻ���Ҫ��������һ�ܵ�����
				LocalDate isnotcalwholewkdate = nodexdatawk.isInNotCalWholeWeekMode();
				if( isnotcalwholewkdate == null ) //�����ܣ�����ĩ
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				else if( CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) ) //��������ģʽ����ָ������������ܣ��ǵ�ָ������ 
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				else if( isnotcalwholewkdate != null && !CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) )	//��������ģʽ���ڹ�ȥ���ڣ�����Ҫ����������������
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
		}
		
		return bankuai;
	}
	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		if(bankuai.getMyOwnCode().equals("999999") ) {
			this.getShangZhengZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		} else
		if(bankuai.getMyOwnCode().equals("399001") ) {
			this.getShenZhenZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		} else
		if(bankuai.getMyOwnCode().equals("399006") ) {
			this.getChuangYeBanZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		if(allbkgg == null || allbkgg.isEmpty())	return bk;
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer zhangtingnum = 0;
			 Integer dietingnum = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( !((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //ȷ�ϵ�ǰ���ڰ����?
					continue;

				Stock stock = ((StockOfBanKuai)stockofbk).getStock();
				NodeXPeriodData stockxdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
				Integer stockzt = stockxdate .getZhangTingTongJi (tmpdate);
				if(stockzt != null)
					zhangtingnum = zhangtingnum + stockzt;
				
				Integer stockdt =  stockxdate .getDieTingTongJi(tmpdate);
				if(stockdt != null)
					dietingnum = dietingnum + stockdt;
			}
			
			if(zhangtingnum == 0)
				zhangtingnum = null;
			if(dietingnum == 0)
				dietingnum = null;
			
			bkwkdate.addZhangDieTingTongJiJieGuo(tmpdate, zhangtingnum, dietingnum, false);
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));

		return bk;
	
	}

	private BkChanYeLianTreeNode getChuangYeBanZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		
		bk = this.bkdbopt.getChuangYeBanZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		return bankuai;
	}
	private BkChanYeLianTreeNode getShenZhenZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		BanKuai bk = (BanKuai) bankuai;
		
		bk = this.bkdbopt.getShenZhenZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		return bankuai;
	}
	private BkChanYeLianTreeNode getShangZhengZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		
		bk = this.bkdbopt.getShangZhengZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		
		return bankuai;
	}
	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(String bkcode, LocalDate requiredrecordsday, LocalDate requiredendday,
			String period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNodeQueKouInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		if(allbkgg == null || allbkgg.isEmpty())
			return bankuai;
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer openup = 0;
			 Integer opendown = 0;
			 Integer huibuup = 0;
			 Integer huibudown = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( ! ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  
					continue;
				
				Stock stock = ((StockOfBanKuai)stockofbk).getStock();
				NodeXPeriodData stockxdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
				Integer stockopenup = stockxdate.getQueKouTongJiOpenUp (tmpdate);
				if(stockopenup != null && stockopenup != 0)
					openup = openup + stockopenup;
				Integer stockopendown = stockxdate.getQueKouTongJiOpenDown(tmpdate);
				if(stockopendown != null && stockopendown !=0 )
					opendown = opendown + stockopendown;
				Integer stockhbdown =  stockxdate.getQueKouTongJiHuiBuDown(tmpdate);
				if(stockhbdown != null && stockhbdown !=0)
					huibudown = huibudown + stockhbdown;
				Integer stockhbup =stockxdate.getQueKouTongJiHuiBuUp(tmpdate);
				if(stockhbup != null && stockhbup  != 0)
					huibuup =  huibuup + stockhbup;
			}
			
			if(openup == 0)
				openup = null;
			if(huibuup == 0)
				huibuup = null;
			if(opendown == 0)
				opendown = null;
			if(huibudown == 0)
				huibudown = null;
			bkwkdate.addQueKouTongJiJieGuo(tmpdate, openup, huibuup, opendown, huibudown, false);
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));

		return bk;
	
	}
	
	private void extraActionsForBanKuai (BanKuai bk)
	{
		NodeXPeriodData nodexdatawk = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		LocalDate bkamostartday =	nodexdatawk.getAmoRecordsStartDate();
		LocalDate bkamoendday =	nodexdatawk.getAmoRecordsEndDate();
		
		this.getNodeQueKouInfo(bk, bkamostartday, bkamoendday, NodeGivenPeriodDataItem.WEEK);
		this.getNodeZhangDieTingInfo(bk, bkamostartday, bkamoendday, NodeGivenPeriodDataItem.WEEK);
		this.getNodeGzMrMcYkInfo(bk,  bkamostartday, bkamoendday, NodeGivenPeriodDataItem.DAY);
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
			
			bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		Interval alreadyinggtimerange = bankuai.getBanKuaiGeGuTimeRange();
		LocalDate alreadystartday; LocalDate alreadyendday ;
		if(alreadyinggtimerange == null) {
			alreadystartday = bkstartday;
			alreadyendday = bkendday;
			
			bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,alreadystartday,alreadyendday);
			bankuai.setBanKuaiGeGuTimeRange (bkstartday,bkendday);
			return bankuai;
		} 
		
		DateTime tmpnewstartdt = alreadyinggtimerange.getStart();
		DateTime tmpnewenddt = alreadyinggtimerange.getEnd();
		alreadystartday = LocalDate.of(tmpnewstartdt.getYear(), tmpnewstartdt.getMonthOfYear(), tmpnewstartdt.getDayOfMonth());
		alreadyendday = LocalDate.of(tmpnewenddt.getYear(), tmpnewenddt.getMonthOfYear(), tmpnewenddt.getDayOfMonth());

		List<Interval> timeintervallist = TimeIntervalUnion.getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
											(bkstartday,bkendday,alreadystartday,alreadyendday);
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				//����Ƚϸ��ӣ�����Ƿ������ܣ��Ǿͼ��㵽�������ڣ�������Ƿ������ܣ��ǹ�ȥʱ�䣬��ȥ�ǻ���Ҫ��������һ�ܵ�����
				LocalDate isnotcalwholewkdate = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).isInNotCalWholeWeekMode();
				if( isnotcalwholewkdate == null ) //�����ܣ�����ĩ
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				else if( CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) ) //��������ģʽ����ָ������������ܣ��ǵ�ָ������ 
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				else if( isnotcalwholewkdate != null && !CommonUtility.isInSameWeek(isnotcalwholewkdate, requiredendday) )	//��������ģʽ���ڹ�ȥ���ڣ�����Ҫ����������������
					requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				
				bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,requiredstartday,requiredendday);
		}

		return bankuai;
	}
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
	{
		LocalDate bkstartday = bankuai.getNodeXPeroidData(period).getOHLCRecordsStartDate();
		LocalDate bkendday = bankuai.getNodeXPeroidData(period).getOHLCRecordsEndDate();
		
		if(bkstartday == null && bkendday == null) 
			return stockofbk;
		
		stockofbk = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai,stockofbk, bkstartday, bkendday,period);

		return stockofbk;
	}
	/*
	 * 
	 */
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode,String period)
	{
		StockOfBanKuai stock = bankuai.getStockOfBanKuai(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuaiData( bankuai,  stock,period);
		return (StockOfBanKuai) stock;
	}
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode,String period) 
	{
		BanKuai bankuai = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getStockOfBanKuai(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuaiData( bankuai,  stock,period);

		return stock;
	}
	/*
	 * 
	 */
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
	@Override
	public List<BkChanYeLianTreeNode> getNodeChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylinfo = this.dboptforcyltree.getChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylinfo;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
			dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylsliding = this.dboptforcyltree.getSlidingInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylsliding;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo(String nodecode) 
	{
		if(dboptforcyltree == null)
		dboptforcyltree = new CylTreeDbOperation (CreateExchangeTree.CreateTreeOfChanYeLian());
		
		List<BkChanYeLianTreeNode> cylchildren  = this.dboptforcyltree.getChildrenInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylchildren;
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
	public BkChanYeLianTreeNode getNodeCjeCjlZhanbiExtremeUpDownLevel(BkChanYeLianTreeNode node) {
		node = this.bkdbopt.getNodeCjeCjlExtremeZhanbiUpDownLevel( (TDXNodes)node);
		return node;
	}

	@Override
	public void setNodeCjeExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		node = this.bkdbopt.setNodeCjeExtremeZhanbiUpDownLevel( (TDXNodes)node, min, max);
		((BanKuai)node).getNodeJiBenMian().setNodeCjeZhanbiLevel (min,max);
		
	}

	@Override
	public void setNodeCjlExtremeUpDownZhanbiLevel(BkChanYeLianTreeNode node, Double min, Double max) {
		node = this.bkdbopt.setNodeCjlExtremeZhanbiUpDownLevel( (TDXNodes)node, min, max);
		((BanKuai)node).getNodeJiBenMian().setNodeCjlZhanbiLevel (min,max);
	}
	
	public BkChanYeLianTreeNode updateBanKuaiBasicOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg,Color bkcolor,boolean corezhishu)
	{
		bkdbopt.updateBanKuaiOperationsSettings (node,importdailydata,exporttogephi,showinbkfx,showincyltree,exporttowkfile,importbkgg,bkcolor,corezhishu);
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
	public Collection<BkChanYeLianTreeNode> getNodesWithKeyWords(String keywords)
	{
		Collection<BkChanYeLianTreeNode> result = this.bkdbopt.searchKeyWordsInBanKuaiJiBenMian (keywords);
		return result;
	}

	@Override
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo(BkChanYeLianTreeNode node, LocalDate selecteddatestart,
			LocalDate selecteddateend, String period) {
		node =  bkdbopt.getNodeGzMrMcYkInfo((TDXNodes)node, selecteddatestart,selecteddateend, period);
		return node;
	}

	@Override
	public void forcedeleteBanKuaiImportedDailyExchangeData(BanKuai bk) {
		bkdbopt.forcedeleteBanKuaiImportedDailyExchangeData (bk);
	}

	@Override
	public void forcedeleteBanKuaiImportedGeGuData(BanKuai bk) {
		bkdbopt.forcedeleteBanKuaiImportedGeGuData (bk);
	}
	
	public void saveBanKuaiExtraDataToDatabase (BanKuai bk , LocalDate date, String extradatakeywords[] ) {
		bkdbopt.saveBanKuaiExtraDataToDatabase (bk, date, extradatakeywords);
	}
	
}
