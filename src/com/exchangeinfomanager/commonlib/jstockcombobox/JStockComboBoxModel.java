package com.exchangeinfomanager.commonlib.jstockcombobox;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;

public class JStockComboBoxModel<BkChanYeLianTreeNode> extends DefaultComboBoxModel 
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<BkChanYeLianTreeNode> nodelist;

	BkChanYeLianTreeNode selection = null;
	private int onlyselectnodetype;
	private LocalDate curdisplaydate;
	
	  
	  public JStockComboBoxModel (int onlyselectnodetype)
	  {
		  nodelist = new ArrayList<BkChanYeLianTreeNode> ();
		  this.onlyselectnodetype = onlyselectnodetype;
	  }

	  public void setCurrentDataDate (LocalDate date)
	  {
		  this.curdisplaydate = date;
	  }
	  public LocalDate getCurrentDataDate ()
	  {
		  return this.curdisplaydate;
	  }
	  public void addElement (Object node)
	  {
		  int nodetype = ((com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode) node).getType();
		  if( this.onlyselectnodetype != -1 && nodetype != this.onlyselectnodetype   )
			  return ;
					  
		  if(!nodelist.contains(node))
			  this.nodelist.add(  (BkChanYeLianTreeNode) node);
	  }
	  public Object getElementAt(int index) 
	  {
//		  String nodecode = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) nodelist.get(index)).getMyOwnCode();
//		  String nodename = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) nodelist.get(index)).getMyOwnName();
//	      
//		  return nodecode + nodename;
		  
		  com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode selected =  (com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode) (nodelist.get(index));
		  return selected;
	  }

	  public int getSize() 
	  {
		  if(nodelist == null)
			  return 0;
		  else
			  return nodelist.size();
	  }

	  public void setSelectedItem(Object anItem) 
	  {
		  for(BkChanYeLianTreeNode tmpnode : nodelist) {
			  String tmpnodecode = ((com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode) tmpnode).getMyOwnCode()
					  				;
			  try {
				  String itemcode = ((com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode) anItem).getMyOwnCode();
				  if(tmpnodecode.equals(itemcode)) {
					  selection = tmpnode;
					  break;
				  }
			  } catch (java.lang.ClassCastException e) {
				  if(tmpnodecode.equals(anItem.toString())) {
					  selection = tmpnode;
					  break;
				  }
			  }
		  }
		 
		  this.fireIntervalAdded(this, nodelist.size(), nodelist.size());
	    
	  } // item from the pull-down list

	  // Methods implemented from the interface ComboBoxModel
	  public Object getSelectedItem() 
	  {
	    return selection; // to add the selection to the combo box
	    
	  }

	public Integer hasTheNode(String nodecode) 
	{
		int i=0;
		for(BkChanYeLianTreeNode tmpnode : nodelist) {
			  String nodefull = ((com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode) tmpnode).getMyOwnCode() ;
			  if(nodefull.equals( nodecode   )  ) {
				  selection = tmpnode;
				  this.setSelectedItem(selection);
				  return i;
			  } else
				  i++;
		}
		
		return -1;
	}
}