package com.exchangeinfomanager.asinglestockinfo;

public class GuPiaoChi extends BkChanYeLianTreeNode
{
	public GuPiaoChi(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.GPC;
	}

	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
