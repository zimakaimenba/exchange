package com.exchangeinfomanager.nodes;


import java.time.LocalDate;

import com.exchangeinfomanager.NodesServices.ServicesForNode;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDZHBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.DaPanXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;


public class DaPan extends TDXNodes
{

	public DaPan(String dapcode, String dapanname) 
	{
		super(dapcode,dapanname);
		super.nodetype = BkChanYeLianTreeNode.DAPAN;
		
//		super.nodetreerelated = new NodesTreeRelated (this);
	}
//	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai shanghai; //
	private BanKuai shenzhen; //
	private ServicesForNode svsdp;
	
	public void setDaPanContents (BanKuai sh,BanKuai sz)
	{
		this.shanghai = sh;
		this.shenzhen = sz;
		
		super.nodewkdata = new DaPanXPeriodDataForJFC (NodeGivenPeriodDataItem.WEEK,shanghai,shenzhen) ;
		super.nodedaydata = new DaPanXPeriodDataForJFC (NodeGivenPeriodDataItem.DAY,shanghai,shenzhen) ;
	}
	
	public BanKuai getDaPanShangHai ()
	{
		return this.shanghai;
	}
	public BanKuai getDaPanShenZhen ()
	{
		return this.shenzhen;
	}
	/*
	 * 
	 */
	public Boolean  isDaPanXiuShi (LocalDate date,String period)
	{
		NodeXPeriodData shnodexdata = this.shanghai.getNodeXPeroidData(period);
		if(shnodexdata.getIndexOfSpecificDateOHLCData(date) != null )
			return false;
		else
			return true;
	}

	public ServicesForNode getServicesForNode (Boolean getorreset)
	{
		if(!getorreset) {
			svsdp = null;
			return null;
		}
		
		if(svsdp != null)
			return this.svsdp;
		
		svsdp = new SvsForNodeOfDaPan ();
//		if(this.shanghai.getType() == BkChanYeLianTreeNode.TDXBK)
//			svsdp = new SvsForNodeOfDaPan ();
//		else if(this.shanghai.getType() == BkChanYeLianTreeNode.DZHBK)
//			svsdp = new SvsForNodeOfDaPan ();
		
		return svsdp;
	}
	
}
