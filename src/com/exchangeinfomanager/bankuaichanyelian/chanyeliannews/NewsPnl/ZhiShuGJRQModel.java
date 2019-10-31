package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ZhiShuGuanJianRiQi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;

public class ZhiShuGJRQModel extends AbstractTableModel
{
	String[] jtableTitleStrings = { "ָ��/������", "��ʼ����","��ֹ����","˵��"};
	private ArrayList<InsertedMeeting> zhishuslists;
	private static Logger logger = Logger.getLogger(ZhiShuGJRQModel.class);
	
	ZhiShuGJRQModel (){
	}
	
	public void refresh  ( Collection<InsertedMeeting> allnewlist)
	{
		if(this.zhishuslists == null) {
			this.zhishuslists = new ArrayList<InsertedMeeting> ();
			this.zhishuslists.addAll(allnewlist);
		}
		else {
			this.deleteAllRows();
			this.zhishuslists.addAll(allnewlist);
		}
		
		this.fireTableDataChanged();
	}

	public int getRowCount() 
	{
		 if(this.zhishuslists == null)
			 return 0;
		 else 
			 return this.zhishuslists.size();
	}

	@Override
	public int getColumnCount() 
	{
	        return jtableTitleStrings.length;
	} 
	
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
	    	if(this.zhishuslists.isEmpty())
	    		return null;
	    	
	    	InsertedMeeting tmpcylnews = this.zhishuslists.get(rowIndex);
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = tmpcylnews.getNewsOwnerCodes();
                break;
            case 1:
            	value = tmpcylnews.getStart();
                break;
            case 2:
            	value = tmpcylnews.getEnd();
                break;
            case 3:
            	value = tmpcylnews.getTitle();
	    	}

	    	return value;
	}

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) 
		      {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		      case 1:
			      clazz = LocalDate.class;
			      break;
		      case 2:
			      clazz = LocalDate.class;
			      break;
		      case 3:
		    	  clazz = String.class;
			      break;
		      }
		      
		      return clazz;
	 }
	    
	 public String getColumnName(int column) 
	 { 
	    	return jtableTitleStrings[column];
	 }//���ñ������ 
		

	 
	 public InsertedMeeting getZhiShuGJRQ (int row) 
	 {
	    	return zhishuslists.get(row) ;
	 }
 
	 public void deleteAllRows()
	 {
	    	if(this.zhishuslists == null)
				 return ;
			 else 
				 zhishuslists.clear();
	 }

}
