package com.exchangeinfomanager.StockCalendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting.Label;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
/**
 * 
 * @author Administrator
 * 每年每个月需要显示的news放在这个view,同时还放每月强势股/板块和弱势股/板块 
 *
 */
public class WholeMonthNewsView extends View 
{
	private JPanel wholemonthpnl = new JPanel(); //
	
	public WholeMonthNewsView(Collection<ServicesForNews> newssvs) 
	{
	        super(newssvs);
	        
	        Collection<NewsCache> caches = new HashSet<> ();
	        for (Iterator<ServicesForNews> lit = newssvs.iterator(); lit.hasNext(); ) {
	    		ServicesForNews f = lit.next();
	    		f.getCache().addCacheListener(this);
	    		
	    		caches.add(f.getCache());
	    	}
	        
	        this.onNewsChange(caches); //获取本月的信息
	        
	        this.initWholeMonthView();
	}
	/*
	 * 
	 */
    private void initWholeMonthView() 
    {
      this.wholemonthpnl.setLayout(new GridLayout(1, 3));
      this.wholemonthpnl.setBackground(ColorScheme.GREY_LINE);
      
      super.setBackground(ColorScheme.BACKGROUND);
      super.setLayout(new BorderLayout());
      super.add(Box.createVerticalStrut(10));
      super.add(this.wholemonthpnl,BorderLayout.CENTER);
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
    		
    		ServicesForNews svs = f.getServicesForNews ();
    		if(svs instanceof ChangQiGuanZhuServices)
    			updateChangQiJiLuPnl (f, firstdayofmonth);
    		
    		if(svs instanceof QiangShiServices )
    			updateQiangRuoShiDQGZPnl (f, firstdayofmonth,firstdayofmonth + "QIANGSHI");
    		
    		if(svs instanceof DuanQiGuanZhuServices )
    			updateQiangRuoShiDQGZPnl (f, firstdayofmonth, firstdayofmonth + "DUANQIGUANZHU");
    		
    		if(svs instanceof RuoShiServices )
    			updateQiangRuoShiDQGZPnl (f, firstdayofmonth, firstdayofmonth + "RUOSHI");
    		
    	}

        this.wholemonthpnl.validate();
        this.wholemonthpnl.repaint();
    }
    /*
     * 
     */
    private void updateQiangRuoShiDQGZPnl(NewsCache cache, LocalDate firstdayofmonth,String pnlname) 
    {
    	Collection<News> meetings = cache.produceNews(firstdayofmonth);
        Collection<InsertedNews.Label> labels = cache.produceLabels();
        
        for (News m : meetings) {
            LocalDate mDate = m.getStart();
            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
            
            if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  )  //强势 弱势板块 需要年月相等即可
            	continue;
            
            if (m.getLabels().isEmpty()) {
            	JLabel label = getFormatedLabelForNoneLabel (m);
                try{
                	this.getRequiredPanel(pnlname).add(label);
                } catch (java.lang.NullPointerException e) {
                	e.printStackTrace();
                }
                continue;
            }
            
            for (News.Label l : labels) {
                if ( !m.getLabels().contains(l))
                	continue;
                
                	JLabel label = getFormatedLabelForWithLabels (m,l);
                    try {
                    	this.getRequiredPanel(pnlname).add(label);
                    } catch (java.lang.NullPointerException e) {
                    	e.printStackTrace();
                    }
                    break;
            }
        }
	}
	/*
     * 
     */
    private void updateChangQiJiLuPnl (NewsCache cache,LocalDate firstdayofmonth)
    {
    	Collection<News> meetings = cache.produceNews(firstdayofmonth);
        Collection<InsertedNews.Label> labels = cache.produceLabels();

        for (News m : meetings) {
            LocalDate mDate = m.getStart();
            String actioncode = m.getNewsOwnerCodes();
            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
            
        	if ( !mDate.getMonth().equals(super.getDate().getMonth())  ) 
        		continue;
        	
            if (m.getLabels().isEmpty()) {
                	JUpdatedLabel label = getFormatedLabelForNoneLabel (m);
                   	this.getRequiredPanel(firstDayInMonth + "CHANGQIGUANZHU").add(label);
                    continue;
            }
                
            for (News.Label l : labels) {
                    if (m.getLabels().contains(l)) {
                    	JUpdatedLabel label = getFormatedLabelForWithLabels (m,l);
                       	this.getRequiredPanel(firstDayInMonth + "CHANGQIGUANZHU").add(label);
                        continue;
                    }
            }
        }
    }
    private JUpdatedLabel getFormatedLabelForWithLabels(News m, News.Label l) 
    {
    	Integer dbid = null ;
		if( m instanceof InsertedNews ) 
			dbid = ((InsertedNews)m).getID();
		else if (m instanceof InsertedExternalNews )
			dbid = ((InsertedExternalNews)m).getID ();
		
    	JUpdatedLabel label = new JUpdatedLabel(m.getTitle());
        label.setToolTipText( getLabelToolTipText(m) );
        label.setOpaque(true);
        label.setName( m.getClass().toString() + dbid );
        label.addMouseListener(new ReviseMeetingController());
        label.setForeground(Color.BLACK);
        label.setBackground(l.getColor());
        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
		return label;
	}

	private JUpdatedLabel getFormatedLabelForNoneLabel(News m) 
    {
		Integer dbid = null ;
		if( m instanceof InsertedNews ) 
			dbid = ((InsertedNews)m).getID();
		else if (m instanceof InsertedExternalNews )
			dbid = ((InsertedExternalNews)m).getID ();
		
		JUpdatedLabel label = new JUpdatedLabel(m.getTitle());
        label.setToolTipText( getLabelToolTipText(m) );
        label.setOpaque(true);
        label.setName( m.getClass().toString() + dbid );
        label.addMouseListener(new ReviseMeetingController());
        label.setForeground(ColorScheme.BLACK_FONT);
        label.setBackground(ColorScheme.GREY_WHITER_DARKER);
        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
		return label;
	}
	 /*
     * 
     */
    private JPanel getRequiredPanel(String pnlname) 
    {
        for (Component day1 : this.wholemonthpnl.getComponents() ) {
        	Component comp = ((JScrollPane)day1).getViewport().getView();
        	String compname = comp.getName();
        	if(compname != null && compname.equals(pnlname)) {
        		return (JPanel)comp;
        	}
        }
        return null;
    }
    /*
     * 初始化monthview的新闻
     */
    private LocalDate initView() 
    {
        this.wholemonthpnl.removeAll();
        
        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
        //每月新闻
       	JScrollPane sclpmonthnews = new JScrollPane ();
       	JPanel pnlmonthnews = new JPanel();
       	pnlmonthnews.setName(firstDayInMonth + "CHANGQIGUANZHU");
        pnlmonthnews.setLayout(new BorderLayout());
        pnlmonthnews.setBackground(ColorScheme.BACKGROUND);
//        pnlmonthnews.addMouseListener(new CreateCQJLController(firstDayInMonth));
                
        JLabel newscontentsLabel = new JLabel(String.valueOf(firstDayInMonth.getMonth()));
        newscontentsLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
        newscontentsLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
        newscontentsLabel.setBackground(ColorScheme.BACKGROUND);
        newscontentsLabel.setOpaque(true);
        
        pnlmonthnews.add(newscontentsLabel, BorderLayout.NORTH);
        pnlmonthnews.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel

        sclpmonthnews.setViewportView (pnlmonthnews);
        
        	//强势板块个股
            JScrollPane sclpqiangshibk = new JScrollPane ();
           	JPanel pnlqiangshibk = new JPanel();
           	pnlqiangshibk.setName(firstDayInMonth + "QIANGSHI");
           	pnlqiangshibk.setLayout(new BorderLayout());
           	pnlqiangshibk.setBackground(ColorScheme.BACKGROUND);
           	pnlqiangshibk.addMouseListener(new CreateQiangShiController(firstDayInMonth));
            
           	JUpdatedLabel qiangshibkLabel = new JUpdatedLabel("强势板块/个股");
            qiangshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
            qiangshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
            qiangshibkLabel.setBackground(ColorScheme.BACKGROUND);
            qiangshibkLabel.setOpaque(true);
            
            pnlqiangshibk.add(qiangshibkLabel, BorderLayout.NORTH);
            pnlqiangshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel

            sclpqiangshibk.setViewportView (pnlqiangshibk);
           	//弱势板块个股
           	JScrollPane sclpruoshibk = new JScrollPane ();
           	JPanel pnlruoshibk = new JPanel();
           	pnlruoshibk.setName(firstDayInMonth  + "RUOSHI");
           	pnlruoshibk.setLayout(new BorderLayout());
           	pnlruoshibk.setBackground(ColorScheme.BACKGROUND);
           	pnlruoshibk.addMouseListener(new CreateRuoShiController(firstDayInMonth));
           	
           	JUpdatedLabel ruoshibkLabel = new JUpdatedLabel("弱势板块/个股");
           	ruoshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
           	ruoshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
           	ruoshibkLabel.setBackground(ColorScheme.BACKGROUND);
           	ruoshibkLabel.setOpaque(true);
           	
            pnlruoshibk.add(ruoshibkLabel, BorderLayout.NORTH);
            pnlruoshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
           	pnlruoshibk.setBackground(ColorScheme.BACKGROUND);
           	
           	sclpruoshibk.setViewportView (pnlruoshibk);
           	
           	//最近应该关注板块
           	JScrollPane sclpguanzhubk = new JScrollPane ();
           	JPanel pnlpguanzhubk = new JPanel();
           	pnlpguanzhubk.setName(firstDayInMonth  + "DUANQIGUANZHU");
           	pnlpguanzhubk.setLayout(new BorderLayout());
           	pnlpguanzhubk.setBackground(ColorScheme.BACKGROUND);
           	pnlpguanzhubk.addMouseListener(new CreateDQGZController(firstDayInMonth));
           	
           	JUpdatedLabel guanzhubkLabel = new JUpdatedLabel("近期关注板块个股");
           	guanzhubkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
           	guanzhubkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
           	guanzhubkLabel.setBackground(ColorScheme.BACKGROUND);
           	guanzhubkLabel.setOpaque(true);
           	
           	pnlpguanzhubk.add(guanzhubkLabel, BorderLayout.NORTH);
           	pnlpguanzhubk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
           	pnlpguanzhubk.setBackground(ColorScheme.BACKGROUND);
           	
           	sclpguanzhubk.setViewportView (pnlpguanzhubk);

            this.wholemonthpnl.add(sclpmonthnews);
            this.wholemonthpnl.add(sclpqiangshibk);
            this.wholemonthpnl.add(sclpruoshibk);
            this.wholemonthpnl.add(sclpguanzhubk);
            
            return firstDayInMonth;
    }
//    /*
//     * //每月新闻只要月一致就可以
//     */
//    private JPanel getWholeMonthNewsPanel(String actiondateandcode) 
//    {
//  	
//        for (Component day1 : this.wholemonthpnl.getComponents() ) {
//        	Component day = ((JScrollPane)day1).getViewport().getView();
////        	String componentname = day.getName();
//            LocalDate d = LocalDate.parse(day.getName().substring(0, 10));
//
//            if (d.getMonth().equals(super.getDate().getMonth()) ) { //每月新闻只要月一致就可以
//                return (JPanel) ((JPanel) day).getComponent(1);
//            }
//        }
//        return null;
//    }
   

    

//    /*
//     * (non-Javadoc)
//     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener#onMeetingChange(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache)
//     * 更新所有month view的新闻
//     */
//    public void createNewsInPnl(NewsCache f,LocalDate firstdayofmonth) 
//    {
//        Collection<News> meetings = f.produceNews(firstdayofmonth);
//        Collection<InsertedNews.Label> labels = f.produceLabels();
//
//        
//        for (News m : meetings) {
//            LocalDate mDate = m.getStart();
//            String actioncode = m.getNewsOwnerCodes();
//            
//            if(m.getMeetingType() == Meeting.CHANGQIJILU) { // 每月固定新闻
//            	updateChangQiJiLuPnl (f, m);
//            	
//            } else if(m.getMeetingType() == Meeting.JINQIGUANZHU) { // 关注板块
//            	
//            	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) { //强势 弱势板块 需要年月相等即可 
//                    if (m.getLabels().isEmpty()) {
//                    	JLabel label = getFormatedLabelForNoneLabel (m);
//                        try{
//                        	this.getQiangRuoShiPanel(actioncode).add(label);
//                        } catch (java.lang.NullPointerException e) {
//                        	e.printStackTrace();
//                        }
//                        
//                        continue;
//                    }
//                    for (Meeting.Label l : labels) {
//                        if (l.isActive() && m.getLabels().contains(l)) {
//                        	JLabel label = getFormatedLabelForWithLabels (m,l);
//                            try {
//                            	this.getQiangRuoShiPanel(actioncode).add(label);
//                            } catch (java.lang.NullPointerException e) {
//                            	e.printStackTrace();
//                            }
//                            
//                            break;
//                        }
//                    }
//                }
//            	
//            } else if(m.getMeetingType() == Meeting.QIANSHI  ) { // 强势 弱势 关注板块
//            	String searchingpnlid = "qqqqqq";
//
//            	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) { //强势 弱势板块 需要年月相等即可 
//                        if (m.getLabels().isEmpty()) {
//                        	JLabel label = getFormatedLabelForNoneLabel (m);
//                            try{
//                            	this.getQiangRuoShiPanel(searchingpnlid).add(label);
//                            } catch (java.lang.NullPointerException e) {
//                            	e.printStackTrace();
//                            }
//                            
//                            continue;
//                        }
//                        for (Meeting.Label l : labels) {
//                            if (l.isActive() && m.getLabels().contains(l)) {
//                            	JLabel label = getFormatedLabelForWithLabels (m,l);
//                                try {
//                                	this.getQiangRuoShiPanel(searchingpnlid).add(label);
//                                } catch (java.lang.NullPointerException e) {
//                                	e.printStackTrace();
//                                }
//                                
//                                break;
//                            }
//                        }
//                    }
//                	
//                } else if(m.getMeetingType() == Meeting.RUOSHI  ) { // 强势 弱势 关注板块
//                    	String searchingpnlid = "rrrrrr";
//                        	
//                        	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) { //强势 弱势板块 需要年月相等即可 
//                                if (m.getLabels().isEmpty()) {
//                                	JLabel label = getFormatedLabelForNoneLabel (m);
//                                    try{
//                                    	this.getQiangRuoShiPanel(searchingpnlid).add(label);
//                                    } catch (java.lang.NullPointerException e) {
//                                    	e.printStackTrace();
//                                    }
//                                    
//                                    continue;
//                                }
//                                for (Meeting.Label l : labels) {
//                                    if (l.isActive() && m.getLabels().contains(l)) {
//                                    	JLabel label = getFormatedLabelForWithLabels (m,l);
//                                        try {
//                                        	this.getQiangRuoShiPanel(searchingpnlid).add(label);
//                                        } catch (java.lang.NullPointerException e) {
//                                        	e.printStackTrace();
//                                        }
//                                        
//                                        break;
//                                    }
//                                }
//                            }
//                        	
//                        } 
//
//            
//        }
//
// 
//    }

//  
//
//	@Override
//    public void onLabelChange(Cache cache) {
//        this.onMeetingChange(cache);
//    }

	private class CreateCQJLController extends MouseAdapter
	{
		public CreateCQJLController(LocalDate firstDayInMonth) {
			// TODO Auto-generated constructor stub
		}

		@Override
        public void mouseClicked(MouseEvent e) {
			
		}
	}
	private class CreateQiangShiController extends MouseAdapter
	{
		public CreateQiangShiController(LocalDate firstDayInMonth) {
			// TODO Auto-generated constructor stub
		}

		@Override
        public void mouseClicked(MouseEvent e) {
			
		}
	}
	private class CreateRuoShiController extends MouseAdapter
	{
		public CreateRuoShiController(LocalDate firstDayInMonth) {
			// TODO Auto-generated constructor stub
		}

		@Override
        public void mouseClicked(MouseEvent e) {
			
		}
	}
	private class CreateDQGZController extends MouseAdapter
	{
		public CreateDQGZController(LocalDate firstDayInMonth) {
			// TODO Auto-generated constructor stub
		}

		@Override
        public void mouseClicked(MouseEvent e) {
			
		}
	}
	
	
    private class WholeMonthCreateNewsController extends MouseAdapter 
    { 
    	private int meetingtype;
//    	private LocalDate createdate ;
    	
    	WholeMonthCreateNewsController (int meetingtype, LocalDate monthbelonged)
    	{
    		this.meetingtype = meetingtype;
    	}
        @Override
        public void mouseClicked(MouseEvent e) {
        	
        	super.mouseClicked(e);
            JPanel panel = (JPanel) e.getSource();
            LocalDate mDate ;
            LocalDate pnldate = LocalDate.parse(panel.getName().substring(0, 10));
            if ( pnldate.getMonth().equals(LocalDate.now().getMonth() ) )
            	mDate = LocalDate.now();
            else
            	mDate = pnldate;

            
//        	if (e.getClickCount() == 1) { //获取选择的日期
//        		setDate (mDate);
//        	}
        	
        	if (e.getClickCount() == 2) { //增加一个新的meeting
        		
        		String title = null;String owner = null; String keywords = null;
        		if(this.meetingtype == Meeting.CHANGQIJILU) {
        			title = "长期记录";
        			owner = "wwwwww";
        			keywords = "关键词";
        		} else if(this.meetingtype == Meeting.QIANSHI) {
        			title = "强势个股板块";
        			owner = "个股板块代码";
        			keywords = "强势个股板块";
        		}  else if(this.meetingtype == Meeting.RUOSHI) {
        			title = "弱势个股板块";
        			owner = "个股板块代码";
        			keywords = "弱势个股板块"; 
        		} else if(this.meetingtype == Meeting.JINQIGUANZHU) {
        			title = "近期关注";
        			owner = "gzgzgz";
        			keywords = "近期关注"; 
        		}  
        		
        		//Set up description of all GPC
//        		String descriptions = "";
//        		BanKuaiAndStockTree cyltree = BanKuaiAndChanYeLian2.getInstance().getBkChanYeLianTree();
//        		int bankuaicount = cyltree.getModel().getChildCount(cyltree.getModel().getRoot());
//    			for(int i=0;i< bankuaicount; i++) {
//    				
//    				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)cyltree.getModel().getChild(cyltree.getModel().getRoot(), i);
//    				String nodename = childnode.getMyOwnName();
//    				String nodecode = childnode.getMyOwnCode();
//    				descriptions = descriptions + nodecode.toUpperCase() + "-" + nodename +   "\n";
//    			}
        		
//                Meeting meeting = new Meeting(title, mDate,
//                		"", keywords, new HashSet<>(),"URL",owner,this.meetingtype);
//                getCreateDialog().setMeeting(meeting);
//                getCreateDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
//                getCreateDialog().setVisible(true);
        	}
            
        }
    }
	
    private class ReviseMeetingController extends MouseAdapter 
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
//            JLabel label = (JLabel) e.getSource();
//            String labelname = label.getName();
//            Meeting meeting = null;
//            Collection<InsertedMeeting> meetingslist = getCache().produceMeetings();
//            for(InsertedMeeting m : meetingslist) {
//            	String searchname = String.valueOf(m.getMeetingType()  ) + String.valueOf(m.getID() );
//            	
//				if(searchname.equals(labelname)) {
//					meeting = m;
//					break;
//            	}
//            }
            
//            getModifyDialog().setMeeting(meeting);
//            getModifyDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
//            getModifyDialog().setVisible(true);
            
//            Optional<InsertedMeeting> meeting = getCache().produceMeetings()
//                                                  .stream()
//                                                  .filter(m -> m.getID() == Integer.valueOf(label.getName()))
//                                                  .findFirst();
//
//            if (meeting.isPresent()) {
//                JLabel source = (JLabel) e.getSource();
////                LocalDate date = LocalDate.parse(source.getParent().getParent().getName());
//                getModifyDialog().setMeeting(meeting.get());
//                getModifyDialog().setVisible(true);
//            }
        }

    }
    
	@Override
	public void onNewsChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(Collection<NewsCache> cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsAdded(News m) {
		// TODO Auto-generated method stub
		
	}
}



