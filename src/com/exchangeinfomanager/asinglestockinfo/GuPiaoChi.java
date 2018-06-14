package com.exchangeinfomanager.asinglestockinfo;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;

public class GuPiaoChi extends BkChanYeLianTreeNode
{
	public GuPiaoChi(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.GPC;
		
		super.nodetreerelated = new TreeRelated (this);
	}

	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
