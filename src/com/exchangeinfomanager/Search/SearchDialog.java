package com.exchangeinfomanager.Search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.exchangeinfomanager.database.ConnectDatabase;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import com.toedter.calendar.JDateChooser;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.beans.VetoableChangeListener;
import java.awt.event.HierarchyListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;

public class SearchDialog extends JDialog {

	private ConnectDatabase connectdb;
	private SystemConfigration sysconfig ;
	
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private EverySearchOperation searchpanelLU;
	private EverySearchOperation searchpanelLD;
	private EverySearchOperation searchpanelRU;
	private EverySearchOperation searchpanelRD;
	private StockInfoManager stockinfomanager;
	

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		try {
//			SearchDialog dialog = new SearchDialog();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public SearchDialog(StockInfoManager stockinfomanager)
	{
		this.connectdb = ConnectDatabase.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
		this.stockinfomanager = stockinfomanager;
		initializeGui ();
		//this.sysconfig = SystemConfigration.getInstance();
		createEvents();
		
		
	}
	
	
//	public SearchDialog(JFrame frame,String title, boolean mode) 
//	{
//		super(frame,title,mode);
//		this.connectdb = ConnectDatabase.getInstance();
//		this.sysconfig = SystemConfigration.getInstance();
//		initializeGui ();
//		//this.sysconfig = SystemConfigration.getInstance();
//		createEvents();
//	}
	
	
	public void initializeGui()
	{
		setTitle("\u67E5\u627E");
		setBounds(100, 100, 967, 863);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		contentPanel.setLayout(null);
		
		//***************************************
		searchpanelLU = new EverySearchOperation (stockinfomanager);
		searchpanelLU.setBounds(0, 15, 469, 386);
		contentPanel.add(searchpanelLU);
		
		searchpanelRU = new EverySearchOperation (stockinfomanager);
		searchpanelRU.setBounds(472, 15, 469, 386);
		contentPanel.add(searchpanelRU);
		
		searchpanelRD = new EverySearchOperation (stockinfomanager);
		searchpanelRD.setBounds(472, 397, 469, 386);
		contentPanel.add(searchpanelRD);
		
		searchpanelLD = new EverySearchOperation (stockinfomanager);
		searchpanelLD.setBounds(0, 397, 469, 386);
		contentPanel.add(searchpanelLD);
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
		}
	}
	
	public void createEvents()
	{
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				dispose();
			}
		});
	}

	//JFame 有操作，集体刷新
	public void updateSearchGui() 
	{
		// TODO Auto-generated method stub
		searchpanelLU.preUpdateSearchresultToGui ();
		searchpanelLD.preUpdateSearchresultToGui ();
		searchpanelRU.preUpdateSearchresultToGui ();
		searchpanelRD.preUpdateSearchresultToGui ();
	}
	
//	public JTable getEverySearchOperationTable ()
//	{
//		return searchpanelLD.getTblSearchResult();
//	}
	
}
