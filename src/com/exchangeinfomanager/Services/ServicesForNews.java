package com.exchangeinfomanager.Services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;

public interface ServicesForNews 
{
	Collection<News> getNews(String nodeid,LocalDate startdate, LocalDate enddate) throws SQLException;

    News createNews(News News) throws SQLException;

    void deleteNews(News News) throws SQLException;

    News updateNews(News News) throws SQLException;
    
    void setCache (NewsCache cache);
    
    NewsCache getCache ();
}

