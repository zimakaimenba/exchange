package com.exchangeinfomanager.News.QiangRuoShiNode;

import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.News;

public class QiangRuoShiNode extends News
{

	public QiangRuoShiNode(String title, LocalDate starttime, String description, String keywords,
			Collection<com.exchangeinfomanager.News.InsertedNews.Label> labels, String qiangruoshitype) {
		super(title, starttime, description, keywords, labels, "", qiangruoshitype);
		// TODO Auto-generated constructor stub
	}
	
	private LocalDate endtime;
	public LocalDate getEnd() {
	    return endtime;
	}

	public void setEnd(LocalDate end) {
	    this.endtime = end;
	}


}
