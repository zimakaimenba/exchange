package com.exchangeinfomanager.bankuaifengxi;

import com.exchangeinfomanager.Services.ServicesOfPaoMaDeng;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class PaoMaDengServices implements ServicesOfPaoMaDeng 
{

	public PaoMaDengServices ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
	}
	private BanKuaiDbOperation bkdbopt;

	@Override
	public String getPaoMaDengInfo() 
	{
		String paomad = bkdbopt.getMingRiJiHua();
		return paomad;
	}

	@Override
	public void setPaoMaDengInfo(BkChanYeLianTreeNode node, String msg) 
	{
		 int paomad = bkdbopt.setMingRiJiHua(node, msg);
	}

}
