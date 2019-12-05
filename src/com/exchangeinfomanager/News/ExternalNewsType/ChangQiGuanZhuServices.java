package com.exchangeinfomanager.News.ExternalNewsType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.TagServices.TagsNewsDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;

public class ChangQiGuanZhuServices implements ServicesForNews 
{

	private StockCalendarAndNewDbOperation database;
    private TagsNewsDbOperation tagsdboptfornews;
    private NewsCache cache;
    
    public ChangQiGuanZhuServices ()
    {
    	 super();
         this.database = new StockCalendarAndNewDbOperation ();
         this.tagsdboptfornews = new  TagsNewsDbOperation ();
    }
    
	@Override
	public Collection<News> getNews(String nodeid, LocalDate startdate, LocalDate enddate) throws SQLException 
	{
		Collection<News> news = this.database.getChangQiJiLuInfo (startdate,  enddate);
		return news;
	}

	@Override
	public News createNews(News news) throws SQLException
	{
		 InsertedExternalNews m = this.database.createChangQiJiLuInfo (news);
//	   	 this.tagsdboptfornews.storeNewsKeyWordsToDataBase (m);
	       
	   	 if(m != null && cache != null)
	   		 cache.addNews(m);
	   	 
	   	 return m;
	}

	@Override
	public void deleteNews(InsertedNews news) throws SQLException 
	{
		 News m = this.database.deleteChangQiJiLuInfo(news);
//        this.tagsdboptfornews.deleteKeyWordsMapsOfDeletedNews (m);
        
        if(m != null && cache != null)
        	cache.removeNews(m);
	}

	@Override
	public News updateNews(InsertedNews News) throws SQLException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCache(NewsCache cache) 
	{
		this.cache = cache;
		
	}


	@Override
	public NewsCache getCache() {
		// TODO Auto-generated method stub
		return this.cache;
	}

}
