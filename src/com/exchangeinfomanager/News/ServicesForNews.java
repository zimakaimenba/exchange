package com.exchangeinfomanager.News;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;

public interface ServicesForNews 
{
	Collection<News> getNews(String nodeid,LocalDate startdate, LocalDate enddate) throws SQLException;
	
	Collection<News> getNews(BkChanYeLianTreeNode node,LocalDate startdate, LocalDate enddate) throws SQLException;

    News createNews(News News) throws SQLException;

    void deleteNews(News News) throws SQLException;

    News updateNews(News News) throws SQLException;
    
    void setCache (NewsCache cache);
    
    NewsCache getCache ();
}

