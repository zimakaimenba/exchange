package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords;

import java.time.LocalDate;
import java.util.Comparator;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;

public class NodeZongShiZhiComparator implements Comparator<BkChanYeLianTreeNode> {
	private String period;
	private LocalDate compareDate;
	
	public NodeZongShiZhiComparator (LocalDate compareDate, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
	}
    public int compare(BkChanYeLianTreeNode node1, BkChanYeLianTreeNode node2) {
    	Stock stock1 = ( (StockOfBanKuai)node1).getStock();
    	Stock stock2 = ( (StockOfBanKuai)node2).getStock();
    	
        Double cje1 = ((StockNodesXPeriodData)stock1.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(compareDate) ;
        Double cje2 = ((StockNodesXPeriodData)stock2.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(compareDate);
        
        return cje2.compareTo(cje1);
    }
}
