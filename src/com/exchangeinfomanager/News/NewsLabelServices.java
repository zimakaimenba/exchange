package com.exchangeinfomanager.News;

import java.sql.SQLException;
import java.util.Collection;

import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;

public class NewsLabelServices implements ServicesForNewsLabel
{
    private StockCalendarAndNewDbOperation database;

	private NewsCache cache;


    public NewsLabelServices ()
    {
    	super();
    	this.database = new StockCalendarAndNewDbOperation ();
    }
    
    public void setCache (NewsCache cache)
    {
    	this.cache = cache;
    }

    @Override
    public Collection<InsertedNews.Label> getLabels() throws SQLException 
    {
        return this.database.getLabels();
    }

    @Override
    public void createLabel(News.Label label) throws SQLException  {
        InsertedNews.Label lbl = this.database.createNewsLabel(label);

        cache.addNewsLabel(lbl);
    }

    @Override
    public void deleteLabel(InsertedNews.Label label) throws SQLException  {
        InsertedNews.Label lbl = this.database.deleteNewsLabel(label);
        cache.removeNewsLabel(lbl);
    }

    @Override
    public void updateLabel(InsertedNews.Label label) throws SQLException  {
        InsertedNews.Label lbl = this.database.updateNewsLabel(label);
        cache.updateNewsLabel(lbl);
    }
}
