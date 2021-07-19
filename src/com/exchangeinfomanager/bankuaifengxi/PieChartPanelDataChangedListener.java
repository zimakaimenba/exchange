package com.exchangeinfomanager.bankuaifengxi;

import java.time.LocalDate;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;

public interface PieChartPanelDataChangedListener 
{

	void updateDate(TDXNodes selectedbk, LocalDate curselectdate, int i, String globeperiod);
	void hightlightSpecificSector (String nodecodename);

}
