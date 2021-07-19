package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.Set;

import javax.swing.JCheckBox;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;

public class TDXNodsInforPnl extends TDXNodesInfomationListsPnlBasic 
{

	public TDXNodsInforPnl(BkChanYeLianTreeNode curnode) 
	{
		super(curnode);

		Integer[] wantednewstypeforall = {Integer.valueOf(Meeting.NODESNEWS), Integer.valueOf(Meeting.CHANGQIJILU),
    								Integer.valueOf(Meeting.QIANSHI),Integer.valueOf(Meeting.RUOSHI)	};
        Cache cacheAll = new Cache("ALL",super.curmeetingService, super.alllabelService,LocalDate.now().minusWeeks(10),LocalDate.now(),
        		wantednewstypeforall);
        TDXNodesInformationTableModle allinformodle = new TDXNodesInformationTableModle ();
        TDXNodesInfomationListsView panelallnews = new TDXNodesInfomationListsView(super.curmeetingService,cacheAll,allinformodle);
        
        
    	Integer[] wantednewstypefornode = {Integer.valueOf(Meeting.NODESNEWS),Integer.valueOf(Meeting.QIANSHI),Integer.valueOf(Meeting.RUOSHI)		};
        Cache cachecurnode = new Cache(super.node.getMyOwnCode(),super.curmeetingService, super.alllabelService,LocalDate.now().minusWeeks(10),LocalDate.now(),wantednewstypefornode);
        TDXNodesInformationTableModle gginformodle = new TDXNodesInformationTableModle ();
        TDXNodesInfomationListsView panelgegunews = new TDXNodesInfomationListsView(curmeetingService,cachecurnode,gginformodle);
		
        
        
		super.initializeGui (panelgegunews,panelallnews);
		this.createEvents();
		
		super.setListsViewTitle ("52周内所有新闻和其他信息");
	}
	
	protected void createEvents()
	{
		super.createEvents();
		
		super.addnewstogegu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		addNewsAsNeeded ();
        	}
        });
	}
	
	private void addNewsAsNeeded ()
	{
		InsertedMeeting selectnewsall = panelallnews.getCurSelectedNews ();
		if(selectnewsall == null)
			return;
		
		if( super.node.getType() != BkChanYeLianTreeNode.TDXBK )
			return ;
		
		if( ((JCheckBox) addnewstofriends.getSelectedItem()).isSelected()) {
			Set<String> friendset = ((BanKuai)node).getSocialFriendsSet ();
			updateNewsToABkGeGu(selectnewsall,friendset);
			updateNewsToABkGeGu(selectnewsall,node.getMyOwnCode());
		} else
			updateNewsToABkGeGu(selectnewsall,node.getMyOwnCode());
	}

}
