package com.exchangeinfomanager.nodes;


import java.time.LocalDate;

import com.exchangeinfomanager.NodesServices.ServicesForNode;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.DaPanXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;


public class DaPan extends TDXNodes
{

	public DaPan(String dapcode, String dapanname) 
	{
		super(dapcode,"╢Сел");
		super.nodetype = BkChanYeLianTreeNode.DAPAN;
		
//		super.nodetreerelated = new NodesTreeRelated (this);
	}
//	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai shanghai; //
	private BanKuai shenzhen; //
	private SvsForNodeOfDaPan svsdp;
	
	public void setDaPanContents (BanKuai sh,BanKuai sz)
	{
		this.shanghai = sh;
		this.shenzhen = sz;
		
		super.nodewkdata = new DaPanXPeriodDataForJFC (NodeGivenPeriodDataItem.WEEK,shanghai,shenzhen) ;
		super.nodedaydata = new DaPanXPeriodDataForJFC (NodeGivenPeriodDataItem.DAY,shanghai,shenzhen) ;
	}
	
	/*
	 * 
	 */
	public Boolean  isDaPanXiuShi (LocalDate date,int difference,String period)
	{
		NodeXPeriodData shnodexdata = this.shanghai.getNodeXPeroidData(period);
		if(shnodexdata.getIndexOfSpecificDateOHLCData(date,difference) != null )
			return false;
		else
			return true;
	}
	
//	public void getNodeData (LocalDate requiredstartday, LocalDate requiredendday, String period, Boolean calwholeweek)
//	{
//		
//		svsdp.getNodeData("000000",requiredstartday, requiredendday,period,calwholeweek);
//	}

	@Override
	public ServicesForNode getServicesForNode(Boolean getornot) {
		if(!getornot) {
			svsdp = null;
			return null;
		}
			
		if(svsdp == null) {
			svsdp = new SvsForNodeOfDaPan ();
			return svsdp;
		}
		else 
			return svsdp;

	}
	
}
