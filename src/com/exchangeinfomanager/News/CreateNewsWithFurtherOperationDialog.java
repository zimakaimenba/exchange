package com.exchangeinfomanager.News;

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
import java.util.Set;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.LabelTag;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddNewsToNode;
import com.exchangeinfomanager.TagManagment.TagSearchOnNewsTableModel;
import com.exchangeinfomanager.TagManagment.TagSearchOnNodesTableModel;
import com.exchangeinfomanager.TagServices.TagsServiceForSystemTags;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

public class CreateNewsWithFurtherOperationDialog extends NewsFutherOperationDialog 
{

	private List<BkChanYeLianTreeNode> searchresult;

	public CreateNewsWithFurtherOperationDialog(ServicesForNews NewsService)
	{
		super(NewsService);
		
		super.setTitle("Create");
		
		searchresult = new ArrayList<> ();
		
		chkbxaddnewtofriends = new JCheckBox ("同时添加到朋友板块/个股");
		chkbxaddnewtofriends.setSelected(true);
	    chkbxaddnewtosamecyl = new JCheckBox ("同时添加到同产业链板块/个股");
	    chkbxaddnewtosamecyl.setSelected(true);
	    chkbxaddnewtosamekw = new JCheckBox ("同时添加到同关键字板块/个股");
	    
	    JPanel chkbx1Panel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
	    chkbx1Panel.add(chkbxaddnewtofriends );
	    JPanel chkbx2Pane = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
	    chkbx2Pane.add(chkbxaddnewtosamecyl );
	    JPanel chkbx3Pane = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
	    chkbx3Pane.add(chkbxaddnewtosamekw);
	    
	    super.eastPanel.add(chkbx1Panel);
	    super.eastPanel.add(chkbx2Pane);
	    super.eastPanel.add(chkbx3Pane);
		
		JLabel newshbtn = JLabelFactory.createButton("Create News",100) ;
		newshbtn.addMouseListener(new CreateController());
		
		JLabel addnewstonodebtn = JLabelFactory.createButton("Add Selected News To Nodes",180) ;
		addnewstonodebtn.addMouseListener(new AddController());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        layoutPanel.add(newshbtn);
        
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        layoutPanel.add(addnewstonodebtn);
        
        super.eastPanel.add(layoutPanel);
        
        this.addPropertyChangeListener(new PropertyChanged() );
        
        JDialogForTagSearchMatrixPanelForAddNewsToNode newstosametagnode =
        		new JDialogForTagSearchMatrixPanelForAddNewsToNode (super.newsService.getCache() );
	}
	
	private JCheckBox chkbxaddnewtofriends;
	private JCheckBox chkbxaddnewtosamecyl;
	private JCheckBox chkbxaddnewtosamekw;
	
	 private class PropertyChanged implements PropertyChangeListener 
	 {
			private String property;
			
	        public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(News.KEYWORDSADD)) {
            		Collection<Tag> tags = (Collection<Tag>) evt.getNewValue();
//            		getNodesOfSameKeyWords (tags);
            	}  
            }
	}
	 
	
	/*
	   * (non-Javadoc)
	   * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsDialog#setNews(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.News)
	   */
	  public Boolean setNews(News event)
	  {
	  	super.setNews( event);
	  	//先把news的node放进去
	  	String owner = event.getNewsOwnerCodes();
	  	
	  	BkChanYeLianTreeNode dpan = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN );
	  	
	  	SvsForNodeOfStock svsstock = new SvsForNodeOfStock ();
	  	BkChanYeLianTreeNode stock = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(owner, BkChanYeLianTreeNode.TDXGG );
	  	
	  	SvsForNodeOfBanKuai svsbankuai = new SvsForNodeOfBanKuai ();
	  	BkChanYeLianTreeNode bankuai = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(owner, BkChanYeLianTreeNode.TDXBK );
	  	if(stock != null)	searchresult.add(stock);
	  	if(dpan != null) searchresult.add(dpan);
	  	if(bankuai != null) searchresult.add(bankuai);
	  	
	  	//
	  	if(chkbxaddnewtofriends.isSelected() ) {
		  	if(bankuai != null) {
		  		svsbankuai.getNodeJiBenMian(bankuai);
		  		Set<String> frisetpos = ((BanKuai)bankuai).getSocialFriendsSetPostive();
		  		for(String fricode : frisetpos) 
		  			searchresult.add( (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(fricode, BkChanYeLianTreeNode.TDXBK ) );
		  		
		  		
		  		Set<String> frisetneg = ((BanKuai)bankuai).getSocialFriendsSetNegtive();
		  		for(String fricode : frisetneg) 
		  			searchresult.add( (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(fricode, BkChanYeLianTreeNode.TDXBK ) );
		  	}
	  	}
	  	if(chkbxaddnewtosamecyl.isSelected()) {
			if(stock != null) {
				List<BkChanYeLianTreeNode> slidingnode = svsstock.getNodeSlidingInChanYeLianInfo(stock.getMyOwnCode());
				searchresult.addAll(slidingnode);
			}
	  		
	  		if(bankuai != null) {
	  			List<BkChanYeLianTreeNode> slidingnode =  svsbankuai.getNodeSlidingInChanYeLianInfo(bankuai.getMyOwnCode());
	  			searchresult.addAll(slidingnode);
	  		}
	  	}
        
		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(searchresult);
		
		NewsServices svsns = new NewsServices ();
		NewsCache newcache = new NewsCache ("ALL",svsns,null,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
		svsns.setCache(newcache);
		((TagSearchOnNewsTableModel)super.tblnews.getModel()).refresh(newcache.produceNews());
		((TagSearchOnNewsTableModel)super.tblnews.getModel()).unselectAll ();
	  	
	  	return true;
	  }
	
//	public void getNodesOfSameKeyWords(Collection<Tag> tags) 
//	{
//		if (!chkbxaddnewtosamekw.isSelected() )
//			return;
//		
//		TagsServiceForSystemTags systag = new TagsServiceForSystemTags (); 
//		Collection<BkChanYeLianTreeNode> nodesearchresult = systag.getNodesSetWithAllSpecificTags(super.keywordsField.getText());
//		this.searchresult.addAll(nodesearchresult);
//		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(nodesearchresult);
//		
//		Collection<News> newsearchresult = systag.getNewsSetWithAllSpecificTags(super.keywordsField.getText());
//		((TagSearchOnNewsTableModel)tblnews.getModel()).refresh(newsearchresult);
//	}

	/*
	   * 
	   */
	  private class CreateController extends MouseAdapter 
	  {

	      @Override
	      public void mouseClicked(MouseEvent e) {
	    	  
	    	  super.mouseClicked(e);
	    	  News mt = getNews();
	          if(mt.getTitle().length() >150) {
	          	JOptionPane.showMessageDialog(null,"新闻标题过长！");
	          	setVisible(true);
	          } else { 
	        	  addNews(mt);
	        	  setVisible(false);
	          }
	      }
	  }
	  private void addNews (News mt)
	  {
			int count = ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getRowCount();
			for(int i= 0; i<count ; i++) {
				Boolean result = (Boolean) ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getValueAt(i, 0);
				if(!result) 
					continue;
				else {
					String nodecode = (String) ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getValueAt(i, 2);
					mt.addNewsToSpecificOwner(nodecode);
				}
			}
			
			try {
				newsService.createNews(mt);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	  
	  
	  	class AddController  extends MouseAdapter
		{
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            super.mouseClicked(e);
	            try {
	                addNewsToSelectNodes ();
	            } catch (Exception e1) {
	                e1.printStackTrace();
	            } 
//	            setVisible(false);
	        }
	    }
		
		public void addNewsToSelectNodes ()
		{
			Collection<BkChanYeLianTreeNode> nodesset = ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getSelectedNodes();
			Set<String> nodecodeset = new HashSet<> ();
			for (BkChanYeLianTreeNode tmpnode : nodesset) 
				nodecodeset.add(tmpnode.getMyOwnCode());
			
			int count = ((TagSearchOnNewsTableModel)super.tblnews.getModel()).getRowCount();
			for(int i= 0; i<count ; i++) {
				
				Boolean result = (Boolean) ((TagSearchOnNewsTableModel)super.tblnews.getModel()).getValueAt(i, 0);
				if(!result) 
					continue;
				else {
					News news;
					news =  ((TagSearchOnNewsTableModel)super.tblnews.getModel()).getNews(i);
					news.addNewsToSpecificOwner(nodecodeset);
					
					try {
						newsService.updateNews( news);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}


}
