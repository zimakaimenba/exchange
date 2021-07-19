package com.exchangeinfomanager.TagManagment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.News.CreateNewsDialog;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

public class TagSearchMatrixPanelForAddSysNewsToNode extends TagSearchMatrixPanel 
{
	private JLabel searchbtn;
	private Collection<News> newsset;
	private JLabel newshbtn;
	private BkChanYeLianTreeNode node;

	public TagSearchMatrixPanelForAddSysNewsToNode (BkChanYeLianTreeNode node)
	{
		this.node = node;
		searchbtn = JLabelFactory.createButton("Add Selected News To Node",180) ;
		searchbtn.addMouseListener(new AddController());
		
		newshbtn = JLabelFactory.createButton("Create News",100) ;
		newshbtn.addMouseListener(new CreateController());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(searchbtn);
        
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        layoutPanel.add(newshbtn);
        
        super.eastPanel.add(layoutPanel);
        
        layoutPanel.add(Box.createHorizontalStrut(25));
        
        Collection<BkChanYeLianTreeNode> searchresult = new ArrayList<> ();
        searchresult.add(node);
		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(searchresult);
		
		
		NewsServices svsns = new NewsServices ();
		NewsCache newcache = new NewsCache ("ALL",svsns,null,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
		svsns.setCache(newcache);
		
		((TagSearchOnNewsTableModel)super.tblnews.getModel()).refresh(newcache.produceNews());
		((TagSearchOnNewsTableModel)super.tblnews.getModel()).unselectAll ();
        
	}
	class CreateController extends MouseAdapter
	{
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	createNewsForNode ();
            } catch (Exception e1) {
                e1.printStackTrace();
            } 
//            setVisible(false);
        }
    } 
	public void createNewsForNode()
	{
		NewsServices svsns = new NewsServices ();
		NewsCache newcache = new NewsCache (node.getMyOwnCode(),svsns,null,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
		svsns.setCache(newcache);
	
		JDialogForTagSearchMatrixPanelForAddNewsToNode pnlsearchtags = new JDialogForTagSearchMatrixPanelForAddNewsToNode(newcache);
		
	    News News = new News("新闻标题",LocalDate.now() ,"描述", "", new HashSet<>(),"URL",node.getMyOwnCode()  );
	    
	    CreateNewsDialog cd = new CreateNewsDialog (svsns);
	    cd.setNews(News);
	    cd.setVisible(true);
		
	}
	
	class AddController  extends MouseAdapter
	{
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                addNewsToSameTagsNodes ();
            } catch (Exception e1) {
                e1.printStackTrace();
            } 
//            setVisible(false);
        }
    }
	
	public void addNewsToSameTagsNodes ()
	{
		NewsServices servicefornews = new NewsServices ();
		int count = ((TagSearchOnNewsTableModel)super.tblnews.getModel()).getRowCount();
		
		for(int i= 0; i<count ; i++) {
			
			Boolean result = (Boolean) ((TagSearchOnNewsTableModel)super.tblnews.getModel()).getValueAt(i, 0);
			if(!result) 
				continue;
			else {
				News news;
				news =  ((TagSearchOnNewsTableModel)super.tblnews.getModel()).getNews(i);
				news.addNewsToSpecificOwner(node.getMyOwnCode());
				
				try {
					servicefornews.updateNews( news);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}



}

