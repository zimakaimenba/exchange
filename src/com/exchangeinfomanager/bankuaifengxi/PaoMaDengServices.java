package com.exchangeinfomanager.bankuaifengxi;

import com.exchangeinfomanager.Services.ServicesOfPaoMaDeng;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

public class PaoMaDengServices implements ServicesOfPaoMaDeng 
{

	private BanKuaiDbOperation bkdbopt;

	@Override
	public String getPaoMaDengInfo() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		String paomad = bkdbopt.getMingRiJiHua();
		
		return paomad;
	}

}
