package com.exchangeinfomanager.Services;

import java.util.List;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;

public interface ServicesOfChanYeLian 
{
	public List<BkChanYeLianTreeNode> getNodeChanYeLian ();
	public List<BkChanYeLianTreeNode> getNodeChanYeLianChildren ();
	public List<BkChanYeLianTreeNode> getNodeChanYeLianSibling ();
}
