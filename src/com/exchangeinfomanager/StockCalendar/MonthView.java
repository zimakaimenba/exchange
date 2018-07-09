package com.exchangeinfomanager.StockCalendar;

import javax.swing.*;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingService;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.commonlib.WrapLayout;

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
import java.util.Optional;

@SuppressWarnings("all")
public class MonthView extends View 
{

    private JPanel calendar = new JPanel();
    private JLabel dateLabel = new JLabel();

    public MonthView(MeetingService meetingService, Cache cache) 
    {
        super(meetingService, cache);

        cache.addCacheListener(this);
        this.onMeetingChange(cache); //获取本月的信息
        this.initMonthView();
    }
    /*
     * 
     */
    private void initMonthView() 
    {
        super.setBackground(ColorScheme.BACKGROUND);
        super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        super.add(this.getWeekdaysPanel());
        super.add(Box.createVerticalStrut(10));

        this.calendar.setLayout(new GridLayout(6, 7, 1, 1));
        this.calendar.setBackground(ColorScheme.GREY_LINE);

        super.add(this.calendar);
    }

    private JPanel getWeekdaysPanel() {

        JPanel weekdays = JPanelFactory.createFixedSizePanel(new GridLayout(1, 7));
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
                panel.addMouseListener(new DayController());
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

            this.calendar.add(scrollpane);
        }
        
        return first;
    }

    private JPanel getMonthDay(LocalDate date) {
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
     * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener#onMeetingChange(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache)
     */
    public void onMeetingChange(Cache cache) 
    {
    	LocalDate firstdayofmonth = this.initView();
    	
        Collection<InsertedMeeting> meetings = cache.produceMeetings(firstdayofmonth);
        Collection<InsertedMeeting.Label> labels = cache.produceLabels();

        
        for (InsertedMeeting m : meetings) {
            LocalDate mDate = m.getStart();
            if (mDate.getMonth().equals(super.getDate().getMonth()) && (mDate.getYear() == super.getDate().getYear()) ) {
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
//                        label.setForeground(ColorScheme.BACKGROUND);
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
    /*
     * 
     */
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
                Meeting meeting = new Meeting("新闻标题",mDate,
                     "描述", "关键词", new HashSet<>(),"SlackURL","999999");
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
                String title = source.getText();
                LocalDate date = LocalDate.parse(source.getParent().getParent().getName());
                
                InsertedMeeting selectedmeeting = meeting.get();
                String owner = selectedmeeting.getNewsownercodes();
                if( owner.contains("000000") && title.contains("总结") ) { //说明是大盘一周总结，内容是XML，用DaPanWeeklyFengXi显示内容
                	showWeeklyFenXiWizardDialog  ("000000",date);
                } else {
                	getModifyDialog().setMeeting(meeting.get());
                    getModifyDialog().setVisible(true);
                }
                
            }
        }

    }
    /*
	 * 
	 */
	private void showWeeklyFenXiWizardDialog(String nodeshouldbedisplayed, LocalDate selectdate) 
	{
		WeeklyFenXiWizard ggfx = new WeeklyFenXiWizard ( nodeshouldbedisplayed,BanKuaiAndStockBasic.DAPAN,selectdate);
    	ggfx.setSize(new Dimension(1400, 800));
    	ggfx.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else
    	ggfx.setLocationRelativeTo(null);
    	if(!ggfx.isVisible() ) 
    		ggfx.setVisible(true);
    	ggfx.toFront();
    	
    	ggfx = null;
	}
    


//	@Override
//	public void actionPerformed(ActionEvent arg0) 
//	{
//		// TODO Auto-generated method stub
//		
//	}

}
