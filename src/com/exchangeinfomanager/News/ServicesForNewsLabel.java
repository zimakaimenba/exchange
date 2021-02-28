package com.exchangeinfomanager.News;

import java.sql.SQLException;
import java.util.Collection;



public interface ServicesForNewsLabel 
{
	Collection<InsertedNews.Label> getLabels() throws SQLException ;

    void createLabel(News.Label label) throws SQLException ;

    void deleteLabel(InsertedNews.Label label) throws SQLException ;

    void updateLabel(InsertedNews.Label label) throws SQLException ;
    
    void setCache (NewsCache cache);

}
