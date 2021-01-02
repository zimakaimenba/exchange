package com.exchangeinfomanager.commonlib;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;

public class UserSelectingForMultiSameCodeNode 
{
	private BanKuaiAndStockTree bkstktree;
	private BkChanYeLianTreeNode userselectednode;
	
	public UserSelectingForMultiSameCodeNode (String nodecode)
	{
		bkstktree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		BkChanYeLianTreeNode nodeshouldbedisplayed1 = (TDXNodes)bkstktree.getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		BkChanYeLianTreeNode nodeshouldbedisplayed2 = (TDXNodes)bkstktree.getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		
		if(nodeshouldbedisplayed1 != null  && nodeshouldbedisplayed2 != null) { //板块和个股都有该代码出现
			List<BkChanYeLianTreeNode> nodeslist = new ArrayList<> ();
			nodeslist.add(nodeshouldbedisplayed1);
			nodeslist.add(nodeshouldbedisplayed2);
			
			SelectMultiNode userselection = new SelectMultiNode(nodeslist);
			 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "请选择", JOptionPane.OK_CANCEL_OPTION);
			 if(exchangeresult == JOptionPane.CANCEL_OPTION)
				 userselectednode = null;
			 
			 try {
				 int userselected = userselection.getUserSelection();
				 userselectednode = (TDXNodes)nodeslist.get(userselected);
			 } catch (java.lang.ArrayIndexOutOfBoundsException e) { //用户没有选择直接回车的情况
				 userselectednode = (TDXNodes)nodeslist.get(0);
			 }
			
		} else 
		if(nodeshouldbedisplayed1 != null  ) {
			userselectednode = nodeshouldbedisplayed1;
		} else
		if (nodeshouldbedisplayed2 != null) {
			userselectednode = nodeshouldbedisplayed2;
		} else
		if(nodeshouldbedisplayed1 == null  && nodeshouldbedisplayed2 == null)  
			userselectednode =  null;
	}
	
	public TDXNodes getUserSelectedNode ()
	{
		return (TDXNodes)userselectednode;
	}

}
