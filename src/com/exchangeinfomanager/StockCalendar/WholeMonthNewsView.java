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
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting.Label;
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
    /*
     * ��ʼ��monthview������
     */
    private LocalDate initView() 
    {
        this.wholemonthpnl.removeAll();
        //ÿ������
        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
       	JScrollPane sclpmonthnews = new JScrollPane ();
       	JPanel pnlmonthnews = new JPanel();
       	pnlmonthnews.setName(firstDayInMonth + "wwwwww");
        
        Color color = null;
//        if (firstDayInMonth.getMonth().equals(super.getDate().getMonth()) ) {
                color = ColorScheme.BACKGROUND;

                pnlmonthnews.setLayout(new BorderLayout());
                pnlmonthnews.addMouseListener(new WholeMonthNewsController(Meeting.CHANGQIJILU));
                
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
           	pnlqiangshibk.addMouseListener(new WholeMonthNewsController(Meeting.QIANSHI));
            
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
           	pnlruoshibk.addMouseListener(new WholeMonthNewsController(Meeting.RUOSHI));
           	
           	JLabel ruoshibkLabel = new JLabel("���ư��/����");
           	ruoshibkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
           	ruoshibkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
           	ruoshibkLabel.setBackground(ColorScheme.BACKGROUND);
           	ruoshibkLabel.setOpaque(true);
            pnlruoshibk.add(ruoshibkLabel, BorderLayout.NORTH);
            pnlruoshibk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //�м�������ʾ��Ϣ��panel
            
           	pnlruoshibk.setBackground(color);
           	sclpruoshibk.setViewportView (pnlruoshibk);
           	
           	//���Ӧ�ù�ע���
           	JScrollPane sclpguanzhubk = new JScrollPane ();
           	JPanel pnlpguanzhubk = new JPanel();
           	pnlpguanzhubk.setName(firstDayInMonth  + "gzgzgz");
           	pnlpguanzhubk.setLayout(new BorderLayout());
           	pnlpguanzhubk.setBackground(color);
           	pnlpguanzhubk.addMouseListener(new WholeMonthNewsController(Meeting.JINQIGUANZHU));
           	
           	JLabel guanzhubkLabel = new JLabel("���ڹ�ע������(̸���۽�)");
           	guanzhubkLabel.setForeground(ColorScheme.GREY_LINE_DARKER);
           	guanzhubkLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
           	guanzhubkLabel.setBackground(ColorScheme.BACKGROUND);
           	guanzhubkLabel.setOpaque(true);
           	
           	pnlpguanzhubk.add(guanzhubkLabel, BorderLayout.NORTH);
           	pnlpguanzhubk.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //�м�������ʾ��Ϣ��panel
           	pnlpguanzhubk.setBackground(color);
           	sclpguanzhubk.setViewportView (pnlpguanzhubk);

            this.wholemonthpnl.add(sclpmonthnews);
            this.wholemonthpnl.add(sclpqiangshibk);
            this.wholemonthpnl.add(sclpruoshibk);
            this.wholemonthpnl.add(sclpguanzhubk);
//            JPanel calendar2 = new JPanel();
//            JPanel calendar3 = new JPanel();
//            this.calendar.add(calendar2);
//            this.calendar.add(calendar3);
            
            return firstDayInMonth;
    }
    /*
     * //ÿ������ֻҪ��һ�¾Ϳ���
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
    /*
     * //Ҫ����һ��
     */
    private JPanel getQiangRuoShiPanel(String actiondateandcode) 
    {
    	if(actiondateandcode.contains("qqqqqq"))
    		actiondateandcode = "qqqqqq";
    	else if(actiondateandcode.contains("rrrrrr"))
    		actiondateandcode = "rrrrrr";
    	else if(actiondateandcode.contains("gzgzgz"))
    		actiondateandcode = "gzgzgz";
    	
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
    /*
     * (non-Javadoc)
     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener#onMeetingChange(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache)
     * ��������month view������
     */
    public void onMeetingChange(Cache cache) 
    {
    	LocalDate firstdayofmonth = this.initView();
    	
        Collection<InsertedMeeting> meetings = cache.produceMeetings(firstdayofmonth);
        Collection<InsertedMeeting.Label> labels = cache.produceLabels();

        
        for (InsertedMeeting m : meetings) {
            LocalDate mDate = m.getStart();
            String actioncode = m.getNewsOwnerCodes();
            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
            
            if(m.getMeetingType() == Meeting.CHANGQIJILU) { // ÿ�¹̶�����
            	
            	if (mDate.getMonth().equals(super.getDate().getMonth())  ) {
                    if (m.getLabels().isEmpty()) {
                    	JLabel label = getFormatedLabelForNoneLabel (m);
//                        JLabel label = new JLabel(m.getTitle());
//                        label.setToolTipText(m.getTitle() );
//                        label.setOpaque(true);
//                        label.setName( String.valueOf(m.getMeetingType()) + String.valueOf(m.getID()) );
//                        label.addMouseListener(new MeetingController());
//                        label.setForeground(ColorScheme.BLACK_FONT);
//                        label.setBackground(ColorScheme.GREY_WHITER_DARKER);
//                        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                       	this.getWholeMonthNewsPanel(actioncode).add(label);
                       	
                        continue;
                    }
                    for (Meeting.Label l : labels) {
                        if (l.isActive() && m.getLabels().contains(l)) {
                        	JLabel label = getFormatedLabelForWithLabels (m,l);
//                            JLabel label = new JLabel(m.getTitle());
//                            label.setToolTipText(m.getTitle() );
//                            label.setOpaque(true);
//                            label.setName( String.valueOf(m.getMeetingType()) + String.valueOf(m.getID()) );
//                            label.addMouseListener(new MeetingController());
//                            label.setForeground(Color.BLACK);
//                            label.setBackground(l.getColor());
//                            label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
                           	this.getWholeMonthNewsPanel(actioncode).add(label);
                           	
                            break;
                        }
                    }
                }
            	
            } else if(m.getMeetingType() == Meeting.JINQIGUANZHU) { // ��ע���
            	
            	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) { //ǿ�� ���ư�� ��Ҫ������ȼ��� 
                    if (m.getLabels().isEmpty()) {
                    	JLabel label = getFormatedLabelForNoneLabel (m);
                        try{
                        	this.getQiangRuoShiPanel(actioncode).add(label);
                        } catch (java.lang.NullPointerException e) {
                        	e.printStackTrace();
                        }
                        
                        continue;
                    }
                    for (Meeting.Label l : labels) {
                        if (l.isActive() && m.getLabels().contains(l)) {
                        	JLabel label = getFormatedLabelForWithLabels (m,l);
                            try {
                            	this.getQiangRuoShiPanel(actioncode).add(label);
                            } catch (java.lang.NullPointerException e) {
                            	e.printStackTrace();
                            }
                            
                            break;
                        }
                    }
                }
            	
            } else if(m.getMeetingType() == Meeting.QIANSHI  ) { // ǿ�� ���� ��ע���
            	String searchingpnlid = "qqqqqq";

            	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) { //ǿ�� ���ư�� ��Ҫ������ȼ��� 
                        if (m.getLabels().isEmpty()) {
                        	JLabel label = getFormatedLabelForNoneLabel (m);
                            try{
                            	this.getQiangRuoShiPanel(searchingpnlid).add(label);
                            } catch (java.lang.NullPointerException e) {
                            	e.printStackTrace();
                            }
                            
                            continue;
                        }
                        for (Meeting.Label l : labels) {
                            if (l.isActive() && m.getLabels().contains(l)) {
                            	JLabel label = getFormatedLabelForWithLabels (m,l);
                                try {
                                	this.getQiangRuoShiPanel(searchingpnlid).add(label);
                                } catch (java.lang.NullPointerException e) {
                                	e.printStackTrace();
                                }
                                
                                break;
                            }
                        }
                    }
                	
                } else if(m.getMeetingType() == Meeting.RUOSHI  ) { // ǿ�� ���� ��ע���
                    	String searchingpnlid = "rrrrrr";
                        	
                        	if ( mDate.getYear() == super.getDate().getYear()  && mDate.getMonth().equals(super.getDate().getMonth())  ) { //ǿ�� ���ư�� ��Ҫ������ȼ��� 
                                if (m.getLabels().isEmpty()) {
                                	JLabel label = getFormatedLabelForNoneLabel (m);
                                    try{
                                    	this.getQiangRuoShiPanel(searchingpnlid).add(label);
                                    } catch (java.lang.NullPointerException e) {
                                    	e.printStackTrace();
                                    }
                                    
                                    continue;
                                }
                                for (Meeting.Label l : labels) {
                                    if (l.isActive() && m.getLabels().contains(l)) {
                                    	JLabel label = getFormatedLabelForWithLabels (m,l);
                                        try {
                                        	this.getQiangRuoShiPanel(searchingpnlid).add(label);
                                        } catch (java.lang.NullPointerException e) {
                                        	e.printStackTrace();
                                        }
                                        
                                        break;
                                    }
                                }
                            }
                        	
                        } 

            
        }

        this.wholemonthpnl.validate();
        this.wholemonthpnl.repaint();
    }

    private JLabel getFormatedLabelForWithLabels(InsertedMeeting m, Label l) 
    {
    	JLabel label = new JLabel(m.getTitle());
        label.setToolTipText(m.getTitle() );
        label.setOpaque(true);
        label.setName( String.valueOf(m.getMeetingType()) + String.valueOf(m.getID()) );
        label.addMouseListener(new MeetingController());
        label.setForeground(Color.BLACK);
        label.setBackground(l.getColor());
        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
		return label;
	}

	private JLabel getFormatedLabelForNoneLabel(InsertedMeeting m) 
    {
    	JLabel label = new JLabel(m.getTitle());
        label.setToolTipText(m.getTitle() );
        label.setOpaque(true);
        label.setName( String.valueOf(m.getMeetingType()) + String.valueOf(m.getID()) );
        label.addMouseListener(new MeetingController());
        label.setForeground(ColorScheme.BLACK_FONT);
        label.setBackground(ColorScheme.GREY_WHITER_DARKER);
        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
		return label;
	}

	@Override
    public void onLabelChange(Cache cache) {
        this.onMeetingChange(cache);
    }

    private class WholeMonthNewsController extends MouseAdapter 
    { 
    	private int meetingtype;
    	
    	WholeMonthNewsController (int meetingtype)
    	{
    		this.meetingtype = meetingtype;
    	}
        @Override
        public void mouseClicked(MouseEvent e) {
        	
        	super.mouseClicked(e);
            JPanel panel = (JPanel) e.getSource();
            LocalDate mDate = LocalDate.now();//.parse(panel.getName().substring(0, 10));
//            LocalDateTime dateTime = LocalDateTime.of(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), 8, 0);
            
            
//        	if (e.getClickCount() == 1) { //��ȡѡ�������
//        		setDate (mDate);
//        	}
        	
        	if (e.getClickCount() == 2) { //����һ���µ�meeting
        		
        		String title = null;String owner = null; String keywords = null;
        		if(this.meetingtype == Meeting.CHANGQIJILU) {
        			title = "���ڼ�¼";
        			owner = "wwwwww";
        			keywords = "�ؼ���";
        		} else if(this.meetingtype == Meeting.QIANSHI) {
        			title = "ǿ�Ƹ��ɰ��";
        			owner = "";
        			keywords = "ǿ�Ƹ��ɰ��";
        		}  else if(this.meetingtype == Meeting.RUOSHI) {
        			title = "���Ƹ��ɰ��";
        			owner = "";
        			keywords = "���Ƹ��ɰ��"; 
        		} else if(this.meetingtype == Meeting.JINQIGUANZHU) {
        			title = "���ڹ�ע";
        			owner = "gzgzgz";
        			keywords = "���ڹ�ע"; 
        		}  
        		
        		
        		
                Meeting meeting = new Meeting(title, mDate,
                     "����", keywords, new HashSet<>(),"SlackURL",owner,this.meetingtype);
                getCreateDialog().setMeeting(meeting);
                getCreateDialog().setVisible(true);
        	}
            
        }
    }
//    private class QiangRuoBkStockController extends MouseAdapter 
//    { 
//        @Override
//        public void mouseClicked(MouseEvent e) {
//        	
//         	super.mouseClicked(e);
//            JPanel panel = (JPanel) e.getSource();
//            String pnlnamedate = panel.getName().substring(0, 10);
////            LocalDate mDate = LocalDate.parse(pnlnamedate);
//            LocalDate mDate = LocalDate.now();
//            
//            String pnltype = panel.getName().substring(10);
//            String meetingname = null ;
//            if(pnltype.equals("qqqqqq"))
//            	meetingname = "ǿ�ư��/����";
//            else
//            if(pnltype.equals("rrrrrr"))
//            	meetingname = "���ư��/����";
//            else
//            if(pnltype.equals("gzgzgz"))
//            	meetingname = "��ע������";
//            	
////            LocalDateTime dateTime = LocalDateTime.of(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), 8, 0);
//        	if (e.getClickCount() == 1) { //��ȡѡ�������
////        		setDate (mDate);
//        	}
//        	
//        	if (e.getClickCount() == 2) { //����һ���µ�meeting
//                Meeting meeting = new Meeting(meetingname,mDate,
//                     "����", pnltype, new HashSet<>(),"SlackURL",pnltype,Meeting.QIANGRUO);
//                getCreateDialog().setMeeting(meeting);
//                getCreateDialog().setVisible(true);
//        	}
//            
//        }
//    }

	
    private class MeetingController extends MouseAdapter 
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            JLabel label = (JLabel) e.getSource();
            String labelname = label.getName();
            Meeting meeting;
            Collection<InsertedMeeting> meetingslist = getCache().produceMeetings();
            for(InsertedMeeting m : meetingslist) {
            	String searchname = String.valueOf(m.getMeetingType()  ) + String.valueOf(m.getID() );
            	
				if(searchname.equals(labelname)) {
            		JLabel source = (JLabel) e.getSource();
//                    String title = source.getText();
//                    LocalDate date = LocalDate.parse(source.getParent().getParent().getName());
                    

                    	getModifyDialog().setMeeting(m);
                        getModifyDialog().setVisible(true);
                    
            	}
            }
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
}



