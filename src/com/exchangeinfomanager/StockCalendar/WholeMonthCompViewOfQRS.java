package com.exchangeinfomanager.StockCalendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.ExternalNewsType.ExternalNewsType;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.Services.ServicesForNews;


public class WholeMonthCompViewOfQRS extends WholeMonthNewsComponentsView 
{

	public WholeMonthCompViewOfQRS(ServicesForNews meetingServices, String title) 
	{
		super(meetingServices, title);
		// TODO Auto-generated constructor stub
	}
	
	public void onNewsChange(NewsCache cache) 
	{
		LocalDate firstdayofmonth = this.initView();
		
		for (Iterator<NewsCache> lit = super.caches.iterator(); lit.hasNext(); ) {
    		NewsCache f = lit.next();
    		
    		updateView (f, firstdayofmonth );
		}
		
		super.cqjlpnl.validate();
        super.cqjlpnl.repaint();
		
	}
	
	private void updateView (NewsCache cache,LocalDate firstdayofmonth) 
	{
		Collection<News> meetings = cache.produceNews(firstdayofmonth);
        Collection<InsertedNews.Label> labels = cache.produceLabels();

        for (News m : meetings) {
            LocalDate mDate = m.getStart();
            LocalDate eDate = ((ExternalNewsType)m).getEnd();
            
            LocalDate superdate = super.getDate();
            LocalDate firstDayInMonth = superdate.withDayOfMonth(1);
            LocalDate lastDayInMonth;
            try {
            	lastDayInMonth = superdate.withDayOfMonth(30);
            } catch (java.time.DateTimeException e) {
            	lastDayInMonth = superdate.withDayOfMonth(28);
            }
            
//    		DateTime requiredstartdt= new DateTime(firstDayInMonth.getYear(), firstDayInMonth.getMonthValue(), firstDayInMonth.getDayOfMonth(), 0, 0, 0, 0);
//    		DateTime requiredenddt = new DateTime(lastDayInMonth.getYear(), lastDayInMonth.getMonthValue(), lastDayInMonth.getDayOfMonth(), 0, 0, 0, 0);
//    		Interval requiredinterval = new Interval(requiredstartdt, requiredenddt);
            
    		if( eDate.isBefore(firstDayInMonth) || mDate.isAfter(lastDayInMonth) )
    			continue;
        	
            if (m.getLabels().isEmpty()) {
                	JUpdatedLabel label = super.getFormatedLabelForNoneLabel (m);
                   	this.contentpnl.add(label);
                    continue;
            }
                
            for (News.Label l : labels) {
                    if (m.getLabels().contains(l)) {
                    	JUpdatedLabel label = super.getFormatedLabelForWithLabels (m,l);
                       	this.contentpnl.add(label);
                        continue;
                    }
            }
        }
	}

}
