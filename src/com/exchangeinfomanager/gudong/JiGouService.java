package com.exchangeinfomanager.gudong;

import com.exchangeinfomanager.database.BanKuaiDbOperation;

public class JiGouService implements ServicesForJiGou
{
	BanKuaiDbOperation dbopt;
	
	public JiGouService ()
	{
		dbopt = new BanKuaiDbOperation ();
	}
	
	@Override
	public void addJiGou(String jigouname) 
	{
		int result = dbopt.storeJiGouToDb (jigouname);
		return;
	}

}
