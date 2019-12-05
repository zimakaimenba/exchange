package com.exchangeinfomanager.News.ExternalNewsType;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public abstract class ExternalNewsType extends News
{

	protected LocalDate endtime;
	protected BkChanYeLianTreeNode node;

	public ExternalNewsType(BkChanYeLianTreeNode node, String description, LocalDate starttime, LocalDate endtime, String detail, String keywords,
			Collection<InsertedNews.Label> labels, String newsUrl)
	{
		super(description, starttime, detail, keywords, labels, newsUrl, node.getMyOwnCode() + node.getMyOwnName() );
		
		this.node = node;
		
		this.endtime = endtime;
	}
	
	public LocalDate getEnd() {
	    return endtime;
	}

	public void setEnd(LocalDate end) {
	    this.endtime = end;
	}

	public BkChanYeLianTreeNode getNode() {
		return node;
	}

	public void setNode(BkChanYeLianTreeNode node) {
		this.node = node;
	}
}
