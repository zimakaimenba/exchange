package com.exchangeinfomanager.nodes;

import java.time.LocalDate;

import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;

public class CylTreeNestedSetNode extends BkChanYeLianTreeNode 
{

	public CylTreeNestedSetNode(String myowncode, String name, Integer type) 
	{
		super(myowncode, name);
		super.nodetype = type;
		
		super.nodetreerelated = new BanKuaiTreeRelated (this);
	}
	
	private int nestedid;
	private int nestedleft;
	private int nestedright;
	private int nestedparent;
	
	private LocalDate isolated;
	
	public void setNodeIsolatedDate (LocalDate iso)
	{
		this.isolated = iso;
	}
	public LocalDate getNodeIsolatedDate ()
	{
		return this.isolated;
	}
	public NodesTreeRelated getNodeTreeRelated ()
	{
		return this.nodetreerelated;
	}
	
	public void setNestedId(int nestedid) {
		this.nestedid = nestedid;
	}
	public int getNestedId() {
		return this.nestedid ;
	}

	public int getNestedLeft() {
		return nestedleft;
	}

	public void setNestedLeft(int nestedleft) {
		this.nestedleft = nestedleft;
	}

	public int getNestedRight() {
		return nestedright;
	}

	public void setNestedRight(int nestedright) {
		this.nestedright = nestedright;
	}
	public void setNestedParent (int parent)
	{
		this.nestedparent = parent;
	}
	public int getNestedParent ()
	{
		return this.nestedparent;
	}

	
}
