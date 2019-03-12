package com.exchangeinfomanager.nodes;

import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;

public class GuPiaoChi extends BkChanYeLianTreeNode
{
	public GuPiaoChi(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BkChanYeLianTreeNode.GPC;
		
		super.nodetreerelated = new NodesTreeRelated (this);
	}

}
