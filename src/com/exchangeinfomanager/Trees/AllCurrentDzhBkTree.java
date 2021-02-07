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
	
	// ï¿½ï¿½ï¿½ï¿½Êµï¿½ï¿½  
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
		DaPan alltopNode = new DaPan("000000","´óÖÇ»ÛÁ½½»Ò×Ëù");
		
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
