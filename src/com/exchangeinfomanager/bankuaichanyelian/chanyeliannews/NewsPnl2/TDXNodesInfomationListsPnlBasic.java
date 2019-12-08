package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.JComboCheckBox.JComboCheckBox;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.google.common.collect.Multimap;

abstract public class TDXNodesInfomationListsPnlBasic extends JDialog 
{
	protected BkChanYeLianTreeNode node;
	private AllCurrentTdxBKAndStoksTree bkstk;
	private BanKuaiDbOperation bkopt;

	public TDXNodesInfomationListsPnlBasic (BkChanYeLianTreeNode curnode)
	{
		this.node = curnode;
		
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
    	
    	ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache ("ALL",svsnews,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsnews.setCache(newcache);
    	
    	ServicesForNews svscqgz = new ChangQiGuanZhuServices ();
    	NewsCache cqgzcache = new NewsCache ("ALL",svscqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svscqgz.setCache(cqgzcache);
    	
    	ServicesForNews svsqs = new QiangShiServices ();
    	NewsCache qiangshicache = new NewsCache ("ALL",svsqs,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsqs.setCache(qiangshicache);
    	
    	ServicesForNews svsrs = new RuoShiServices ();
    	NewsCache ruoshicache = new NewsCache ("ALL",svsrs,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsrs.setCache(ruoshicache);
    	
    	ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsdqgz.setCache(dqgzcache);
    	
		bkstk = AllCurrentTdxBKAndStoksTree.getInstance();
		bkopt = new BanKuaiDbOperation ();
		
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	}
	
	protected void createEvents() 
	{
		deletenewstogegu.addMouseListener(new RemoveController() );
		
		panelgegunews.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	//用户添加新新闻后，确定是否要对朋友板块和所属个股添加
                if (evt.getPropertyName().equals(TDXNodesInfomationListsView.ANEWSADDED)) {
                	if( node.getType() != BkChanYeLianTreeNode.TDXBK )
            			return ;
                	
                	InsertedMeeting mt = (InsertedMeeting) evt.getNewValue();
                	updatedNewsToFriendsAndChildrenBasedOnConfigration (mt);
                }
            }
		});
	}
	/*
	 * 
	 */
	private void updatedNewsToFriendsAndChildrenBasedOnConfigration (InsertedMeeting mt)
	{
		int count = addnewstofriends.getItemCount();
		for(int i=0; i<addnewstofriends.getItemCount(); i++) {
			JCheckBox tmpitem = (JCheckBox) addnewstofriends.getItemAt(i);
			if(tmpitem.isSelected() && tmpitem.getText().contains("板块")) {
				Set<String> friendset = ((BanKuai)node).getSocialFriendsSet ();
				if(friendset.isEmpty())
					continue;
				
				updateNewsToABkGeGu(mt,friendset);
//				updateNewsToABkGeGu(mt,node.getMyOwnCode());
			} else if(tmpitem.isSelected() && tmpitem.getText().contains("个股")) {
				node = bkopt.getTDXBanKuaiGeGuOfHyGnFg (  (BanKuai)node,CommonUtility.getSettingRangeDate(LocalDate.now(),"large"), LocalDate.now(),bkstk.getAllBkStocksTree());
				Collection<BkChanYeLianTreeNode> children = ((BanKuai)node).getSpecificPeriodBanKuaiGeGu(LocalDate.now(), 0);
				if(children.isEmpty())
				 continue;
				 
				Set<String> childrenset = new HashSet<> ();
				for (BkChanYeLianTreeNode tmpnode : children) {
						childrenset.add(tmpnode.getMyOwnCode());
				}
				updateNewsToABkGeGu (mt, childrenset);
			}
		}
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
            	
            	if( ((JCheckBox)addnewstofriends.getSelectedItem()).isSelected() ) {
            		
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
	protected JComboBox addnewstofriends;
	
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
        
		Vector v = new Vector();
		v.add(new JCheckBox("对朋友板块相同操作",true));
		v.add(new JCheckBox("对所属个股相同操作",true));
		addnewstofriends = new JComboCheckBox(v);
//		addnewstofriends = new JCheckBox ("对朋友板块相同操作");
		addnewstofriends.setFont(new Font("宋体", Font.PLAIN, 12));
        if(this.node != null  && this.node.getType() != BkChanYeLianTreeNode.TDXBK)
        	addnewstofriends.setEnabled(false);
        else
        	addnewstofriends.setEnabled(true);
        
        deletenewstogegu = new JButton("从当前移除");
        addp.add(addnewstogegu);
        addp.add(new Label("   "));
        addp.add(deletenewstogegu);
        addp.add(new Label(""));
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
