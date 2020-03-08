package com.exchangeinfomanager.NodesServices;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Services.ServicesForNode;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class SvsForNodeOfBanKuai implements ServicesForNode
{

	private BanKuaiAndStockTree allbkstk;
	private BanKuaiDbOperation bkdbopt;
	private CylTreeDbOperation dboptforcyltree;
	private TreeOfChanYeLian cyltree;

	public SvsForNodeOfBanKuai ()
	{
		allbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		this.cyltree = CreateExchangeTree.CreateTreeOfChanYeLian();
		
		this.bkdbopt = new BanKuaiDbOperation ();
		dboptforcyltree = new CylTreeDbOperation (this.cyltree);
	}
	/*
	 * 
	 */
	public List<TDXNodes> getBanKuaiFenXiQualifiedNodes (LocalDate checkdate,
			 String globeperiod, Boolean globecalwholeweek, Boolean forcetogetnodedataagain)
	{
		List<TDXNodes> bkwithcje = new ArrayList<TDXNodes> ();
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) allbkstk.getModel().getRoot();
		int bankuaicount = allbkstk.getModel().getChildCount(treeroot);

		LocalDate requirestart = CommonUtility.getSettingRangeDate(checkdate,"middle").with(DayOfWeek.MONDAY);
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) allbkstk.getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if(forcetogetnodedataagain)
				childnode = this.getNodeData( (BanKuai)childnode, requirestart, checkdate,globeperiod,globecalwholeweek);
			
			NodeXPeriodData bkxdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			if(bkxdata.getIndexOfSpecificDateOHLCData(checkdate, 0) != null)//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
				bkwithcje.add( (BanKuai)childnode );

		}
		
		return bkwithcje;
	}
	@Override
	public Collection<BkChanYeLianTreeNode> getNodes(Collection<String> nodenames)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BkChanYeLianTreeNode getNode(String nodenames)
	{
		return allbkstk.getSpecificNodeByHypyOrCode(nodenames, BkChanYeLianTreeNode.TDXBK);
	}
//	@Override
//	public BkChanYeLianTreeNode getNodeByNameOrCode(String nameorcode, int nodetype) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Collection<BkChanYeLianTreeNode> getAllNodes() 
	{
		Collection<BkChanYeLianTreeNode> allbks = new ArrayList<> ();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)allbkstk.getModel().getRoot();
		int bankuaicount = allbkstk.getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbkstk.getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			allbks.add(childnode);
		}
		
		return allbks;
	}

	@Override
	public BkChanYeLianTreeNode getNodeJiBenMian(BkChanYeLianTreeNode node) 
	{
		node = bkdbopt.getBanKuaiBasicInfo ((BanKuai) node);
		return node;
	}

	@Override
	public Collection<BkChanYeLianTreeNode> getRequiredSubSetOfTheNodes(Set<String> subtypesset) 
	{
		Collection<BkChanYeLianTreeNode> bklist = new ArrayList<BkChanYeLianTreeNode> ();
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbkstk.getModel().getRoot();
		int bankuaicount = allbkstk.getModel().getChildCount(treeroot);
		
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbkstk.getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			String bktype = ((BanKuai)childnode).getBanKuaiLeiXing();
			if(subtypesset.contains(bktype))
				bklist.add(childnode);
		}
		
		return bklist;
	}

	@Override
	public BkChanYeLianTreeNode getNodeData(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period, Boolean calwholeweek) 
	{
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
			bankuai = bkdbopt.getBanKuaiZhanBi ((BanKuai)bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(requiredstartday,requiredendday,nodedayperioddata.getAmoRecordsStartDate(),nodedayperioddata.getAmoRecordsEndDate());
		if(timeintervallist == null) 
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
				
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
		NodeXPeriodData nodedayperioddata = ((BanKuai) bankuai).getNodeXPeroidData(period);
		if(nodedayperioddata.getOHLCRecordsStartDate() == null) {
			bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
			
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
				
				bankuai = bkdbopt.getBanKuaiKXianZouShi (((BanKuai) bankuai),requiredstartday,requiredendday,period);
		}
		
//		this.getNodeData(bankuai, requiredstartday, requiredendday, period, calwholeweek);
		
		return bankuai;
	}

	@Override
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		if(bankuai.getMyOwnCode().equals("999999") ) {
			this.getShangZhengZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		else
		if(bankuai.getMyOwnCode().equals("399001") ) {
			this.getShenZhenZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		else
		if(bankuai.getMyOwnCode().equals("399006") ) {
			this.getChuangYeBanZhangDieTingInfo (bankuai,requiredstartday,requiredendday,period);
			return bankuai;
		}
		
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer zhangtingnum = 0;
			 Integer dietingnum = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( !((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //确锟较碉拷前锟斤拷锟节帮拷锟斤拷锟?
					continue;

				Stock stock = ((StockOfBanKuai)stockofbk).getStock();
				NodeXPeriodData stockxdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
				Integer stockzt = stockxdate .getZhangTingTongJi (tmpdate,0);
				if(stockzt != null)
					zhangtingnum = zhangtingnum + stockzt;
				
				Integer stockdt =  stockxdate .getDieTingTongJi(tmpdate, 0);
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
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
		bk = this.bkdbopt.getChuangYeBanZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		
//		LocalDate tmpdate = requiredstartday;
//		do {
//			 
//			bk = this.bkdbopt.getChangYeBanZhangDieTingInfo (bk,  tmpdate, tmpdate.with(DayOfWeek.FRIDAY),  period);
//			
//			if(period.equals(NodeGivenPeriodDataItem.WEEK))
//				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
//			else if(period.equals(NodeGivenPeriodDataItem.DAY))
//					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
//			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
//					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
//			
//		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));
		
		return bankuai;
		
	}
	private BkChanYeLianTreeNode getShenZhenZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) {
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
		bk = this.bkdbopt.getShenZhenZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		
//		LocalDate tmpdate = requiredstartday;
//		do {
//			 
//			bk = this.bkdbopt.getShenZhenZhangDieTingInfo (bk,  tmpdate, tmpdate.with(DayOfWeek.FRIDAY),  period);
//			
//			if(period.equals(NodeGivenPeriodDataItem.WEEK))
//				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
//			else if(period.equals(NodeGivenPeriodDataItem.DAY))
//					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
//			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
//					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
//			
//		} while (tmpdate.isBefore( requiredendday) || tmpdate.isEqual(requiredendday));
		
		return bankuai;
	}
	private BkChanYeLianTreeNode getShangZhengZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		BanKuai bk = (BanKuai) bankuai;
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		
		bk = this.bkdbopt.getShangZhengZhangDieTingInfo (bk,  requiredstartday, requiredendday,  period);
		
		return bankuai;
	}
	private BkChanYeLianTreeNode getKeChuangBanZhangDieTingInfo(BkChanYeLianTreeNode bankuai, LocalDate requiredstartday,
			LocalDate requiredendday, String period) 
	{
		
		return null;
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
//		bk = this.getAllGeGuOfBanKuai (bk,period); //锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷锟角该帮拷锟侥革拷锟斤拷
		NodeXPeriodData bkwkdate = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
		
		LocalDate tmpdate = requiredstartday;
		do {
			 Integer openup = 0;
			 Integer opendown = 0;
			 Integer huibuup = 0;
			 Integer huibudown = 0;
			 
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
				if( ! ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(tmpdate)  )  //确锟较碉拷前锟斤拷锟节帮拷锟斤拷锟?
					continue;
				
				Stock stock = ((StockOfBanKuai)stockofbk).getStock();
				NodeXPeriodData stockxdate = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
				Integer stockopenup = stockxdate.getQueKouTongJiOpenUp (tmpdate,0);
				if(stockopenup != null && stockopenup != 0)
					openup = openup + stockopenup;
				Integer stockopendown = stockxdate.getQueKouTongJiOpenDown(tmpdate, 0);
				if(stockopendown != null && stockopendown !=0 )
					opendown = opendown + stockopendown;
				Integer stockhbdown =  stockxdate.getQueKouTongJiHuiBuDown(tmpdate, 0);
				if(stockhbdown != null && stockhbdown !=0)
					huibudown = huibudown + stockhbdown;
				Integer stockhbup =stockxdate.getQueKouTongJiHuiBuUp(tmpdate, 0);
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

	@Override
	public void syncNodeData(BkChanYeLianTreeNode bankuai)
	{
		BanKuai bk = (BanKuai)bankuai;
		
		LocalDate bkohlcstartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsStartDate();
		LocalDate bkohlcendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY).getOHLCRecordsEndDate();
		
		LocalDate bkamostartday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getAmoRecordsStartDate();
		LocalDate bkamoendday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK).getAmoRecordsEndDate();
		
		if(bkohlcstartday == null && bkamostartday == null) //都为空，
			return ;
		
		if(bkohlcstartday == null) {
			bkohlcstartday = bkamostartday;
			bkohlcendday = bkamoendday;
			bankuai = this.getNodeKXian(bankuai, bkohlcstartday,bkohlcendday, NodeGivenPeriodDataItem.DAY,true);
			return;
		}
		
		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
				(bkohlcstartday,bkohlcendday,bkamostartday,bkamoendday );
		
		if(timeintervallist == null)
			return ;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = this.getNodeKXian(bankuai, requiredstartday,requiredendday, NodeGivenPeriodDataItem.DAY,true);
		}
		
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

		List<Interval> timeintervallist = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval(bkstartday,bkendday,alreadystartday,alreadyendday);
		if(timeintervallist == null)
			return bankuai;
		
		for(Interval tmpinterval : timeintervallist) {
				DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
				
				bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,requiredstartday,requiredendday);
		}

		return bankuai;
	}
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, StockOfBanKuai stockofbk,String period)
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
	public StockOfBanKuai getGeGuOfBanKuai(BanKuai bankuai, String stockcode,String period)
	{
		StockOfBanKuai stock = bankuai.getStockOfBanKuai(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);
		return (StockOfBanKuai) stock;
	}
	public StockOfBanKuai getGeGuOfBanKuai(String bkcode, String stockcode,String period) 
	{
		BanKuai bankuai = (BanKuai) allbkstk.getSpecificNodeByHypyOrCode(bkcode,BkChanYeLianTreeNode.TDXBK);
		if(bankuai == null)
			return null;
		
		StockOfBanKuai stock = bankuai.getStockOfBanKuai(stockcode);
		if(stock == null)
			return null;
		
		stock = this.getGeGuOfBanKuai( bankuai,  stock,period);

		return stock;
	}
	
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException
	{
		this.getNodeData(bk, requiredstartday, requiredendday, period,calwholeweek);
		this.syncNodeData(bk);
		//板块数据同步后，板块个股的时间轴如果比板块短，要和板块的时间轴一致。如果比板块时间长，不需要同步时间轴
		NodeXPeriodData bknodexdata = bk.getNodeXPeroidData(period);
		LocalDate bkdatastartday = bknodexdata.getAmoRecordsStartDate();
		if(bkdatastartday.isBefore(requiredstartday) )
			requiredstartday = bkdatastartday;
		LocalDate bkdataendday = bknodexdata.getAmoRecordsEndDate();
		if(bkdataendday.isAfter(requiredendday) )
			requiredendday = bkdataendday;
	
		if(bk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) {
			
			SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
			
			bk = this.getAllGeGuOfBanKuai (bk); 
			List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
			for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
		    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(requiredendday)  ) { 
		    		 Stock stock = (Stock) svstock.getNodeData( ((StockOfBanKuai)stockofbk).getStock(), requiredstartday, requiredendday, period,calwholeweek);
	    			 svstock.syncNodeData(stock);
		    	 }
			}
			
			if(calwholeweek ) { //
				this.getNodeQueKouInfo(bk, requiredstartday, requiredendday, period);
				this.getNodeZhangDieTingInfo(bk, requiredstartday, requiredendday, period);
			}
		} else
		if(bk.getMyOwnCode().equals("999999")) {
			this.getNodeZhangDieTingInfo(bk, requiredstartday, requiredendday, period);
		}
	}
	@Override
	public List<BkChanYeLianTreeNode> getNodeChanYeLianInfo(String nodecode) 
	{
		List<BkChanYeLianTreeNode> cylinfo = this.dboptforcyltree.getChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylinfo;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo(String nodecode) 
	{
		List<BkChanYeLianTreeNode> cylsliding = this.dboptforcyltree.getSlidingInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylsliding;
	}

	@Override
	public List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo(String nodecode) 
	{
		List<BkChanYeLianTreeNode> cylchildren  = this.dboptforcyltree.getChildrenInChanYeLianInfo (nodecode,BkChanYeLianTreeNode.TDXBK);
		return cylchildren;
	}

}
