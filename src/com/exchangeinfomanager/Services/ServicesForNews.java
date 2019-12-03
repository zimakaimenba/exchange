package com.exchangeinfomanager.Services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;

public interface ServicesForNews 
{
	Collection<InsertedNews> getNews(String nodeid,LocalDate startdate, LocalDate enddate) throws SQLException;

    News createNews(News News) throws SQLException;

    void deleteNews(InsertedNews News) throws SQLException;

    News updateNews(InsertedNews News) throws SQLException;
    
    void setCache (NewsCache cache);
}
