package com.exchangeinfomanager.bankuaifengxi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;

public class NodeChenJiaoErComparator implements Comparator<TDXNodes> 
{
	private String period;
	private LocalDate compareDate;
	private int difference;
	private SvsForNodeOfStock svsstk;
	private SvsForNodeOfBanKuai svsbk;
	
	public NodeChenJiaoErComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
		
		this.svsstk = new SvsForNodeOfStock  ();
		this.svsbk = new SvsForNodeOfBanKuai  ();
	}
    public int compare(TDXNodes node1, TDXNodes node2) {
    	try {
	        Double cje1 = node1.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference) ;
	        if(cje1 == null) 
	        	cje1 = this.getNodeChenJiaoEr(node1); 
	        
	        Double cje2 = node2.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference);
	        if(cje2 == null) 
	        	cje2 = this.getNodeChenJiaoEr(node1); 
	        	return cje2.compareTo(cje1);
    	}catch (Exception e) {//    		e.printStackTrace();
        	return 1;
        }
    }
    private Double getNodeChenJiaoEr (TDXNodes node) 
    {
    	LocalDate requirestart = CommonUtility.getSettingRangeDate(compareDate,"middle").with(DayOfWeek.MONDAY);
    	if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
    		node = (BanKuai) svsbk.getNodeData( node,requirestart,compareDate,period,true);
    	} else if(node.getType() == BkChanYeLianTreeNode.TDXGG) {
    		node = (Stock) svsstk.getNodeData( node,requirestart,compareDate,period,true);
    	}
    	
    	Double cje = node.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference);
    	
    	return cje;
    }
}


