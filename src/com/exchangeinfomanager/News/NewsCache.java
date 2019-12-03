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
	private String nodecode; //"ALL":�������ţ�"000000":ÿ���̶��¶���ʾ�����š�"999999":��ʾ��jstockcalendar������
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
     * cachֻѡ���ʼһ������ţ�������������Χ��Ҫ���´����ݿ���ѡ������
     * @param firstdayofmonth
     * @return
     */
    public Collection<InsertedNews> produceNewss(LocalDate firstdayofmonth) 
    {
    	//cashֻѡ���ʼһ������ţ�������������Χ��Ҫ���´����ݿ���ѡ������
    	try{
    		if(firstdayofmonth.isAfter(this.cashestartdate) && firstdayofmonth.isBefore(this.casheenddate))
        		return this.Newss;
    	} catch (java.lang.NullPointerException e) { //ÿ��head������һ�ζ��ҳ����ģ�����������
    		return this.Newss;
    	}
    	
    	//�����û�ѡ��������Ѿ�������cash�ڴ�����ŵ��������ڣ�Ҫ����ѡ������
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
