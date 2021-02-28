package com.exchangeinfomanager.nodes;

import java.sql.SQLException;
import java.time.LocalDate;

public interface ServicesForNodeBanKuai 
{
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException;
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai);
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk,String period);
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode,String period);
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode,String period);
	public BkChanYeLianTreeNode updateBanKuaiBasicOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg);
}
