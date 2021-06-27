package com.exchangeinfomanager.Trees;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;

public class CreateExchangeTree 
{
	public static TreeOfChanYeLian CreateTreeOfChanYeLian ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","两交易所",BkChanYeLianTreeNode.DAPAN);
		TreeOfChanYeLian treecyl = new TreeOfChanYeLian(alltopNode,"CYLTREE"); 
		return treecyl;
	}
	
	public static TreeOfChanYeLian CreateTDXBankuaiSocialTree ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","两交易所",BkChanYeLianTreeNode.DAPAN);
		TreeOfChanYeLian treecyl = new TreeOfChanYeLian(alltopNode,"TDXBANKUAISOCIALTREE"); 
		return treecyl;
	}
	
	public static BanKuaiAndStockTree CreateTreeOfBanKuaiAndStocks ()
	{
		AllCurrentTdxBKAndStoksTree allbkstks = AllCurrentTdxBKAndStoksTree.getInstance();
		return allbkstks.getAllBkStocksTree();
	}
	
	public static BanKuaiAndStockTree CreateTreeOfDZHBanKuaiAndStocks ()
	{
		AllCurrentDzhBkTree allbkstks = AllCurrentDzhBkTree.getInstance();
		return allbkstks.getAllBkStocksTree();
	}
}
