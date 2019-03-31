package com.exchangeinfomanager.StockCalendar;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JLabelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingService;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressWarnings("all")
public class StockCalendar extends JCalendar {

    private static final Dimension SIZE = new Dimension(1150, 900);
    private static final Dimension MIN_SIZE = new Dimension(1150, 900);

    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String WHOLEMONTH = "wholemonth";

    private MonthView monthView;
    private YearView yearView;
    private WholeMonthNewsView wholemonthview;

    private CardLayout layout = new CardLayout();
    private JPanel headerPanel = JPanelFactory.createPanel();
    private JPanel sidebar;
    private JPanel viewDeck = JPanelFactory.createPanel(layout);
    private LocalDate date = LocalDate.now();
    private JLabel dateLabel = new JLabel();
    private JPanel currentView;
	private Cache cache;
	private Cache cachewholemonth;
	private SettingOfDisplayNewsArea settingsofnewsdisplay;
    
    public StockCalendar ()
    {
    	super ();
    	
    	//所有过去一年的新闻，
    	settingsofnewsdisplay = new SettingOfDisplayNewsArea ();
    	
    	MeetingService meetingService = new DBMeetingService ( );
    	LabelService labelService = new DBLabelService ();
        cache = new Cache("ALL",meetingService, labelService,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
        
        this.monthView = new MonthView(meetingService, cache,settingsofnewsdisplay);
        this.yearView = new YearView(meetingService, cache);
        this.sidebar = new Sidebar(meetingService,labelService, cache);
        
        //每月固定信息，用于记录长期月度重复信息,显示在Cal星期的上部
//        MeetingService meetingServicewholemonth = new DBMeetingService ( );
//    	LabelService labelServicewholemonth = new DBLabelService ();
//        cachewholemonth = new Cache("HEADLINE",meetingServicewholemonth, labelServicewholemonth,null,null);
//        this.wholemonthview = new WholeMonthNewsView (meetingServicewholemonth, cachewholemonth);
        this.wholemonthview = new WholeMonthNewsView (meetingService, cache);

	    this.initHeaderPanel(); //
	    this.initViewDeck();
	    this.initJFrame();

    }

//    public StockCalendar(MeetingService meetingService, Cache cache, LabelService labelService) {
//    	super ();
//        this.monthView = new MonthView(meetingService, cache);
//        this.yearView = new YearView(meetingService, cache);
//        this.wholemonthview = new WholeMonthNewsView (meetingService, cache);
//        this.sidebar = new Sidebar(labelService, cache);
//        this.cache = cache;
//
//        this.initHeaderPanel();
//        this.initViewDeck();
//        this.initJFrame();
//    }
    
    public void refreshNews ()
    {
    	cache.refreshNews ();
    }

    

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

        this.setPreferredSize(SIZE);
        this.setMinimumSize(MIN_SIZE);
    }

    private void initViewDeck() {
        viewDeck.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        monthView.setName(MONTH);
        yearView.setName(YEAR);       

        viewDeck.add(monthView, MONTH);
        viewDeck.add(yearView, YEAR);

        currentView = monthView;
        layout.show(viewDeck, currentView.getName());
    }

    private void initHeaderPanel() 
    {
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20,20));
        headerPanel.setLayout(new GridLayout(1, 2));
        JPanel buttonsPanel = JPanelFactory.createFixedSizePanel();
        JLabel today = JLabelFactory.createButton("Today");
        today.addMouseListener(new TemporalController(0));

        JLabel previous = JLabelFactory.createButton("<", 40);
        previous.addMouseListener(new TemporalController(-1));

        JLabel next = JLabelFactory.createButton(">", 40);
        next.addMouseListener(new TemporalController(1));

        JLabel month = JLabelFactory.createButton("Month");
        month.addMouseListener(new ViewController(monthView));

        JLabel year = JLabelFactory.createButton("Year");
        year.addMouseListener(new ViewController(yearView));
        
        JLabel setting = JLabelFactory.createButton("", 40);
        setting.setToolTipText("设置显示新闻范围");
//        setting.setText("");
        setting.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/gear-black-shape.png")));
        setting.addMouseListener(new settingController ());
        
        

        dateLabel = new JLabel(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
        dateLabel.setForeground(ColorScheme.BLACK_FONT);

        JPanel tempPanel = JPanelFactory.createPanel();
        tempPanel.add(today);
        tempPanel.add(Box.createHorizontalStrut(40));
        tempPanel.add(previous);
        tempPanel.add(next);
        tempPanel.add(Box.createHorizontalStrut(40));
        tempPanel.add(dateLabel);
        JPanel viewPanel = JPanelFactory.createPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        viewPanel.add(month);
        viewPanel.add(year);
        viewPanel.add(setting);

        this.headerPanel.add(tempPanel);
        this.headerPanel.add(viewPanel);
    }

////    public LocalDate getStockCalendarCurDate ()
////    {
////    	return ((view.view.View)this.currentView).getDate();
////    }
//    
//	public void propertyChange(PropertyChangeEvent evt) {
//		if (evt.getPropertyName().equals("stock")) {
//			System.out.println((Date) evt.getNewValue());
//			firePropertyChange("date", evt.getOldValue(), evt.getNewValue());
//		} 
//	}
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
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            else if (currentView == yearView)
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
        }
    }


    /*
     * 
     */
    private class TemporalController extends MouseAdapter {

        private int action;

        TemporalController(int action) {
            this.action = action;
        }

        @Override
        public void mouseClicked(MouseEvent e) 
        {
            super.mouseClicked(e);
            if (action == 0)
                nowDate();
            else if (action < 0)
                minusDate(action);
            else
                plusDate(action);
        }

        private void nowDate() 
        {
            date = LocalDate.now();
            if (currentView == monthView) {
                monthView.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            } else if (currentView == yearView) {
                yearView.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            }
            wholemonthview.setDate(date);
            
        }

        private void minusDate(int action) {
            if (currentView == monthView) {
                date = date.minusMonths(Math.abs(action));
                monthView.setDate(date);
                wholemonthview.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
        		
            } else if (currentView == yearView) {
                date = date.minusYears(1);
                yearView.setDate(date);
                wholemonthview.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            }
        }

        private void plusDate(int action) {
            if (currentView == monthView) {
                date = date.plusMonths(action);
                monthView.setDate(date);
                wholemonthview.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            } else if (currentView == yearView) {
                date = date.plusYears(1);
                yearView.setDate(date);
                wholemonthview.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            }
            
            firePropertyChange("day", 0, dateLabel);
        }
    }



	public Container getMonthView()
	{
		// TODO Auto-generated method stub
		return monthView;
	}
	
	class GBC extends GridBagConstraints  
	{  
	   //初始化左上角位置  
	   public GBC(int gridx, int gridy)  
	   {  
	      this.gridx = gridx;  
	      this.gridy = gridy;  
	   }  
	  
	   //初始化左上角位置和所占行数和列数  
	   public GBC(int gridx, int gridy, int gridwidth, int gridheight)  
	   {  
	      this.gridx = gridx;  
	      this.gridy = gridy;  
	      this.gridwidth = gridwidth;  
	      this.gridheight = gridheight;  
	   }  
	  
	   //对齐方式  
	   public GBC setAnchor(int anchor)  
	   {  
	      this.anchor = anchor;  
	      return this;  
	   }  
	  
	   //是否拉伸及拉伸方向  
	   public GBC setFill(int fill)  
	   {  
	      this.fill = fill;  
	      return this;  
	   }  
	  
	   //x和y方向上的增量  
	   public GBC setWeight(double weightx, double weighty)  
	   {  
	      this.weightx = weightx;  
	      this.weighty = weighty;  
	      return this;  
	   }  
	  
	   //外部填充  
	   public GBC setInsets(int distance)  
	   {  
	      this.insets = new Insets(distance, distance, distance, distance);  
	      return this;  
	   }  
	  
	   //外填充  
	   public GBC setInsets(int top, int left, int bottom, int right)  
	   {  
	      this.insets = new Insets(top, left, bottom, right);  
	      return this;  
	   }  
	  
	   //内填充  
	   public GBC setIpad(int ipadx, int ipady)  
	   {  
	      this.ipadx = ipadx;  
	      this.ipady = ipady;  
	      return this;  
	   }  
	}  

}
