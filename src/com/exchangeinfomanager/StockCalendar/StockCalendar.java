package com.exchangeinfomanager.StockCalendar;


import com.exchangeinfomanager.StockCalendar.view.Cache;
import com.exchangeinfomanager.StockCalendar.view.DBLabelService;
import com.exchangeinfomanager.StockCalendar.view.DBMeetingService;
import com.exchangeinfomanager.StockCalendar.view.JLabelFactory;
import com.exchangeinfomanager.StockCalendar.view.JPanelFactory;
import com.exchangeinfomanager.StockCalendar.view.LabelService;
import com.exchangeinfomanager.StockCalendar.view.MeetingService;
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

    private static final Dimension SIZE = new Dimension(1150, 750);
    private static final Dimension MIN_SIZE = new Dimension(1150, 750);

    private static final String MONTH = "month";
    private static final String YEAR = "year";

    private MonthView monthView;
    private YearView yearView;

    private CardLayout layout = new CardLayout();
    private JPanel headerPanel = JPanelFactory.createPanel();
    private JPanel sidebar;
    private JPanel viewDeck = JPanelFactory.createPanel(layout);
    private LocalDate date = LocalDate.now();
//    private LocalDate date = super.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    private JLabel dateLabel = new JLabel();
    private JPanel currentView;
//    private View currentView;
    
    public StockCalendar ()
    {
    	super ();
    	
    	MeetingService meetingService = new DBMeetingService ( );
    	LabelService labelService = new DBLabelService ();
        Cache cache = new Cache("000000",meetingService, labelService);
        
        this.monthView = new MonthView(meetingService, cache);
        this.yearView = new YearView(meetingService, cache);
        this.sidebar = new Sidebar(labelService, cache);

	    this.initHeaderPanel();
	    this.initViewDeck();
	    this.initJFrame();
    }

    public StockCalendar(MeetingService meetingService, Cache cache, LabelService labelService) {
    	super ();
        this.monthView = new MonthView(meetingService, cache);
        this.yearView = new YearView(meetingService, cache);
        this.sidebar = new Sidebar(labelService, cache);

        this.initHeaderPanel();
        this.initViewDeck();
        this.initJFrame();
    }
    

    private void initJFrame() {
    	GridBagLayout gbl = new GridBagLayout(); 
        this.setLayout(gbl);
//        ���в��ֱ��밴���ϵ��£�������˳��
        //����1
//        this.add(monthChooser.getParent(),new GBC(0,0,1,1).  
//                setFill(GBC.HORIZONTAL).setIpad(50,50).setWeight(0, 0));
//        this.add(headerPanel, new GBC(1,0,4,1).  
//                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0));
//        this.add(dayChooser,new GBC(0,1,1,1).  
//                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0)); 
//        
//     
//        this.add(sidebar,new GBC(0,2,1,2).  
//                setFill(GBC.BOTH).setIpad(0, 0).setWeight(0, 0)); 
//        this.add(viewDeck,new GBC(1,2,4,4).setFill(GBC.BOTH).setWeight(0, 0).setIpad(0,0));  
        //����2
        this.add(monthChooser.getParent(),new GBC(0,0,1,1).  
                setFill(GBC.BOTH).setIpad(50,50).setWeight(0, 0));
        this.add(headerPanel, new GBC(1,0,6,1).  
                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0));
        this.add(dayChooser,new GBC(0,1,1,1).  
                setFill(GBC.BOTH).setIpad(0,0).setWeight(0, 0)); 
        this.add(viewDeck,new GBC(1,1,6,2).setFill(GBC.BOTH).setWeight(0, 0).setIpad(0,0));
     
        this.add(sidebar,new GBC(0,2,1,2).  
                setFill(GBC.BOTH).setIpad(0, 0).setWeight(0, 0)); 

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

    private void initHeaderPanel() {
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
        }

        private void minusDate(int action) {
            if (currentView == monthView) {
                date = date.minusMonths(Math.abs(action));
                monthView.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
                
              //jcalendar part
//                Date oldDate = getCalendar().getTime();
//                int year = date.getYear();
//        		int month = date.getMonthValue();
//        		int day = date.getDayOfMonth();
//        		GregorianCalendar gc = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
//        		yearChooser.setYear(year);
//        		monthChooser.setMonth(month);
//        		dayChooser.setCalendar(gc);
//        		dayChooser.setDay(day);
//        		firePropertyChange("date", oldDate, date);
        		
            } else if (currentView == yearView) {
                date = date.minusYears(1);
                yearView.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            }
            

        }

        private void plusDate(int action) {
            if (currentView == monthView) {
                date = date.plusMonths(action);
                monthView.setDate(date);
                dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
            } else if (currentView == yearView) {
                date = date.plusYears(1);
                yearView.setDate(date);
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
	   //��ʼ�����Ͻ�λ��  
	   public GBC(int gridx, int gridy)  
	   {  
	      this.gridx = gridx;  
	      this.gridy = gridy;  
	   }  
	  
	   //��ʼ�����Ͻ�λ�ú���ռ����������  
	   public GBC(int gridx, int gridy, int gridwidth, int gridheight)  
	   {  
	      this.gridx = gridx;  
	      this.gridy = gridy;  
	      this.gridwidth = gridwidth;  
	      this.gridheight = gridheight;  
	   }  
	  
	   //���뷽ʽ  
	   public GBC setAnchor(int anchor)  
	   {  
	      this.anchor = anchor;  
	      return this;  
	   }  
	  
	   //�Ƿ����켰���췽��  
	   public GBC setFill(int fill)  
	   {  
	      this.fill = fill;  
	      return this;  
	   }  
	  
	   //x��y�����ϵ�����  
	   public GBC setWeight(double weightx, double weighty)  
	   {  
	      this.weightx = weightx;  
	      this.weighty = weighty;  
	      return this;  
	   }  
	  
	   //�ⲿ���  
	   public GBC setInsets(int distance)  
	   {  
	      this.insets = new Insets(distance, distance, distance, distance);  
	      return this;  
	   }  
	  
	   //�����  
	   public GBC setInsets(int top, int left, int bottom, int right)  
	   {  
	      this.insets = new Insets(top, left, bottom, right);  
	      return this;  
	   }  
	  
	   //�����  
	   public GBC setIpad(int ipadx, int ipady)  
	   {  
	      this.ipadx = ipadx;  
	      this.ipady = ipady;  
	      return this;  
	   }  
	}  

}