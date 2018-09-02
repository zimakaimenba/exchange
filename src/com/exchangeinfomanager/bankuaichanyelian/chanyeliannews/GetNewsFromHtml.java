package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;
/*
 * 
 */
import javax.swing.JPanel;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JEditorPane;

public class GetNewsFromHtml extends JPanel 
{
	private JTextField tfldUrl;
	private JButton btnSetAsTitle;
	private JButton btnsetkeywords;
	private JButton btnsetdescr;
	private JEditorPane jep;
	private Meeting meeting;
	private JTextArea taraSelectedText;

	/**
	 * Create the panel.
	 */
	public GetNewsFromHtml(Meeting meeting2) 
	{
		this.meeting = meeting2;
		initializeGui ();
		createEvents ();
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		
		try	{
			String ctrlresult = (String) clipboard.getData(DataFlavor.stringFlavor);
			if(ctrlresult.toLowerCase().startsWith("http") ) {
				tfldUrl.setText(ctrlresult);
				searchHtmlByUrl (ctrlresult);
			}
		}catch(Exception e)	{
//			e.printStackTrace();
		}

	}

	public Meeting getMeeting ()
	{
		return this.meeting;
	}

	private void createEvents() 
	{
		
		tfldUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println("this is the test");
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				tfldUrl.setText("");
				
				if (SwingUtilities.isRightMouseButton(e))  {
			    	
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();
					
					try	{
						String ctrlresult = (String) clipboard.getData(DataFlavor.stringFlavor);
						if(ctrlresult.toLowerCase().startsWith("http") ) {
							tfldUrl.setText(ctrlresult);
							searchHtmlByUrl (ctrlresult);
						}
					}catch(Exception ex)	{
//						e.printStackTrace();
					}
			    }
			}
			
			
			
		});
		
		jep.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent event) 
			{
			    if (SwingUtilities.isRightMouseButton(event))  {
			    	
			    	String selectedtext = jep.getSelectedText();
			    	
			    	if(taraSelectedText.getText().contains("设置成功"))
			    		taraSelectedText.setText("");
			    	
					taraSelectedText.setText(taraSelectedText.getText() + selectedtext );
			    }
			 }
		});
		
		btnsetdescr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String selectedtext = taraSelectedText.getText();
				meeting.setDescription(meeting.getDescription()  + taraSelectedText.getText());
				
				taraSelectedText.setText("设置成功");
			}
		});
		btnsetkeywords.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String selectedtext = taraSelectedText.getText();
				meeting.setLocation(meeting.getLocation() + " " + selectedtext);
				
				taraSelectedText.setText("设置成功");
			}
		});
		
		btnSetAsTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				String selectedtext = taraSelectedText.getText();
				meeting.setTitle(selectedtext);
				
				taraSelectedText.setText("设置成功");
			}
		});
		
		tfldUrl.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				
				String url = tfldUrl.getText().trim();
				searchHtmlByUrl (url);
				
				 
			}
		});
		
	}

	protected void searchHtmlByUrl(String url) 
	{
		try {
			jep.setPage(url);
			jep.repaint();
			
			this.meeting.setSlackUrl(url);
		} catch (IOException e) {
			  jep.setContentType("text/html");
			  jep.setText("<html>Could not load</html>");
		}
		
		jep.setCaretPosition(0);
		
	}

	private void initializeGui() 
	{
		JLabel lblNewLabel = new JLabel("\u7F51\u5740");
		
		tfldUrl = new JTextField();
		tfldUrl.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		btnSetAsTitle = new JButton("\u8BBE\u7F6E\u4E3A\u6807\u9898");
		
		btnsetkeywords = new JButton("\u8BBE\u7F6E\u4E3A\u5173\u952E\u5B57");
		
		btnsetdescr = new JButton("\u8BBE\u7F6E\u4E3A\u8BE6\u7EC6\u63CF\u8FF0");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldUrl))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 839, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnSetAsTitle)
							.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
							.addComponent(btnsetkeywords))
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnsetdescr))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(tfldUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnSetAsTitle)
								.addComponent(btnsetkeywords))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnsetdescr))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 663, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		jep = new JEditorPane();
		jep.setEditable(false);
		scrollPane.setViewportView(jep);
		
		taraSelectedText = new JTextArea();
		taraSelectedText.setLineWrap(true);
		scrollPane_1.setViewportView(taraSelectedText);
		setLayout(groupLayout);
		
	}

}
