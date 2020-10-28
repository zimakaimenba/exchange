package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.time.LocalDate;
import java.util.Comparator;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;



/*
 * 
 */
public class NodeChenJiaoErComparator implements Comparator<BkChanYeLianTreeNode> 
{
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeChenJiaoErComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(BkChanYeLianTreeNode node1,  BkChanYeLianTreeNode node2) { 
    	
    	Double cje1 = null ;
        Double cje2 = null;
    	if(node1.getType() == BkChanYeLianTreeNode.BKGEGU) {
    		Stock node1stock = ((StockOfBanKuai)node1).getStock();
    		cje1 = (node1stock.getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference) ;
    	} else {
    		cje1 = ( ((TDXNodes)node1).getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference) ;
    	}
    	
    	if(node2.getType() == BkChanYeLianTreeNode.BKGEGU){
    		Stock node2stock = ((StockOfBanKuai)node2).getStock();
            cje2 = (node2stock.getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference);
            
    	} else {
    		cje2 = ( ((TDXNodes)node2).getNodeXPeroidData( period)).getChengJiaoEr(compareDate, difference);
    	}
       
        try{
        	return cje2.compareTo(cje1);
        } catch (java.lang.NullPointerException e) {
        	return -1;
        }
    }
}