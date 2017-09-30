package com.exchangeinfomanager.asinglestockinfo;

import java.util.HashSet;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class BanKuai extends BkChanYeLianTreeNode
{

	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		nodetype = BanKuaiAndStockBasic.TDXBK;
	}
	private String suoshujiaoyisuo;
	private DefaultCategoryDataset barchartdataset;
	private DefaultPieDataset piechartdataset;
	
	
	 public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
	 {
	    	if(super.parsefilestockset == null) {
	    		this.parsefilestockset = new HashSet<String> ();
	    		this.parsefilestockset = parsefilestockset2;
	    	}
	    	else
	    		this.parsefilestockset = parsefilestockset2;
	 }
	 
	 

}
