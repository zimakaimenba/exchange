package com.exchangeinfomanager.Trees;

import java.sql.SQLException;
import java.time.DayOfWeek;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;


import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
/*
 * 
 */
public class AllCurrentTdxBKAndStoksTree 
{
	
	private AllCurrentTdxBKAndStoksTree ()
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		initializeAllStocksTree ();
		setupDaPan ();
	}
	
	// 锟斤拷锟斤拷实锟斤拷  
	 public static AllCurrentTdxBKAndStoksTree getInstance ()
	 {  
	        return Singtonle.instance;  
	 }
	 
	 private static class Singtonle 
	 {  
	        private static AllCurrentTdxBKAndStoksTree instance =  new AllCurrentTdxBKAndStoksTree ();  
	 }

	private BanKuaiDbOperation bkdbopt;
	private BanKuaiAndStockTree allbkggtree;

	private void initializeAllStocksTree() 
	{
		DaPan alltopNode = new DaPan("000000","通达信两交易所");
		
		List<BanKuai> allbkandzs = bkdbopt.getTDXBanKuaiList ("all");
		for (BanKuai bankuai : allbkandzs ) 
		    alltopNode.add(bankuai);
		
		ArrayList<Stock> allstocks = bkdbopt.getAllStocks ();
		for (Stock stock : allstocks ) 
		    alltopNode.add(stock);

		allbkggtree = new BanKuaiAndStockTree(alltopNode,"ALLTDXBKSTOCKS");
		
		allstocks = null;
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
		BanKuai shdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		BanKuai szdpbankuai = (BanKuai) allbkggtree.getSpecificNodeByHypyOrCode("399001",BkChanYeLianTreeNode.TDXBK);
		treeallstockrootdapan.setDaPanContents(shdpbankuai,szdpbankuai);
	}
}
