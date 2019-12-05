package com.exchangeinfomanager.News.ExternalNewsType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.TagServices.TagsNewsDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;

public class DuanQiGuanZhuServices implements ServicesForNews 
{

	private StockCalendarAndNewDbOperation database;
    private TagsNewsDbOperation tagsdboptfornews;
    private NewsCache cache;
    
    public DuanQiGuanZhuServices ()
    {
    	 super();
         this.database = new StockCalendarAndNewDbOperation ();
         this.tagsdboptfornews = new  TagsNewsDbOperation ();
    }

	@Override
	public Collection<News> getNews(String nodeid, LocalDate startdate, LocalDate enddate) throws SQLException 
	{
		Collection<News> news = this.database.getDuanQiJiLuInfo (startdate,  enddate);
		return news;
	}

	@Override
	public News createNews(News News) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNews(InsertedNews News) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public News updateNews(InsertedNews News) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCache(NewsCache cache) {
		this.cache = cache;
		
	}

	@Override
	public NewsCache getCache() {
		// TODO Auto-generated method stub
		return this.cache;
	}
    

}
