package com.exchangeinfomanager.labelmanagement;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;

public class InsertedTag extends Tag
{


    private int tagid;

    public InsertedTag(Tag label, int tagid, int matchid) {
        super(label.getName(), label.getColor() );
        this.tagid = tagid;
    }

//    public Tag getLabel() {
//        return new Tag(getName());
//    }

    public void setTag(Tag label) {
        setName(label.getName());
        setColor(label.getColor());
        setActive(label.isActive());
    }

    public int getID() {
        return this.id;
    }


}
