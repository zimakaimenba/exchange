package com.exchangeinfomanager.bankuaifengxi.PieChart;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;







import com.exchangeinfomanager.nodes.BanKuai;

import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;


public class BanKuaiFengXiPieChartCjlPnl extends BanKuaiFengXiPieChartPnl 
{
	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiPieChartCjlPnl() 
	{
		super ();
	}
	
	public void updatedDate(TDXNodes node, LocalDate date, int difference, String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.DAY))
			date = date.plus(difference,ChronoUnit.DAYS);
		else if(period.equals(NodeGivenPeriodDataItem.WEEK))
			date = date.plus(difference,ChronoUnit.WEEKS);
		else if(period.equals(NodeGivenPeriodDataItem.MONTH))
			date = date.plus(difference,ChronoUnit.MONTHS);
		
		setBanKuaiCjeNeededDisplay((BanKuai)node,10,date,period);
	}
	
	public void setBanKuaiCjeNeededDisplay (BanKuai bankuai,int weightgate,LocalDate weeknumber,String period) 
	{
		
		
	}

	@Override
	public void updateDate(TDXNodes selectedbk, LocalDate curselectdate, int i, String globeperiod) {
		// TODO Auto-generated method stub
		
	}

}

