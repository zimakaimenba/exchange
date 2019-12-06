package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class GuanZhu extends ExternalNewsType
{
	private String guanzhutype;

	public GuanZhu(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<com.exchangeinfomanager.News.InsertedNews.Label> labels, String newsUrl,Boolean changqiorduanqi) 
	{
		super(node, description, starttime, endtime, detail, keywords, labels, newsUrl);
		
		if(changqiorduanqi)
			this.guanzhutype = "CHANGQI";
		else
			this.guanzhutype = "DUANQI";
	}

	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GuanZhu gz = (GuanZhu) o;
        
        if(!node.equals(gz.getNode()))
        	return false;

        if (!starttime.equals(gz.getStart() ))
            return false;
        
        if(!endtime.equals(gz.getEnd() ))
        	return false;

        return true;
    }

    @Override
    public int hashCode() {
    	int result = 0;

		result = starttime.hashCode();
        result = 31 * result + endtime.hashCode();
        result = 31 * result + node.hashCode();
        
        return result;
    }

	

}
