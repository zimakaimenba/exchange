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
		
		NodeXPeriodData nodexdataperiod = ((TDXNodes)node).getNodeXPeroidData(period);
		
		Boolean origmode = true; //ԭ�������Ƿ���NCW / CW״̬, true��ʾ�Ǽ���whole wk
		LocalDate finialisInNotCalWholeWeekModeData = null;
		if(period.equalsIgnoreCase(NodeGivenPeriodDataItem.DAY)) { //day ���ڴ������ϼ� week
			NodeXPeriodData nodexdatawk = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			LocalDate isInNotCalWholeWeekModeData = nodexdatawk.isInNotCalWholeWeekMode ();
			if(isInNotCalWholeWeekModeData != null ) {
				origmode = false;
				finialisInNotCalWholeWeekModeData = isInNotCalWholeWeekModeData; 
			}
		} else if(period.equalsIgnoreCase(NodeGivenPeriodDataItem.WEEK)) {
			LocalDate isInNotCalWholeWeekModeData = nodexdataperiod.isInNotCalWholeWeekMode ();
			if(isInNotCalWholeWeekModeData != null ) {
				origmode = false;
				finialisInNotCalWholeWeekModeData = isInNotCalWholeWeekModeData;
			}
		}
		
		if(calwholeweek && origmode) { //CW TO CW
			nodexdataperiod.setNotCalWholeWeekMode (null);
			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result;
		}
		if(calwholeweek && !origmode) { //NCW TO CW�����һ�ܵ������ǲ������ģ��ǾͰ����һ�ܵ�����ɾ��������isInNotCalWholeWeekModeData��Ȼ��amorecordsenddate��ͬһ��
			LocalDate amorecordsenddate = nodexdataperiod.getAmoRecordsEndDate();
			LocalDate ohlcrecordsenddate = nodexdataperiod.getOHLCRecordsEndDate(); //����AMODATE����ΪNULL
			
			LocalDate removestartdate = null;
			if(amorecordsenddate != null) removestartdate = amorecordsenddate;
			else if(ohlcrecordsenddate != null) removestartdate = ohlcrecordsenddate;
			else removestartdate = requiredendday;
			
			if(period.equalsIgnoreCase(NodeGivenPeriodDataItem.DAY))
				nodexdataperiod.removeNodeDataFromSpecificDate(removestartdate.with(DayOfWeek.MONDAY)); //��һ�ܵ����ݶ���Ҫ��
			else nodexdataperiod.removeNodeDataFromSpecificDate(removestartdate);
				
			nodexdataperiod.setNotCalWholeWeekMode (null);

			result  = requiredendday.with(DayOfWeek.FRIDAY);
			return result; 
		}
		if(!calwholeweek && !origmode) {  //NCW TO NCW
			LocalDate curdatadate ;
			LocalDate amorecordsenddate = nodexdataperiod.getAmoRecordsEndDate();
			LocalDate ohlcrecordsenddate = nodexdataperiod.getOHLCRecordsEndDate(); //����AMODATE����ΪNULL
			if(amorecordsenddate != null) curdatadate = amorecordsenddate;
			else if(ohlcrecordsenddate != null) curdatadate = ohlcrecordsenddate;
			else curdatadate = requiredendday; //��������NULL,˵���ǰ���������ߵ�һ�ζ�ȡ����
			
			LocalDate removestartdate = null;
			if( curdatadate.isBefore(requiredendday)) {
				removestartdate = curdatadate;
			} else if( curdatadate.isAfter(requiredendday)) {
				removestartdate = requiredendday;
			} else if(curdatadate.isEqual(requiredendday))
				removestartdate = requiredendday;
				
			if(period.equalsIgnoreCase(NodeGivenPeriodDataItem.DAY))
				nodexdataperiod.removeNodeDataFromSpecificDate(removestartdate.with(DayOfWeek.MONDAY)); //��һ�ܵ����ݶ���Ҫ��
			else nodexdataperiod.removeNodeDataFromSpecificDate(removestartdate);
					
			nodexdataperiod.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		if(!calwholeweek && origmode) {  //CW TO NCW��
			LocalDate amorecordsenddate = nodexdataperiod.getAmoRecordsEndDate();
			LocalDate ohlcrecordsenddate = nodexdataperiod.getOHLCRecordsEndDate(); //����AMODATE����ΪNULL
			LocalDate curdatadate = null; 
			if(amorecordsenddate != null) curdatadate = amorecordsenddate;
			else if(ohlcrecordsenddate != null) curdatadate = ohlcrecordsenddate;
			else curdatadate = requiredendday; //��������NULL,˵�������ߵ�һ�ζ�ȡ����

			LocalDate removestartdate = null;
			if( curdatadate.isBefore(requiredendday))
				removestartdate = curdatadate;
			else if( curdatadate.isAfter(requiredendday))
				removestartdate = requiredendday;
			else if(curdatadate.isEqual(requiredendday))
				removestartdate = requiredendday;

			if(period.equalsIgnoreCase(NodeGivenPeriodDataItem.DAY))
				nodexdataperiod.removeNodeDataFromSpecificDate(removestartdate.with(DayOfWeek.MONDAY)); //��һ�ܵ����ݶ���Ҫ��
			else nodexdataperiod.removeNodeDataFromSpecificDate(removestartdate);
				
			nodexdataperiod.setNotCalWholeWeekMode (requiredendday);
			result  = requiredendday;
			return result;
		}
		
		return null;
	}
}
