package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.time.LocalDate;
import java.util.Comparator;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;

/*
 * 
 */
class NodeChenJiaoErComparator implements Comparator<BkChanYeLianTreeNode> {
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeChenJiaoErComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(BkChanYeLianTreeNode node1, BkChanYeLianTreeNode node2) {
   	
        Double cje1 = (node1.getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference) ;
        Double cje2 = (node2.getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference);
        
        try{
        	return cje2.compareTo(cje1);
        } catch (java.lang.NullPointerException e) {
        	return -1;
        }
    }
}