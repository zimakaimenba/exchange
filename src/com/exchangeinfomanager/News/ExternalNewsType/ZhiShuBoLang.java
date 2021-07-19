package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;

public class ZhiShuBoLang extends ExternalNewsType
{
	private String bolangtype;

	public ZhiShuBoLang(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<com.exchangeinfomanager.News.InsertedNews.Label> labels, String newsUrl,String bolangtype) 
	{
		super(node, description, starttime, endtime, detail, keywords, labels, newsUrl);

		this.bolangtype = bolangtype;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ZhiShuBoLang gz = (ZhiShuBoLang) o;
        
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
