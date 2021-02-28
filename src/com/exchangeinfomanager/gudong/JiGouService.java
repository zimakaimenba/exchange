package com.exchangeinfomanager.gudong;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.Stock;

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

	@Override
	public Stock  getStockGuDong(Stock stock, Boolean onlyimportwithjigougudong, Boolean forcetorefrshallgudong) 
	{
		stock = dbopt.refreshStockGuDongData ( stock,  onlyimportwithjigougudong, forcetorefrshallgudong);
		return stock;
		
	}

}
