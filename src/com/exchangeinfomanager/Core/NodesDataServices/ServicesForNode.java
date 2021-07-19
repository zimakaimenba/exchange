package com.exchangeinfomanager.Core.NodesDataServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;

public interface ServicesForNode 
{
	public BkChanYeLianTreeNode getNodeJiBenMian (BkChanYeLianTreeNode node);
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificGntxString (String requiredgntxstring);
	public Collection<BkChanYeLianTreeNode> getSubSetOfTheNodesWithSpecificFmxxString (String requiredfmxxstring);
	
	public BkChanYeLianTreeNode getNodeData (BkChanYeLianTreeNode bankuai,LocalDate requiredstartday,LocalDate requiredendday,String period ,Boolean calwholeweek );
	public BkChanYeLianTreeNode getNodeData (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek);
	public BkChanYeLianTreeNode getNodeKXian (String bkcode,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek);
	public BkChanYeLianTreeNode getNodeKXian (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek);
	
	public BkChanYeLianTreeNode getNodeZhangDieTingInfo (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeQueKouInfo (String bkcode,LocalDate requiredrecordsday,LocalDate requiredendday,String period);
	public BkChanYeLianTreeNode getNodeQueKouInfo (BkChanYeLianTreeNode bk,LocalDate requiredstartday,LocalDate requiredendday,String period);
	public void syncNodeData (BkChanYeLianTreeNode bk);
	
	public BkChanYeLianTreeNode getNodeCjeCjlZhanbiExtremeUpDownLevel (BkChanYeLianTreeNode node);
	public void setNodeCjeExtremeUpDownZhanbiLevel (BkChanYeLianTreeNode node, Double min, Double max);
	public void setNodeCjlExtremeUpDownZhanbiLevel (BkChanYeLianTreeNode node, Double min, Double max);
	
	List<BkChanYeLianTreeNode> getNodeChanYeLianInfo (String nodecode  );
	List<BkChanYeLianTreeNode> getNodeSlidingInChanYeLianInfo (String nodecode );
	List<BkChanYeLianTreeNode> getNodeChildrenInChanYeLianInfo (String nodecode );
	Collection<BkChanYeLianTreeNode> getNodesWithKeyWords(String keywords);
	
	public BkChanYeLianTreeNode getNodeGzMrMcYkInfo (BkChanYeLianTreeNode node,LocalDate selecteddatestart,LocalDate selecteddateend,String period);
	
	public static LocalDate getNodeCaculateEndDateAndRelatedActions(BkChanYeLianTreeNode node, String period, LocalDate requiredendday, Boolean calwholeweek)
	{
		LocalDate result = null;
		
		DayOfWeek dayofweek = requiredendday.getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) )	requiredendday = requiredendday.minus(2,ChronoUnit.DAYS);
		else if(dayofweek.equals(DayOfWeek.SATURDAY) )	requiredendday = requiredendday.minus(1,ChronoUnit.DAYS);
		
		NodeXPeriodData nodeperioddata = ((TDXNodes)node).getNodeXPeroidData(period);
		NodeXPeriodData nodexdataday = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		
		Boolean origmode = true; //原来数据是否处于NCW / CW状态, true表示是计算whole wk
		LocalDate isInNotCalWholeWeekModeData = nodeperioddata.isInNotCalWholeWeekMode ();
		if(nodeperioddata.isInNotCalWholeWeekMode () != null ) 
			origmode = false;
		
		if(calwholeweek && origmode) { //CW TO CW
			nodeperioddata.setNotCalWholeWeekMode (null);
			nodexdataday.setNotCalWholeWeekMode (null);
			
			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result;
		}
		if(calwholeweek && !origmode) { //NCW TO CW，那就把最后一周的数据删除
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null) {
				nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate);
				
				nodexdataday.removeNodeDataFromSpecificDate(amorecordsenddate.with(DayOfWeek.MONDAY)); //那一周的数据都不要了
				nodeperioddata.setNotCalWholeWeekMode (null);
				nodexdataday.setNotCalWholeWeekMode (null);
			}
			
			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result; 
		}
		
		if(!calwholeweek && !origmode) {  //NCW TO NCW
			if(isInNotCalWholeWeekModeData == requiredendday) { //说明已经在相同日期计算过了
				result  = requiredendday;
				return result;
			}
				
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null && amorecordsenddate.isBefore(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(amorecordsenddate);
			else if(amorecordsenddate != null && amorecordsenddate.isAfter(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(requiredendday);
			
			LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
			if(ohlcrecordsenddate != null && ohlcrecordsenddate.isBefore(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(ohlcrecordsenddate);
			else if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(requiredendday);
			
			nodeperioddata.setNotCalWholeWeekMode (requiredendday);
			nodexdataday.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		if(!calwholeweek && origmode) {  //CW TO NCW
			LocalDate amorecordsenddate = nodeperioddata.getAmoRecordsEndDate();
			if(amorecordsenddate != null && amorecordsenddate.isAfter(requiredendday))
				nodeperioddata.removeNodeDataFromSpecificDate(requiredendday);
			
			LocalDate ohlcrecordsenddate = nodexdataday.getOHLCRecordsEndDate();
			if(ohlcrecordsenddate != null && ohlcrecordsenddate.isAfter(requiredendday))
				nodexdataday.removeNodeDataFromSpecificDate(requiredendday);
			
			nodeperioddata.setNotCalWholeWeekMode (requiredendday);
			nodexdataday.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		return null;
	}
}
