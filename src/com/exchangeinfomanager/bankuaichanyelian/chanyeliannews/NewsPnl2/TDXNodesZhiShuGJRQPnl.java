package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TDXNodesZhiShuGJRQPnl   extends TDXNodesInfomationListsPnlBasic 
{

	public TDXNodesZhiShuGJRQPnl(BkChanYeLianTreeNode curnode) 
	{
		super(curnode);

		//有些新闻就是指数关键日期的来源，把新闻也放进来，减少输入次数
		Integer[] wantednewstypeall = {Integer.valueOf(Meeting.ZHISHUDATE),Integer.valueOf(Meeting.NODESNEWS)};
	    Cache cacheAll = new Cache("ALL",super.curmeetingService, super.alllabelService,LocalDate.now().minusWeeks(52),LocalDate.now(),wantednewstypeall);
	    TDXNodesZhiShuGJRQModle allmodle = new TDXNodesZhiShuGJRQModle ();
	    TDXNodesInfomationListsView panelallnews = new TDXNodesInfomationListsView(super.curmeetingService,cacheAll,allmodle);
	    
	    String checkcode;
	    if(curnode == null) {
	    	checkcode = "ALL";
	    }
	    else 
	    	 checkcode = super.node.getMyOwnCode();
	    Integer[] wantednewstypesingle = {Integer.valueOf(Meeting.ZHISHUDATE)};
	    Cache cachecurnode = new Cache(checkcode,super.curmeetingService, super.alllabelService,LocalDate.now().minusWeeks(52),LocalDate.now(),wantednewstypesingle);
	    TDXNodesZhiShuGJRQModle wantedmodle = new TDXNodesZhiShuGJRQModle ();
	    TDXNodesInfomationListsView panelgegunews = new TDXNodesInfomationListsView(super.curmeetingService,cachecurnode,wantedmodle);
	    
	    super.initializeGui (panelgegunews,panelallnews);
	    this.createEvents();
	    
	    super.setListsViewTitle ("52周内关键指数");
	}
	
	protected void createEvents()
	{
		super.createEvents();
		
		super.addnewstogegu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		InsertedMeeting selectnewsall = panelallnews.getCurSelectedNews ();
        		if(selectnewsall == null)
        			return;
        		
        		createZsgjrqOrUpdatedZsgjrq (selectnewsall);
        	}
        });
		
	}
	
	private void createZsgjrqOrUpdatedZsgjrq(InsertedMeeting selectnewsall) 
	{
		if(selectnewsall.getMeetingType() == Meeting.ZHISHUDATE) {
			if(addnewstofriends.isSelected()) {
    			Set<String> friendset = ((BanKuai)node).getSocialFriendsSet ();
    			updateNewsToABkGeGu(selectnewsall,friendset);
    			updateNewsToABkGeGu(selectnewsall,node.getMyOwnCode());
    		} else
    			updateNewsToABkGeGu(selectnewsall,node.getMyOwnCode());
		}
		else if(selectnewsall.getMeetingType() == Meeting.NODESNEWS) {
			Meeting meeting = new Meeting(selectnewsall.getTitle(),selectnewsall.getStart(),
					selectnewsall.getTitle(), "关键词", new HashSet<>(),"SlackURL",node.getMyOwnCode(),Meeting.ZHISHUDATE);
			
			try {
				super.curmeetingService.createMeeting(meeting);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent evt)
//	{
//		String evtpropertyname = evt.getPropertyName();
//		if (evt.getPropertyName().equals("CREATENEWS")) {
//			int i = 0;
//			
//		}
//	}

}
