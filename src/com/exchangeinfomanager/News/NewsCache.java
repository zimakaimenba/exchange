package com.exchangeinfomanager.News;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;

public class NewsCache 
{

    private Set<NewsCacheListener> listeners;
    private Set<InsertedNews> Newss;
    private Set<InsertedNews.Label> NewsLabels;
    private ServicesForNews NewsService;
    private ServicesForNewsLabel labelService;
	private String nodecode; //"ALL":所有新闻；"000000":每个固定月都显示的新闻。"999999":显示在jstockcalendar的新闻
//	private boolean datachanged = false;
	private LocalDate cashestartdate;
	private LocalDate casheenddate;
	private Integer[] eventtype;

    public NewsCache(String nodecode,ServicesForNews NewsService, ServicesForNewsLabel labelService,LocalDate startdate, LocalDate enddate) 
    {
    	this.nodecode = nodecode;
        this.NewsService = NewsService;
        this.NewsService.setCache(this);
        this.labelService = labelService;
        if(this.labelService != null)
        	this.labelService.setCache(this);
        this.listeners = new HashSet<>();
        this.Newss = new HashSet<>();
        this.NewsLabels = new HashSet<>();
        this.cashestartdate = startdate;
        this.casheenddate = enddate;
        
        this.refreshNewss(nodecode,cashestartdate,casheenddate);
        if(this.labelService != null)
        	this.refreshLabels();
    }
    /*
     * 
     */
    public void refresh ()
    {
    		this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        	this.listeners.forEach(l -> l.onNewsChange(this));
    }
    
    public String getNodeCode ()
    {
    	return this.nodecode;
    }
    /*
     * 
     */
    public void addCacheListener(NewsCacheListener listener) {
        this.listeners.add(listener);
    }
    /*
     * 
     */
    public Collection<InsertedNews> produceNewss() 
    {
    	return this.Newss;
    }
    /**
     * cach只选择初始一年的新闻，如果超过这个范围需要重新从数据库中选择数据
     * @param firstdayofmonth
     * @return
     */
    public Collection<InsertedNews> produceNewss(LocalDate firstdayofmonth) 
    {
    	//cash只选择初始一年的新闻，如果超过这个范围需要重新从数据库中选择数据
    	try{
    		if(firstdayofmonth.isAfter(this.cashestartdate) && firstdayofmonth.isBefore(this.casheenddate))
        		return this.Newss;
    	} catch (java.lang.NullPointerException e) { //每月head新闻是一次都找出来的，不设置限制
    		return this.Newss;
    	}
    	
    	//可能用户选择的日期已经超过了cash内存的新闻的最早日期，要从新选择新闻
    	if(firstdayofmonth.isBefore(this.cashestartdate)) {
    		this.cashestartdate = firstdayofmonth.minusMonths(6).with(DayOfWeek.MONDAY);
    	} else if(firstdayofmonth.isAfter(this.casheenddate)) {
    		this.casheenddate = firstdayofmonth.plusMonths(6).with(DayOfWeek.SUNDAY);
    	}
    	this.refreshNewss (this.nodecode,this.cashestartdate ,this.casheenddate ) ;
        return this.Newss;
    }
    /*
     * 
     */
    public Collection<InsertedNews.Label> produceLabels() {
        return this.NewsLabels;
    }

    public void updateNews(InsertedNews News) {
        this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onNewsChange(this));
    }

    public void removeNews(InsertedNews News) {
        this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onNewsChange(this));
    }

    public void addNews(InsertedNews News) {
        this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onNewsChange(this));
        this.listeners.forEach(l -> l.onNewsAdded(News));
    }

    public void updateNewsLabel(InsertedNews.Label label) {
        this.refreshLabels();
        this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void removeNewsLabel(InsertedNews.Label label) {
        this.refreshLabels();
        this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void addNewsLabel(InsertedNews.Label label) 
    {
        this.refreshLabels();
        this.refreshNewss(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    private void refreshNewss(String nodecode,LocalDate startdate,LocalDate enddate) 
    {
        this.Newss.clear();

        try {
            this.Newss.addAll(NewsService.getNews(nodecode,startdate,enddate));
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    private void refreshLabels() {
        this.NewsLabels.clear();

        try {
            this.NewsLabels.addAll(labelService.getLabels());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.lang.NullPointerException ex) {
        	ex.printStackTrace();
        }
    }


}
