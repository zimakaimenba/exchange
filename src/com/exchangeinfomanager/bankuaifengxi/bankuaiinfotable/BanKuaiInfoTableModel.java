package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.HanYuPinYing;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeChenJiaoErComparator;


public class BanKuaiInfoTableModel extends BandKuaiAndGeGuTableBasicModel 
{

	private BanKuai intersectionbk;

	public BanKuaiInfoTableModel(Properties prop) 
	{
		super (prop);
	}
	/*
	 * 
	 */
	public void refresh  (LocalDate curselectdate, String period )
	{
		super.showwknum = curselectdate;
		super.curperiod = period;
		super.curbk =  (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		
		try{ if(entryList != null) //按成交额排序
				Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,curperiod) );
		} catch (java.lang.IllegalArgumentException e) {//			e.printStackTrace();
		}
	 	
		this.fireTableDataChanged();
	}
	
	public void refresh  (List<BkChanYeLianTreeNode> bklist,LocalDate curselectdate, String period )
	{
		super.entryList = bklist;
		super.showwknum = curselectdate;
		super.curperiod = period;
		super.curbk = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		
		try{ if(entryList != null) //按成交额排序
				Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,curperiod) );
		} catch (java.lang.IllegalArgumentException e) {//			e.printStackTrace();
		}
	 	
		this.fireTableDataChanged();
	}
	public void addBanKuai ( BanKuai bankuai)
	{
		if(entryList == null)	entryList = new ArrayList<BkChanYeLianTreeNode> ();
		if(!entryList.contains(bankuai))
			entryList.add(bankuai);
	}
	public void addBanKuai ( List<BkChanYeLianTreeNode> bankuaiwithcje)
	{
		if(entryList == null)	entryList = new ArrayList<BkChanYeLianTreeNode> ();
		
		for(BkChanYeLianTreeNode tmpnode : bankuaiwithcje) 
			if(!this.entryList.contains(tmpnode) )
				entryList.add(tmpnode);
	}
	public Object getValueAt(int rowIndex, int columnIndex) 
    {
		Object value = super.getValueAt(rowIndex, columnIndex);
    	return value;
    }
	 public Class<?> getColumnClass(int columnIndex) { 
		 Class<?> clazz = super.getColumnClass(columnIndex);
	      
	      return clazz;
	 }
	 
	 public BanKuai getInterSectionBanKuai ()
	 {
		 return this.intersectionbk;
	 }
	 public void setInterSectionBanKuai (BanKuai insbk)
	 {
		 this.intersectionbk = insbk;
	 }
}


