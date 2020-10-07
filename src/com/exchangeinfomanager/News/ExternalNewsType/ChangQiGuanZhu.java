package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.google.common.collect.Range;


public class ChangQiGuanZhu  extends ExternalNewsType
{
	private String guanzhutype;
	private String gpc;

	public ChangQiGuanZhu(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<com.exchangeinfomanager.News.InsertedNews.Label> labels, String newsUrl) 
	{
		super(node, description, starttime, endtime, detail, keywords, labels, newsUrl);
		
//		if(changqiorduanqi)
//			this.guanzhutype = "CHANGQI";
//		else
//			this.guanzhutype = "DUANQI";
		
//		Range<LocalDate> range;
//		try {
//			range = Range.closed(starttime, endtime);
//		} catch (java.lang.IllegalArgumentException e) {
//			range = Range.closed(endtime, starttime);
//		}
//		if(changqiorduanqi)
//			;
//		else
//			((TDXNodes)node).addNewDuanQiGuanZhuRange (range);
	}
	
//	public void setDqgzGuPiaoChi (String gpc)
//	{
//		this.gpc = gpc;
//	}
//	public String getDqgzGuPiaoChi (String gpc)
//	{
//		return this.gpc ;
//	}

	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ChangQiGuanZhu gz = (ChangQiGuanZhu) o;
        
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

