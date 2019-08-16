package com.exchangeinfomanager.commonlib.jstockcombobox;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;

public class JStockComboBoxModel<BkChanYeLianTreeNode> extends DefaultComboBoxModel 
{
	 private ArrayList<BkChanYeLianTreeNode> nodelist;
	 private AllCurrentTdxBKAndStoksTree allbkstock;

	 BkChanYeLianTreeNode selection = null;
	
	  
	  public JStockComboBoxModel ()
	  {
//		  allbkstock = AllCurrentTdxBKAndStoksTree.getInstance();
		  nodelist = new ArrayList<BkChanYeLianTreeNode> ();
	  }

	  public void addElement (Object node)
	  {
		  if(!nodelist.contains(node))
			  this.nodelist.add((BkChanYeLianTreeNode) node);
		  
		  
	  }
	  public Object getElementAt(int index) 
	  {
//		  String nodecode = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) nodelist.get(index)).getMyOwnCode();
//		  String nodename = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) nodelist.get(index)).getMyOwnName();
//	      
//		  return nodecode + nodename;
		  
		  com.exchangeinfomanager.nodes.BkChanYeLianTreeNode selected =  (com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) (nodelist.get(index));
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
			  String tmpnodecode = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) tmpnode).getMyOwnCode()
//					  				+ ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) tmpnode).getMyOwnName()
					  				;
			  try {
				  String itemcode = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) anItem).getMyOwnCode();
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
			  String nodefull = ((com.exchangeinfomanager.nodes.BkChanYeLianTreeNode) tmpnode).getMyOwnCode() ;
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