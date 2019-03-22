package com.exchangeinfomanager.nodes;

import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;


public class SubGuPiaoChi extends BkChanYeLianTreeNode
{
	public SubGuPiaoChi(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BkChanYeLianTreeNode.SUBGPC;
		
		super.nodetreerelated = new NodesTreeRelated (this);
	}



}
