package com.exchangeinfomanager.gudong;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagManagment.TagSearchOnNewsTableModel;
import com.exchangeinfomanager.TagManagment.TagSearchOnNodesTableModel;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagsServiceForSystemTags;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.TableCellListener;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;

public class JiGouManagementPnl extends JDialog 
{
	protected static final int WIDTH = 400;
    protected static final int HEIGHT = 500;
    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;
    
	protected JPanel eastPanel;
	protected JPanel centerPanel;
	protected JTable tblofnodes;
	protected JTable tblnews;
	private CacheForInsertedTag syscache;
	protected TagsServiceForSystemTags sysservice;
	protected JUpdatedTextField tfldmusthavetags;
	protected JUpdatedTextField tfldoneoftags;
	

	/**
	 * Create the panel.
	 */
	public JiGouManagementPnl()
	{
		createGui ();
		
		createEvents ();

	}
	public void setPreSearchMustHaveTags (Collection<Tag> musthavetags)
	{
		if(musthavetags == null) {
			tfldmusthavetags.setText("");
			return ;
		}
//		Joiner.on(" ").skipNulls().join(set_1);
		for(Tag tmpsltlbs : musthavetags) {
			String tagname = tmpsltlbs.getName();
			if ( ! tfldmusthavetags.getText().contains(tagname) )
				tfldmusthavetags.setText( tfldmusthavetags.getText() + tagname + "  ");
		}
	}
	public void setPreSearchOrHaveTags (Collection<Tag> musthavetags)
	{
		if(musthavetags == null) {
			tfldoneoftags.setText("");
			return;
		}
//		Joiner.on(" ").skipNulls().join(set_1);
		for(Tag tmpsltlbs : musthavetags) {
			String tagname = tmpsltlbs.getName();
			if ( ! tfldoneoftags.getText().contains(tagname) )
				tfldoneoftags.setText( tfldoneoftags.getText() + tagname + "  ");
		}
	}
	/*
	 * 
	 */
	public void setPreSearchOrHaveTags (String orhavetags)
	{
		tfldoneoftags.setText( tfldoneoftags.getText() + " " + orhavetags + "  ");
	}
	
	/*
	 * 
	 */
	private void createEvents() 
	{
		
	}

	private void createGui()
	{
		setBounds(100, 100, 874, 821);
		getContentPane().setLayout(new FlowLayout());
		contentPanel.setLayout(new BorderLayout () );
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		this.eastPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
	    this.eastPanel.setLayout(new BoxLayout(this.eastPanel, BoxLayout.PAGE_AXIS));

	    this.contentPanel.add(eastPanel, BorderLayout.WEST);
	    createEastSeachPanel ();
	    
	    this.centerPanel = JPanelFactory.createPanel();
	    this.contentPanel.add(centerPanel, BorderLayout.CENTER);
	    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
	    createCenterSeachPanel ();
	    
		getContentPane().add(contentPanel, BorderLayout.CENTER);
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

	private void createCenterSeachPanel() 
	{
		JScrollPane nodepane = new JScrollPane ();
        nodepane.setAutoscrolls(true);
        TagSearchOnNodesTableModel nodetablemodel = new TagSearchOnNodesTableModel ();
        tblofnodes = new JTable (nodetablemodel);
        nodepane.setViewportView(tblofnodes);
        
        centerPanel.add(nodepane);
        
        JScrollPane newspane = new JScrollPane ();
        newspane.setAutoscrolls(true);
        TagSearchOnNewsTableModel newstablemodel = new TagSearchOnNewsTableModel ();
        tblnews = new JTable (newstablemodel);
        newspane.setViewportView(tblnews);
        
        centerPanel.add(newspane);
        
        Action action = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener)e.getSource();
//                System.out.println("Row   : " + tcl.getRow());
//                System.out.println("Column: " + tcl.getColumn());
//                System.out.println("Old   : " + tcl.getOldValue());
//                System.out.println("New   : " + tcl.getNewValue());
            }
        };

        TableCellListener tcl = new TableCellListener(tblnews, action);
	}
	/*
	 * 
	 */
	public void createEastSeachPanel ()
	{
		JiGouTableModel jgmodel = new JiGouTableModel ();
		JTable jitable = new JTable(jgmodel);
        
		JScrollPane despane = new JScrollPane ();
        despane.setAutoscrolls(true);
        despane.setViewportView(jitable);
        
        eastPanel.add(despane);
        eastPanel.add(Box.createVerticalStrut(10));
        
        eastPanel.add(JLabelFactory.createLabel("Must contain all of these Tags"));
        
        tfldmusthavetags = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
        tfldmusthavetags.setMouseLeftClearEnabled(false);
    	eastPanel.add(tfldmusthavetags);
    	
    	eastPanel.add(JLabelFactory.createLabel("At least one of these Tags"));
    	
    	tfldoneoftags = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	tfldoneoftags.setMouseLeftClearEnabled(false);
    	eastPanel.add(tfldoneoftags);
    	
    	eastPanel.add(JLabelFactory.createLabel("None of these Tags"));
    	
    	JUpdatedTextField tfldnoneoftags = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	tfldnoneoftags.setMouseLeftClearEnabled(false);
    	eastPanel.add(tfldnoneoftags);
    	
    	eastPanel.add(JLabelFactory.createLabel("Searching Fields:"));
    	
    	JUpdatedTextField tflsearchfield = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	tflsearchfield.setMouseLeftClearEnabled(false);
    	eastPanel.add(tflsearchfield);
    	
    	eastPanel.add(Box.createVerticalStrut(10));
	}

	private final JPanel contentPanel = new JPanel();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JiGouManagementPnl dialog = new JiGouManagementPnl();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class JiGouTableModel extends DefaultTableModel 
{
	private List<JiGou> jglist;
//	private ArrayList<String> acntnamelist;
	private String stockcode;
	String[] jtableTitleStrings = { "机构名称", "机构全称","明星","皇亲国戚","说明"};
	
	public JiGouTableModel () {}
	
	public void refresh  (List<JiGou> jigouList)
	{
		this.jglist = jigouList;
		
		this.fireTableDataChanged();
	}
	 public int getRowCount() 
	 {
		 if(this.jglist == null)
			 return 0;
		 else if(this.jglist.isEmpty()  )
			 return 0;
		 else
			 return this.jglist.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(jglist.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	JiGou jigou = jglist.get(rowIndex);
	    	
	    	switch (columnIndex) {
            case 0:
                value = jigou.getJiGouName();
                break;
            case 1:
            	value = jigou.getJiGouQuanChen ();

            	break;
            case 2:
            	value = jigou.isMingXing();
                break;
            case 3:
            	value = jigou.isHuangQinGuoQie();
                break;
            case 4:
            	value = jigou.JiGouShuoMing ();
                break;

	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = Boolean.class;
			          break;
		        case 4:
			          clazz = Boolean.class;
			          break;
		        case 5:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
//	    @Override
//	    public Class<?> getColumnClass(int columnIndex) {
//	        return // Return the class that best represents the column...
//	    }
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public JiGou getAccountsAt(int row) {
	        return this.jglist.get(row);
	    }
	    
	    public void deleteAllRows()
	    {
	    	if(jglist != null) {
	    		this.jglist.clear();
		    	this.fireTableDataChanged();
	    	}
	    }
	    

}
