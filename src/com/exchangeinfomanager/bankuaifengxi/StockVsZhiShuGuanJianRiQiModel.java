package com.exchangeinfomanager.bankuaifengxi;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.DefaultTableModel;


import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;


public class StockVsZhiShuGuanJianRiQiModel  extends DefaultTableModel 
{
	private List<LocalDate> riqilist;
	private List<String> riqidescplist;

	String[] jtableTitleStrings = { "指数关键日期", "描述","个股涨幅"};
	private ZhiShuBoLangServices svszsbl;
	private TDXNodes curnode;
	private LocalDate displaydate;
	
	public StockVsZhiShuGuanJianRiQiModel (ZhiShuBoLangServices svszsbl1)
	{
		riqilist = new ArrayList<>();
		riqidescplist = new ArrayList<>();
		
		this.svszsbl = svszsbl1;
		
		LocalDate requirestart = CommonUtility.getSettingRangeDate(LocalDate.now(),"large").with(DayOfWeek.MONDAY);
		try {
			Collection<News> zsbl = this.svszsbl.getNews("ALL", requirestart, LocalDate.now());
			for(News tmpzsbl : zsbl) {
				LocalDate startdate = tmpzsbl.getStart();
				String descip = tmpzsbl.getTitle();
				riqilist.add(startdate);
				riqidescplist.add(descip);
				
				LocalDate enddate = ((InsertedExternalNews)tmpzsbl).getEnd();
				if(enddate == null) continue;
				
				LocalDate tmpdate = startdate.plusDays(1);
				while ( tmpdate.isBefore( enddate) || tmpdate.isEqual(enddate)) {
					DayOfWeek dayofweek = tmpdate.getDayOfWeek();
					if(dayofweek.equals(DayOfWeek.SUNDAY) || dayofweek.equals(DayOfWeek.SATURDAY) ) {
						tmpdate = tmpdate.plusDays(1);
						continue;
					}
					
					riqilist.add(tmpdate);
					riqidescplist.add(descip);
					
					tmpdate = tmpdate.plusDays(1);
				} ;
			}
			
		} catch (SQLException e) { e.printStackTrace();}
	}
	
	public void refresh  (TDXNodes node)
	{
		this.curnode = node;
		this.fireTableDataChanged();
	}
	public void setCurHighLightDisplayDate(LocalDate  displaydate1)
	{
		this.displaydate = displaydate1;
	}
	public LocalDate getCurHighLightDisplayDate()
	{
		return this.displaydate;
	}
	 public int getRowCount() 
	 {
		 if(this.riqilist == null)
			 return 0;
		 else if(this.riqilist.isEmpty()  )
			 return 0;
		 else
			 return this.riqilist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(riqilist.isEmpty())
	    		return null;
	    	
	    	if(this.curnode == null)
	    		return null;
	    	
	    	Object value = "??";
	    	
	    	LocalDate gjrq = riqilist.get(rowIndex);
	    	switch (columnIndex) {
            case 0:
                value = riqilist.get(rowIndex);
            break;
            case 1:
            	value = riqidescplist.get(rowIndex); 
            break;
            case 2:
            	NodeXPeriodData nodexdataday = this.curnode.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
            	Double zhangdiefu = nodexdataday.getSpecificOHLCZhangDieFu(gjrq);
            	if(zhangdiefu != null) {
            		DecimalFormat decimalformate = new DecimalFormat("%#0.00000");
                	String formatresult = decimalformate.format((Double)zhangdiefu);
                	value = formatresult;
            	} else value = null;
            break;
            }

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = LocalDate.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
//	    @Override
//	    public Class<?> getColumnClass(int columnIndex) {
//	        return // Return the class that best represents the column...
//	    }
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public void deleteAllRows()
	    {
//	    	if(riqidescplist != null) 
//	    		this.riqidescplist.clear();
//		    	
//	    	if(riqilist != null)
//	    		riqilist.clear();
	    	
	    	this.curnode = null;
	    	
	    	this.fireTableDataChanged();
	    }
}