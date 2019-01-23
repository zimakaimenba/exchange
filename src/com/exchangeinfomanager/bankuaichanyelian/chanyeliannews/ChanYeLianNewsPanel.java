package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.StockCalendar.View;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.toedter.calendar.JDateChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;

import java.awt.event.MouseAdapter;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Dialog.ModalityType;

public class ChanYeLianNewsPanel extends JDialog  
{
	 	protected static final int WIDTH = 500;
	    protected static final int HEIGHT = 500;
	    protected static final int PADDING = 20;
	    protected static final int TITLE_SIZE = 40;
	    protected static final int TITLE_FONT_SIZE = 20;
		private MeetingService curmeetingService;
	/**
	 * Create the panel.
	 */
	public ChanYeLianNewsPanel(String curnodecode)
	{
		this.myowncode = curnodecode;
		MeetingService allmeetingService = new DBMeetingService ();
    	LabelService alllabelService = new DBLabelService ();
    	
        Cache cacheAll = new Cache("ALL",allmeetingService, alllabelService,LocalDate.now().minusWeeks(10),LocalDate.now());
        panelallnews = new ChanYeLianGeGuNews(allmeetingService,cacheAll,"10周内所有新闻");
        
        curmeetingService = new DBMeetingService ();
    	LabelService curlabelService = new DBLabelService ();
        Cache cachecurnode = new Cache(curnodecode,curmeetingService, curlabelService,LocalDate.now().minusWeeks(10),LocalDate.now());
        panelgegunews = new ChanYeLianGeGuNews(curmeetingService,cachecurnode,curnodecode + "10周内个股新闻");
		
		initializeGui ();
		createEvents ();
	}

	private String myowncode;
//	private StockCalendarAndNewDbOperation newsdbopt;
	private ChanYeLianGeGuNews panelgegunews;
	private ChanYeLianGeGuNews panelallnews;
	private JButton addnewstogegu;
	private JPanel centerPanel;
	private JButton deletenewstogegu;
	private static Logger logger = Logger.getLogger(ChanYeLianNewsPanel.class);
	
	private class RemoveController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	InsertedMeeting upmeeting = panelgegunews.getCurSelectedNews ();
            	upmeeting.removeMeetingSpecficOwner (myowncode);
            	curmeetingService.updateMeeting(upmeeting);
            } catch (Exception e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }

	private void createEvents() 
	{
		
		deletenewstogegu.addMouseListener(new RemoveController() );
		
		addnewstogegu.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		InsertedMeeting selectnewsall = panelallnews.getCurSelectedNews ();
        		if(selectnewsall == null)
        			return;
        		
        		panelgegunews.updateNewsToABkGeGu(selectnewsall,myowncode);
        	}
        });
	}
	
	private void initializeGui() 
	{
		this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.PAGE_AXIS));
        
        this.centerPanel.add(panelgegunews);
        this.centerPanel.add(Box.createVerticalStrut(5));
        JPanel p = JPanelFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
        deletenewstogegu = new JButton("从当前前个股/板块删除");
        addnewstogegu = new JButton("添加到当前个股/板块");
        p.add(deletenewstogegu);
        p.add(new Label("                       "));
        p.add(addnewstogegu);
        this.centerPanel.add(p);
        this.centerPanel.add(Box.createVerticalStrut(5));
        this.centerPanel.add(panelallnews);
        

//       
//
//        this.centerPanel.add(Box.createVerticalStrut(PADDING));
//        
//        this.centerPanel.add(Box.createVerticalStrut(10));
//        
//        this.centerPanel.add(Box.createVerticalStrut(10));
//       
//        this.centerPanel.add(Box.createVerticalStrut(PADDING));

        // add main panel to the dialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.centerPanel, BorderLayout.CENTER);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);

        

	}

}


