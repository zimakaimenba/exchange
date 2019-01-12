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
 * ÿ��ÿ������Ҫ��ʾ��news�������view,ͬʱ����ÿ��ǿ�ƹ�/�������ƹ�/��� 
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
        //ÿ������
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
                pnlmonthnews.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //�м�������ʾ��Ϣ��panel
                
//            } else {
//                color = ColorScheme.GREY_WHITER;
//            }
            pnlmonthnews.setBackground(color);
            sclpmonthnews.setViewportView (pnlmonthnews);
            //ǿ�ư�����
            JScrollPane sclpqiangshibk = new JScrollPane ();
           	JPanel pnlqiangshibk = new JPanel();
           	pnlqiangshibk.setName(firstDayInMonth + "qqqqqq");
           	
           	pnlqiangshibk.setLayout(new BorderLayout());
           	pnlqiangshibk.setBackground(color);
           	pnlqiangshibk.addMouseListener(new QiangRuoBkStockController());
            
            JLabel qiangshibkLabel = new JLabel("ǿ�ư��/����");
            qiangshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
            qiangshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
            qiangshibkLabel.setBackground(ColorScheme.BACKGROUND);
            qiangshibkLabel.setOpaque(true);
            pnlqiangshibk.add(qiangshibkLabel, BorderLayout.NORTH);
            pnlqiangshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //�м�������ʾ��Ϣ��panel

            sclpqiangshibk.setViewportView (pnlqiangshibk);
           	//���ư�����
           	JScrollPane sclpruoshibk = new JScrollPane ();
           	JPanel pnlruoshibk = new JPanel();
           	pnlruoshibk.setName(firstDayInMonth  + "rrrrrr");
           	
           	pnlruoshibk.setLayout(new BorderLayout());
           	pnlruoshibk.setBackground(color);
           	pnlruoshibk.addMouseListener(new QiangRuoBkStockController());
           	
           	JLabel ruoshibkLabel = new JLabel("���ư��/����");
           	ruoshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
           	ruoshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
           	ruoshibkLabel.setBackground(ColorScheme.BACKGROUND);
           	ruoshibkLabel.setOpaque(true);
            pnlruoshibk.add(ruoshibkLabel, BorderLayout.NORTH);
            pnlruoshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //�м�������ʾ��Ϣ��panel
            
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

            if (d.getMonth().equals(super.getDate().getMonth()) ) { //ÿ������ֻҪ��һ�¾Ϳ���
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
            	
            } else { // ǿ�� ���ư��
            	
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
            
            
        	if (e.getClickCount() == 1) { //��ȡѡ�������
//        		setDate (mDate);
        	}
        	
        	if (e.getClickCount() == 2) { //����һ���µ�meeting
                Meeting meeting = new Meeting("���ڼ�¼",mDate,
                     "����", "�ؼ���", new HashSet<>(),"SlackURL","000000");
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
            	meetingname = "ǿ�ư��";
            else
            	meetingname = "���ư��";
            	
//            LocalDateTime dateTime = LocalDateTime.of(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), 8, 0);
        	if (e.getClickCount() == 1) { //��ȡѡ�������
//        		setDate (mDate);
        	}
        	
        	if (e.getClickCount() == 2) { //����һ���µ�meeting
                Meeting meeting = new Meeting(meetingname,mDate,
                     "����", "�ؼ���", new HashSet<>(),"SlackURL",pnltype);
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



