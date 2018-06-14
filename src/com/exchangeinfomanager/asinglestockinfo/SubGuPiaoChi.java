package com.exchangeinfomanager.asinglestockinfo;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;

public class SubGuPiaoChi extends BkChanYeLianTreeNode
{
	public SubGuPiaoChi(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.SUBGPC;
		
		super.nodetreerelated = new TreeRelated (this);
	}

	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
