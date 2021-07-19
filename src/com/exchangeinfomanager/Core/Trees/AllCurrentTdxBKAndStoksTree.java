package com.exchangeinfomanager.Core.Trees;

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

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
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
