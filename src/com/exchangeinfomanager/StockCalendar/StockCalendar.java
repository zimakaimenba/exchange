package com.exchangeinfomanager.StockCalendar;

import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ServicesForNews;
import com.exchangeinfomanager.News.ServicesForNewsLabel;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddNewsToNode;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;


import javax.swing.*;

import org.joda.time.Months;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

@SuppressWarnings("all")
public class StockCalendar extends JCalendar 
{

//    private static final Dimension SIZE = new Dimension(1250, 900);
//    private static final Dimension MIN_SIZE = new Dimension(1250, 900);

    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String WHOLEMONTH = "wholemonth";

    private MonthView monthView;
    private YearView yearView;
    private WholeMonthNewsView wholemonthview;
    private JPanel sidebar;

    private CardLayout layout = new CardLayout();
    private JPanel headerPanel = JPanelFactory.createPanel();
    private JPanel viewDeck = JPanelFactory.createPanel(layout);
    private LocalDate calrightdate = LocalDate.now();
    private JLabel dateLabel = new JLabel();
    private JPanel currentView;

	private SettingOfDisplayNewsArea settingsofnewsdisplay;
	private JDialogForTagSearchMatrixPanelForAddNewsToNode pnlsearchtags;
	
	private ServicesForNews svsdqgz ;
	private ServicesForNews svsnews;
    
	   public StockCalendar ()
	    {
	    	super ();
	    	
	    	//所有过去一年的新闻，
	    	settingsofnewsdisplay = new SettingOfDisplayNewsArea ();
	    	
	    	ServicesForNewsLabel svslabel = new NewsLabelServices ();
	    	
	    	svsnews = new NewsServices ();
	    	NewsCache newcache = new NewsCache ("ALL",svsnews,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//	    	svsnews.setCache(newcache);
	    	
	    	ServicesForNews svscqgz = new ChangQiGuanZhuServices ();
	    	NewsCache cqgzcache = new NewsCache ("ALL",svscqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//	    	svscqgz.setCache(cqgzcache);
	    	
	    	ServicesForNews svsqs = new QiangShiServices ();
	    	NewsCache qiangshicache = new NewsCache ("ALL",svsqs,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//	    	svsqs.setCache(qiangshicache);
	    	
	    	ServicesForNews svsrs = new RuoShiServices ();
	    	NewsCache ruoshicache = new NewsCache ("ALL",svsrs,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//	    	svsrs.setCache(ruoshicache);
	    	
	    	svsdqgz = new DuanQiGuanZhuServices ();
	    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//	    	svsdqgz.setCache(dqgzcache);
	    	
	    	ServicesForNews svszsbl = new ZhiShuBoLangServices ();
	    	NewsCache zsblcache = new NewsCache ("ALL",svszsbl,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//	    	svszsbl.setCache(zsblcache);
	    	
	    	Collection<ServicesForNews> monthservices = new HashSet<> ();
	    	monthservices.add(svsnews);
	    	monthservices.add(svsrs);
	    	monthservices.add(svsqs);
	    	monthservices.add(svscqgz);
	    	monthservices.add(svsdqgz);
	    	monthservices.add(svszsbl);
	    	
	        this.monthView = new MonthView(monthservices, settingsofnewsdisplay);
	        this.yearView = new YearView(monthservices);
	        this.sidebar = new Sidebar(monthservices,svslabel);
	        
	        //每月固定信息，用于记录长期月度重复信息,显示在Cal星期的上部
	        Collection<ServicesForNews> wholemonthservices = new HashSet<> ();
	        wholemonthservices.add(svsrs);
	        wholemonthservices.add(svsqs);
	        wholemonthservices.add(svscqgz);
	        wholemonthservices.add(svsdqgz);
	        wholemonthservices.add(svszsbl);
	        this.wholemonthview = new WholeMonthNewsView (wholemonthservices);
	        
            pnlsearchtags = new JDialogForTagSearchMatrixPanelForAddNewsToNode(newcache);
	        
		    this.initHeaderPanel(); //
		    this.initViewDeck();
		    this.initJFrame();
	    }
	   
	   private void testJFrame ()
	   {
		   this.setLayout(new BorderLayout() );
		   this.add(this.monthView, BorderLayout.CENTER);
	   }
	   
	   public ServicesForNews getDuanQiGuanZhuService ()
	   {
		   return this.svsdqgz;
	   }
	   public ServicesForNews getNewsService ()
	   {
		   return this.svsnews;
	   }
    /*
     * 
     */
	private void initJFrame() 
    {
    	GridBagLayout gbl = new GridBagLayout(); 
        this.setLayout(gbl);
//        所有布局必须按从上到下，从左到右顺序
        //方案3
        this.add(monthChooser.getParent(),new GBC(0,0,1,1).  
                setFill(GBC.BOTH).setIpad(50,50).setWeight(0, 0));
        
        this.add(headerPanel, new GBC(1,0,6,1).  
                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0));
        
        this.add(dayChooser,new GBC(0,1,1,1).  
                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0)); 
      
        this.add(wholemonthview,new GBC(1,1,6,1).  
                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0)); 
        
        this.add(sidebar,new GBC(0,2,1,2).  
                setFill(GBC.BOTH).setIpad(0, 0).setWeight(0, 0)); 
       
        this.add(viewDeck,new GBC(1,2,6,2).setFill(GBC.BOTH).setWeight(0, 0).setIpad(0,0));

//        this.setPreferredSize(SIZE);
//        this.setMinimumSize(MIN_SIZE);
        
       
    }
	/*
	 * 
	 */
    private void initViewDeck()
    {
        viewDeck.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        monthView.setName(MONTH);
//        yearView.setName(YEAR);       

        viewDeck.add(monthView, MONTH);
//        viewDeck.add(yearView, YEAR);

        currentView = monthView;
        layout.show(viewDeck, currentView.getName() );
    }
    /*
     * 
     */
    private void initHeaderPanel() 
    {
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20,20));
        headerPanel.setLayout(new GridLayout(1, 2));
//        JPanel buttonsPanel = JPanelFactory.createFixedSizePanel();
        
        JLabel syncdate = JLabelFactory.createButton("", 40);
        syncdate.setToolTipText("同步左边日期");
        syncdate.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/right-dark-arrow.png")));
        syncdate.setEnabled(false);
        syncdate.addMouseListener( new DateController(this,null));
        		
        
        JLabel today = JLabelFactory.createButton("Today");
        today.addMouseListener(new DateController(this,0));

        JLabel previous = JLabelFactory.createButton("<", 40);
        previous.addMouseListener(new DateController(this,-1));

        JLabel next = JLabelFactory.createButton(">", 40);
        next.addMouseListener(new DateController(this,1));

        JLabel month = JLabelFactory.createButton("Month");
        month.addMouseListener(new ViewController(monthView));

        JLabel year = JLabelFactory.createButton("Year");
        year.addMouseListener(new ViewController(yearView));
        
        JLabel setting = JLabelFactory.createButton("", 40);
        setting.setToolTipText("设置显示新闻范围");
        setting.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/gear-black-shape.png")));
        setting.addMouseListener(new settingController ());
        
        
        //布局界面
        dateLabel = new JLabel(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
        dateLabel.setForeground(ColorScheme.BLACK_FONT);

        JPanel tempPanel = JPanelFactory.createPanel();
        tempPanel.add(syncdate);
        
        tempPanel.add(Box.createHorizontalStrut(20));
        
        tempPanel.add(dateLabel);
        
        tempPanel.add(Box.createHorizontalStrut(40));
        
        
        tempPanel.add(today);
        tempPanel.add(Box.createHorizontalStrut(10));
        tempPanel.add(previous);
        tempPanel.add(next);
        
        tempPanel.add(Box.createHorizontalStrut(40));
        
        JPanel viewPanel = JPanelFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        viewPanel.add(month);
        viewPanel.add(year);
        
        tempPanel.add(Box.createHorizontalStrut(20));
        
        viewPanel.add(setting);

        this.headerPanel.add(tempPanel);
        this.headerPanel.add(viewPanel);
    }
    /*
     * 
     */
    private void updateMonthAndYearView() 
    {
		Date leftdate = super.getDate();
		LocalDate rightdate = calrightdate;
	}
    /*
     * 
     */
//    public void refreshStockCalander ()
//    {
//    	this.cache.refresh();
//    }
   /*
    * 用户更改设置
    */
    private class settingController extends MouseAdapter 
    {
    	@Override
        public void mouseClicked(MouseEvent e) {
    		
    		int exchangeresult = JOptionPane.showConfirmDialog(null, settingsofnewsdisplay, "新闻显示设置", JOptionPane.OK_CANCEL_OPTION);
			
			if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
			
			firePropertyChange("day", 0, dateLabel);
			
    	}
    }
    /*
     * 
     */
    private class ViewController extends MouseAdapter {

        private JPanel view;

        public ViewController(JPanel view) {
            this.view = view;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            currentView = view;
            layout.show(viewDeck, currentView.getName());
            if (currentView == monthView)
                dateLabel.setText(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
            else if (currentView == yearView)
                dateLabel.setText(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
        }
    }


    /*
     * 
     */
    private class DateController extends MouseAdapter 
    {

        private Integer action;
		private StockCalendar skcal;

		DateController(StockCalendar stockCalendar, Integer action) 
        {
            this.action = action;
            this.skcal = stockCalendar;
        }

        @Override
        public void mouseClicked(MouseEvent e) 
        {
            super.mouseClicked(e);
            if(action == null) {
            	 Date leftdate = this.skcal.getDate();
            	 LocalDate ldleftdate; 
            	 try {   ldleftdate = leftdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         		} catch (java.lang.NullPointerException evt) {	ldleftdate = null;}
            	
            	long difference = ChronoUnit.MONTHS.between(calrightdate, ldleftdate );
        		if (difference == 0) {
        				int leftmonthvalue = ldleftdate.getMonthValue();
                    	int rightmonthvalue = calrightdate.getMonthValue();
                    	if(leftmonthvalue != rightmonthvalue) 
                    		setNewDate(rightmonthvalue - leftmonthvalue )  ;
        		} else
        			setNewDate ( (int) difference) ;
            } else if(action == 0)
            	nowDate();
            else
            	setNewDate(action);
        }
        /*
         * 
         */
        private void nowDate() 
        {
        	calrightdate = LocalDate.now();
            if (currentView == monthView) {
                monthView.setDate(calrightdate);
                dateLabel.setText(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
            } else if (currentView == yearView) {
                yearView.setDate(calrightdate);
                dateLabel.setText(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
            }
            wholemonthview.setDate(calrightdate);
            
            firePropertyChange("day", 0, dateLabel);
        }
        /*
         * 
         */
        private void setNewDate(int action) 
        {
            if (currentView == monthView) {
            	calrightdate = calrightdate.plusMonths(action);
                monthView.setDate(calrightdate);
                wholemonthview.setDate(calrightdate);
                dateLabel.setText(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
        		
            } else if (currentView == yearView) {
            	calrightdate = calrightdate.minusYears(action);
                yearView.setDate(calrightdate);
                wholemonthview.setDate(calrightdate);
                dateLabel.setText(calrightdate.format(DateTimeFormatter.ofPattern("MMM uuuu")));
            }
            
            firePropertyChange("day", 0, dateLabel);
        }

    }



	public Container getMonthView()
	{
		// TODO Auto-generated method stub
		return monthView;
	}
	
	  

}
