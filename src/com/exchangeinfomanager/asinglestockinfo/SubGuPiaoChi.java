package com.exchangeinfomanager.asinglestockinfo;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;

public class SubGuPiaoChi extends BkChanYeLianTreeNode
{
	public SubGuPiaoChi(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.SUBGPC;
	}

	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
