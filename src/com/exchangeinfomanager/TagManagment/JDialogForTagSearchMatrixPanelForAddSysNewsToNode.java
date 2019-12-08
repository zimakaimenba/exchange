package com.exchangeinfomanager.TagManagment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsCacheListener;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class JDialogForTagSearchMatrixPanelForAddSysNewsToNode extends JDialog implements NewsCacheListener
{
	private final JPanel contentPanel = new JPanel();
	private TagSearchMatrixPanelForAddSysNewsToNode pnlofaddnewstonode;
	private BkChanYeLianTreeNode node;
	
	
	public JDialogForTagSearchMatrixPanelForAddSysNewsToNode (BkChanYeLianTreeNode node)
	{
		this.node = node;
		
		initializeGui();
	}
	
	private void initializeGui() 
	{
		setBounds(100, 100, 1200, 789);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			pnlofaddnewstonode = new TagSearchMatrixPanelForAddSysNewsToNode(this.node);
			contentPanel.add(pnlofaddnewstonode, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
	}

	@Override
	public void onNewsChange(Collection<NewsCache> caches) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(Collection<NewsCache> cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsAdded(News m) {
		// TODO Auto-generated method stub
		
	}
}
