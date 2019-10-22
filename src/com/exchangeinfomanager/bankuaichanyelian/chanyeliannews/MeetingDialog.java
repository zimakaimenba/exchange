package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;


import com.exchangeinfomanager.StockCalendar.ColorScheme;

import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@SuppressWarnings("all")
public  class MeetingDialog<T extends Meeting> extends JDialog 
{
    protected static final int WIDTH = 400;
    protected static final int HEIGHT = 600;
    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;

    protected static final DateTimeFormatter LABEL_DATE = DateTimeFormatter.ofPattern("dd MMM uuuu");
    protected JUpdatedTextField newsownersField;
    protected JUpdatedTextField newstitleField;
    protected JUpdatedTextField keywordsField; 
    protected JUpdatedTextField slackurlField;
    protected JTextArea descriptionArea;
    protected JLocalDateChooser startTimeChooser;
    protected JLocalDateChooser endTimeChooser;
    
    protected JPanel centerPanel;
    protected JLabel labelButton;
    protected Cache cache;
    protected EventService meetingService;
    protected LabelListDialog labelListDialog;
    private BanKuaiDbOperation bkdbopt;

    private T event;

    public MeetingDialog(EventService meetingService, Cache cache) 
    {
        this.meetingService = meetingService;
        this.cache = cache;
        this.bkdbopt = new BanKuaiDbOperation ();
        
        this.createUIComponents();
        this.createCenterPanel();
        
        this.createEvent ();
        
    }

    private void createEvent() 
    {
    	descriptionArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				
				if (SwingUtilities.isRightMouseButton(event)) {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					try	{
						String result = (String) clipboard.getData(DataFlavor.stringFlavor);
						descriptionArea.setText(descriptionArea.getText() + result);
						
					}catch(Exception ex)	{
						
					}
				}
			}
		});
		
	}

	public JPanel getNewsPanel ()
    {
    	return this.centerPanel;
    }
    
    private void createUIComponents() 
    {
    	this.newsownersField = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	this.newsownersField.setMouseLeftClearEnabled(false);
        this.newstitleField = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
        this.newstitleField.setMouseLeftClearEnabled(false);
        this.keywordsField = (JUpdatedTextField) JTextFactory.createTextField();
        this.keywordsField.setMouseLeftClearEnabled(false);
        this.slackurlField = (JUpdatedTextField) JTextFactory.createTextField();
        this.slackurlField.setMouseLeftClearEnabled(false);
        this.slackurlField.setMouseRightPasteEnabled(true, JUpdatedTextField.PASTEWITHCLEAR);
        this.descriptionArea = JTextFactory.createTextArea();
        this.descriptionArea.setLineWrap(true);
        this.startTimeChooser = new JLocalDateChooser();
        this.endTimeChooser = new JLocalDateChooser ();

        this.centerPanel = new JPanel();
        this.labelListDialog = new LabelListDialog();
    }


    private void createCenterPanel()
    {
        this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.PAGE_AXIS));

        JPanel p = JPanelFactory.createPanel(new FlowLayout(FlowLayout.CENTER));
        this.centerPanel.add(p);
        this.centerPanel.add(Box.createVerticalStrut(PADDING));
        this.centerPanel.add(this.newsownersField);
        this.centerPanel.add(this.newstitleField);
        this.centerPanel.add(Box.createVerticalStrut(30));
                this.centerPanel.add(this.getTimeChooserPanel());
        this.centerPanel.add(Box.createVerticalStrut(10));

        JPanel labelPanel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 2));
        labelPanel.add(new JLabel("设置 labels: "));
        labelButton = JLabelFactory.createButton("Labels");
        labelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                labelListDialog.display();
            }
        });

        labelPanel.add(labelButton);
        this.centerPanel.add(labelPanel);

        this.centerPanel.add(Box.createVerticalStrut(PADDING));
        this.centerPanel.add(this.keywordsField);
        this.centerPanel.add(Box.createVerticalStrut(10));
        this.centerPanel.add(this.slackurlField);
        this.centerPanel.add(Box.createVerticalStrut(10));
        JScrollPane despane = new JScrollPane ();
        despane.setAutoscrolls(true);
        despane.setViewportView(this.descriptionArea);
        this.centerPanel.add(despane);
        this.centerPanel.add(Box.createVerticalStrut(PADDING));

        // add main panel to the dialog
        getContentPane().setLayout(new BorderLayout());
        super.add(this.centerPanel, BorderLayout.CENTER);
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setSize(new Dimension(WIDTH, HEIGHT));
        super.setResizable(false);
    }

    private JPanel getTimeChooserPanel() 
    {
        JPanel panel = JPanelFactory.createPanel(new GridLayout(1, 3) );
        panel.add(new JLabel("日期:"));
        panel.add(this.startTimeChooser);
        panel.add(this.endTimeChooser);
        return panel;
    }

    public Boolean setMeeting(T event)  
    {
        this.event = event;

        newstitleField.setText(event.getTitle());
        newsownersField.setText(event.getNewsOwnerCodes());
        keywordsField.setText(event.getKeyWords());
        descriptionArea.setText(event.getDescription());
        startTimeChooser.setLocalDate( event.getStart() );
        try{
        	endTimeChooser.setLocalDate( event.getEnd() );
        } catch (java.lang.NullPointerException ex) {
        	
        }
        slackurlField.setText(event.getSlackUrl());
        
        endTimeChooser.setVisible(false);
        
        //根据event类型不同定制界面
        if(event.getMeetingType() == Meeting.QIANSHI || event.getMeetingType() == Meeting.RUOSHI ) {
    		keywordsField.setVisible(false);
    		slackurlField.setVisible(false);
        } else if(event.getMeetingType() == Meeting.ZHISHUDATE ) {
        	newstitleField.setVisible(true);
        	newstitleField.setEnabled(true);
        	
        	newsownersField.setVisible(true);
        	newsownersField.setEnabled(true);
    		
    		keywordsField.setVisible(false);
    		slackurlField.setVisible(false);
        } else if(event.getMeetingType() == Meeting.CHANGQIJILU ) {

        	newsownersField.setVisible(false);
        	
        	keywordsField.setVisible(true);
    		slackurlField.setVisible(true);
    		newstitleField.setVisible(true);
        	
        	newstitleField.setEnabled(true);
        	keywordsField.setEnabled(true);
        	slackurlField.setEnabled(true);
        } else if ( event.getMeetingType() == Meeting.NODESNEWS) {
        	newsownersField.setVisible(true);
        	newsownersField.setEnabled(false);
        	
        	keywordsField.setVisible(true);
    		slackurlField.setVisible(true);
    		newstitleField.setVisible(true);
        	
        	newstitleField.setEnabled(true);
        	keywordsField.setEnabled(true);
        	slackurlField.setEnabled(true);
        	
        } else if (event.getMeetingType() == Meeting.JINQIGUANZHU) {
        	newstitleField.setVisible(true);
        	newstitleField.setEnabled(true);
        	
        	keywordsField.setVisible(false);
    		slackurlField.setVisible(false);
    		newsownersField.setVisible(false);
        }
        
    	
    	
        centerPanel.revalidate();
    	centerPanel.repaint();

        
        return true;
    }

    public T getMeeting() 
    {
    	if(event.getMeetingType() == Meeting.QIANSHI || event.getMeetingType() == Meeting.RUOSHI) { //指数和板块有时候代码重叠，强弱如果输入的重叠的话，要用户明确是个股还是板块
    		String nodecode = newsownersField.getText ();
    		if(nodecode.length() == 6) { 
    			
    			ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
    			BkChanYeLianTreeNode nodeshouldbedisplayed;
            	if(nodeslist.size() == 0) {
        			 JOptionPane.showMessageDialog(null,"股票/板块代码不存在，请再次输入正确股票代码！");
        			 return null;
        		 } else if(nodeslist.size() >1 ) {
        			 SelectMultiNode userselection = new SelectMultiNode(nodeslist);
                 	
            		 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "请选择", JOptionPane.OK_CANCEL_OPTION);
            		 if(exchangeresult == JOptionPane.CANCEL_OPTION)
            				return null;
            		 
            		 
            		 try {
            			 int userselected = userselection.getUserSelection();
            			 nodeshouldbedisplayed = nodeslist.get(userselected);
            		 } catch (java.lang.ArrayIndexOutOfBoundsException e) { //用户没有选择直接回车的情况
            			 nodeshouldbedisplayed = nodeslist.get(0);
            		 }
        		 } else
        			  nodeshouldbedisplayed = nodeslist.get(0);
				
				if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXBK )
					newsownersField.setText(nodecode + "bk");
				else if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXGG )
					newsownersField.setText(nodecode + "gg");
    		}
    		
    	}
		
		 
    	event.setNewsOwnerCodes(newsownersField.getText());
    	event.setTitle(newstitleField.getText());
    	event.setStart(startTimeChooser.getLocalDate() );
    	event.setEnd(endTimeChooser.getLocalDate() );
    	event.setKeyWords(keywordsField.getText());
    	event.setDescription(descriptionArea.getText());
    	event.setSlackUrl(slackurlField.getText());
        return event;
    }

    private LocalTime toLocalTime(Instant instant) {
        return LocalTime.from(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    private Instant adjustTime(Instant instant, LocalTime time) {
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute())
                            .toInstant(ZoneOffset.UTC);
    }

    private class LabelListDialog extends JDialog 
    {
        JPanel centerPanel;

        private LabelListDialog() 
        {
            this.centerPanel = JPanelFactory.createPanel();
            this.centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            this.centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            super.add(this.centerPanel);
            super.setModalityType(ModalityType.APPLICATION_MODAL);
            super.setTitle("Labels");
        }

        void display() 
        {
            this.centerPanel.removeAll();
            Collection<InsertedMeeting.Label> labels = cache.produceLabels();
            for (InsertedMeeting.Label label : labels) {
                JPanel mPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
                mPanel.setName(event.getLabels().contains(label) ? "selected" : "");

                JLabel name = JLabelFactory.createLabel("  " + label.getName());
                name.setName(String.valueOf(label.getID()));
                name.setOpaque(true);
                name.setBackground(event.getLabels().contains(label) ? label.getColor() : ColorScheme.BACKGROUND);
                name.setForeground(
                		event.getLabels().contains(label) ? ColorScheme.BACKGROUND : ColorScheme.BLACK_FONT);
                name.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.GREY_LINE));

                mPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        Component l = ((Component) e.getSource());
                        if (l.getName().equals("selected")) {
                            l.setName("");
                            name.setBackground(ColorScheme.BACKGROUND);
                            name.setForeground(ColorScheme.BLACK_FONT);
                        } else {
                            l.setName("selected");
                            name.setBackground(label.getColor());
                            name.setForeground(ColorScheme.BACKGROUND);
                        }
                    }
                });

                mPanel.add(name, BorderLayout.CENTER);

                this.centerPanel.add(mPanel);
                this.centerPanel.add(Box.createVerticalStrut(5));
            }

            super.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    event.getLabels().clear();

                    for (Component c : centerPanel.getComponents()) {
                        if (c instanceof JPanel && c.getName().equals("selected")) {
                            int id = Integer.valueOf(((JPanel) c).getComponent(0).getName());
                            Optional<InsertedMeeting.Label> label = cache.produceLabels()
                                                                 .stream()
                                                                 .filter(l -> l.getID() == id)
                                                                 .findFirst();
                            if (label.isPresent())
                            	event.getLabels().add(label.get());
                        }
                    }

                }
            });

            super.setMinimumSize(new Dimension(200, (int) this.centerPanel.getPreferredSize().getHeight() + 40));
            super.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
            super.setVisible(true);
        }
    }
}
