package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;

public interface PieChartPanelDataChangedListener 
{
	

	public void updateDate(TDXNodes node, LocalDate date, int difference,String period);

}
