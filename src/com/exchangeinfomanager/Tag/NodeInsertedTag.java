package com.exchangeinfomanager.Tag;

import java.awt.Color;

public class NodeInsertedTag extends InsertedTag
{
	private InsertedTag inserttag;
	private int matchid;
	private String gegubk;
	private Color nodematchcolor;
	
	public NodeInsertedTag (InsertedTag label, int nodematchid, String bkstkcode, Color nodematchcolor)
	{
    	super(label ,label.getID());
    	this.inserttag = label;
        this.matchid = nodematchid;
        this.gegubk = bkstkcode;
        this.nodematchcolor = nodematchcolor;
	}
	
	public Tag getInsertedTag ()
	{
		return inserttag;
	}

    public int getMatchID() {
        return this.matchid;
    }
    public void setNodeMachColor (Color color)
    {
    	this.nodematchcolor = color;
    }
    public Color getNodeMachColor ()
    {
    	return this.nodematchcolor;
    }
    public String getOwner ()
    {
    	return this.gegubk;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (o == null )
            return false;
        
        if(getClass() != o.getClass())
        return false;
        
        if(!super.equals(o))
        	return false;

//        NodeInsertedTag label = (NodeInsertedTag) o;
//        if( label.getTag() != this.getTag() )
//        	return false;
//        
//        if(label.getInsertedTag() != this.getInsertedTag() )
//        	return false;
//        
//        if( this.matchid != label.getMatchID())
//        	return false;
//        
//        if(this.nodematchcolor != label.getNodeMachColor() )
//        	return false;

        return true;
    }

    @Override
    public int hashCode()
    {
    	return super.hashCode();
//    	int result = nodematchcolor.hashCode();
//        result = 31 * result + super.hashCode();
//        result = result  + this.gegubk.hashCode() ;
//        return result;
    }

}
