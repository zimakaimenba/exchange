package com.exchangeinfomanager.StockCalendar;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import com.exchangeinfomanager.News.CreateNewsDialog;
import com.exchangeinfomanager.News.CreateNewsWithFurtherOperationDialog;
import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.InsertedNews.Label;
import com.exchangeinfomanager.News.ModifyNewsDiaLog;
import com.exchangeinfomanager.News.ModifyNewsWithFurtherOperationDialog;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.News.ExternalNewsType.ModifyExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShi;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShi;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddNewsToNode;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForWholeSearchTags;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.commonlib.JMultiLineToolTip;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

@SuppressWarnings("all")
public class MonthView extends View 
{
	private static final Dimension SIZE = new Dimension(1050, 700);
    private static final Dimension MIN_SIZE = new Dimension(1050, 700);
      
    private JPanel calendar = new JPanel();//JPanelFactory.createFixedSizePanel (MONTHVIEWWIDTH,800);;
    private JLabel dateLabel = new JLabel();
    private SettingOfDisplayNewsArea settingsofnewsdisplay;
	private NewsServices svsns;
	private ChangQiGuanZhuServices svscqgz;
	private QiangShiServices svsqs;
	private RuoShiServices svsrs;
	private DuanQiGuanZhuServices svsdqgz;
	private ZhiShuBoLangServices svszsbl;
    
    public MonthView( Collection<ServicesForNews> newssvs, SettingOfDisplayNewsArea settingsofnewsdisplay) 
    {
        super(newssvs);
        this.settingsofnewsdisplay = settingsofnewsdisplay;
        
        for (Iterator<ServicesForNews> lit = newssvs.iterator(); lit.hasNext(); ) {
    		ServicesForNews f = lit.next();
    		f.getCache().addCacheListener(this);
    		
    		if(f instanceof NewsServices)
    			this.svsns = (NewsServices) f;
    		else
    		if( f instanceof ChangQiGuanZhuServices) {
    			this.svscqgz = (ChangQiGuanZhuServices) f;
    		} else
    		if( f instanceof QiangShiServices) {
    			this.svsqs = (QiangShiServices) f;
    		} else
    		if( f instanceof RuoShiServices) {
    			this.svsrs = (RuoShiServices) f;
    		} else
    		if( f instanceof DuanQiGuanZhuServices) {
    			this.svsdqgz = (DuanQiGuanZhuServices) f;
    		} else
    		if( f instanceof ZhiShuBoLangServices) {
    			this.svszsbl = (ZhiShuBoLangServices) f;
    		}
    	}
        
        this.onNewsChange(super.caches); //获取本月的信息
        
        this.initMonthView();
    }
    @Override
    /*
     * (non-Javadoc)
     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener#onNewsChange(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache)
     */
    public void onNewsChange(Collection<NewsCache> caches) 
    {
    	LocalDate firstdayofmonth = this.initView();
    	
    	for (Iterator<NewsCache> lit = caches.iterator(); lit.hasNext(); ) {
    		NewsCache f = lit.next();
    		
    		createNewsInPnl (f,firstdayofmonth);
    	}

        this.calendar.validate();
        this.calendar.repaint();
    }
    private void createNewsInPnl (NewsCache cache, LocalDate firstdayofmonth)
    {
    	Collection<News> news = cache.produceNews(firstdayofmonth);
		Collection<Label> labels = cache.produceLabels();
		
		for (News m : news) {
			Integer dbid = null ;
			if( m instanceof InsertedNews ) 
				dbid = ((InsertedNews)m).getID();
			else if (m instanceof InsertedExternalNews )
				dbid = ((InsertedExternalNews)m).getID ();
			
            LocalDate mDate = m.getStart();
            
            if(!shouldBeDisplayedOnPanel (m) )
            	continue;
            
            if (mDate.getMonth().equals(super.getDate().getMonth()) && (mDate.getYear() == super.getDate().getYear()) ) {
            	
                if (m.getLabels().isEmpty()) { //没有Label的情况
                	JUpdatedLabel label = new JUpdatedLabel( getCorrectDiaplayInfo (cache,m) );
                    
                    label.setToolTipText( getLabelToolTipText(m) );
                    label.setOpaque(true);
                    label.setName( String.valueOf(dbid));
                    label.addMouseListener(getNeededMouseAdapter(cache));
                    label.setForeground(ColorScheme.BLACK_FONT);
                    label.setBackground(ColorScheme.GREY_WHITER_DARKER);
                    label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                    this.getMonthDayPnl(mDate).add(label);
                    continue;
                }
                
                for (News.Label l : labels) { //有LABEL的情况
                    if ( m.getLabels().contains(l)) {
                    	JUpdatedLabel label = new JUpdatedLabel( getCorrectDiaplayInfo (cache,m) );
                        label.setToolTipText(getLabelToolTipText(m) );
                        label.setOpaque(true);
                        label.setName(  String.valueOf(dbid));
                        label.addMouseListener(getNeededMouseAdapter(cache));
//                        label.setForeground(ColorScheme.BACKGROUND);
                        label.setForeground(Color.BLACK);
                        label.setBackground(l.getColor());
                        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                        this.getMonthDayPnl(mDate).add(label);
                        break;
                    }
                }
            }
		}
    }
    private String getCorrectDiaplayInfo (NewsCache cache, News news)
    {
    	ServicesForNews svs = cache.getServicesForNews();
    	if(svs instanceof NewsServices)
    		return news.getTitle();
    	if( svs instanceof ChangQiGuanZhuServices) {
    		return news.getNewsOwnerCodes() + news.getTitle();
		}
		if( svs instanceof QiangShiServices) {
			return news.getNewsOwnerCodes() + news.getTitle();
		}
		if( svs instanceof RuoShiServices) {
			return news.getNewsOwnerCodes() + news.getTitle();
		}
		if( svs instanceof DuanQiGuanZhuServices) {
			return news.getNewsOwnerCodes() + news.getTitle();
		}
		if( svs instanceof ZhiShuBoLangServices) {
			return news.getNewsOwnerCodes() + news.getTitle();
		}
		
		return null;
    }
    private MouseAdapter getNeededMouseAdapter (NewsCache cache)
    {
    	ServicesForNews svs = cache.getServicesForNews();
    	if(svs instanceof NewsServices)
    		return new ReviseNewsController ();
    	
    	if( svs instanceof ChangQiGuanZhuServices) {
    		return new ReviseExternalNewsController (svscqgz);
		}
		if( svs instanceof QiangShiServices) {
			return new ReviseExternalNewsController (svsqs);
		}
		if( svs instanceof RuoShiServices) {
			return new ReviseExternalNewsController (svsrs);
		}
		if( svs instanceof DuanQiGuanZhuServices) {
			return new ReviseExternalNewsController (svsdqgz);
		}
		if( svs instanceof ZhiShuBoLangServices) {
			return new ReviseExternalNewsController (svszsbl);
		}
			
		return null;
    }

    @Override
	public void onNewsAdded(News m) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNewsChange(NewsCache cache) {
		this.onNewsChange (super.caches);
		
	}
	@Override
	public void onLabelChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}
    /*
     * 
     */
    private void initMonthView() 
    {
        super.setBackground(ColorScheme.BACKGROUND);
        super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        super.add(this.getWeekdaysPanel() );
        super.add(Box.createVerticalStrut(10));

        this.calendar.setLayout(new GridLayout(6, 7, 1, 1));
        this.calendar.setBackground(ColorScheme.GREY_LINE);

        super.add(this.calendar);
        
        this.setPreferredSize(SIZE);
        this.setMinimumSize(MIN_SIZE);
        
      
    }
    /*
     * 
     */
    private JPanel getWeekdaysPanel() 
    {
        JPanel weekdays = JPanelFactory.createFixedSizePanel(new GridLayout(1, 7));
//        JPanel weekdays = JPanelFactory.createFixedSizePanel (MONTHVIEWWIDTH,12);
        weekdays.setLayout(new GridLayout(1, 7));
        weekdays.add(new JLabel("MonDay"));
        weekdays.add(new JLabel("Tuesday"));
        weekdays.add(new JLabel("Wednesday"));
        weekdays.add(new JLabel("Thursday"));
        weekdays.add(new JLabel("Friday"));
        weekdays.add(new JLabel("Saturday"));
        weekdays.add(new JLabel("Sunday"));
        return weekdays;
    }
    /*
     * 
     */
    private LocalDate initView() 
    {
        this.calendar.removeAll();

        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
        LocalDate first = firstDayInMonth.minusDays(firstDayInMonth.getDayOfWeek().getValue() - 1);

        int i = 0;

        for (LocalDate d = first; i < 42; d = d.plusDays(1), i++) {
        	JScrollPane scrollpane = new JScrollPane ();
            JPanel panel = new JPanel();
            panel.setName(d.toString());
            Color color = null;
            if (d.getMonth().equals(super.getDate().getMonth())) {
                color = ColorScheme.BACKGROUND;
                
                panel.setLayout(new BorderLayout());
                panel.addMouseListener(new CreateNewsController());
                
                JLabel dayLabel = new JLabel(String.valueOf(d.getDayOfMonth()));
                if(d.equals(LocalDate.now()))
                	dayLabel.setForeground(ColorScheme.TODAY);
                else
                	dayLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
                dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
                dayLabel.setBackground(ColorScheme.BACKGROUND);
                dayLabel.setOpaque(true);
                
                panel.add(dayLabel, BorderLayout.PAGE_START);
                panel.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER);
                
            } else {
                color = ColorScheme.GREY_WHITER;
            }
            panel.setBackground(color);
            
            scrollpane.setViewportView (panel);
            scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());

            this.calendar.add(scrollpane);
        }
        
        return first;
    }

    private JPanel getMonthDayPnl(LocalDate date) {
        for (Component day1 : this.calendar.getComponents() ) {
        	Component day = ((JScrollPane)day1).getViewport().getView();
            LocalDate d = LocalDate.parse(day.getName());
            if (d.getMonth().equals(super.getDate().getMonth()) && d.getDayOfMonth() == date.getDayOfMonth()) {
                return (JPanel) ((JPanel) day).getComponent(1);
            }
        }
        return null;
    }

    
    
    private Boolean shouldBeDisplayedOnPanel(News m) 
    {
//    	if( settingsofnewsdisplay.shouldDisplayAllExtraNews() )
//    		return true;
//    	
////    	String ownersid = m.getNewsOwnerCodes();
//    	if(m.getNewsType() == News.JINQIGUANZHU ) {
//    		if( settingsofnewsdisplay.shouldDisplayGuanZhuNews() )
//    			return true;
//    		else
//    			return false;
//    	}
//    	
//    	if(m.getNewsType() == News.QIANSHI || m.getNewsType() == News.RUOSHI ) {
//    		if( settingsofnewsdisplay.shouldDisplayQiangRuoNews() )
//    			return true;
//    		else
//    			return false;
//    	}
//    	
//    	if(m.getNewsType() == News.CHANGQIJILU  ) {
//    		if( settingsofnewsdisplay.shouldDisplayMonthNews() )
//    			return true;
//    		else
//    			return false;
//    	}
//		
//    	if(m.getNewsType() == News.ZHISHUDATE  ) {
//    		if( settingsofnewsdisplay.shouldDisplayZhishuDate() )
//    			return true;
//    		else
//    			return false;
//    	}
//    	
    	return true;
	}
	@Override
    public void onLabelChange(Collection<NewsCache> cache) {
        this.onNewsChange(cache);
    }
    /*
     * 
     */
    private class CreateNewsController extends MouseAdapter 
    { 
        @Override
        public void mouseClicked(MouseEvent e) {
               	super.mouseClicked(e);
            JPanel panel = (JPanel) e.getSource();
            LocalDate mDate = LocalDate.parse(panel.getName());
            LocalDateTime dateTime = LocalDateTime.of(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), 8, 0);
            
            
        	if (e.getClickCount() == 1) { //获取选择的日期
        		setDate (mDate);
        	}
        	
        	if (e.getClickCount() == 2) { //增加一个新的News
                News News = new News("新闻标题",mDate,"描述", "", new HashSet<InsertedNews.Label>(),"URL","000000");
                
                CreateNewsWithFurtherOperationDialog cnd = new CreateNewsWithFurtherOperationDialog (svsns);
                cnd.setNews(News);
                cnd.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                cnd.setVisible(true);
        	}
        }
    }

    private class ReviseExternalNewsController extends MouseAdapter 
    {
    	private ServicesForNews service;
		public ReviseExternalNewsController (ServicesForNews service)
    	{
    		this.service = service;
    	}
        @Override
        public void mouseClicked(MouseEvent e) {

			 super.mouseClicked(e);
	            JLabel label = (JLabel) e.getSource();
	            String labelname = label.getName();
	            
	            InsertedExternalNews n = null ;
	            Collection<News> cqjlist = service.getCache().produceNews();
	            for (Iterator<News> it = cqjlist.iterator(); it.hasNext(); ) {
	            	News f = it.next();
	            	Integer dbid  = ((InsertedExternalNews)f).getID ();
	            	
	            	if(dbid == Integer.parseInt(labelname)) {
	            		n = (InsertedExternalNews) f;
	            		break;
	            	}
	            	
	            }
	            
	            ModifyExternalNewsDialog mod = new ModifyExternalNewsDialog (service);
	            mod.setNews(n);
	            mod.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	            mod.setVisible(true);
        }
    }
    private class ReviseNewsController extends MouseAdapter 
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            JLabel label = (JLabel) e.getSource();
            String labelname = label.getName();
            
            InsertedNews n = null ;
            Collection<News> cqjlist = svsns.getCache().produceNews();
            for (Iterator<News> it = cqjlist.iterator(); it.hasNext(); ) {
            	News f = it.next();
            	Integer dbid  = ((InsertedNews)f).getID ();
            	
            	if(dbid == Integer.parseInt(labelname)) {
            		n = (InsertedNews) f;
            		break;
            	}
            	
            }
            
            ModifyNewsWithFurtherOperationDialog mod = new ModifyNewsWithFurtherOperationDialog (svsns);
            mod.setNews(n);
            mod.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
            mod.setVisible(true);
            
          
            
//            Optional<InsertedNews> News = getCache().produceNewss()
//                                                  .stream()
//                                                  .filter(   label.getName().equals(m -> (m.getNewsType() + m.getID() )) )
//                                                  .findFirst();
//            News News = null;
//            Collection<InsertedNews> Newsslist = getCache().produceNewss();
//            for(InsertedNews m : Newsslist) {
//            	String searchname = String.valueOf(m.getNewsType()  ) + String.valueOf(m.getID() );
//            	if(searchname.equals(labelname)) {
//            		News = m;
//            		break;
//            	}
//            }
            
        }

    }

}
