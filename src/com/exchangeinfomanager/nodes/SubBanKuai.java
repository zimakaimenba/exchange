package com.exchangeinfomanager.nodes;

import java.util.HashSet;

import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;


public class SubBanKuai extends BkChanYeLianTreeNode 
{

	public SubBanKuai( String myowncode1,String name) {
		super(myowncode1,name);
		nodetype = BkChanYeLianTreeNode.SUBBK;
		
		super.nodetreerelated = new NodesTreeRelated (this);
	}
	
}
