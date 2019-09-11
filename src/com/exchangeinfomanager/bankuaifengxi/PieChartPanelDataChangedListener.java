package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;

public interface PieChartPanelDataChangedListener 
{

	void updateDate(TDXNodes selectedbk, LocalDate curselectdate, int i, String globeperiod);
	void hightlightSpecificSector (String nodecodename);

}
