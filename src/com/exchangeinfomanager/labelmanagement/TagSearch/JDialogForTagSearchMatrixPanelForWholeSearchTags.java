package com.exchangeinfomanager.labelmanagement.TagSearch;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;

public class JDialogForTagSearchMatrixPanelForWholeSearchTags extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private TagSearchMatrixPanelForWholeSearchTags pnlofsearchmatrix;


	/**
	 * Create the dialog.
	 * @param cache 
	 */
	public JDialogForTagSearchMatrixPanelForWholeSearchTags ()
	{
		initializeGui ();
		
		pnlofsearchmatrix.setPreSearchMustHaveTags(null);
		
	}
	public void setPreSearchMustHaveTags (Collection<Tag> musthavetags)
	{
		pnlofsearchmatrix.setPreSearchMustHaveTags(musthavetags);
	}
//	public void setPreSearchOrHaveTags (Collection<Tag> musthavetags)
//	{
//
//	}
	
	private void initializeGui() 
	{
		setBounds(100, 100, 1200, 789);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			pnlofsearchmatrix = new TagSearchMatrixPanelForWholeSearchTags();
			contentPanel.add(pnlofsearchmatrix, BorderLayout.CENTER);
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


}
