package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.NodeChenJiaoErComparator;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.HanYuPinYing;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;


public class BanKuaiInfoTableModel extends BandKuaiAndGeGuTableBasicModel 
{

	public BanKuaiInfoTableModel(Properties prop) 
	{
		super (prop);
	}
	
	
	/*
	 * 
	 */
	public void refresh  (LocalDate curselectdate,int difference2, String period)
	{
		super.showwknum = curselectdate;
		super.difference = difference2;
		super.curperiod = period;

		try{
			if(entryList != null) //∞¥≥…Ωª∂Ó≈≈–Ú
				Collections.sort(entryList, new NodeChenJiaoErComparator(showwknum,difference,curperiod) );
		} catch (java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
		}
	 	
		this.fireTableDataChanged();
	}

	public void addBanKuai ( BanKuai bankuai)
	{
		if(entryList == null)
			entryList = new ArrayList<BkChanYeLianTreeNode> ();
		
		entryList.add(bankuai);
	}
	public void addBanKuai ( List<BkChanYeLianTreeNode> bankuaiwithcje)
	{
		if(entryList == null)
			entryList = new ArrayList<BkChanYeLianTreeNode> ();
		
		entryList.addAll( bankuaiwithcje );
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
	
}


