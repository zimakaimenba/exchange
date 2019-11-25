package com.exchangeinfomanager.nodes.treeservices;

import java.util.ArrayList;
import java.util.List;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;

public class TreeOfAllBkStk 
{
	public TreeOfAllBkStk ()
	{
		initializeAllStocksTree ();
		
		bkstkservice = new BanKuaiAndStockServicesDbOpt ();
	}
	
	private BanKuaiDbOperation bkdbopt;
	private BanKuaiAndStockTree allbkggtree;
	BanKuaiAndStockServices bkstkservice;
	
	private void initializeAllStocksTree() 
	{
		
		DaPan alltopNode = new DaPan("000000","两交易所");
		
		List<BanKuai> allbkandzs = bkstkservice.getAllTDXBanKuai ();
		for (BanKuai bankuai : allbkandzs ) {
		    alltopNode.add(bankuai);
		}
		
		List<Stock> allstocks = bkstkservice.getAllTDXStocks ();
		for (Stock stock : allstocks ) {
		    alltopNode.add(stock);

		}

		allbkggtree = new BanKuaiAndStockTree(alltopNode,"ALLBKSTOCKS");
		allstocks = null;
	}
	
	public  BanKuaiAndStockTree getAllBkStocksTree()
	{
		return allbkggtree;
	}


}
