package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;

class NewsTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "新闻标题", "录入日期"};
	private ArrayList<InsertedMeeting> cylnewslists;
	private static Logger logger = Logger.getLogger(BanKuaiAndChanYeLian.class);
	
	NewsTableModel (){
	}
	
	public void refresh  ( Collection<InsertedMeeting> allnewlist)
	{
		this.cylnewslists = new ArrayList<InsertedMeeting>(allnewlist);
		
		this.fireTableDataChanged();
	}

	public int getRowCount() 
	{
		 if(this.cylnewslists == null)
			 return 0;
		 else 
			 return this.cylnewslists.size();
	}

	    @Override
	public int getColumnCount() 
	{
	        return jtableTitleStrings.length;
	} 
	
	public InsertedMeeting getNews (int row) 
	{
		return this.cylnewslists.get(row);
	}
	    
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
	    	if(this.cylnewslists.isEmpty())
	    		return null;
	    	
	    	InsertedMeeting tmpcylnews = this.cylnewslists.get(rowIndex);
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = tmpcylnews.getTitle();
                break;
            case 1:
            	value = tmpcylnews.getStart();
                break;
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
		      }
		      
		      return clazz;
	 }
	    
	 public String getColumnName(int column) 
	 { 
	    	return jtableTitleStrings[column];
	 }//设置表格列名 
		

	 
	 public InsertedMeeting getChanYeLianNews (int row) 
	 {
	    	return cylnewslists.get(row) ;
	 }
 
	 public void deleteAllRows()
	 {
	    	if(this.cylnewslists == null)
				 return ;
			 else 
				 cylnewslists.clear();
	 }
}
