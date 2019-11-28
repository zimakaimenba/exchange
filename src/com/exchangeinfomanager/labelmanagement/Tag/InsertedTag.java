package com.exchangeinfomanager.labelmanagement.Tag;

import java.util.Set;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class InsertedTag extends Tag
{
	private Set<BkChanYeLianTreeNode> owners ;
	private int id;
	private Tag tag;
	private String des;
	

    public InsertedTag(Tag label, Integer id) 
    {
    	super(label.getName(), label.getColor() );
    	this.tag = label;
        this.id = id;
	}
	
	public Tag getTag ()
	{
		return tag;
	}

    public int getID() {
        return this.id;
    }
    
    public void setDescription (String des)
    {
    	this.des = des;
    }
    public String getDescription ()
    {
    	return this.des ;
    } 
    
    public void addOwners (BkChanYeLianTreeNode newowner)
    {
    	this.owners.add(newowner);
    }
    public Set<BkChanYeLianTreeNode> getOwners ()
    {
    	return this.owners;
    }
    public boolean checkerOwner (String checkowner)
    {
    	for(BkChanYeLianTreeNode node : this.owners) {
    		String ownername = node.getMyOwnCode();
    		String ownercode = node.getMyOwnCode();
    		
    		if(ownername.equals(checkowner) || ownercode.equals(checkowner) )
    			return true;
    	}
    	
    	return false;
    }
    
}
