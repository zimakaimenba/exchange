package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.google.common.collect.Range;

public class QiangShi extends ExternalNewsType 
{

	public QiangShi(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<InsertedNews.Label> labels, String newsUrl) 
	{
		super(node, description , starttime, endtime, detail, keywords, labels, newsUrl);
		
		Range<LocalDate> range;
		try {
			range = Range.closed(starttime, endtime);
		} catch (java.lang.IllegalArgumentException e) {
			range = Range.closed(endtime, starttime);
		}
		((TDXNodes)node).addNewQiangShiRange (range);
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
