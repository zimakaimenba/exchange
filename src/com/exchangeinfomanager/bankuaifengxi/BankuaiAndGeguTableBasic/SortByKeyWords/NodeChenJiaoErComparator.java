package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords;

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
	
	public NodeChenJiaoErComparator (LocalDate compareDate,String period )
	{
		this.period = period;
		this.compareDate = compareDate;
	}
    public int compare(BkChanYeLianTreeNode node1,  BkChanYeLianTreeNode node2) { 
    	
    	Double cje1 = null ;
        Double cje2 = null;
        try {
        	if(node1.getType() == BkChanYeLianTreeNode.BKGEGU) {
        		Stock node1stock = ((StockOfBanKuai)node1).getStock();
        		cje1 = (node1stock.getNodeXPeroidData( period)).getChengJiaoEr(compareDate ) ;
        	} else {
        		cje1 = ( ((TDXNodes)node1).getNodeXPeroidData( period)).getChengJiaoEr(compareDate ) ;
        	}
        	
        	if(node2.getType() == BkChanYeLianTreeNode.BKGEGU){
        		Stock node2stock = ((StockOfBanKuai)node2).getStock();
                cje2 = (node2stock.getNodeXPeroidData( period)).getChengJiaoEr(compareDate);
                
        	} else {
        		cje2 = ( ((TDXNodes)node2).getNodeXPeroidData( period)).getChengJiaoEr(compareDate);
        	}
        } catch (java.lang.NullPointerException e) {
        	e.printStackTrace();
        	return -1;
        }

        try{
        	return cje2.compareTo(cje1);
        } catch (java.lang.NullPointerException e) { 	return -1;
    	} catch ( java.lang.ClassCastException e) {	return -1;
    	} catch ( java.lang.IllegalArgumentException e) {	return -1; 
    	}
    }
}