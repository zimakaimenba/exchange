package com.exchangeinfomanager.News.ExternalNewsType;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ServicesForNews;
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
	public Collection<News> getNews(BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate)
			throws SQLException 
    {
    	Collection<News> news = this.database.getDuanQiJiLuInfo (node, startdate, enddate);
		return news;
	}
    
	@Override
	public Collection<News> getNews(String nodeid, LocalDate startdate, LocalDate enddate) throws SQLException 
	{
		Collection<News> news = this.database.getDuanQiJiLuInfo ( nodeid, startdate,  enddate);
		return news;
	}
	/*
	 * 
	 */
	public Collection<News> getCurrentNews(LocalDate curdate)  
	{
		Collection<News> news = null;
		try {			news = this.getNews("ALL", curdate, curdate.with( DayOfWeek.SUNDAY ) );
		} catch (SQLException e) {e.printStackTrace();}
		
//		for (Iterator<News> lit = news.iterator(); lit.hasNext(); ) {
//			InsertedExternalNews f = (InsertedExternalNews)lit.next();
//    		LocalDate start = f.getStart();
//    		LocalDate end = f.getEnd();
//    		if( curdate.isBefore(start) || curdate.isAfter(end)   )
//    			lit.remove();
//    	}
		
		return news;
	}
	@Override
	public News createNews(News news) throws SQLException 
	{
		 InsertedExternalNews m = this.database.createDuanQiJiLuInfo (news);
//	   	 this.tagsdboptfornews.storeNewsKeyWordsToDataBase (m);
	       
	   	 if(m != null && cache != null)
	   		 cache.addNews(m);
	   	 
	   	 return m;
	}

	@Override
	public void deleteNews(News news) throws SQLException 
	{
		News m = this.database.deleteGuanZhuJiLuInfo(news);
//      this.tagsdboptfornews.deleteKeyWordsMapsOfDeletedNews (m);
      
      if(m != null && cache != null)
      	cache.removeNews(m);
	}

	@Override
	public News updateNews(News news) throws SQLException 
	{
		News m = this.database.updateGuanZhuJiLuInfo(news);

		if(m != null && cache != null)
        	cache.removeNews(m);
		
		return m;
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
