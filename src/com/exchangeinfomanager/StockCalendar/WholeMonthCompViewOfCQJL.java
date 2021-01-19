package com.exchangeinfomanager.StockCalendar;

import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;



import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.ExternalNewsType;
import com.exchangeinfomanager.Services.ServicesForNews;

public class WholeMonthCompViewOfCQJL extends WholeMonthNewsComponentsView
{
	public WholeMonthCompViewOfCQJL(ServicesForNews meetingServices, String title) 
	{
		super(meetingServices, title);
	}
	
	protected LocalDate initView() 
	{
		LocalDate resultdate = super.initView();
		return resultdate;
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
            int mDateYear = mDate.getYear();
            int mDateMonth = mDate.getMonthValue();
            int eDateYear = eDate.getYear();
            int eDateMonth = eDate.getMonthValue();
            
            LocalDate superdate = super.getDate();
            int superdayYear = superdate.getYear();
            int superdayMonth = superdate.getMonthValue();
            
            if (mDateYear == eDateYear) { 
            	if( superdayMonth < mDateMonth || superdayMonth > eDateMonth )
        			continue;
            } else
            if (mDateYear != eDateYear) {
            	if( superdayMonth < mDateMonth && superdayMonth > eDateMonth )
        			continue;
            }
        	
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
