package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.google.common.base.Strings;

import net.coderazzi.filters.gui.TableFilterHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class BanKuaiAndGeGuTablesFilterSetting extends JDialog 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final int WHOLESIZEWIDTH = 1200;
	protected static final int WHOLESIZEHIGHT = 700;
    protected static final int NEWSWIDTH = 400;
    protected static final int NEWSHEIGHT = 500;
    protected static final int PADDING = 20;
    
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;


	private final JPanel contentPanel = new JPanel();
	private TableFilterHeader filterHeader;
	private Properties prop;
	private BanKuaiandGeGuTableBasic bkggtable;
	private JButton okButton;
	JUpdatedTextField[] filtertxtfldcollection = new JUpdatedTextField[11]; 
	private JPanel pnlfilter;

	/**
	 * Create the dialog.
	 */
	public BanKuaiAndGeGuTablesFilterSetting(BanKuaiandGeGuTableBasic bkggtable1, Properties prop1, TableFilterHeader filterHeader1) 
	{
		setTitle("Filter Setting");
		this.bkggtable = bkggtable1;
		this.prop = prop1;
		this.filterHeader = filterHeader1;
		createGui ();
		
		createUIComponents ();
		createEvents ();
		
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	}
	
	private void createEvents() 
	{
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for(int i=0;i<=10;i++) {
					if( filtertxtfldcollection[i]  == null )
						continue;
					
					String filtercontent = filtertxtfldcollection[i].getText();
					if( Strings.isNullOrEmpty(filtercontent) )
						filterHeader.getFilterEditor(i).setContent("");
					else
						filterHeader.getFilterEditor(i).setContent(filtercontent);
				}
				
				dispose ();
			}
		});
		
	}
	
	private void createUIComponents() 
	{
		for(int i=0;i<=10;i++) {
			String column_name = prop.getProperty (String.valueOf(i) + "column_name");
			
			if(column_name == null || column_name.equalsIgnoreCase("NULL"))
				continue;
			
			JPanel filterPanel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 2));
			JLabel columnname = new JLabel(column_name + ":  ");
			columnname.setHorizontalAlignment(SwingConstants.RIGHT);
			filterPanel.add(columnname);
			
			JUpdatedTextField filterField = (JUpdatedTextField) JTextFactory.createTextField();
			filterField.setMouseLeftClearEnabled(false);
			try {
				String  curfilter = (String) (filterHeader.getFilterEditor(i).getContent());
				filterField.setText(curfilter);
		    	filterPanel.add(filterField);
			} catch (Exception e) {
				filterPanel.add(filterField);
			}

			filtertxtfldcollection[i] = filterField; 
	        this.contentPanel.add(filterPanel);
		}
		
//		this.pnlfilter.revalidate();
//		this.pnlfilter.repaint();
//		this.contentPanel.revalidate();
//		this.contentPanel.repaint();
//		this.revalidate();
//		this.repaint();
		
	}
	private void createGui ()
	{
		setBounds(100, 100, 300, 337);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			contentPanel.add(scrollPane);
			{
				pnlfilter = new JPanel();
				pnlfilter.setLayout(new BoxLayout(this.pnlfilter, BoxLayout.PAGE_AXIS));
				scrollPane.setViewportView(pnlfilter);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
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
}
