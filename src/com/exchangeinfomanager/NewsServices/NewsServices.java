package com.exchangeinfomanager.NewsServices;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsNewsDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;


public class NewsServices implements ServicesForNews
{

    private StockCalendarAndNewDbOperation database;
    private TagsNewsDbOperation tagsdboptfornews;
    private NewsCache cache;
    
    public NewsServices()  
    {
        super();
        this.database = new StockCalendarAndNewDbOperation ();
    }

    public void setCache (NewsCache cache)
    {
    	this.cache = cache;
    }
    @Override
    public Collection<InsertedNews> getNews(String nodeid,LocalDate startdate, LocalDate enddate) throws SQLException 
    {
        Collection<InsertedNews> result = this.database.getNodeRelatedNews (nodeid, startdate,  enddate);
        return result;
    }

    @Override
    public News createNews(News News) throws SQLException {

    	 InsertedNews m  = this.database.createNodeNews (News);
    	 this.tagsdboptfornews.storeNewsKeyWordsToDataBase (m);
        
    	 if(m != null && cache != null)
    		 cache.addNews(m);
    	 
    	 return m;
    }
    @Override
    public void deleteNews(InsertedNews News) throws SQLException {
        
        InsertedNews m = this.database.deleteNews(News);
        this.tagsdboptfornews.deleteKeyWordsMapsOfDeletedNews (m);
        
        if(m != null && cache != null)
        	cache.removeNews(m);
    }
    public News removeNewsOwners (Collection<BkChanYeLianTreeNode> removedowner, InsertedNews news) 
    {
    	for (Iterator<BkChanYeLianTreeNode> lit = removedowner.iterator(); lit.hasNext(); ) {
    		BkChanYeLianTreeNode f = lit.next();
    		news.removeNewsSpecficOwner(f.getMyOwnCode());
    	}
    	
    	try {
			this.updateNews(news);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return news;
    }
    @Override
    public News updateNews(InsertedNews news) throws SQLException {
    	
        InsertedNews m = this.database.updateNews(news);
        
        if(m != null && cache != null) 
        	cache.updateNews(m);
        
        return m;
    }
    
    





}
