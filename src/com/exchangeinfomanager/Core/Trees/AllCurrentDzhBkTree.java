package com.exchangeinfomanager.Core.Trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

public class AllCurrentDzhBkTree 
{
	private AllCurrentDzhBkTree ()
	{
		this.bkdbopt = new BanKuaiDZHDbOperation ();
		initializeAllStocksTree ();
		
		setupDaPan();
	}
	
	 public static AllCurrentDzhBkTree getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static AllCurrentDzhBkTree instance =  new AllCurrentDzhBkTree ();  
	 }

	private BanKuaiDZHDbOperation bkdbopt;
	private BanKuaiAndStockTree allbkggtree;

	private void initializeAllStocksTree() 
	{
		DaPan alltopNode = new DaPan("000000","大智慧两交易所");
		
		List<BanKuai> allbkandzs = bkdbopt.getDZHBanKuaiList ();
		for (BanKuai bankuai : allbkandzs ) 
		    alltopNode.add(bankuai);
		
		allbkggtree = new BanKuaiAndStockTree(alltopNode,"ALLDZHBKS");
		
		allbkandzs = null;
	}
	
	public  BanKuaiAndStockTree getAllBkStocksTree()
	{
		return allbkggtree;
	}
	/*
	 * 
	 */
	public void setupDaPan ()
	{
		DaPan treeallstockrootdapan = (DaPan)allbkggtree.getModel().getRoot();
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.DZHBK); //大智慧的大盘依然使用通达信的代码
		BanKuai szdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.DZHBK);
		treeallstockrootdapan.setDaPanContents(shdpbankuai,szdpbankuai);
	}
	
}
