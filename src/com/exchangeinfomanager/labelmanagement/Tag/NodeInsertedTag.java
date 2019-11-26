package com.exchangeinfomanager.labelmanagement.Tag;

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

}
