package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ZhiShuGuanJianRiQi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianGeGuNews;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;


public class ZhiShuGJRQManagementPnl extends JDialog
{
	protected static final int WIDTH = 500;
    protected static final int HEIGHT = 500;
    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;
	private EventService curmeetingService;
	/**
	 * Create the panel.
	 */
	public ZhiShuGJRQManagementPnl(String curnodecode)
	{
		this.myowncode = curnodecode;
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		
		Integer[] wantednewstype = {Integer.valueOf(Meeting.ZHISHUDATE)};
		
		LabelService alllabelService = new DBLabelService ();
		EventService allDbmeetingService = new DBMeetingService ();
	    Cache cacheAll = new Cache("ALL",allDbmeetingService, alllabelService,LocalDate.now().minusWeeks(52),LocalDate.now(),wantednewstype);
	    panelallnews = new ZhiShuGJRQView(allDbmeetingService,cacheAll,"25周内所有关键日期");
	    
	    curmeetingService = new DBMeetingService ();
		LabelService curlabelService = new DBLabelService ();
	    Cache cachecurnode = new Cache(curnodecode,curmeetingService, curlabelService,LocalDate.now().minusWeeks(52),LocalDate.now(),wantednewstype);
	    panelgegunews = new ZhiShuGJRQView(curmeetingService,cachecurnode,curnodecode + "25周内关键日期");
		
		initializeGui ();
		createEvents ();
	}

	private String myowncode;
	private ZhiShuGJRQView panelgegunews;
	private ZhiShuGJRQView panelallnews;
	private JButton addnewstogegu;
	private JPanel centerPanel;
	private JButton deletenewstogegu;
	private static Logger logger = Logger.getLogger(ChanYeLianNewsPanel.class);

	private class RemoveController extends MouseAdapter 
	{
	    @Override
	    public void mouseClicked(MouseEvent e) {
	        super.mouseClicked(e);
	        try {
	        	InsertedMeeting upmeeting = panelgegunews.getCurSelectedNews ();
	        	upmeeting.removeMeetingSpecficOwner (myowncode);
	        	if(!upmeeting.getNewsOwnerCodes().isEmpty())
	        		curmeetingService.updateMeeting(upmeeting);
	        	else
	        		curmeetingService.deleteMeeting(upmeeting);
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        } 
	       	setVisible(false);
    	}
	}

	private void createEvents() 
	{
		
		deletenewstogegu.addMouseListener(new RemoveController() );
		
		addnewstogegu.addMouseListener(new MouseAdapter() {
	    	@Override
	    	public void mouseClicked(MouseEvent arg0) {
	    		InsertedMeeting selectnewsall = panelallnews.getCurSelectedNews ();
	    		if(selectnewsall == null)
	    			return;
	    		
	    		panelgegunews.updateNewsToABkGeGu(selectnewsall,myowncode);
	    	}
	    });
	}
	
	private void initializeGui() 
	{
		this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
	    this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.PAGE_AXIS));
	    
	    this.centerPanel.add(panelgegunews);
	    this.centerPanel.add(Box.createVerticalStrut(5));
	    JPanel p = JPanelFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
	    deletenewstogegu = new JButton("从当前前个股/板块删除");
	    addnewstogegu = new JButton("添加到当前个股/板块");
	    p.add(deletenewstogegu);
	    p.add(new Label("                       "));
	    p.add(addnewstogegu);
	    this.centerPanel.add(p);
	    this.centerPanel.add(Box.createVerticalStrut(5));
	    this.centerPanel.add(panelallnews);
	    
	
	//   
	//
	//    this.centerPanel.add(Box.createVerticalStrut(PADDING));
	//    
	//    this.centerPanel.add(Box.createVerticalStrut(10));
	//    
	//    this.centerPanel.add(Box.createVerticalStrut(10));
	//   
	//    this.centerPanel.add(Box.createVerticalStrut(PADDING));
	
	    // add main panel to the dialog
	    getContentPane().setLayout(new BorderLayout());
	    getContentPane().add(this.centerPanel, BorderLayout.CENTER);
	    setModalityType(ModalityType.APPLICATION_MODAL);
	    setSize(new Dimension(WIDTH, HEIGHT));
	    setResizable(false);
	
	    
	
	}


}
