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

import com.exchangeinfomanager.News.CreateNewsWithFurtherOperationDialog.AddController;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagManagment.TagSearchOnNewsTableModel;
import com.exchangeinfomanager.TagManagment.TagSearchOnNodesTableModel;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.base.Splitter;

public class ModifyNewsWithFurtherOperationDialog extends NewsFutherOperationDialog 
{

	private ArrayList<BkChanYeLianTreeNode> searchresult;

	public ModifyNewsWithFurtherOperationDialog(ServicesForNews NewsService)
	{
		super(NewsService);
		
		super.setTitle("Create");
		
		searchresult = new ArrayList<> ();
		
//		chkbxaddnewtofriends = new JCheckBox ("同时添加到朋友板块/个股");
//		chkbxaddnewtofriends.setSelected(true);
//	    chkbxaddnewtosamecyl = new JCheckBox ("同时添加到同产业链板块/个股");
//	    chkbxaddnewtosamecyl.setSelected(true);
//	    chkbxaddnewtosamekw = new JCheckBox ("同时添加到同关键字板块/个股");
//	    
//	    JPanel chkbx1Panel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
//	    chkbx1Panel.add(chkbxaddnewtofriends );
//	    JPanel chkbx2Pane = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
//	    chkbx2Pane.add(chkbxaddnewtosamecyl );
//	    JPanel chkbx3Pane = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
//	    chkbx3Pane.add(chkbxaddnewtosamekw);
//	    
//	    super.eastPanel.add(chkbx1Panel);
//	    super.eastPanel.add(chkbx2Pane);
//	    super.eastPanel.add(chkbx3Pane);
		
		JLabel updatebtn = JLabelFactory.createButton("更新",50) ;
		updatebtn.addMouseListener(new UpdateController());
		
		JLabel delbtn = JLabelFactory.createButton("删除",50) ;
		delbtn.addMouseListener(new DeleteController());
		
		JLabel delfromnodebtn = JLabelFactory.createButton("Del news from selected Nodes",200) ;
		delfromnodebtn.addMouseListener(new DelFromNodeController());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        layoutPanel.add(updatebtn);
        
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        layoutPanel.add(delbtn);
        
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        layoutPanel.add(delfromnodebtn);
        
        super.eastPanel.add(layoutPanel);
        
        this.addPropertyChangeListener(new PropertyChanged() );
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
	  	
	  	String owner = event.getNewsOwnerCodes();
	  	List<String> ownerlist = Splitter.on("|").omitEmptyStrings().splitToList(owner); 
	  	for(String ownercode : ownerlist) {
	  		BkChanYeLianTreeNode stock = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(ownercode, BkChanYeLianTreeNode.TDXGG );
		  	BkChanYeLianTreeNode bankuai = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(ownercode, BkChanYeLianTreeNode.TDXBK );
		  	
		  	if(stock != null)	searchresult.add(stock);
		  	if(bankuai != null) searchresult.add(bankuai);
	  	}
	  	
		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(searchresult);
		
	  	return true;
	  }
	
	  private class DeleteController extends MouseAdapter {

	        @Override
	        public void mouseClicked(MouseEvent e) {
	            super.mouseClicked(e);
	            try {
	                newsService.deleteNews(getNews());
	            } catch (SQLException e1) {
	                e1.printStackTrace();
	            }
	            setVisible(false);
	        }
	    }

	    private class UpdateController extends MouseAdapter {

	        @Override
	        public void mouseClicked(MouseEvent e) {
	            super.mouseClicked(e);
	            try {
	                newsService.updateNews(getNews());
	            } catch (SQLException e1) {
	                e1.printStackTrace();
	            } 
	            setVisible(false);
	        }
	    }
	    
	    private class DelFromNodeController  extends MouseAdapter {

	        @Override
	        public void mouseClicked(MouseEvent e) {
	            super.mouseClicked(e);
	            
	            removeNewsFromSelectNodes();
	             
	            setVisible(false);
	        }
	    }
	  
		public void removeNewsFromSelectNodes ()
		{
			Collection<BkChanYeLianTreeNode> nodesset = ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getSelectedNodes();
			Set<String> nodecodeset = new HashSet<> ();
			for (BkChanYeLianTreeNode tmpnode : nodesset) 
				nodecodeset.add(tmpnode.getMyOwnCode());
			
			News news = getNews();
			news.removeNewsSpecficOwner(nodecodeset);
			
			if(news.getNewsOwnerCodes().isEmpty() ) //已经没有OWNER了, 直接删除
				try {
					newsService.deleteNews(news);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			try {
				newsService.updateNews( news);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


}

