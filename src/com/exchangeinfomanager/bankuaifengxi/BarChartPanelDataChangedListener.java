package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;

/*
 * 
 */
public interface BarChartPanelDataChangedListener 
{
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate date, int difference,String period);
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate,String period);

}
