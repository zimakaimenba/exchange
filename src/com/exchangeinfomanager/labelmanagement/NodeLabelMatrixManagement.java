package com.exchangeinfomanager.labelmanagement;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Label;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class NodeLabelMatrixManagement extends JDialog 
{

	private final JPanel contentPanel = new JPanel();
	private JTextField tfldcurkeywords;
	private JPanel pnlcenter;
	private JTextField tfldurl;
	private LabelsManagement pnlallsyskw;
	private LabelsManagement pnlnewkw;
	private LabelsManagement pnlbkkwds;
	private LabelsManagement pnlcylkw;
	private LabelsManagement pnlurlkw;

	

	/**
	 * Create the dialog.
	 */
	public NodeLabelMatrixManagement() 
	{
		initializeGui ();
		
		Set<String> all = new HashSet<> ();
		all.add("000000");
		pnlallsyskw.initializeLabelsManagement ( all);
	}
	
	private void initializeGui ()
	{
		setBounds(100, 100, 886, 722);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel pnlup = new JPanel();
			pnlup.setLayout(new BorderLayout());
			JLabel lblcurkeywords = new JLabel ("µ±Ç°¹Ø¼ü×Ö");
			tfldcurkeywords = new JTextField ();
			pnlup.add(lblcurkeywords,BorderLayout.WEST);
			pnlup.add(tfldcurkeywords,BorderLayout.CENTER);
			
			
			contentPanel.add(pnlup, BorderLayout.NORTH);
		}
		{
			pnlcenter = new JPanel();
			contentPanel.add(pnlcenter, BorderLayout.CENTER);
			
			JLabel lblNewLabel = new JLabel("\u7CFB\u7EDF\u6240\u6709\u5173\u952E\u5B57");
			
			pnlallsyskw = new LabelsManagement();
			
			JLabel label = new JLabel("\u6240\u5C5E\u677F\u5757\u5173\u952E\u5B57");
			
			pnlbkkwds = new LabelsManagement();
			
			JLabel label_1 = new JLabel("\u540C\u4EA7\u4E1A\u94FE\u4E2A\u80A1\u5173\u952E\u5B57");
			
			pnlcylkw = new LabelsManagement();
			
			JLabel label_2 = new JLabel("\u540D\u4E0B\u65B0\u95FB\u5173\u952E\u5B57");
			
			pnlnewkw = new LabelsManagement();
			
			JLabel lblNewLabel_1 = new JLabel("\u8BA1\u7B97URL\u5173\u952E\u5B57");
			
			tfldurl = new JTextField();
			tfldurl.setColumns(10);
			
			pnlurlkw = new LabelsManagement();
			GroupLayout gl_pnlcenter = new GroupLayout(pnlcenter);
			gl_pnlcenter.setHorizontalGroup(
				gl_pnlcenter.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_pnlcenter.createSequentialGroup()
						.addGap(20)
						.addGroup(gl_pnlcenter.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlcenter.createSequentialGroup()
								.addGroup(gl_pnlcenter.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_pnlcenter.createSequentialGroup()
										.addGroup(gl_pnlcenter.createParallelGroup(Alignment.LEADING)
											.addComponent(pnlallsyskw, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE)
											.addComponent(label_2))
										.addPreferredGap(ComponentPlacement.RELATED, 42, Short.MAX_VALUE))
									.addGroup(Alignment.TRAILING, gl_pnlcenter.createSequentialGroup()
										.addComponent(pnlnewkw, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE)
										.addGap(42)))
								.addPreferredGap(ComponentPlacement.RELATED))
							.addGroup(gl_pnlcenter.createSequentialGroup()
								.addComponent(lblNewLabel)
								.addGap(284)))
						.addGroup(gl_pnlcenter.createParallelGroup(Alignment.TRAILING)
							.addComponent(label, Alignment.LEADING)
							.addGroup(Alignment.LEADING, gl_pnlcenter.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_pnlcenter.createParallelGroup(Alignment.LEADING, false)
									.addComponent(lblNewLabel_1)
									.addComponent(label_1)
									.addComponent(pnlbkkwds, GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
									.addComponent(pnlcylkw, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(tfldurl))
								.addComponent(pnlurlkw, GroupLayout.PREFERRED_SIZE, 462, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap())
			);
			gl_pnlcenter.setVerticalGroup(
				gl_pnlcenter.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_pnlcenter.createSequentialGroup()
						.addGap(18)
						.addGroup(gl_pnlcenter.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel)
							.addComponent(label))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_pnlcenter.createParallelGroup(Alignment.LEADING, false)
							.addGroup(gl_pnlcenter.createSequentialGroup()
								.addComponent(pnlbkkwds, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(label_1)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(pnlcylkw, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE))
							.addComponent(pnlallsyskw, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_pnlcenter.createParallelGroup(Alignment.LEADING, false)
							.addGroup(gl_pnlcenter.createSequentialGroup()
								.addComponent(label_2)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(pnlnewkw, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_pnlcenter.createSequentialGroup()
								.addComponent(lblNewLabel_1)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(tfldurl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(pnlurlkw, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addContainerGap(17, Short.MAX_VALUE))
			);
			pnlcenter.setLayout(gl_pnlcenter);
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
			NodeLabelMatrixManagement dialog = new NodeLabelMatrixManagement();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
