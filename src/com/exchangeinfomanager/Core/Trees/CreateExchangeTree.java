package com.exchangeinfomanager.Core.Trees;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.CylTreeNestedSetNode;

public class CreateExchangeTree 
{
	public static TreeOfChanYeLian CreateTreeOfChanYeLian ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","��ҵ��",BkChanYeLianTreeNode.DAPAN);
		TreeOfChanYeLian treecyl = new TreeOfChanYeLian(alltopNode,"CYLTREE"); 
		return treecyl;
	}
	
	public static TreeOfChanYeLian CreateCopyOfTDXBankuaiSocialTree ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","���SocialFriends",BkChanYeLianTreeNode.DAPAN);
		TreeOfChanYeLian treecyl = new TreeOfChanYeLian(alltopNode,"TDXBANKUAISOCIALTREE"); 
		return treecyl;
	}
	
	public static TreeOfChanYeLian CreateTreeOfBanKuaiSocialFriends ()
	{
		AllCurrentBanKuaiSocialFriendsTree allbksocialfriends = AllCurrentBanKuaiSocialFriendsTree.getInstance();
		return allbksocialfriends.getAllBkStocksTree();
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
