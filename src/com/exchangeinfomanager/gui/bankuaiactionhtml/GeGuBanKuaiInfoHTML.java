package com.exchangeinfomanager.gui.bankuaiactionhtml;

import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.database.StockDbOperations;

public class GeGuBanKuaiInfoHTML {

	public GeGuBanKuaiInfoHTML(ASingleStockInfo stockbasicinfo2) 
	{
		this.stockbasicinfo = stockbasicinfo2;
		this.stockcode = stockbasicinfo.getStockcode();
	}
	
	private String stockcode;
	private ASingleStockInfo stockbasicinfo;
	private StockDbOperations stockdbopt;
	
	private void getStockSuoSuBanKuai ()
	{
		stockdbopt.getTDXBanKuaiInfo (stockbasicinfo); //通达信板块信息
	}

}
