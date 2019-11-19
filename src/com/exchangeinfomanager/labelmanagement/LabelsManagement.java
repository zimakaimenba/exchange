package com.exchangeinfomanager.labelmanagement;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import javax.swing.JScrollPane;

import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.StockCalendar.JUpdatedLabel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting.Label;
import com.exchangeinfomanager.commonlib.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class LabelsManagement extends JPanel 
{
	private JButton btnadd;
	private JButton btndel;
	private JPanel pnllabelcontain;
	private DBSystemTagsService lbdbservice;
	private LabelCache cache;

	/**
	 * Create the panel.
	 */
	public LabelsManagement()
	{
		setBorder(new LineBorder(new Color(0, 0, 0)));
		
	}
	public void initializeLabelsManagement(Set<String> nodecode) 
	{
		lbdbservice = new DBSystemTagsService ();
		cache = new LabelCache(nodecode,lbdbservice); 
		
		createGui ();
		
		initializeTags ();
	}
	
	private void initializeTags()
	{
		Collection<Tag> alltags = cache.produceLabels();
		
		for (Tag l : alltags) {
		    
			JUpdatedLabel label = getFormatedLabelForWithLabels (l);
			pnllabelcontain.add(label);
      }
	}
	
	 private JUpdatedLabel getFormatedLabelForWithLabels( Tag l) 
	    {
	    	JUpdatedLabel label = new JUpdatedLabel( l.getName() );
	        label.setOpaque(true);
	        label.setName( l.getName()   );
	        label.setForeground(Color.BLACK);
	        label.setBackground(l.getColor());
	        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
	        
			return label;
		}

	public void createGui ()
	{
		Color color = ColorScheme.BACKGROUND;

        this.setLayout(new BorderLayout(0,0));
        
        JPanel pnlup = new JPanel();
		this.add(pnlup, BorderLayout.NORTH);
		btnadd = new JButton("Add");
		btnadd.setOpaque(true);
		pnlup.add(btnadd);
		
		btndel = new JButton ("Delete");
		btndel.setOpaque(true);
		pnlup.add(btndel);
        
		JScrollPane sclpcenter = new JScrollPane();
		JPanel pnlcenter = new JPanel();
		pnlcenter.add(JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5)), BorderLayout.CENTER); //中间用来显示信息的panel
		pnlcenter.setBackground(color);
	    sclpcenter.setViewportView (pnlcenter);

		this.add(sclpcenter);
	}

}
