package com.exchangeinfomanager.labelmanagement.TagSearch;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.ServicesForNodes.SvsForNodeOfFileNodes;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

import com.exchangeinfomanager.labelmanagement.TagCache;
import com.exchangeinfomanager.labelmanagement.TagsServiceForNodes;
import com.exchangeinfomanager.labelmanagement.TagSearch.TagSearchMatrixPanelForAddNewsToNode.AddController;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class TagSearchMatrixPanelForTagsBundleAdd extends TagSearchMatrixPanel 
{

	private JLabel searchbtn;
	private Collection<Tag> taglist;

	public TagSearchMatrixPanelForTagsBundleAdd ()
	{
		searchbtn = JLabelFactory.createButton("Add Tags To Select Nodes",200) ;
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
                addTagsToSelectedNodes ();
            } catch (Exception e1) {
                e1.printStackTrace();
            } 
            setVisible(true);
        }
    }
	public void setTags (Collection<Tag> tags)
	{
		this.taglist = tags;
		super.setPreSearchMustHaveTags(tags);
		
		String parsedpath = "E:\\";
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(parsedpath) );
		
		String filename;
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		    if(chooser.getSelectedFile().isDirectory())
		    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    else
		    	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
		} else
			return;
	
		if(!filename.endsWith("EBK") ) { //不是板块文件
			JOptionPane.showMessageDialog(null,"不是通达信板块导出文件，请使用正确格式文件。","Warning",JOptionPane.WARNING_MESSAGE);
	   		return;
		}
		
		SvsForNodeOfFileNodes svsfornodefile = new SvsForNodeOfFileNodes (filename);
		Collection<BkChanYeLianTreeNode> addnodeset = svsfornodefile.getAllNodes();
		
		((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).refresh(addnodeset);
		
	}
	public void addTagsToSelectedNodes() 
	{
		Collection<BkChanYeLianTreeNode> slnode = ((TagSearchOnNodesTableModel)super.tblofnodes.getModel()).getSelectedNodes();
		TagService tagserviceofbunch = new TagsServiceForNodes (slnode);
		TagCache tagcacheofbuch = new TagCache (tagserviceofbunch);
		tagserviceofbunch.setCache(tagcacheofbuch);
		try {
			tagserviceofbunch.createTags (this.taglist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
