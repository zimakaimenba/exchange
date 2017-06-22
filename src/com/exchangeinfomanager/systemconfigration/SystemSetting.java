package com.exchangeinfomanager.systemconfigration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class SystemSetting extends JDialog 
{
	/**
	 * Create the dialog.
	 */
	public SystemSetting(String systemxmlfile) 
	{
		initializeGui ();
		createEvents ();
		this.systemxmlfile = systemxmlfile;
		curdbmap = new HashMap<String,CurDataBase> ();
		parseSystemSettingXML ();
	}

	private String systemxmlfile;
	private HashMap<String, CurDataBase> curdbmap;
	private boolean  newsystemsetting = false;
	
	private void parseSystemSettingXML()  
	{
		File directory = new File(this.systemxmlfile);//设定为当前文件夹
		try{
		    System.out.println(directory.getCanonicalPath());//获取标准的路径
		    System.out.println(directory.getAbsolutePath());//获取绝对路径
		    System.out.println(directory.getParent() );

		    tfldSysInstallPath.setText( toUNIXpath(directory.getParent()+ "\\")  );
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		Document document;
    	Element xmlroot ;
    	SAXReader reader ;
    	
    	FileInputStream xmlfileinput = null;
		try {
			xmlfileinput = new FileInputStream(this.systemxmlfile );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		reader = new SAXReader();
		try {
			document = reader.read(xmlfileinput); 
			xmlroot = document.getRootElement();
			
			Element eletdx = xmlroot.element("tdxpah");
			if(eletdx != null ) {
				String tdxpath = eletdx.getText();
				tfldTDXInstalledPath.setText(eletdx.getText() );
			}
		
			
			Element elesorce = xmlroot.element("databasesources");
			Iterator it = elesorce.elementIterator();
			 while (it.hasNext()) 
			 {
				 Element elementdbs = (Element) it.next();

				 System.out.println( elementdbs.attributeValue("dbsname") ) ;
				 CurDataBase tmpdb = new CurDataBase (elementdbs.attributeValue("dbsname"));
				 System.out.println( elementdbs.attributeValue("user") ) ;
				 tmpdb.setUser(elementdbs.attributeValue("user"));
				 System.out.println( elementdbs.attributeValue("password") ) ;
				 tmpdb.setPassWord(elementdbs.attributeValue("password"));
				 System.out.println( elementdbs.getText() ) ;
				 tmpdb.setDataBaseConStr(elementdbs.getText());
				 tmpdb.setCurDatabaseType( elementdbs.attributeValue("databasetype") );
				 System.out.println( elementdbs.attributeValue("curselecteddbs") ) ;
				 if(elementdbs.attributeValue("curselecteddbs").equals("yes")){
					 tmpdb.setCurrentSelectedDbs(true);
					 //curselecteddbs = elementdbs.attributeValue("dbsname");
				 }
				 else
					 tmpdb.setCurrentSelectedDbs(false);
				 
				 curdbmap.put( elementdbs.attributeValue("dbsname"), tmpdb);
			 }
			 
			((DatabaseSourceTableModel)table.getModel()).refresh(curdbmap );
			((DatabaseSourceTableModel)table.getModel()).fireTableDataChanged();
			
			
			 
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("SystemSetting2 parse xml error");
		}
		
		try {
			xmlfileinput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
	}
	public boolean isNewSystemSetting() 
	{
		return newsystemsetting;
	}
//	public String getTDXInstalledPath() 
//	{
//		return tfdTDXInstalledPath.getText().trim();
//	}
//	public String getDataBaseConnectStr() 
//	{
//		return tfdDbInstalledPath.getText();
//	}
//	public String getCurDBUser() 
//	{
//		return tfldUser.getText();
//	}
//	public String getCurDBPaswd() 
//	{
//		return tfldPaswd.getText();
//	}
	private void setTDXInstalledPath() 
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
		    System.out.println(chooser.getSelectedFile());
		    String linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    System.out.println(linuxpath);
		    tfldTDXInstalledPath.setText(linuxpath);
		}
	}
	private  String toUNIXpath(String filePath) 
	{
		    return filePath.replace('\\', '/');
	 }
	private void createEvents() 
	{
		
		btnEditDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = table.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择数据源","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
				String dbnewname = tmpcur.getCurDataBaseName();
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, tmpcur, "加入数据源", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;

				curdbmap.put( dbnewname, tmpcur );
				
				((DatabaseSourceTableModel)table.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)table.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);

			}
		});
		
		btnChosTDXDict.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				setTDXInstalledPath ();
				
				
	
				
			}

			
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();
				
				
				if(column !=0)
					return;
				
				Object tmp = ((DatabaseSourceTableModel)table.getModel()).getValueAt(row, column);
				System.out.println(tmp);
				 ((DatabaseSourceTableModel)table.getModel()).setValueAt(row, column);
//				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
//				String dbsdeleted = tmpcur.getCurDataBaseName ();
				
			}
		});
		btndeletedbs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = table.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择数据源","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int n = JOptionPane.showConfirmDialog(null, "确认删除该数据源?", "删除数据源",JOptionPane.YES_NO_OPTION);//i=0/1
				if(n == 1)
					return;
				
				((DatabaseSourceTableModel)table.getModel()).deleteRow(row);
				
//				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
//				
//				String dbsdeleted = tmpcur.getCurDataBaseName (); 
//				curdbmap.remove(dbsdeleted);
//				
//				((DatabaseSourceTableModel)table.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)table.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);
			}
		});
		btmaddnewdb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				String dbnewname = JOptionPane.showInputDialog(null,"请输入新的数据库源名:","加入数据库源", JOptionPane.QUESTION_MESSAGE);
				KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0); 
				if(curdbmap.keySet().contains(dbnewname)) {
					JOptionPane.showMessageDialog(null,"数据库源名重复！");
					return;
				}
				
				
				CurDataBase addnewsr = new CurDataBase (dbnewname);
				int exchangeresult = JOptionPane.showConfirmDialog(null, addnewsr, "加入数据源", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				System.out.println(addnewsr.getCurDataBaseName());
				curdbmap.put( dbnewname, addnewsr );
				
				((DatabaseSourceTableModel)table.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)table.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);
			}
		});
		
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(!saveButton.isEnabled()) { //保存当前的数据源头
					return;
				}
				
				if(!preSaveCheck ())
					return;
				
				Document document = DocumentFactory.getInstance().createDocument();
		        Element rootele = document.addElement("SystemSettings");//添加文档根
		        
				Element eletdx = rootele.addElement("tdxpah");
				eletdx.setText( toUNIXpath(tfldTDXInstalledPath.getText() ) );
				
				
				
				Element elesorce = rootele.addElement("databasesources");
				
				Set<String> dbsnameset = curdbmap.keySet();
				Iterator<String> dbsit = dbsnameset.iterator();
				while(dbsit.hasNext()) {
					String tmpdbsname = dbsit.next();
					
					CurDataBase tmpcurbs = curdbmap.get(tmpdbsname);
					
					Element eleonedbs = elesorce.addElement("singledbs");
					
					eleonedbs.addAttribute("dbsname", tmpcurbs.getCurDataBaseName());
					eleonedbs.addAttribute("user", tmpcurbs.getUser() );
					eleonedbs.addAttribute("password", tmpcurbs.getPassWord() );
					eleonedbs.addAttribute("databasetype", tmpcurbs.getCurDatabaseType() );
					eleonedbs.setText(tmpcurbs.getDataBaseConStr());
					if(tmpcurbs.getCurrentSelectedDbs())
						eleonedbs.addAttribute("curselecteddbs", "yes" );
					else
						eleonedbs.addAttribute("curselecteddbs", "no" );
				}
				
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("GBK");    // 指定XML编码        
				try {
					XMLWriter writer = new XMLWriter(new FileOutputStream(systemxmlfile),format); // 输出全部原始数据，并用它生成新的我们需要的XML文件  
						writer.write(document); //输出到文件  
						//writer.flush();
						writer.close();
						//return true;
				} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
				}
				newsystemsetting = true;
				dispose ();
			}
		});
	
//		okButton_1.addActionListener(new ActionListener() 
//		{
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				
//			}
//		});

		
	}

	private boolean preSaveCheck() 
	{
		txtareacheckresult.setText("系统设置检查结果:" + "\n");
		boolean sucessfail = true;
		
		if(tfldTDXInstalledPath.getText().trim().isEmpty() ) {
			txtareacheckresult.append("通达信安装路径必须设置！" + "\n");
			 sucessfail = false;
		}
		
		
		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "tdxzs.cfg").exists()  ) {
			txtareacheckresult.append("通达信系统不存在于设置的路径中！" + "\n");
			sucessfail = false;
		}
		
		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "block_gn.dat").exists()  ) {
			txtareacheckresult.append("通达信系统不存在于设置的路径中！" + "\n");
			sucessfail = false;
		}

		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "block_fg.dat").exists()  ) {
			txtareacheckresult.append("通达信系统不存在于设置的路径中！" + "\n");
			sucessfail = false;
		}
		
		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "tdxhy.cfg").exists()  ) {
			txtareacheckresult.append("通达信系统不存在于设置的路径中！" + "\n");
			sucessfail = false;
		}
		
		if(   ((DatabaseSourceTableModel)table.getModel()).getCurSelectedDbs().trim().isEmpty() ) {
			txtareacheckresult.append("必须选择一个数据库连接源！" + "\n");
			sucessfail = false;
		}
		
		if(!new File (tfldSysInstallPath.getText() + "买入checklist政策.xml").exists() ) {
			txtareacheckresult.append("买入checklist政策.xml在安装目录下没用找到，将不能显示！" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "买入checklist题材.xml").exists() ) {
			txtareacheckresult.append("买入checklist题材.xml在安装目录下没用找到，将不能显示！" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "买入checklist股东.xml" ).exists() ) {
			txtareacheckresult.append("买入checklist股东.xml在安装目录下没用找到，将不能显示！" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "买入checklist技术.xml" ).exists() ) {
			txtareacheckresult.append("买入checklist技术.xml在安装目录下没用找到，将不能显示！" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "买入checklist技术.xml" ).exists() ) {
			txtareacheckresult.append("买入checklist财务.xml在安装目录下没用找到，将不能显示！" + "\n");
		}
				
		txtareacheckresult.append("Finished!");
		return sucessfail;
	}

	private final JPanel contentPanel = new JPanel();
	private JLabel lblNewLabel;
	private JTextField tfldSysInstallPath;
	private JTextField tfldTDXInstalledPath;
	private JTable table;
	private JButton btnEditDb;
	private JButton btmaddnewdb;
	private JButton btndeletedbs;
	private JButton btnChosTDXDict;
	private JScrollPane scrollPane;
	private JButton saveButton;
	private JButton okButton;
	private JTextArea txtareacheckresult;
	private void initializeGui() 
	{
		setTitle("\u7CFB\u7EDF\u8BBE\u7F6E");
		
		setBounds(100, 100, 603, 664);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblNewLabel = new JLabel("\u7CFB\u7EDF\u5B89\u88C5\u8DEF\u5F84");
		}
		
		tfldSysInstallPath = new JTextField();
		tfldSysInstallPath.setEditable(false);
		tfldSysInstallPath.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\u901A\u8FBE\u4FE1\u5B89\u88C5\u8DEF\u5F84");
		
		tfldTDXInstalledPath = new JTextField();
		tfldTDXInstalledPath.setEditable(false);
		tfldTDXInstalledPath.setText("");
		tfldTDXInstalledPath.setColumns(10);
		
		btnChosTDXDict = new JButton("");
		btnChosTDXDict.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/open24.png")));
		
		JLabel lblNewLabel_2 = new JLabel("\u6570\u636E\u6E90\u5217\u8868");
		
		btnEditDb = new JButton("");
		btnEditDb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/edit_23.851162790698px_1200630_easyicon.net.png")));
		
		btmaddnewdb = new JButton("");
		btmaddnewdb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/add_24px_1181422_easyicon.net.png")));
		
		btndeletedbs = new JButton("");
		btndeletedbs.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/minus_red.png")));
		
		scrollPane = new JScrollPane();
		
		saveButton = new JButton("");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(26)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldTDXInstalledPath, GroupLayout.PREFERRED_SIZE, 439, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnChosTDXDict))
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 501, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblNewLabel_2)
									.addGap(120)
									.addComponent(btnEditDb)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btmaddnewdb)
									.addGap(18)
									.addComponent(btndeletedbs, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(saveButton))
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
						.addComponent(tfldSysInstallPath))
					.addContainerGap(45, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfldSysInstallPath, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnChosTDXDict, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(tfldTDXInstalledPath, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel_2)
						.addComponent(btnEditDb)
						.addComponent(btndeletedbs)
						.addComponent(btmaddnewdb)
						.addComponent(saveButton))
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		txtareacheckresult = new JTextArea();
		txtareacheckresult.setEditable(false);
		scrollPane_1.setViewportView(txtareacheckresult);
		
		DatabaseSourceTableModel rongquanmodel = new DatabaseSourceTableModel( null );
		table = new  JTable(rongquanmodel){
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
		};
		scrollPane.setViewportView(table);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("\u4FDD\u5B58");
				
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						dispose ();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		saveButton.setOpaque(false);//
		saveButton.setContentAreaFilled(false); //隐藏按钮的方法
		
		this.setLocationRelativeTo(null);
	}
}


class DatabaseSourceTableModel extends AbstractTableModel 
{
	private HashMap<String,CurDataBase> curdbmap;
	ArrayList<String> dbsnamelist ; 
	private String selecteddbs;
	

	String[] jtableTitleStrings = {  "选择","数据源名","数据库连接字","登录名","密码","数据库类型"};
	
	DatabaseSourceTableModel (HashMap<String, CurDataBase> curdbmap)
	{
		this.curdbmap =  curdbmap;
		dbsnamelist = new ArrayList<String> ();
		if(curdbmap != null) {
			dbsnamelist.addAll(this.curdbmap.keySet());
		}
		selecteddbs = "";
	}

	public void refresh (HashMap<String, CurDataBase> curdbmap) 
	{
		this.curdbmap =  curdbmap;
		dbsnamelist.clear();
		dbsnamelist.addAll(this.curdbmap.keySet());
		System.out.print(dbsnamelist);
	}


	 public int getRowCount() 
	 {
		 if(curdbmap != null)
			 return curdbmap.size();
		 else return 0;
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(curdbmap == null)
	    		return null;
	    	
	    	String dbname = dbsnamelist.get(rowIndex);
	    	CurDataBase tmpcub = curdbmap.get(dbname);
	    	
	    	Object value = "??";

	    	switch (columnIndex) {
            case 0:
            	 value = new Boolean(tmpcub.getCurrentSelectedDbs() );
            	 if((Boolean)value == true) {
            		 selecteddbs = dbname;
//            		 System.out.println("cur dbs = " + dbname);
            	 }
            	 
                break;
            case 1:
            	value = tmpcub.getCurDataBaseName();
                break;
            case 2:
            	value = tmpcub.getDataBaseConStr();
                break;
            case 3:
            	value = tmpcub.getUser();
                break;    
            case 4:
            	value = tmpcub.getPassWord();
                break;
            case 5:
            	value = tmpcub.getCurDatabaseType();
                break; 
	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = Boolean.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		        case 4:
			          clazz = String.class;
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
		
	    
	    public CurDataBase getDbsAtRow (int row)
	    {
	    	String dbname = dbsnamelist.get(row);
	    	CurDataBase tmpcub = curdbmap.get(dbname);
	    	return tmpcub;
	    }
	    public boolean isCellEditable(int row,int column) {
	    	return (column == 0);
	    		
		}
	    
	    public void deleteAllRows()
	    {
	    }
	    public void deleteRow(int row)
	    {
	    	String name = dbsnamelist.get(row);
	    	if(name.equals(selecteddbs))
	    		selecteddbs = "";
	    	curdbmap.remove(name);
	    	dbsnamelist.remove(name);
	    }
	    
	    public void setValueAt(int row, int col) 
	    {
	    	
            if (col == 0) {
            	
            	String dbname = dbsnamelist.get(row);
            	if(selecteddbs.isEmpty()) { //还没用选择数据源
            		selecteddbs = dbname;
            		CurDataBase tmpcub = curdbmap.get(dbname);
        	    	boolean curselected = tmpcub.getCurrentSelectedDbs ();
        	    	tmpcub.setCurrentSelectedDbs(!curselected);
        	    	selecteddbs = dbname;

            	} else
            	if(dbname.equals(selecteddbs)) { //对当前数据源去否

            		CurDataBase tmpcub = curdbmap.get(selecteddbs);
        	    	boolean curselected = tmpcub.getCurrentSelectedDbs ();
        	    	tmpcub.setCurrentSelectedDbs(!curselected);
        	    	
        	    	selecteddbs = "";

            	} else 	{ //选择了新的数据源,老的去否，新的变ture

            		CurDataBase tmpcub = curdbmap.get(selecteddbs);
        	    	boolean curselected = tmpcub.getCurrentSelectedDbs ();
        	    	tmpcub.setCurrentSelectedDbs(!curselected);
        	    	
        	    	CurDataBase tmpcubnew = curdbmap.get(dbname);
        	    	boolean curselectednw = tmpcubnew.getCurrentSelectedDbs ();
        	    	tmpcubnew.setCurrentSelectedDbs(!curselectednw);

        	    	selecteddbs = dbname;
            	}
            	
                //this.fireTableCellUpdated(row, col);
            	this.fireTableDataChanged();
            }
        }
	    
	    public String getCurSelectedDbs () 
	    {
	    	return selecteddbs;
	    }
	    
	    
}


