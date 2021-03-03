package com.exchangeinfomanager.TagManagment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagSearchOnNodesTableModel extends DefaultTableModel 
{
	private List<BkChanYeLianTreeNode> info;
	String[] jtableTitleStrings = { "选择", "关键词","代码","名称"};
	private Set<Integer> selectedrowindex ;
	
	public TagSearchOnNodesTableModel ()
	{
		selectedrowindex = new HashSet<> ();
	}
	
	public void refresh (Collection<BkChanYeLianTreeNode> searchresult)
	{
		if (searchresult instanceof List)
		  info = (List)searchresult;
		else
		  info = new ArrayList(searchresult);
		
		selectedrowindex.clear();
		
		fireTableDataChanged();
	}
	
	public Collection<BkChanYeLianTreeNode> getSelectedNodes ()
	{
		Collection<BkChanYeLianTreeNode> selectednode = new HashSet<> ();
		for(int i= 0; i<this.getRowCount() ; i++) {
			Boolean result = (Boolean) this.getValueAt(i, 0);
			if(result) {
				selectednode.add(this.info.get(i));
			}
		}
		
		return selectednode;
	}
	
	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(this.info.isEmpty())
	    		return null;
	    	
	    	BkChanYeLianTreeNode node = info.get(rowIndex);
	    	TagsServiceForNodes tagsservicesfornode = new TagsServiceForNodes (node);
	    	Collection<Tag> nodetags = null;
			try { nodetags = tagsservicesfornode.getTags();
			} catch (SQLException e) {e.printStackTrace();}
			
	    	String tagslist = " ";
	    	for (Iterator<Tag> it = nodetags.iterator(); it.hasNext(); ) {
	    		Tag t = it.next();
	    		tagslist = tagslist + t.getName() + "  ";
	    	}
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = !selectedrowindex.contains(Integer.valueOf(rowIndex) );
                break;
            case 1:
            	value = tagslist;
            	break;
            case 2:
            	value = node.getMyOwnCode();
                break;
            case 3:
            	value = node.getMyOwnName();
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
	    	if(column == 0 && (Boolean)value == false)
	    		selectedrowindex.add(row);
	    	else if(column == 0 && (Boolean)value == true)
	    		selectedrowindex.remove(row);
	     }

}

