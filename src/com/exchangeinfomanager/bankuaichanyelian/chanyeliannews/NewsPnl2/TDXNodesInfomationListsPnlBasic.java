package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

abstract public class TDXNodesInfomationListsPnlBasic extends JDialog 
{
	protected BkChanYeLianTreeNode node;
	protected DBMeetingService curmeetingService;
	protected DBLabelService alllabelService;

	public TDXNodesInfomationListsPnlBasic (BkChanYeLianTreeNode curnode)
	{
		this.node = curnode;
		curmeetingService = new DBMeetingService ();
		alllabelService = new DBLabelService ();
		
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	}
	
	protected void createEvents() 
	{
		deletenewstogegu.addMouseListener(new RemoveController() );
	}
	
	/*
	 * 
	 */
	public void updateNewsToABkGeGu (InsertedMeeting news,String bkggcode)
	{
		Boolean addresult = news.addMeetingToSpecificOwner(bkggcode);
		if(addresult) {
			try {
				curmeetingService.updateMeeting(news);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void updateNewsToABkGeGu (InsertedMeeting news,Set<String> bkggcodeset)
	{
		Boolean addresult = news.addMeetingToSpecificOwner(bkggcodeset);
		if(addresult) {
			try {
				curmeetingService.updateMeeting(news);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setListsViewTitle (String title)
	{
		panelgegunews.setListsViewTitle ( title);
		panelallnews.setListsViewTitle ( title);
	}
	
	private class RemoveController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	InsertedMeeting upmeeting = panelgegunews.getCurSelectedNews ();
            	
            	if(addnewstofriends.isSelected()) {
        			Set<String> friendset = ((BanKuai)node).getSocialFriendsSet ();
        			upmeeting.removeMeetingSpecficOwner (friendset );
        			upmeeting.removeMeetingSpecficOwner (node.getMyOwnCode() );
                	curmeetingService.updateMeeting(upmeeting);
        		} else {
        			upmeeting.removeMeetingSpecficOwner (node.getMyOwnCode() );
                	curmeetingService.updateMeeting(upmeeting);
        		}
            	setVisible(true);
            } catch (Exception e1) {
                e1.printStackTrace();
            	setVisible(true);
            } 
        }
    }
	
	protected TDXNodesInfomationListsView panelgegunews;
	protected TDXNodesInfomationListsView panelallnews;
	protected JButton addnewstogegu;
	private JPanel centerPanel;
	private JButton deletenewstogegu;
	protected JCheckBox addnewstofriends;
	
	protected static final int WIDTH = 500;
    protected static final int HEIGHT = 500;
    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;
	
	protected void initializeGui(TDXNodesInfomationListsView panelgegunews1,TDXNodesInfomationListsView panelallnews1) 
	{
		this.panelgegunews = panelgegunews1;
		this.panelallnews = panelallnews1;
		
		this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.PAGE_AXIS));
        
        this.centerPanel.add(panelgegunews);
        this.centerPanel.add(Box.createVerticalStrut(5));
        JPanel addp = JPanelFactory.createPanel(new FlowLayout(FlowLayout.LEFT));
        
        addnewstogegu = new JButton("添加到当前");
        addnewstofriends = new JCheckBox ("对朋友板块相同操作");
        if(this.node != null  && this.node.getType() != BkChanYeLianTreeNode.TDXBK)
        	addnewstofriends.setEnabled(false);
        deletenewstogegu = new JButton("从当前移除");
        addp.add(addnewstogegu);
        addp.add(new Label("          "));
        addp.add(deletenewstogegu);
        addp.add(new Label("          "));
        addp.add(addnewstofriends);

        this.centerPanel.add(addp);

        this.centerPanel.add(Box.createVerticalStrut(5));
        
        this.centerPanel.add(panelallnews);
        
        JTextField description = new JTextField("* 可到新闻界面查看颜色含义") ;
        description.setEditable(false);
        
        this.centerPanel.add(description);
        

        // add main panel to the dialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.centerPanel, BorderLayout.CENTER);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(new Dimension(500, 520));
        setResizable(false);

	}



}
