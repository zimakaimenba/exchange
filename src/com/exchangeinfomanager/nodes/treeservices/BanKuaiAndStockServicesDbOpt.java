package com.exchangeinfomanager.nodes.treeservices;

import java.util.ArrayList;
import java.util.List;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;

public class BanKuaiAndStockServicesDbOpt implements BanKuaiAndStockServices
{
	private BanKuaiDbOperation bkdbopt;


	public BanKuaiAndStockServicesDbOpt ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
	}

	public void setTree (BanKuaiAndStockTree allbkggtree)
	{
		
	}
	@Override
	public List<BanKuai> getAllTDXBanKuai() 
	{
		List<BanKuai> allbkandzs = bkdbopt.getTDXBanKuaiList ("all"); 
		return allbkandzs;
	}


	@Override
	public List<Stock> getAllTDXStocks()
	{
		List<Stock> allstocks = bkdbopt.getAllStocks ();
		return allstocks;
	}

	
	

}
