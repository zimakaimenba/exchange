package com.exchangeinfomanager.nodes;


import java.time.LocalDate;



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
	
	public void getNodeData (LocalDate requiredstartday, LocalDate requiredendday, String period, Boolean calwholeweek)
	{
		SvsForNodeOfDaPan svsdp = new SvsForNodeOfDaPan ();
		svsdp.getNodeData("000000",requiredstartday, requiredendday,period,calwholeweek);
	}

	@Override
	public ServicesForNode getServicesForNode() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
