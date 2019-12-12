package com.exchangeinfomanager.TagManagment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;

public class TagSearchOnNewsTableModel extends DefaultTableModel 
{
	private List<InsertedNews> info;
	String[] jtableTitleStrings = { "选择","关键词", "新闻","详细信息"};
	private Set<Integer> selectedrowindex ;
	
	public TagSearchOnNewsTableModel ()
	{
		selectedrowindex = new HashSet<> ();
	}
	
	public void refresh (Collection<News> searchresult)
	{
		if (searchresult instanceof List)
		  info = (List)searchresult;
		else
		  info = new ArrayList(searchresult);

		selectedrowindex.clear();
		for(int i=0;i<info.size();i++)
			selectedrowindex.add(i);

		fireTableDataChanged();
	}
	
	public void unselectAll ()
	{
//		this.selectedrowindex.clear();
//		this.fireTableDataChanged();
	}
	
	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public News getNews (int row)
	    {
	    	return this.info.get(row);
	    }
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(this.info.isEmpty())
	    		return null;
	    	
	    	InsertedNews news = info.get(rowIndex);
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = !selectedrowindex.contains(Integer.valueOf(rowIndex) );
                break;
            case 1:
            	value = news.getKeyWords();
            	break;
            case 2:
            	value = news.getTitle();
                break;
            case 3:
            	value = news.getDescription();
                break;

	    	}

	    	return value;
	    }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = Boolean.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) 
	    {
	    	return  column == 0; 
		}
	    
	    public int getRowCount() 
		{
			 if(this.info == null)
				 return 0;
			 else if(this.info.isEmpty()  )
				 return 0;
			 else
				 return this.info.size();
		}
	    
	    public void setValueAt(Object value, int row, int column)
	    {
	    	if(column == 0 && (Boolean)value == true)
	    		selectedrowindex.remove(row);
	    	else if(column == 0 && (Boolean)value == false)
	    		selectedrowindex.add(row);
	    }
	    public void setRowSelected (int row)
	    {
	    	selectedrowindex.add(row);
	    	this.fireTableDataChanged();
	    }

}
