package com.exchangeinfomanager.Trees;

import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;

public class CreateExchangeTree 
{
	public static TreeOfChanYeLian CreateTreeOfChanYeLian ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","两交易所",0);
		TreeOfChanYeLian treecyl = new TreeOfChanYeLian(alltopNode,"CYLTREE");
		return treecyl;
	}
	
	public static BanKuaiAndStockTree CreateTreeOfBanKuaiAndStocks ()
	{
		AllCurrentTdxBKAndStoksTree allbkstks = AllCurrentTdxBKAndStoksTree.getInstance();
		return allbkstks.getAllBkStocksTree();
	}
}
