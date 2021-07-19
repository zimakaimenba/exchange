package com.exchangeinfomanager.News;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagServicesForNews;
import com.exchangeinfomanager.TagServices.TagsNewsDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;


public class NewsServices implements ServicesForNews
{

    protected StockCalendarAndNewDbOperation database;
    protected TagsNewsDbOperation tagsdboptfornews;
    protected NewsCache cache;
    
    public NewsServices()  
    {
        super();
        this.database = new StockCalendarAndNewDbOperation ();
        this.tagsdboptfornews = new  TagsNewsDbOperation ();
    }

    public void setCache (NewsCache cache)
    {
    	this.cache = cache;
    }
    @Override
	public Collection<News> getNews(BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate)
			throws SQLException 
    {
		return this.getNews(node.getMyOwnCode(), startdate, enddate); 
	}
    @Override
    public Collection<News> getNews(String nodeid,LocalDate startdate, LocalDate enddate) throws SQLException 
    {
        Collection<News> result = this.database.getNodeRelatedNews (nodeid, startdate,  enddate);
        return result;
    }
    @Override
    public News createNews(News News) throws SQLException {

    	 InsertedNews m  = this.database.createNodeNews (News);
    	 
    	 TagServicesForNews tagfornews = new TagServicesForNews (m);
    	 tagfornews.createTag(new Tag("") ) ;
    	 
    	 if(m != null && cache != null)
    		 cache.addNews(m);
    	 
    	 return m;
    }
    @Override
    public void deleteNews(News News) throws SQLException {
        
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
    public News updateNews(News news) throws SQLException {
    	
        InsertedNews m = this.database.updateNews(news);
        
        if(m != null && cache != null) 
        	cache.updateNews(m);
        
        return m;
    }

	@Override
	public NewsCache getCache() 
	{
		return this.cache;
	}

	
    
    





}
