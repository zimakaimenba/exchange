package com.exchangeinfomanager.Services;

import java.sql.SQLException;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;



public interface ServicesForNewsLabel 
{
	Collection<InsertedNews.Label> getLabels() throws SQLException ;

    void createLabel(News.Label label) throws SQLException ;

    void deleteLabel(InsertedNews.Label label) throws SQLException ;

    void updateLabel(InsertedNews.Label label) throws SQLException ;
    
    void setCache (NewsCache cache);

}
