package com.exchangeinfomanager.StockCalendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


import javax.swing.BorderFactory;
import javax.swing.Box;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.CreateExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.ExternalNewsType;
import com.exchangeinfomanager.News.ExternalNewsType.GuanZhu;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.News.ExternalNewsType.ModifyExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;

import com.exchangeinfomanager.commonlib.WrapLayout;

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
	private static final Dimension SIZE = new Dimension(1050, 70);
    private static final Dimension MIN_SIZE = new Dimension(1050, 70);
    
	private JPanel wholemonthpnl = new JPanel(); //
	
	private ChangQiGuanZhuServices svscqgz;
	private QiangShiServices svsqs;
	private RuoShiServices svsrs;
	private DuanQiGuanZhuServices svsdqgz;
	private ZhiShuBoLangServices svszsbl;
	
	private WholeMonthNewsComponentsView cqjlview;

	private WholeMonthNewsComponentsView qsview;

	private WholeMonthNewsComponentsView rsview;

	private WholeMonthNewsComponentsView dqgz;

	private WholeMonthNewsComponentsView dqgzview;
	
	private WholeMonthNewsComponentsView zsblview;
	
	
	public WholeMonthNewsView(Collection<ServicesForNews> newssvs) 
	{
	        super(newssvs);
	        
	        Collection<NewsCache> caches = new HashSet<> ();
	        for (Iterator<ServicesForNews> lit = newssvs.iterator(); lit.hasNext(); ) {
	    		ServicesForNews f = lit.next();
//	    		f.getCache().addCacheListener(this);
//	    		
//	    		caches.add(f.getCache());
	    		
	    		if( f instanceof ChangQiGuanZhuServices) {
	    			svscqgz = (ChangQiGuanZhuServices) f;
	    			
	    			LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
	    			cqjlview = new WholeMonthNewsComponentsView (svscqgz, "长期关注" + String.valueOf(firstDayInMonth.getMonth()) );
	    		} else
	    		if( f instanceof QiangShiServices) {
	    			svsqs = (QiangShiServices) f;
	    			qsview = new WholeMonthNewsComponentsView (svsqs, "强势板块个股");
	    		} else
	    		if( f instanceof RuoShiServices) {
	    			svsrs = (RuoShiServices) f;
	    			rsview = new WholeMonthNewsComponentsView (svsrs, "弱势板块个股");
	    		} else
	    		if( f instanceof DuanQiGuanZhuServices) {
	    			svsdqgz = (DuanQiGuanZhuServices) f;
	    			dqgzview = new WholeMonthNewsComponentsView (svsdqgz, "近期关注");
	    		} else
	    		if( f instanceof ZhiShuBoLangServices) {
	    			svszsbl = (ZhiShuBoLangServices) f;
	    			zsblview = new WholeMonthCompViewOfZSBL (svszsbl, "指数关键日期");
	    			
	    		}
	    	}
	        
	        
//	        this.onNewsChange(caches); //获取本月的信息
	        
	        this.initWholeMonthView();
	}
	/*
	 * 
	 */
    private void initWholeMonthView() 
    {
    	this.wholemonthpnl.add(cqjlview);
    	this.wholemonthpnl.add(dqgzview);
    	this.wholemonthpnl.add(qsview);
        this.wholemonthpnl.add(rsview);
        this.wholemonthpnl.add(zsblview);
        
        int cachesize = super.caches.size();
      this.wholemonthpnl.setLayout(new GridLayout(1, super.caches.size()));
      this.wholemonthpnl.setBackground(ColorScheme.GREY_LINE);
     
      super.setBackground(ColorScheme.BACKGROUND);
      super.setLayout(new BorderLayout());
      super.add(Box.createVerticalStrut(10));
      super.add(this.wholemonthpnl,BorderLayout.CENTER);
      
      this.setPreferredSize(SIZE);
      this.setMinimumSize(MIN_SIZE);
    }
    @Override
    /*当日期变化时候，通知几个下面的comp同时刷新相关数据
     * (non-Javadoc)
     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener#onNewsChange(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache)
     */
    public void onNewsChange(Collection<NewsCache> caches) 
    {
    	cqjlview.setDate(this.getDate());
    	dqgzview.setDate(this.getDate());
    	qsview.setDate(this.getDate());
        rsview.setDate(this.getDate());
        zsblview.setDate(this.getDate());
//    	LocalDate firstdayofmonth = this.initView();
//    	
//    	for (Iterator<NewsCache> lit = caches.iterator(); lit.hasNext(); ) {
//    		NewsCache f = lit.next();
//    		
//    		ServicesForNews svs = f.getServicesForNews ();
////    		if(svs instanceof ChangQiGuanZhuServices)
////    			updateChangQiJiLuPnl (f, firstdayofmonth);
//    		
//    		if(svs instanceof QiangShiServices )
//    			updateQiangRuoShiDQGZPnl (f, firstdayofmonth,firstdayofmonth + "QIANGSHI");
//    		
//    		if(svs instanceof DuanQiGuanZhuServices )
//    			updateQiangRuoShiDQGZPnl (f, firstdayofmonth, firstdayofmonth + "DUANQIGUANZHU");
//    		
//    		if(svs instanceof RuoShiServices )
//    			updateQiangRuoShiDQGZPnl (f, firstdayofmonth, firstdayofmonth + "RUOSHI");
//    		
//    	}
//    	
//    	
//    	
//        this.wholemonthpnl.validate();
//        this.wholemonthpnl.repaint();
    }
    /*
     * 
     */
//    private void updateQiangRuoShiDQGZPnl(NewsCache cache, LocalDate firstdayofmonth,String pnlname) 
//    {
//    	Collection<News> meetings = cache.produceNews(firstdayofmonth);
//        Collection<InsertedNews.Label> labels = cache.produceLabels();
//        
//        LocalDate currentdisplaydate = super.getDate();
//        for (News m : meetings) {
//            LocalDate mDate = m.getStart();
//            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
//            
//            if ( mDate.getYear() != super.getDate().getYear()    )  //强势 弱势板块 需要年月相等即可
//            	continue;
//            if( !mDate.getMonth().equals(super.getDate().getMonth()) )
//            	continue;
//            
//            if (m.getLabels().isEmpty()) {
//            	JLabel label = getFormatedLabelForNoneLabel (m);
//                try{
//                	this.getRequiredPanel(pnlname).add(label);
//                } catch (java.lang.NullPointerException e) {
//                	e.printStackTrace();
//                }
//                continue;
//            }
//            
//            for (News.Label l : labels) {
//                if ( !m.getLabels().contains(l))
//                	continue;
//                
//                	JLabel label = getFormatedLabelForWithLabels (m,l);
//                    try {
//                    	this.getRequiredPanel(pnlname).add(label);
//                    } catch (java.lang.NullPointerException e) {
//                    	e.printStackTrace();
//                    }
//                    break;
//            }
//        }
//	}
//	/*
//     * 
//     */
//    private void updateChangQiJiLuPnl (NewsCache cache,LocalDate firstdayofmonth)
//    {
//    	
//    	Collection<News> meetings = cache.produceNews(firstdayofmonth);
//        Collection<InsertedNews.Label> labels = cache.produceLabels();
//
//        for (News m : meetings) {
//            LocalDate mDate = m.getStart();
//            String actioncode = m.getNewsOwnerCodes();
//            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
//            
//        	if ( !mDate.getMonth().equals(super.getDate().getMonth())  ) 
//        		continue;
//        	
//            if (m.getLabels().isEmpty()) {
//                	JUpdatedLabel label = getFormatedLabelForNoneLabel (m);
//                   	this.getRequiredPanel(firstDayInMonth + "CHANGQIGUANZHU").add(label);
//                    continue;
//            }
//                
//            for (News.Label l : labels) {
//                    if (m.getLabels().contains(l)) {
//                    	JUpdatedLabel label = getFormatedLabelForWithLabels (m,l);
//                       	this.getRequiredPanel(firstDayInMonth + "CHANGQIGUANZHU").add(label);
//                        continue;
//                    }
//            }
//        }
//    }
//    private JUpdatedLabel getFormatedLabelForWithLabels(News m, News.Label l) 
//    {
//    	Integer dbid = null ;
//		if( m instanceof InsertedNews ) 
//			dbid = ((InsertedNews)m).getID();
//		else if (m instanceof InsertedExternalNews )
//			dbid = ((InsertedExternalNews)m).getID ();
//		
//    	JUpdatedLabel label = new JUpdatedLabel(m.getTitle());
//        label.setToolTipText( getLabelToolTipText(m) );
//        label.setOpaque(true);
//        label.setName( String.valueOf(  dbid ) );
//        label.addMouseListener(new ReviseMeetingController());
//        label.setForeground(Color.BLACK);
//        label.setBackground(l.getColor());
//        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
//        
//		return label;
//	}
//
//	private JUpdatedLabel getFormatedLabelForNoneLabel(News m) 
//    {
//		Integer dbid = null ;
//		if( m instanceof InsertedNews ) 
//			dbid = ((InsertedNews)m).getID();
//		else if (m instanceof InsertedExternalNews )
//			dbid = ((InsertedExternalNews)m).getID ();
//		
//		JUpdatedLabel label = new JUpdatedLabel(m.getTitle());
//        label.setToolTipText( getLabelToolTipText(m) );
//        label.setOpaque(true);
//        label.setName( String.valueOf( dbid ) );
//        label.addMouseListener(new ReviseMeetingController());
//        label.setForeground(ColorScheme.BLACK_FONT);
//        label.setBackground(ColorScheme.GREY_WHITER_DARKER);
//        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
//        
//		return label;
//	}
//	 /*
//     * 
//     */
//    private JPanel getRequiredPanel(String pnlname) 
//    {
//        for (Component day1 : this.wholemonthpnl.getComponents() ) {
//        	Component comp = ((JScrollPane)day1).getViewport().getView();
//        	String compname = comp.getName();
//        	if(compname != null && compname.equals(pnlname)) {
//        		return (JPanel) ((JPanel) comp).getComponent(1);
//        	}
//        }
//        return null;
//    }
//    /*
//     * 初始化monthview的新闻
//     */
//    private LocalDate initView () 
//    {
//        this.wholemonthpnl.removeAll();
//        
//        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
//        //每月新闻
////       	JScrollPane sclpmonthnews = new JScrollPane ();
////       	JPanel pnlmonthnews = new JPanel();
////       	pnlmonthnews.setName(firstDayInMonth + "CHANGQIGUANZHU");
////        pnlmonthnews.setLayout(new BorderLayout());
////        pnlmonthnews.setBackground(ColorScheme.BACKGROUND);
////        pnlmonthnews.addMouseListener(new CreateCQJLController(firstDayInMonth));
////                
////        JLabel newscontentsLabel = new JLabel(String.valueOf(firstDayInMonth.getMonth()));
////        newscontentsLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
////        newscontentsLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
////        newscontentsLabel.setBackground(ColorScheme.BACKGROUND);
////        newscontentsLabel.setOpaque(true);
////        
////        pnlmonthnews.add(newscontentsLabel, BorderLayout.NORTH);
////        pnlmonthnews.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
////
////        sclpmonthnews.setViewportView (pnlmonthnews);
//        
//        	//强势板块个股
//            JScrollPane sclpqiangshibk = new JScrollPane ();
//           	JPanel pnlqiangshibk = new JPanel();
//           	pnlqiangshibk.setName(firstDayInMonth + "QIANGSHI");
//           	pnlqiangshibk.setLayout(new BorderLayout());
//           	pnlqiangshibk.setBackground(ColorScheme.BACKGROUND);
//           	pnlqiangshibk.addMouseListener(new CreateQiangShiController(firstDayInMonth));
//            
//           	JUpdatedLabel qiangshibkLabel = new JUpdatedLabel("强势板块/个股");
//            qiangshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
//            qiangshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
//            qiangshibkLabel.setBackground(ColorScheme.BACKGROUND);
//            qiangshibkLabel.setOpaque(true);
//            
//            pnlqiangshibk.add(qiangshibkLabel, BorderLayout.NORTH);
//            pnlqiangshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
//
//            sclpqiangshibk.setViewportView (pnlqiangshibk);
//            
//           	//弱势板块个股
//           	JScrollPane sclpruoshibk = new JScrollPane ();
//           	JPanel pnlruoshibk = new JPanel();
//           	pnlruoshibk.setName(firstDayInMonth  + "RUOSHI");
//           	pnlruoshibk.setLayout(new BorderLayout());
//           	pnlruoshibk.setBackground(ColorScheme.BACKGROUND);
//           	pnlruoshibk.addMouseListener(new CreateRuoShiController(firstDayInMonth));
//           	
//           	JUpdatedLabel ruoshibkLabel = new JUpdatedLabel("弱势板块/个股");
//           	ruoshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
//           	ruoshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
//           	ruoshibkLabel.setBackground(ColorScheme.BACKGROUND);
//           	ruoshibkLabel.setOpaque(true);
//           	
//            pnlruoshibk.add(ruoshibkLabel, BorderLayout.NORTH);
//            pnlruoshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
//           	pnlruoshibk.setBackground(ColorScheme.BACKGROUND);
//           	
//           	sclpruoshibk.setViewportView (pnlruoshibk);
//           	
//           	//最近应该关注板块
//           	JScrollPane sclpguanzhubk = new JScrollPane ();
//           	JPanel pnlpguanzhubk = new JPanel();
//           	pnlpguanzhubk.setName(firstDayInMonth  + "DUANQIGUANZHU");
//           	pnlpguanzhubk.setLayout(new BorderLayout());
//           	pnlpguanzhubk.setBackground(ColorScheme.BACKGROUND);
//           	pnlpguanzhubk.addMouseListener(new CreateDQGZController(firstDayInMonth));
//           	
//           	JUpdatedLabel guanzhubkLabel = new JUpdatedLabel("近期关注板块个股");
//           	guanzhubkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
//           	guanzhubkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
//           	guanzhubkLabel.setBackground(ColorScheme.BACKGROUND);
//           	guanzhubkLabel.setOpaque(true);
//           	
//           	pnlpguanzhubk.add(guanzhubkLabel, BorderLayout.NORTH);
//           	pnlpguanzhubk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
//           	pnlpguanzhubk.setBackground(ColorScheme.BACKGROUND);
//           	
//           	sclpguanzhubk.setViewportView (pnlpguanzhubk);
//
////            this.wholemonthpnl.add(sclpmonthnews);
//            this.wholemonthpnl.add(sclpqiangshibk);
//            this.wholemonthpnl.add(sclpruoshibk);
//            this.wholemonthpnl.add(sclpguanzhubk);
//            
//            
//            
//            return firstDayInMonth;
//    }
    
	/*
	 * 
	 */
//    private class ReviseMeetingController extends MouseAdapter 
//    {
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            super.mouseClicked(e);
//            JLabel label = (JLabel) e.getSource();
//            String labelname = label.getName();
//            
//            
//            
//            
////            Meeting meeting = null;
////            Collection<InsertedMeeting> meetingslist = getCache().produceMeetings();
////            for(InsertedMeeting m : meetingslist) {
////            	String searchname = String.valueOf(m.getMeetingType()  ) + String.valueOf(m.getID() );
////            	
////				if(searchname.equals(labelname)) {
////					meeting = m;
////					break;
////            	}
////            }
//            
////            getModifyDialog().setMeeting(meeting);
////            getModifyDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
////            getModifyDialog().setVisible(true);
//            
////            Optional<InsertedMeeting> meeting = getCache().produceMeetings()
////                                                  .stream()
////                                                  .filter(m -> m.getID() == Integer.valueOf(label.getName()))
////                                                  .findFirst();
////
////            if (meeting.isPresent()) {
////                JLabel source = (JLabel) e.getSource();
//////                LocalDate date = LocalDate.parse(source.getParent().getParent().getName());
////                getModifyDialog().setMeeting(meeting.get());
////                getModifyDialog().setVisible(true);
////            }
//        }
//
//    }
    
	@Override
	public void onNewsChange(NewsCache cache) 
	{
//		this.onNewsChange(super.caches);
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



