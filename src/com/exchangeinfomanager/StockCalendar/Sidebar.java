package com.exchangeinfomanager.StockCalendar;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DialogFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JLabelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JTextFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.MeetingService;
import com.toedter.calendar.JCalendar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@SuppressWarnings("all")
public class Sidebar extends View implements CacheListener {

    private LabelService labelService;
    private JPanel labels;
    private JLabel colorButton;
    private JLabel createLabel;
    private JLabel createMilestoneDateForZhiShu;

    private JTextField nameField;
    private LabelDialog modifyLabelDialog;
    private LabelDialog createLabelDialog;
    private Cache cache;
	private JScrollPane labelsscrollpane;

    public Sidebar(MeetingService meetingService, LabelService labelService, Cache cache) {
    	super (meetingService,cache);
        this.labelService = labelService;
        this.cache = cache;
        this.createLabelDialog = DialogFactory.createLabelDialog(labelService);
        this.modifyLabelDialog = DialogFactory.modifyLabelDialog(labelService);
        this.initUI();
        this.initSidebar();
        cache.addCacheListener(this);
        this.onLabelChange(cache);
    }

    private void initUI() {
        this.labels = JPanelFactory.createPanel();
        this.createLabel = JLabelFactory.createButton("New label");
        this.createMilestoneDateForZhiShu = JLabelFactory.createButton("指数关键日期");
        this.colorButton = JLabelFactory.createLabel("", 40, 30);
        this.nameField = JTextFactory.createTextField();
    }

    private void initSidebar() {
        super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        super.setPreferredSize(new Dimension(150, Integer.MAX_VALUE));
        super.setPreferredSize(new Dimension(150, 150));
        super.setBackground(ColorScheme.BACKGROUND);

        JPanel labelPanel = JPanelFactory.createFixedSizePanel();
        labelPanel.add(new JLabel("Labels: "));
        super.add(labelPanel);

        super.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
        this.labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labelsscrollpane = new JScrollPane ();
        labelsscrollpane.setViewportView(this.labels);
        super.add(labelsscrollpane);
        super.add(Box.createVerticalStrut(5));

        JPanel createLabelPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        this.createLabel.addMouseListener(new CreateNewLabelController());
        createLabelPanel.add(this.createLabel, BorderLayout.CENTER);
        super.add(createLabelPanel);
        
        JPanel createDapanDatePanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        this.createMilestoneDateForZhiShu.addMouseListener(new CreateDapanMileStoneDateController());
        createDapanDatePanel.add(this.createMilestoneDateForZhiShu, BorderLayout.CENTER);
        super.add(createDapanDatePanel);
        
        
    }

    @Override
    public void onMeetingChange(Cache cache) {

    }

    @Override
    public void onLabelChange(Cache cache) {
        this.labels.removeAll();
        for (InsertedMeeting.Label label : cache.produceLabels()) {
            JPanel mPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
            mPanel.setName(String.valueOf(label.getID()));
            mPanel.setPreferredSize(labelsscrollpane.getPreferredSize());

            JLabel name = JLabelFactory.createLabel("  " + label.getName());
            name.setOpaque(true);
            name.addMouseListener(new NameController());
            name.setBackground(label.isActive()? label.getColor(): ColorScheme.BACKGROUND);
//            name.setForeground(label.isActive()? ColorScheme.BACKGROUND: ColorScheme.BLACK_FONT);
            name.setForeground( ColorScheme.BLACK_FONT);
            name.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, ColorScheme.GREY_LINE));

            JLabel option = JLabelFactory.createButton("*", 20);
            option.addMouseListener(new ModifyLabelController());
            mPanel.add(name, BorderLayout.CENTER);
            mPanel.add(option, BorderLayout.LINE_END);
            labels.add(mPanel);
            labels.add(Box.createVerticalStrut(5));
        }
//        Dimension d = labelsscrollpane.getPreferredSize();
//        this.labels.setPreferredSize(new Dimension(d.width,cache.produceLabels().size()+1));
        super.validate();
        super.repaint();
    }

    private class NameController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int id = Integer.valueOf(((Component) e.getSource()).getParent().getName());
            Optional<InsertedMeeting.Label> label = cache.produceLabels().stream().filter(l -> l.getID() == id).findFirst();
            if (label.isPresent()) {
                InsertedMeeting.Label lbl = label.get();
                if(lbl.isActive())
                    lbl.setActive(false);
                else
                    lbl.setActive(true);
                try {
                    labelService.updateLabel(lbl);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } 
            }
        }
    }

    private class CreateNewLabelController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            createLabelDialog.setLabel(new Meeting.Label("New label", ColorScheme.ORANGE_LIGHT, true));
            createLabelDialog.setVisible(true);
        }
    }

    private class CreateDapanMileStoneDateController extends MouseAdapter {
         
//    	MeetingService meetingService;
//		private MeetingDialog createDialog;
//		private MeetingDialog modifyDialog;

		@Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            
//            ZhiShuMilestoneDatePanel zsdate = new ZhiShuMilestoneDatePanel ();
//            JOptionPane.showMessageDialog(null, zsdate, "设置指数特别关注日期", JOptionPane.OK_CANCEL_OPTION);
//            
//            zsdate.getZhiShuCode();
//            zsdate.getZhiShuMilestoneDate();
//            zsdate.getZhiShuMilestoneShuoming();
//            MeetingService meetingService = new DBMeetingService ( );
//        	LabelService labelService = new DBLabelService ();
//            cache = new Cache("ALL",meetingService, labelService,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//                        this.createDialog = DialogFactory.createMeetingDialog(meetingService, cache);
//            this.modifyDialog = DialogFactory.modifyMeetingDialog(meetingService, cache);
//            
//            Meeting meeting = new Meeting("设置指数关键日期",LocalDate.now(),
//                    "描述", "指数关键日期", new HashSet<>(),"SlackURL","指数代码",Meeting.ZHISHUDATE);
//            
//            getCreateDialog().setMeeting(meeting);
//            getCreateDialog().setVisible(true);
//            createDialog.setMeeting(meeting);
//            createDialog.setVisible(true);
            
            Meeting meeting = new Meeting("设置指数关键日期",LocalDate.now(),
                  "描述", "指数关键日期", new HashSet<>(),"SlackURL","指数代码",Meeting.ZHISHUDATE);
            		
               getCreateDialog().setMeeting(meeting);
               getCreateDialog().setVisible(true);
        }
    }

    private class ModifyLabelController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int id = Integer.valueOf(((Component) e.getSource()).getParent().getName());
            Optional<InsertedMeeting.Label> label = cache.produceLabels().stream().filter(l -> l.getID() == id).findFirst();
            if (label.isPresent()) {
                modifyLabelDialog.setLabel(label.get());
                modifyLabelDialog.setVisible(true);
            }
        }
    }
}
