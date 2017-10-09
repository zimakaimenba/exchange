package com.exchangeinfomanager.asinglestockinfo;

import java.util.HashMap;
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
	private boolean notexporttogehpi = false;
	private HashMap<String, Stock> allbkge;
	
	
	 /**
	 * @return the tmpallbkge
	 */
	public HashMap<String, Stock> getBanKuaiGeGu() {
		return allbkge;
	}
	/**
	 * @param tmpallbkge the tmpallbkge to set
	 */
	public void setBanKuaiGeGu(HashMap<String, Stock> tmpallbkge) {
		this.allbkge = tmpallbkge;
	}
	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
	 {
	    	if(super.parsefilestockset == null) {
	    		this.parsefilestockset = new HashSet<String> ();
	    		this.parsefilestockset = parsefilestockset2;
	    	}
	    	else
	    		this.parsefilestockset = parsefilestockset2;
	 }
	 public void setNotExportToGephi ()
	 {
		 this.notexporttogehpi = true;
	 }
	 public boolean shouldNotExportToGephi ()
	 {
		 return this.notexporttogehpi;
	 }
	 
	 

}
