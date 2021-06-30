package com.exchangeinfomanager.Trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;

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
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("000001",BkChanYeLianTreeNode.DZHBK);
		BanKuai szdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.DZHBK);
		treeallstockrootdapan.setDaPanContents(shdpbankuai,szdpbankuai);
	}
	
}
