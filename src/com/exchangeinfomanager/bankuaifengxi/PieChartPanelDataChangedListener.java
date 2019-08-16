package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.nodes.BanKuai;

public interface PieChartPanelDataChangedListener 
{

	void updateDate(BanKuai selectedbk, LocalDate curselectdate, int i, String globeperiod);

}
