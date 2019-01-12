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
/**
 * 
 * @author Administrator
 * 每年每个月需要显示的news放在这个view,同时还放每月强势股/板块和弱势股/板块 
 *
 */
public class WholeMonthNewsView extends View 
{
	private static Logger logger = Logger.getLogger(WholeMonthNewsView.class);
	private JPanel wholemonthpnl = new JPanel(); //
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
      this.wholemonthpnl.setLayout(new GridLayout(1, 3));
      this.wholemonthpnl.setBackground(ColorScheme.GREY_LINE);
      
      super.setBackground(ColorScheme.BACKGROUND);
      super.setLayout(new BorderLayout());
      super.add(Box.createVerticalStrut(10));
      super.add(this.wholemonthpnl,BorderLayout.CENTER);
    }

    private LocalDate initView() 
    {
        this.wholemonthpnl.removeAll();
        //每月新闻
        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
       	JScrollPane sclpmonthnews = new JScrollPane ();
       	JPanel pnlmonthnews = new JPanel();
       	pnlmonthnews.setName(firstDayInMonth + "000000");
        
        Color color = null;
//        if (firstDayInMonth.getMonth().equals(super.getDate().getMonth()) ) {
                color = ColorScheme.BACKGROUND;

                pnlmonthnews.setLayout(new BorderLayout());
                pnlmonthnews.addMouseListener(new WholeMonthNewsController());
                
                JLabel newscontentsLabel = new JLabel(String.valueOf(firstDayInMonth.getMonth()));
                newscontentsLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
                newscontentsLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
                newscontentsLabel.setBackground(ColorScheme.BACKGROUND);
                newscontentsLabel.setOpaque(true);
                pnlmonthnews.add(newscontentsLabel, BorderLayout.NORTH);
                pnlmonthnews.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
                
//            } else {
//                color = ColorScheme.GREY_WHITER;
//            }
            pnlmonthnews.setBackground(color);
            sclpmonthnews.setViewportView (pnlmonthnews);
            //强势板块个股
            JScrollPane sclpqiangshibk = new JScrollPane ();
           	JPanel pnlqiangshibk = new JPanel();
           	pnlqiangshibk.setName(firstDayInMonth + "qqqqqq");
           	
           	pnlqiangshibk.setLayout(new BorderLayout());
           	pnlqiangshibk.setBackground(color);
           	pnlqiangshibk.addMouseListener(new QiangRuoBkStockController());
            
            JLabel qiangshibkLabel = new JLabel("强势板块/个股");
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
           	pnlruoshibk.setName(firstDayInMonth  + "rrrrrr");
           	
           	pnlruoshibk.setLayout(new BorderLayout());
           	pnlruoshibk.setBackground(color);
           	pnlruoshibk.addMouseListener(new QiangRuoBkStockController());
           	
           	JLabel ruoshibkLabel = new JLabel("弱势板块/个股");
           	ruoshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
           	ruoshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
           	ruoshibkLabel.setBackground(ColorScheme.BACKGROUND);
           	ruoshibkLabel.setOpaque(true);
            pnlruoshibk.add(ruoshibkLabel, BorderLayout.NORTH);
            pnlruoshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
            
           	pnlruoshibk.setBackground(color);
           	sclpruoshibk.setViewportView (pnlruoshibk);

            this.wholemonthpnl.add(sclpmonthnews);
            this.wholemonthpnl.add(sclpqiangshibk);
            this.wholemonthpnl.add(sclpruoshibk);
//            JPanel calendar2 = new JPanel();
//            JPanel calendar3 = new JPanel();
//            this.calendar.add(calendar2);
//            this.calendar.add(calendar3);
            
            return firstDayInMonth;
    }
    /*
     * 
     */
    private JPanel getWholeMonthNewsPanel(String actiondateandcode) 
    {
  	
        for (Component day1 : this.wholemonthpnl.getComponents() ) {
        	Component day = ((JScrollPane)day1).getViewport().getView();
//        	String componentname = day.getName();
            LocalDate d = LocalDate.parse(day.getName().substring(0, 10));

            if (d.getMonth().equals(super.getDate().getMonth()) ) { //每月新闻只要月一致就可以
                return (JPanel) ((JPanel) day).getComponent(1);
            }
        }
        return null;
    }
    private JPanel getQiangRuoShiPanel(String actiondateandcode) 
    {
  	
        for (Component day1 : this.wholemonthpnl.getComponents() ) {
        	Component day = ((JScrollPane)day1).getViewport().getView();
//        	String componentname = day.getName();
            LocalDate d = LocalDate.parse(day.getName().substring(0, 10));
            String componentcode = day.getName().substring(10).trim().toUpperCase();

            if (d.getYear() == super.getDate().getYear() && d.getMonth().equals(super.getDate().getMonth()) && componentcode.equals(actiondateandcode.toUpperCase())) {
                return (JPanel) ((JPanel) day).getComponent(1);
            }
        }
        return null;
    }

    @Override
    public void onMeetingChange(Cache cache) 
    {
    	LocalDate firstdayofmonth = this.initView();
    	
        Collection<InsertedMeeting> meetings = cache.produceMeetings(firstdayofmonth);
        Collection<InsertedMeeting.Label> labels = cache.produceLabels();

        
        for (InsertedMeeting m : meetings) {
            LocalDate mDate = m.getStart();
            String actioncode = m.getCurrentownercode();
            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
            
            if(actioncode.equals("000000")) {
            	
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
                       	this.getWholeMonthNewsPanel(actioncode).add(label);
                       	
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
                           	this.getWholeMonthNewsPanel(actioncode).add(label);
                           	
                            break;
                        }
                    }
                }
            	
            } else { // 强势 弱势板块
            	
            	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) {
                    if (m.getLabels().isEmpty()) {
                        JLabel label = new JLabel(m.getTitle());
                        label.setToolTipText(m.getTitle() );
                        label.setOpaque(true);
                        label.setName(String.valueOf(m.getID()));
                        label.addMouseListener(new MeetingController());
                        label.setForeground(ColorScheme.BLACK_FONT);
                        label.setBackground(ColorScheme.GREY_WHITER_DARKER);
                        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                        this.getQiangRuoShiPanel(actioncode).add(label);
                        
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
                            this.getQiangRuoShiPanel(actioncode).add(label);
                            break;
                        }
                    }
                }
            	
            }
        }

        this.wholemonthpnl.validate();
        this.wholemonthpnl.repaint();
    }

    @Override
    public void onLabelChange(Cache cache) {
        this.onMeetingChange(cache);
    }

    private class WholeMonthNewsController extends MouseAdapter 
    { 
        @Override
        public void mouseClicked(MouseEvent e) {
        	
        	super.mouseClicked(e);
            JPanel panel = (JPanel) e.getSource();
            LocalDate mDate = LocalDate.parse(panel.getName().substring(0, 10));
            LocalDateTime dateTime = LocalDateTime.of(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), 8, 0);
            
            
        	if (e.getClickCount() == 1) { //获取选择的日期
//        		setDate (mDate);
        	}
        	
        	if (e.getClickCount() == 2) { //增加一个新的meeting
                Meeting meeting = new Meeting("长期记录",mDate,
                     "描述", "关键词", new HashSet<>(),"SlackURL","000000");
                getCreateDialog().setMeeting(meeting);
                getCreateDialog().setVisible(true);
        	}
            
        }
    }
    private class QiangRuoBkStockController extends MouseAdapter 
    { 
        @Override
        public void mouseClicked(MouseEvent e) {
        	
        	super.mouseClicked(e);
            JPanel panel = (JPanel) e.getSource();
            String pnlnamedate = panel.getName().substring(0, 10);
            LocalDate mDate = LocalDate.parse(pnlnamedate);
            
            String pnltype = panel.getName().substring(10);
            String meetingname ;
            if(pnltype.equals("qqqqqq"))
            	meetingname = "强势板块";
            else
            	meetingname = "弱势板块";
            	
//            LocalDateTime dateTime = LocalDateTime.of(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), 8, 0);
        	if (e.getClickCount() == 1) { //获取选择的日期
//        		setDate (mDate);
        	}
        	
        	if (e.getClickCount() == 2) { //增加一个新的meeting
                Meeting meeting = new Meeting(meetingname,mDate,
                     "描述", "关键词", new HashSet<>(),"SlackURL",pnltype);
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
//                LocalDate date = LocalDate.parse(source.getParent().getParent().getName());
                getModifyDialog().setMeeting(meeting.get());
                getModifyDialog().setVisible(true);
            }
        }

    }
}



