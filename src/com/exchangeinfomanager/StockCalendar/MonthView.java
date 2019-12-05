package com.exchangeinfomanager.StockCalendar;

import javax.swing.*;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.InsertedNews.Label;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.Services.ServicesForNews;
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
    
    public MonthView( Collection<ServicesForNews> newssvs, SettingOfDisplayNewsArea settingsofnewsdisplay) 
    {
        super(newssvs);
        this.settingsofnewsdisplay = settingsofnewsdisplay;
        
//        Collection<NewsCache> caches = new HashSet<> ();
        for (Iterator<ServicesForNews> lit = newssvs.iterator(); lit.hasNext(); ) {
    		ServicesForNews f = lit.next();
    		f.getCache().addCacheListener(this);
    		
//    		caches.add(f.getCache());
    	}
        
        this.onNewsChange(caches); //获取本月的信息
        this.initMonthView();
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

    private JPanel getWeekdaysPanel() {

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

    @Override
    /*
     * (non-Javadoc)
     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener#onNewsChange(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache)
     */
    public void onNewsChange(Collection<NewsCache> caches) 
    {
    	LocalDate firstdayofmonth = this.initView();
    	
//    	for (Iterator<NewsCache> lit = caches.iterator(); lit.hasNext(); ) {
//    		NewsCache f = lit.next();
//    		
//    		createNewsInPnl (f,firstdayofmonth);
//    	}

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
                	JUpdatedLabel label = new JUpdatedLabel(m.getTitle());
                    
                    label.setToolTipText( getLabelToolTipText(m) );
                    label.setOpaque(true);
                    label.setName( m.getClass().toString() + String.valueOf(dbid));
                    label.addMouseListener(new ReviseNewsController());
                    label.setForeground(ColorScheme.BLACK_FONT);
                    label.setBackground(ColorScheme.GREY_WHITER_DARKER);
                    label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                    this.getMonthDayPnl(mDate).add(label);
                    continue;
                }
                
                for (News.Label l : labels) { //有LABEL的情况
                    if ( m.getLabels().contains(l)) {
                    	JUpdatedLabel label = new JUpdatedLabel(m.getTitle());
                        label.setToolTipText(getLabelToolTipText(m) );
                        label.setOpaque(true);
                        label.setName( m.getClass().toString() + String.valueOf(dbid));
                        label.addMouseListener(new ReviseNewsController());
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
        	
//        	if (e.getClickCount() == 2) { //增加一个新的News
//                News News = new News("新闻标题",mDate,
//                     "描述", "关键词", new HashSet<>(),"SlackURL","000000",News.NODESNEWS);
//                getCreateDialog().setNews(News);
//                getCreateDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
//                getCreateDialog().setVisible(true);
//        	}
            
        }
    }

	
    private class ReviseNewsController extends MouseAdapter 
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            JLabel label = (JLabel) e.getSource();
            String labelname = label.getName();
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
            
////            JLabel source = (JLabel) e.getSource();
//            String title = label.getText();
//            LocalDate date = LocalDate.parse(label.getParent().getParent().getName());
//            
////            InsertedNews selectedNews = News.get();
////            String owner = selectedNews.getNewsOwnerCodes();
//            if( News.getNewsType() == News.WKZONGJIE ) { //说明是大盘一周总结，内容是XML，用DaPanWeeklyFengXi显示内容
//            	showWeeklyFenXiWizardDialog  ("000000",date);
//            } else {
//            	getModifyDialog().setNews(News);
//            	getModifyDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
//                getModifyDialog().setVisible(true);
//            }
            
        }

    }
    /*
	 * 
	 */
	private void showWeeklyFenXiWizardDialog(String nodeshouldbedisplayed, LocalDate selectdate) 
	{
		WeeklyFenXiWizard ggfx = new WeeklyFenXiWizard ( nodeshouldbedisplayed,BkChanYeLianTreeNode.DAPAN,selectdate);
    	ggfx.setSize(new Dimension(1400, 800));
    	ggfx.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else
    	ggfx.setLocationRelativeTo(null);
    	if(!ggfx.isVisible() ) 
    		ggfx.setVisible(true);
    	ggfx.toFront();
    	
    	ggfx = null;
	}
	@Override
	public void onNewsAdded(News m) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNewsChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLabelChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}
	
	



}
