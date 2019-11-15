package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;

public class TDXNodesInformationTableModle extends AbstractTableModel implements InformationTableModelBasic
{
	String[] jtableTitleStrings = { "新闻标题", "录入日期"};
	private ArrayList<InsertedMeeting> cylnewslists;
	
	public void refresh  ( Collection<InsertedMeeting> allnewlist)
	{
		this.cylnewslists = new ArrayList<InsertedMeeting> ();
		for(InsertedMeeting meeting : allnewlist) {
			if(!meeting.getTitle().equals("一周总结") ) //一周总结不需要出现在新闻列表里面
				this.cylnewslists.add(meeting);
		}
		
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
		

	 
	 public InsertedMeeting getRequiredInformationOfTheRow (int row) 
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

	@Override
	public Integer getMainInforTypeForCreating() 
	{
		return Meeting.NODESNEWS;
	}

	@Override
	public InsertedMeeting getLastestInformation() 
	{
		if(cylnewslists.isEmpty())
			return null;
		else
			return cylnewslists.get( cylnewslists.size()-1);
	}
}
