package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.google.common.collect.Range;

public class RuoShi extends ExternalNewsType 
{

	public RuoShi(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<com.exchangeinfomanager.News.InsertedNews.Label> labels, String newsUrl)
	{
		super(node, description, starttime, endtime, detail, keywords, labels, newsUrl);
		
//		Range<LocalDate> range;
//		try {
//			range = Range.closed(starttime, endtime);
//		} catch (java.lang.IllegalArgumentException e) {
//			range = Range.closed(endtime, starttime);
//		}
		((TDXNodes)node).addNewRuoShiRange (starttime,endtime);
	}

	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RuoShi gz = (RuoShi) o;
        
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
