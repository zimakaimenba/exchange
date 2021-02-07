package com.exchangeinfomanager.Services;

import java.sql.SQLException;
import java.time.LocalDate;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.StockOfBanKuai;

public interface ServicesForNodeBanKuai 
{
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException;
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai);
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk,String period);
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode,String period);
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode,String period);
}
