package com.exchangeinfomanager.News.ExternalNewsType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ServicesForNews;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class ZhiShuBoLangServices implements ServicesForNews 
{

	private StockCalendarAndNewDbOperation database;
    private NewsCache cache;
    
    public ZhiShuBoLangServices ()
    {
    	 super();
         this.database = new StockCalendarAndNewDbOperation ();
    }
    
    @Override
	public Collection<News> getNews(BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate)
			throws SQLException 
    {
    	Collection<News> news = this.database.getZhiShuKeyDates (node.getMyOwnCode(), startdate,  enddate);
		return news;
	}
	@Override
	public Collection<News> getNews(String nodeid, LocalDate startdate, LocalDate enddate) throws SQLException 
	{
		Collection<News> news = this.database.getZhiShuKeyDates (nodeid, startdate,  enddate);
		return news;
	}

	@Override
	public News createNews(News news) throws SQLException
	{
		 News m = this.database.createZhiShuGuanJianRiQi ((ZhiShuBoLang)news);
//	   	 this.tagsdboptfornews.storeNewsKeyWordsToDataBase (m);
	       
	   	 if(m != null && cache != null)
	   		 cache.addNews(m);
	   	 
	   	 return m;
	}

	@Override
	public void deleteNews(News news) throws SQLException 
	{
		 News m = this.database.deleteZhiShuGuanJianRiQi(news);
//        this.tagsdboptfornews.deleteKeyWordsMapsOfDeletedNews (m);
        
        if(m != null && cache != null)
        	cache.removeNews(m);
	}

	@Override
	public News updateNews(News news) throws SQLException 
	{
		News m = this.database.updateZhiShuGuanJIanRiQi(news);

		if(m != null && cache != null)
        	cache.removeNews(m);
		
		return m;
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

