package com.exchangeinfomanager.StockCalendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingService;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.gui.StockInfoManager;

public class WholeMonthNewsView extends View 
{
	private static Logger logger = Logger.getLogger(WholeMonthNewsView.class);
	private JPanel calendar = new JPanel();
//    private JLabel dateLabel = new JLabel();

    public WholeMonthNewsView(MeetingService meetingService, Cache cache) 
    {
        super(meetingService, cache);

        cache.addCacheListener(this);
        this.onMeetingChange(cache);
        this.initMonthView();
    }

    private void initMonthView() 
    {
    	super.setBackground(ColorScheme.BACKGROUND);
        super.setLayout(new BorderLayout());
        super.add(Box.createVerticalStrut(10));
        
//        this.calendar.setLayout(new GridLayout(6, 7, 1, 1));
//        this.calendar.setLayout(new BorderLayout());
//        this.calendar.setBackground(ColorScheme.GREY_LINE);

        super.add(this.calendar,BorderLayout.CENTER);
    }

    private void initView() {
        this.calendar.removeAll();
        this.calendar.setLayout(new BorderLayout());

        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
//        LocalDate first = firstDayInMonth.minusDays(firstDayInMonth.getDayOfWeek().getValue() - 1);

        	JScrollPane scrollpane = new JScrollPane ();
        	JPanel panel = new JPanel();
            panel.setName(firstDayInMonth.toString());
            Color color = null;
            if (firstDayInMonth.getMonth().equals(super.getDate().getMonth())) {
                color = ColorScheme.BACKGROUND;
//                super.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
                panel.setLayout(new BorderLayout());
                panel.addMouseListener(new DayController());
                
                JLabel dayLabel = new JLabel(String.valueOf(firstDayInMonth.getMonth()));
                dayLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
                dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
                dayLabel.setBackground(ColorScheme.BACKGROUND);
                dayLabel.setOpaque(true);
                panel.add(dayLabel, BorderLayout.NORTH);
                
                panel.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER);
//                panel.setPreferredSize(scrollpane.getPreferredSize());
                
            } else {
                color = ColorScheme.GREY_WHITER;
            }
            panel.setBackground(color);
            
            scrollpane.setViewportView (panel);

            this.calendar.add(scrollpane,BorderLayout.CENTER);

    }

    private JPanel getMonthDay(LocalDate date) {
  	
        for (Component day1 : this.calendar.getComponents() ) {
        	Component day = ((JScrollPane)day1).getViewport().getView();
            LocalDate d = LocalDate.parse(day.getName());
            logger.debug(d.getMonth() );
            logger.debug(super.getDate().getMonth() );
            if (d.getMonth().equals(super.getDate().getMonth()) ) {
                return (JPanel) ((JPanel) day).getComponent(1);
            }
        }
        return null;
    }

    @Override
    public void onMeetingChange(Cache cache) 
    {
        Collection<InsertedMeeting> meetings = cache.produceMeetings();
        Collection<InsertedMeeting.Label> labels = cache.produceLabels();

        this.initView();
        for (InsertedMeeting m : meetings) {
            LocalDate mDate = m.getStart();
            logger.debug(mDate.getMonth());
            logger.debug(super.getDate().getMonth());
            if (mDate.getMonth().equals(super.getDate().getMonth())  ) {
                if (m.getLabels().isEmpty()) {
                    JLabel label = new JLabel(m.getTitle());
                    label.setToolTipText(m.getTitle() );
                    label.setOpaque(true);
                    label.setName(String.valueOf(m.getID()));
                    label.addMouseListener(new MeetingController());
                    label.setForeground(ColorScheme.BLACK_FONT);
                    label.setBackground(ColorScheme.GREY_WHITER_DARKER);
                    label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                    this.getMonthDay(mDate).add(label);
                    continue;
                }
                for (Meeting.Label l : labels) {
                    if (l.isActive() && m.getLabels().contains(l)) {
                        JLabel label = new JLabel(m.getTitle());
                        label.setToolTipText(m.getTitle() );
                        label.setOpaque(true);
                        label.setName(String.valueOf(m.getID()));
                        label.addMouseListener(new MeetingController());
                        label.setForeground(Color.BLACK);
                        label.setBackground(l.getColor());
                        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                        this.getMonthDay(mDate).add(label);
                        break;
                    }
                }
            }
        }

        this.calendar.validate();
        this.calendar.repaint();
    }

    @Override
    public void onLabelChange(Cache cache) {
        this.onMeetingChange(cache);
    }

    private class DayController extends MouseAdapter 
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
        	
        	if (e.getClickCount() == 2) { //增加一个新的meeting
                Meeting meeting = new Meeting("长期记录",mDate,
                     "描述", "关键词", new HashSet<>(),"SlackURL","000000");
                getCreateDialog().setMeeting(meeting);
                getCreateDialog().setVisible(true);
        	}
            
        }
    }

	
    private class MeetingController extends MouseAdapter 
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            JLabel label = (JLabel) e.getSource();
            Optional<InsertedMeeting> meeting = getCache().produceMeetings()
                                                  .stream()
                                                  .filter(m -> m.getID() == Integer.valueOf(label.getName()))
                                                  .findFirst();

            if (meeting.isPresent()) {
                JLabel source = (JLabel) e.getSource();
                LocalDate date = LocalDate.parse(source.getParent().getParent().getName());
                getModifyDialog().setMeeting(meeting.get());
                getModifyDialog().setVisible(true);
            }
        }

    }


//	@Override
//	public void actionPerformed(ActionEvent arg0) 
//	{
//		// TODO Auto-generated method stub
//		
//	}

}


