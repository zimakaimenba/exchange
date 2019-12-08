package com.exchangeinfomanager.TagManagment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Collection;


import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;


import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagSearchMatrixPanelForWholeSearchTags extends TagSearchMatrixPanel 
{
	private JLabel searchbtn;
	private JLabel resetbtn;

	public TagSearchMatrixPanelForWholeSearchTags ()
	{
		searchbtn = JLabelFactory.createButton("Search") ;
		searchbtn.addMouseListener(new SearchController());
		
		JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(searchbtn);
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        super.eastPanel.add(layoutPanel);
        
        layoutPanel.add(Box.createHorizontalStrut(25));
		
        
        resetbtn = JLabelFactory.createButton("Reset") ;
//        resetbtn.addMouseListener(new SearchController());
        layoutPanel.add(resetbtn);
        layoutPanel.add(Box.createHorizontalStrut(5));
        
        super.eastPanel.add(layoutPanel);
	}
	
	class SearchController  extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                searchTags ();
            } catch (Exception e1) {
                e1.printStackTrace();
            } 
            setVisible(true);
        }
    }

	private void searchTags ()
	{
		Collection<BkChanYeLianTreeNode> nodesearchresult = super.sysservice.getNodesSetWithAllSpecificTags(tfldmusthavetags.getText());
		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(nodesearchresult);
		
		Collection<News> newsearchresult = super.sysservice.getNewsSetWithAllSpecificTags(tfldmusthavetags.getText());
		((TagSearchOnNewsTableModel)tblnews.getModel()).refresh(newsearchresult);
		
	}
	
}
