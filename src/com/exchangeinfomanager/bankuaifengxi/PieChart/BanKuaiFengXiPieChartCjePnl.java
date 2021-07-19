package com.exchangeinfomanager.bankuaifengxi.PieChart;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;


public class BanKuaiFengXiPieChartCjePnl extends BanKuaiFengXiPieChartPnl 
{
	private int pianyiliang;

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiPieChartCjePnl(int pianyiliang) 
	{
		super ();
		this.pianyiliang = pianyiliang;
	}
	@Override
	public void updateDate (TDXNodes node, LocalDate date, int difference,String period) 
	{
		if(period.equals(NodeGivenPeriodDataItem.DAY))
			date = date.plus(difference+pianyiliang,ChronoUnit.DAYS);
		else if(period.equals(NodeGivenPeriodDataItem.WEEK))
			date = date.plus(difference+this.pianyiliang,ChronoUnit.WEEKS);
		else if(period.equals(NodeGivenPeriodDataItem.MONTH))
			date = date.plus(difference+this.pianyiliang,ChronoUnit.MONTHS);
		
		setBanKuaiCjeNeededDisplay(node,date,period);
	}
	
	private void setBanKuaiCjeNeededDisplay (TDXNodes bankuai,LocalDate weeknumber, String period) 
	{
		this.curdisplaybk = bankuai;
		this.displayedweeknumber = weeknumber;
		
		Set<BkChanYeLianTreeNode> tmpallbkge = ((BanKuai)bankuai).getSpecificPeriodStockOfBanKuai(weeknumber,0);
		
		piechartdataset.clear();
		super.piechart.setNotify(false);
    	
    	if(tmpallbkge == null || tmpallbkge.isEmpty())
    		return;
    	
    	for ( BkChanYeLianTreeNode tmpstock : tmpallbkge) {
    		String ggcode = tmpstock.getMyOwnCode();
    		String stockname = tmpstock.getMyOwnName();
    		
    		//�ҵ���Ӧ�ܵ�����
    		NodeXPeriodData stockxdataforbk = ((StockOfBanKuai)tmpstock).getStock().getNodeXPeroidData(period);
    		Double cje = stockxdataforbk.getChengJiaoEr(weeknumber);
    		if(cje != null) {
       	    	if(stockname != null)
        	    		piechartdataset.setValue(ggcode+stockname,cje);
       	    	else 
        	    		piechartdataset.setValue(ggcode,cje);
    		}
    	}
    	
    	super.piechart.setNotify(true);

		setPanelTitle ("�ɽ���ռ��");
		
	}

	


}

