package com.exchangeinfomanager.Core.NodesDataServices;

import java.awt.Color;
import java.sql.SQLException;
import java.time.LocalDate;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;

public interface ServicesForNodeBanKuai 
{
	public void syncBanKuaiAndItsStocksForSpecificTime (BanKuai bk,LocalDate requiredstartday,LocalDate requiredendday,String period,Boolean calwholeweek  ) throws SQLException;
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai);
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, StockOfBanKuai stockofbk,String period);
	public StockOfBanKuai getGeGuOfBanKuaiData(BanKuai bankuai, String stockcode,String period);
	public StockOfBanKuai getGeGuOfBanKuaiData(String bkcode, String stockcode,String period);
	public BkChanYeLianTreeNode updateBanKuaiBasicOperationsSettings(BkChanYeLianTreeNode node, boolean importdailydata, boolean exporttogephi, 
			boolean showinbkfx,boolean showincyltree, boolean exporttowkfile, boolean importbkgg, Color color, boolean corezhishu);
	
	public void forcedeleteBanKuaiImportedDailyExchangeData (BanKuai bk);
	public void forcedeleteBanKuaiImportedGeGuData (BanKuai bk);
}
