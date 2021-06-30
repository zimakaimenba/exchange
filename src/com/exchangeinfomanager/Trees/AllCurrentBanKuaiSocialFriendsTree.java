package com.exchangeinfomanager.Trees;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;

public class AllCurrentBanKuaiSocialFriendsTree
{
	TreeOfChanYeLian treebksocialfriends;
	private AllCurrentBanKuaiSocialFriendsTree ()
	{
		CylTreeNestedSetNode alltopNode = new CylTreeNestedSetNode ("000000","°å¿éSocialFriends",BkChanYeLianTreeNode.DAPAN);
		treebksocialfriends = new TreeOfChanYeLian(alltopNode,"TDXBANKUAISOCIALTREE"); 
	}
	
	 public static AllCurrentBanKuaiSocialFriendsTree getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static AllCurrentBanKuaiSocialFriendsTree instance =  new AllCurrentBanKuaiSocialFriendsTree ();  
	 }

		
	public  TreeOfChanYeLian getAllBkStocksTree()
	{
		return treebksocialfriends;
	}
}
