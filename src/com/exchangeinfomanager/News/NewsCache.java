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
    private Set<News> News;
    private Set<InsertedNews.Label> NewsLabels;
    private ServicesForNews Newservice;
    private ServicesForNewsLabel labelService;
	private String nodecode; //"ALL":�������ţ�"000000":ÿ���̶��¶���ʾ�����š�"999999":��ʾ��jstockcalendar������
//	private boolean datachanged = false;
	private LocalDate cashestartdate;
	private LocalDate casheenddate;
	private Integer[] eventtype;

    public NewsCache(String nodecode,ServicesForNews Newservice, ServicesForNewsLabel labelService,LocalDate startdate, LocalDate enddate) 
    {
    	this.nodecode = nodecode;
        this.Newservice = Newservice;
        this.Newservice.setCache(this);
        this.labelService = labelService;
        if(this.labelService != null)
        	this.labelService.setCache(this);
        this.listeners = new HashSet<>();
        this.News = new HashSet<>();
        this.NewsLabels = new HashSet<>();
        this.cashestartdate = startdate;
        this.casheenddate = enddate;
        
        this.refreshNews(nodecode,cashestartdate,casheenddate);
        if(this.labelService != null)
        	this.refreshLabels();
    }
    public ServicesForNews getServicesForNews ()
    {
    	return Newservice;
    }
    /*
     * 
     */
    public void refresh ()
    {
    		this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
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
    public Collection<News> produceNews() 
    {
    	return this.News;
    }
    /**
     * cachֻѡ���ʼһ������ţ�������������Χ��Ҫ���´����ݿ���ѡ������
     * @param firstdayofmonth
     * @return
     */
    public Collection<News> produceNews(LocalDate firstdayofmonth) 
    {
    	//cashֻѡ���ʼһ������ţ�������������Χ��Ҫ���´����ݿ���ѡ������
    	try{
    		if(firstdayofmonth.isAfter(this.cashestartdate) && firstdayofmonth.isBefore(this.casheenddate))
        		return this.News;
    	} catch (java.lang.NullPointerException e) { //ÿ��head������һ�ζ��ҳ����ģ�����������
    		return this.News;
    	}
    	
    	//�����û�ѡ��������Ѿ�������cash�ڴ�����ŵ��������ڣ�Ҫ����ѡ������
    	if(firstdayofmonth.isBefore(this.cashestartdate)) {
    		this.cashestartdate = firstdayofmonth.minusMonths(6).with(DayOfWeek.MONDAY);
    	} else if(firstdayofmonth.isAfter(this.casheenddate)) {
    		this.casheenddate = firstdayofmonth.plusMonths(6).with(DayOfWeek.SUNDAY);
    	}
    	this.refreshNews (this.nodecode,this.cashestartdate ,this.casheenddate ) ;
        return this.News;
    }
    /*
     * 
     */
    public Collection<InsertedNews.Label> produceLabels() {
        return this.NewsLabels;
    }

    public void updateNews(News News) {
        this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onNewsChange(this));
    }

    public void removeNews(News News) {
        this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onNewsChange(this));
    }

    public void addNews(News News) {
        this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onNewsChange(this));
        this.listeners.forEach(l -> l.onNewsAdded(News));
    }

    public void updateNewsLabel(InsertedNews.Label label) {
        this.refreshLabels();
        this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void removeNewsLabel(InsertedNews.Label label) {
        this.refreshLabels();
        this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    public void addNewsLabel(InsertedNews.Label label) 
    {
        this.refreshLabels();
        this.refreshNews(nodecode,this.cashestartdate,this.casheenddate);
        this.listeners.forEach(l -> l.onLabelChange(this));
    }

    private void refreshNews(String nodecode,LocalDate startdate,LocalDate enddate) 
    {
        this.News.clear();

        try {
            this.News.addAll(Newservice.getNews(nodecode,startdate,enddate));
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
