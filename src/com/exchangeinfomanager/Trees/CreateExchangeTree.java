package com.exchangeinfomanager.Trees;

import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;

public class CreateExchangeTree 
{
	public static TreeOfChanYeLian CreateTreeOfChanYeLian ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","��������",0);
		TreeOfChanYeLian treecyl = new TreeOfChanYeLian(alltopNode,"CYLTREE");
		return treecyl;
	}
	
	public static BanKuaiAndStockTree CreateTreeOfBanKuaiAndStocks ()
	{
		AllCurrentTdxBKAndStoksTree allbkstks = AllCurrentTdxBKAndStoksTree.getInstance();
		return allbkstks.getAllBkStocksTree();
	}
}
