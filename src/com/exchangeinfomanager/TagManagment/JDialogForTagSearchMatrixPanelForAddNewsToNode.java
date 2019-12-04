package com.exchangeinfomanager.TagManagment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;

public class JDialogForTagSearchMatrixPanelForAddNewsToNode  extends JDialog implements CacheListener
{
	private final JPanel contentPanel = new JPanel();
	private TagSearchMatrixPanelForAddNewsToNode pnlofaddnewstonode;
	private Meeting news;
	
	public JDialogForTagSearchMatrixPanelForAddNewsToNode (Cache cache)
	{
		initializeGui ();
		
		cache.addCacheListener(this);
	}
	
	private void initializeGui() 
	{
		setBounds(100, 100, 1200, 789);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			pnlofaddnewstonode = new TagSearchMatrixPanelForAddNewsToNode();
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JDialogForTagSearchMatrixPanelForWholeSearchTags dialog = new JDialogForTagSearchMatrixPanelForWholeSearchTags();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onMeetingChange(Cache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(Cache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMeetingAdded(Meeting m) 
	{
		((TagSearchMatrixPanelForAddNewsToNode)pnlofaddnewstonode).setNews(m);
//		pnlofaddnewstonode.setPreSearchOrHaveTags (m.getKeyWords());
		this.toFront();
		this.setVisible(true);
	}
	
	
}
