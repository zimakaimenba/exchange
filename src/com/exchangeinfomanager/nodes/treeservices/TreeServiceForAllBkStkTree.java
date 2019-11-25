package com.exchangeinfomanager.nodes.treeservices;

import java.util.List;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TreeServiceForAllBkStkTree implements TreeServices 
{
	private TreeOfAllBkStk allbkstk;

	public TreeServiceForAllBkStkTree ()
	{
		allbkstk = new TreeOfAllBkStk ();
	}
	
	public BkChanYeLianTreeNode getSpecificBanKuaiOrStock (String nodecode, int nodetype)
	{
		this.allbkstk.
	}

	@Override
	public List<BkChanYeLianTreeNode> getAllTDXBanKuai() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BkChanYeLianTreeNode> getAllTDXStock() {
		// TODO Auto-generated method stub
		return null;
	}
}
