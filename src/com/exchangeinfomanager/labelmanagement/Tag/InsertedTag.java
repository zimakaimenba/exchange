package com.exchangeinfomanager.labelmanagement.Tag;

import java.awt.Color;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;

public class InsertedTag extends Tag
{
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

}
