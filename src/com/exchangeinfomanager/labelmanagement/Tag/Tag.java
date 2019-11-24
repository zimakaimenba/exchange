package com.exchangeinfomanager.labelmanagement.Tag;

import java.awt.Color;
import java.util.ArrayList;

import com.exchangeinfomanager.nodes.HanYuPinYing;


public class Tag 
{
    private Color color;
    private String name;

    public Tag(  String name, Color color) {
        this.color = color;
        this.name = name;
    }
    
    public Boolean checkHanYuPinYing (String checkedhypy)
    {	
    	HanYuPinYing hypy = new HanYuPinYing ();
	
		String namehypy = hypy.getQuanBuOfPinYin(this.name );
		String souzimuhypy = hypy.getSouZiMuOfPinYin(this.name);
		
		if(this.name.equals(checkedhypy) )
			return true;
		
		if(namehypy.contains(checkedhypy) || souzimuhypy.contains(checkedhypy) )
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
        this.name = name;
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

        return true;
    }

    @Override
    public int hashCode() {
    	
        int result = color.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }


}
