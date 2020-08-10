package com.exchangeinfomanager.StockCalendar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
//import javax.swing.JMenuItem;
//import javax.swing.JPopupMenu;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.ExternalNewsType;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;

public class WholeMonthCompViewOfDQGZ  extends WholeMonthNewsComponentsView 
{

//	private JPopupMenu Pmenu;
//	private JMenuItem menuItemEdit;
	private JLabel exportCurrentDqgz;

	public WholeMonthCompViewOfDQGZ(ServicesForNews meetingServices, String title) 
	{
		super(meetingServices, title);
	}
	
	protected LocalDate initView() 
	{
		LocalDate resultdate = super.initView();
		
		// TODO Auto-generated constructor stub
		this.exportCurrentDqgz = JLabelFactory.createButton("导出当前关注");
		super.pnmmonthnresnorth.add(exportCurrentDqgz);
				
		exportCurrentDqgz.addMouseListener( new ExportCurrentDqgzController () );
		
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

	
	 private class ExportCurrentDqgzController extends MouseAdapter 
	 {

			@Override
	        public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == MouseEvent.BUTTON1) {
					TDXFormatedOpt.parserDuanQiGuanZhuToTDXCode();
	            } else if (e.getButton() == MouseEvent.BUTTON3) {
	            
	            }
	        }
	  }


}

