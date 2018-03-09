package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.Stock;

public interface BarChartPanelDataChangedListener 
{
	void updatedDate (BanKuai bankuai,Stock stock,LocalDate data, String period);

}
