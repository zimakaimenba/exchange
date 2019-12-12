package com.exchangeinfomanager.TagManagment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.TagManagment.TagSearchMatrixPanelForWholeSearchTags.SearchController;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagSearchMatrixPanelForAddNewsToNode extends TagSearchMatrixPanel 
{
	private JLabel searchbtn;
	private News news;

	public TagSearchMatrixPanelForAddNewsToNode ()
	{
		searchbtn = JLabelFactory.createButton("Add News To Select Nodes",200) ;
		searchbtn.addMouseListener(new AddController());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(searchbtn);
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        super.eastPanel.add(layoutPanel);
        
        layoutPanel.add(Box.createHorizontalStrut(25));
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
        }
    }
	
	public int setNews (News m)
	{
		this.news = m;
		
		super.setPreSearchOrHaveTags (m.getKeyWords());
		
		Collection<BkChanYeLianTreeNode> searchresult = super.sysservice.getNodesSetWithOneOfSpecificTags(m.getKeyWords());
		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(searchresult);
		
		Collection<News> newsset = new HashSet<> ();
		newsset.add(m);
		((TagSearchOnNewsTableModel)super.tblnews.getModel()).refresh(newsset);
		((TagSearchOnNewsTableModel)super.tblnews.getModel()).setRowSelected (0);
		
		return searchresult.size(); 
	}
	
	public void addNewsToSameTagsNodes ()
	{
//		Collection<BkChanYeLianTreeNode> slnode = ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getSelectedNodes();
		int count = ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getRowCount();
		for(int i= 0; i<count ; i++) {
			Boolean result = (Boolean) ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getValueAt(i, 0);
			if(!result) 
				continue;
			else {
				String nodecode = (String) ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getValueAt(i, 2);
				this.news.addNewsToSpecificOwner(nodecode);
			}
		}
		
		NewsServices servicefornews = new NewsServices ();
		try {
			servicefornews.updateNews( this.news);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
