package com.exchangeinfomanager.TagManagment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.Tag.Tag;

public class JDislogForTagSearchMatrixPanelForTagsBundleAdd extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private TagSearchMatrixPanelForTagsBundleAdd bundlepnl;
	private String nodefile;
	
	/**
	 * Create the dialog.
	 * @param filename 
	 */
	public JDislogForTagSearchMatrixPanelForTagsBundleAdd( ) {
		
//		this.nodefile = filename;
		
		setBounds(100, 100, 1200, 789);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			bundlepnl = new TagSearchMatrixPanelForTagsBundleAdd();
			contentPanel.add(bundlepnl, BorderLayout.CENTER);
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
	
	public void setPreSearchMustHaveTags (Collection<Tag> musthavetags)
	{
		bundlepnl.setTags(musthavetags);
	}
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			JDislogForTagSearchMatrixPanelForTagsBundleAdd dialog = new JDislogForTagSearchMatrixPanelForTagsBundleAdd();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
