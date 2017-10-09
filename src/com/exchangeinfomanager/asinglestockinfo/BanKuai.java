package com.exchangeinfomanager.asinglestockinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.commonlib.CommonUtility;

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
	private Date cursavedstocklisdate;
	
	public Date getCurrentSavedStockListDate ()
	{
		return this.cursavedstocklisdate ;
	}
    public boolean checkSavedStockListIsTheSame ( Date checkdate)
    {
    	if(this.cursavedstocklisdate != null)
    		return CommonUtility.getWeekNumber( this.cursavedstocklisdate) ==  CommonUtility.getWeekNumber(checkdate);
    	else 
    		return false;
    	
    }
	
	 /**
	 * @return the tmpallbkge
	 */
	public HashMap<String, Stock> getBanKuaiGeGu() {
		return allbkge;
	}
	/**
	 * @param tmpallbkge the tmpallbkge to set
	 */
	public void setBanKuaiGeGu(HashMap<String, Stock> tmpallbkge) 
	{
		this.allbkge = tmpallbkge;
		
		if(allbkge == null  )
			return;
		if(allbkge.isEmpty()  )
			return;
		
		//存放板块当前的股票list的日期，以后可以毕竟，如果一样，就直接去不用到数据库里找
		Random       random    = new Random();
		List<String> keys      = new ArrayList<String>(this.allbkge.keySet());
		String lastkey = keys.get(keys.size()-1);
		Stock stock = this.allbkge.get(lastkey );
		ArrayList<ChenJiaoZhanBiInGivenPeriod> cjelist = stock.getChenJiaoErZhanBiInGivenPeriod();
		ChenJiaoZhanBiInGivenPeriod records = cjelist.get(cjelist.size()-1);
		this.cursavedstocklisdate = records.getDayofEndofWeek();
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
