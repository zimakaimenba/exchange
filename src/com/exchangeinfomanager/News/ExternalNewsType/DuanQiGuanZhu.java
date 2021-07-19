package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.google.common.collect.Range;


public class DuanQiGuanZhu extends ExternalNewsType
{
	private String guanzhutype;
	private String gpc;

	public DuanQiGuanZhu(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<com.exchangeinfomanager.News.InsertedNews.Label> labels, String newsUrl) 
	{
		super(node, description, starttime, endtime, detail, keywords, labels, newsUrl);
		
//		Range<LocalDate> range;
//		try {
//			range = Range.closed(starttime, endtime);
//		} catch (java.lang.IllegalArgumentException e) {
//			range = Range.closed(endtime, starttime);
//		}
		((TDXNodes)node).addNewDuanQiGuanZhuRange (starttime,endtime);
	}
	
	public void setDqgzGuPiaoChi (String gpc)
	{
		this.gpc = gpc;
	}
	public String getDqgzGuPiaoChi ()
	{
		return this.gpc ;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DuanQiGuanZhu gz = (DuanQiGuanZhu) o;
        
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
