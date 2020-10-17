package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.exchangeinfomanager.Services.ServicesOfNodeJiBenMianInfo;
import com.google.common.collect.Multimap;

public class DisplayNodeExchangeDataServicesPanel extends DisplayNodeInfoPanel 
{
	JPopupMenu jPopupMenue; 
	private JMenuItem menuItemclear;
	private JMenuItem menuItemgcsv;
	public static final String EXPORTCSV_PROPERTY = "exporttocsv";
//	private Multimap<String,LocalDate> displayedBankuaiinfomap;
//	private Multimap<String,LocalDate> displayedStockinfomap;

	public DisplayNodeExchangeDataServicesPanel(ServicesOfNodeJiBenMianInfo svsdisplay)
	{
		super(svsdisplay);
		
		jPopupMenue = new JPopupMenu();
		menuItemclear = new JMenuItem("清空内容"); 
		menuItemgcsv = new JMenuItem("导出到CSV");
		
		jPopupMenue.add(menuItemgcsv);
		jPopupMenue.add(menuItemclear);
		
		createEvents ();
	}

	private void createEvents() 
	{
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println("this is the test");
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
                  
              } else if (e.getButton() == MouseEvent.BUTTON3) {
            	  showpopupMenu (e);

              }
				
			}
			
		});
		
		menuItemgcsv.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	exportContentsToCsv ();
            }
        });
		
		menuItemclear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	clearPaneContents ();
            }
        });
	
	}
	
	/*
	 * 
	 */
	protected void clearPaneContents()
	{
		super.setText("");
//		this.displayedBankuaiinfomap.clear();
//		this.displayedStockinfomap.clear();
	}
	/*
	 * 
	 */
	protected void exportContentsToCsv() 
	{
		String htmlstring = super.getText();
		
//		PropertyChangeEvent evt = new PropertyChangeEvent(this, EXPORTCSV_PROPERTY, "",  htmlstring );
		this.firePropertyChange(EXPORTCSV_PROPERTY, "", htmlstring);
	}
	
	protected void showpopupMenu(MouseEvent e) 
	{
		jPopupMenue.show(this, e.getX(),   e.getY());
		
	}

}
