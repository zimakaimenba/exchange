package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.Strings;

import net.coderazzi.filters.gui.TableFilterHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

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
	private JLabel btnsave;
	private JLabel btnapply;
	private JComboBox jcbxsavedfilter;
	private Properties savedfiltersprop;
	private int columnmaxnumber;

	/**
	 * Create the dialog.
	 */
	public BanKuaiAndGeGuTablesFilterSetting(BanKuaiandGeGuTableBasic bkggtable1, Properties prop1, TableFilterHeader filterHeader1) 
	{
		setTitle("Filter Setting");
		this.bkggtable = bkggtable1;
		this.prop = prop1;
		this.filterHeader = filterHeader1;
		setupBkfxHighLightSettingProperties ();
		
		String columnmaxnumberstr = prop.getProperty ( "columnmaxnumber");
		columnmaxnumber = Integer.parseInt(columnmaxnumberstr);
		
		createGui ();
		
		createUIComponents ();
		createEvents ();
		
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	}
	
	private void setupBkfxHighLightSettingProperties ()
	{
		// TODO Auto-generated method stub
		File directory = new File("");//设定为当前文件夹
		String systeminstalledpath = null;
		try{
		    Properties properties = System.getProperties();
		    systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		} catch(Exception e) {System.exit(0);}
		
		try {
			savedfiltersprop = new Properties();
			String propFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/BanKuaiGeGuSavedFilter.properties";
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) 
				savedfiltersprop.load(inputStream);
			 
			inputStream.close();
			
		} catch (Exception e) {	e.printStackTrace();} finally {}
	}
	
	private void createEvents() 
	{
		btnsave.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		String selectedfilter = (String) jcbxsavedfilter.getSelectedItem();
        		saveFilter ();
        	}
        });
		
		btnapply.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		String selectedfilter = (String) jcbxsavedfilter.getSelectedItem();
        		applyFilter ();
        	}
        });
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				filterHeader.setToolTipText("FILTER设定:");
				for(int i=0;i<columnmaxnumber;i++) {
					String column_name = prop.getProperty (String.valueOf(i) + "column_name");
					
					if(column_name == null || column_name.equalsIgnoreCase("NULL"))
						continue;
					
					if( filtertxtfldcollection[i]  == null )
						continue;
					
					String filtercontent = filtertxtfldcollection[i].getText();
					if( Strings.isNullOrEmpty(filtercontent) )
						filterHeader.getFilterEditor(i).setContent("");
					else {
						filterHeader.getFilterEditor(i).setContent(filtercontent);
//						filterHeader.setToolTipText( filterHeader.getToolTipText() + column_name + ":" +  filtercontent + ",");
					}
				}
				
				dispose ();
			}
		});
		
	}
	
	protected void applyFilter()
	{
		String selectedfilter = (String) jcbxsavedfilter.getSelectedItem();
		String filteridstr = selectedfilter.substring(selectedfilter.length()-1);
		Integer filterid = Integer.parseInt(filteridstr);
		for (Component comp : this.contentPanel.getComponents() ) {
			if( !(comp instanceof JPanel) )
				continue;
			for (Component filtercomp : ((JPanel)comp).getComponents() ) {
				if(! (filtercomp instanceof JUpdatedTextField ) )
					continue;
				
				String keyword = filteridstr + ((JUpdatedTextField)filtercomp).getName();
				String filterinfo = savedfiltersprop.getProperty (keyword);
				if(Strings.isNullOrEmpty(filterinfo) )
					((JUpdatedTextField)filtercomp).setText("");
				else
					((JUpdatedTextField)filtercomp).setText(filterinfo);
			}
		}
		
		return;
	}

	protected void saveFilter() 
	{
		String selectedfilter = (String) jcbxsavedfilter.getSelectedItem();
		String filteridstr = selectedfilter.substring(selectedfilter.length()-1);
		Integer filterid = Integer.parseInt(filteridstr);
		for (Component comp : this.contentPanel.getComponents() ) {
			if( !(comp instanceof JPanel) )
				continue;
			for (Component filtercomp : ((JPanel)comp).getComponents() ) {
				if(! (filtercomp instanceof JUpdatedTextField ) )
					continue;
				
				String filterinfo = ((JUpdatedTextField)filtercomp).getText();
				String keyword = filteridstr +  filtercomp.getName();
				if(Strings.isNullOrEmpty(filterinfo) )
					savedfiltersprop.remove(keyword); 
				else			
					savedfiltersprop.setProperty(keyword, filterinfo); // update an old value
			}
		}
		
		String propFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/BanKuaiGeGuSavedFilter.properties";
		try {
			savedfiltersprop.store(new FileWriter(propFileName), "store to properties file");
		} catch (IOException e) {e.printStackTrace();}
		
		return;
	}

	private void createUIComponents() 
	{
		for(int i=0;i<columnmaxnumber;i++) {
			String column_name = prop.getProperty (String.valueOf(i) + "column_name");
			
			if(column_name == null || column_name.equalsIgnoreCase("NULL"))
				continue;
			
			String column_keyword = prop.getProperty (String.valueOf(i) + "column_info_keyword");
			
			JPanel filterPanel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 2));
			JLabel columnname = new JLabel(column_name + ":  ");
			columnname.setHorizontalAlignment(SwingConstants.RIGHT);
			filterPanel.add(columnname);
			
			JUpdatedTextField filterField = (JUpdatedTextField) JTextFactory.createTextField();
			filterField.setName(column_keyword);
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
	}
	private void createGui ()
	{
		setBounds(100, 100, 364, 466);
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
//				pnlfilter.setLayout(new BoxLayout(this.pnlfilter, BoxLayout.PAGE_AXIS));
				pnlfilter.setLayout(new FlowLayout());
				Integer filternumber;
				try {
					filternumber = Integer.parseInt( this.savedfiltersprop.getProperty("MaxSavedFiltersNumber") );
				} catch (java.lang.NumberFormatException e) { filternumber = 3;}
				String[] filterStrings = new String[filternumber];
				for(int i=0;i<filternumber;i++) 
					filterStrings[i] = "Saved filter" + String.valueOf(i);
				jcbxsavedfilter = new JComboBox<String> (filterStrings);
				
				this.btnsave = JLabelFactory.createButton("Save filter");
				this.btnapply = JLabelFactory.createButton("Apply filter");
				
				pnlfilter.add(jcbxsavedfilter);
				pnlfilter.add(this.btnsave);
				pnlfilter.add(this.btnapply);
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
	
	private  String toUNIXpath(String filePath) 
   	{
   		    return filePath.replace('\\', '/');
   	}
}
