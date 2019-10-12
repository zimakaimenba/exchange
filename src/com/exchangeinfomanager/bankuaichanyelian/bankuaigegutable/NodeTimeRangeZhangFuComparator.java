package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.time.LocalDate;
import java.util.Comparator;

import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

public class NodeTimeRangeZhangFuComparator implements Comparator<TDXNodes> 
{
	private String period;
	private LocalDate startDate;
	private LocalDate endtDate;
	
	public NodeTimeRangeZhangFuComparator (LocalDate startDate, LocalDate endDate, String period )
	{
		this.period = period;
		this.startDate = startDate;
		this.endtDate = endDate;
	}
	
    public int compare(TDXNodes node1, TDXNodes node2) 
    {
    	NodeXPeriodData node1data = ((StockOfBanKuai)node1).getStock().getNodeXPeroidData(period);
    	Double node1zhangfu = null;
    	if(node1data != null)
    		node1zhangfu = node1data.getSpecificTimeRangeOHLCHightestZhangFu (startDate, endtDate );
    	
    	NodeXPeriodData node2data = ((StockOfBanKuai)node2).getStock().getNodeXPeroidData(period);
    	Double node2zhangfu = null;
    	if(node2data != null)
    		node2zhangfu = node2data.getSpecificTimeRangeOHLCHightestZhangFu (startDate, endtDate );
    	
        try{
        	return node2zhangfu.compareTo(node1zhangfu);
        } catch (java.lang.NullPointerException e) {
        	return -1;
        }
    }
}