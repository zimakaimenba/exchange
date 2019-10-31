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
import javax.swing.JLabel;
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
		
		//有些新闻就是指数关键日期的来源，把新闻也放进来，减少输入次数
		Integer[] wantednewstypeall = {Integer.valueOf(Meeting.ZHISHUDATE),Integer.valueOf(Meeting.NODESNEWS)};
		LabelService alllabelService = new DBLabelService ();
		EventService allDbmeetingService = new DBMeetingService ();
	    Cache cacheAll = new Cache("ALL",allDbmeetingService, alllabelService,LocalDate.now().minusWeeks(52),LocalDate.now(),wantednewstypeall);
	    panelallnews = new ZhiShuGJRQView(allDbmeetingService,cacheAll,"'25周内所有关键日期和新闻'");
	    
	    Integer[] wantednewstypesingle = {Integer.valueOf(Meeting.ZHISHUDATE)};
	    curmeetingService = new DBMeetingService ();
		LabelService curlabelService = new DBLabelService ();
	    Cache cachecurnode = new Cache(curnodecode,curmeetingService, curlabelService,LocalDate.now().minusWeeks(52),LocalDate.now(),wantednewstypesingle);
	    panelgegunews = new ZhiShuGJRQView(curmeetingService,cachecurnode,curnodecode + "'25周内关键日期'");
		
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
	        	if(upmeeting == null)
	        		return ;
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
	    		InsertedMeeting selectnews = panelallnews.getCurSelectedNews ();
	    		if(selectnews == null)
	    			return;
	    		
	    		if(selectnews.getMeetingType() == Meeting.ZHISHUDATE)
	    			panelgegunews.updateNewsToABkGeGu(selectnews,myowncode);
	    		else if(selectnews.getMeetingType() == Meeting.NODESNEWS)
	    			panelgegunews.addZhiShuGuanJianRiQi(selectnews);
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
	    deletenewstogegu = new JButton("从当前板块移除");
	    addnewstogegu = new JButton("添加到当前板块");
	    p.add(deletenewstogegu);
	    p.add(new Label("                       "));
	    p.add(addnewstogegu);
	    this.centerPanel.add(p);
	    this.centerPanel.add(Box.createVerticalStrut(5));
	    this.centerPanel.add(panelallnews);
	    this.centerPanel.add(new JLabel("* 红色为指数关键日期，黄色为近期新闻。") );

	
	    // add main panel to the dialog
	    getContentPane().setLayout(new BorderLayout());
	    getContentPane().add(this.centerPanel, BorderLayout.CENTER);
	    setModalityType(ModalityType.APPLICATION_MODAL);
	    setSize(new Dimension(WIDTH, HEIGHT));
	    setResizable(false);
	
	    
	
	}


}
