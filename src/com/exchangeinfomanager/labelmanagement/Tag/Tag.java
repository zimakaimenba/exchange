package com.exchangeinfomanager.labelmanagement.Tag;

import java.awt.Color;
import java.util.ArrayList;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.nodes.HanYuPinYing;


public class Tag 
{
    private Color color;
    private String name;
    private boolean selected;

    public Tag(  String name, Color color1) 
    {
    	this.name = name;
    	if(color1 == null)
    		this.color = Color.WHITE;
    	else 
    		this.color = color1;
    }
    
    public Boolean checkHanYuPinYing (String checkedhypy)
    {	
    	HanYuPinYing hypy = new HanYuPinYing ();
	
		String namehypy = hypy.getQuanBuOfPinYin(this.name ).toUpperCase();
		String souzimuhypy = hypy.getSouZiMuOfPinYin(this.name).toUpperCase();
		
		if(this.name.equals(checkedhypy.toUpperCase()) || this.name.contains(checkedhypy.toUpperCase()) )
			return true;
		
		if(namehypy.contains(checkedhypy.toUpperCase()) || souzimuhypy.contains(checkedhypy.toUpperCase()) )
			return true;
		else
			return false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return String.format("name: %s, color: %s", getName(), getColor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (o == null || getClass() != o.getClass())
            return false;

        Tag label = (Tag) o;
        if(!name.equals(label.getName()))
        	return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return result;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean active) {
        this.selected = active;
    }
}
