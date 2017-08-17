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
		rmtcurdbmap = new HashMap<String,CurDataBase> ();
		parseSystemSettingXML ();
	}

	private String systemxmlfile;
	private HashMap<String, CurDataBase> curdbmap;
	private HashMap<String, CurDataBase> rmtcurdbmap;
	private boolean  newsystemsetting = false;
	
	private void parseSystemSettingXML()  
	{
		File directory = new File(this.systemxmlfile);//�趨Ϊ��ǰ�ļ���
		try{
		    System.out.println(directory.getCanonicalPath());//��ȡ��׼��·��
		    System.out.println(directory.getAbsolutePath());//��ȡ����·��
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
			 
			((DatabaseSourceTableModel)tablelocal.getModel()).refresh(curdbmap );
			((DatabaseSourceTableModel)tablelocal.getModel()).fireTableDataChanged();
			
			//serverdatabasesources
			Element elermtsorce = xmlroot.element("serverdatabasesources");
			Iterator itrmt = elermtsorce.elementIterator();
			 while (itrmt.hasNext()) 
			 {
				 Element elementdbs = (Element) itrmt.next();

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
				 
				 rmtcurdbmap.put( elementdbs.attributeValue("dbsname"), tmpdb);
			 }
			 
			((DatabaseSourceTableModel)tablermt.getModel()).refresh(rmtcurdbmap );
			((DatabaseSourceTableModel)tablermt.getModel()).fireTableDataChanged();
			
			 
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
		btnchsparsefilepath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setBanKuaiParseFileInstalledPath ();
			}
		});
		
		btnrmteditdb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tablermt.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ������Դ","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)tablermt.getModel()).getDbsAtRow(row);
				String dbnewname = tmpcur.getCurDataBaseName();
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, tmpcur, "�޸�����Դ", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;

				rmtcurdbmap.put( dbnewname, tmpcur );
				
				((DatabaseSourceTableModel)tablermt.getModel()).refresh(rmtcurdbmap );
				((DatabaseSourceTableModel)tablermt.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);

			}
		});
		btnrmtadd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				String dbnewname = JOptionPane.showInputDialog(null,"�������µ����ݿ�Դ��:","�������ݿ�Դ", JOptionPane.QUESTION_MESSAGE);
				KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0); 
				if(rmtcurdbmap.keySet().contains(dbnewname)) {
					JOptionPane.showMessageDialog(null,"���ݿ�Դ���ظ���");
					return;
				}
				
				
				CurDataBase addnewsr = new CurDataBase (dbnewname);
				int exchangeresult = JOptionPane.showConfirmDialog(null, addnewsr, "��������Դ", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				System.out.println(addnewsr.getCurDataBaseName());
				rmtcurdbmap.put( dbnewname, addnewsr );
				
				((DatabaseSourceTableModel)tablermt.getModel()).refresh(rmtcurdbmap );
				((DatabaseSourceTableModel)tablermt.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);
			}
		});
		btndelrmtdbs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablermt.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ������Դ","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int n = JOptionPane.showConfirmDialog(null, "ȷ��ɾ��������Դ?", "ɾ������Դ",JOptionPane.YES_NO_OPTION);//i=0/1
				if(n == 1)
					return;
				
				((DatabaseSourceTableModel)tablermt.getModel()).deleteRow(row);
				
//				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
//				
//				String dbsdeleted = tmpcur.getCurDataBaseName (); 
//				curdbmap.remove(dbsdeleted);
//				
//				((DatabaseSourceTableModel)table.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)tablermt.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);
			}
		});
		
		
		
		btnEditDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablelocal.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ������Դ","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)tablelocal.getModel()).getDbsAtRow(row);
				String dbnewname = tmpcur.getCurDataBaseName();
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, tmpcur, "��������Դ", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;

				curdbmap.put( dbnewname, tmpcur );
				
				((DatabaseSourceTableModel)tablelocal.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)tablelocal.getModel()).fireTableDataChanged();
				
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
		
		tablermt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablermt.getSelectedRow();
				int column = tablermt.getSelectedColumn();
				
				
				if(column !=0)
					return;
				
				Object tmp = ((DatabaseSourceTableModel)tablermt.getModel()).getValueAt(row, column);
				System.out.println(tmp);
				 ((DatabaseSourceTableModel)tablermt.getModel()).setValueAt(row, column);
//				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
//				String dbsdeleted = tmpcur.getCurDataBaseName ();
			}
		});
		
		tablelocal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tablelocal.getSelectedRow();
				int column = tablelocal.getSelectedColumn();
				
				
				if(column !=0)
					return;
				
				Object tmp = ((DatabaseSourceTableModel)tablelocal.getModel()).getValueAt(row, column);
				System.out.println(tmp);
				 ((DatabaseSourceTableModel)tablelocal.getModel()).setValueAt(row, column);
//				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
//				String dbsdeleted = tmpcur.getCurDataBaseName ();
				
			}
		});
		btndeletedbs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablelocal.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ������Դ","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int n = JOptionPane.showConfirmDialog(null, "ȷ��ɾ��������Դ?", "ɾ������Դ",JOptionPane.YES_NO_OPTION);//i=0/1
				if(n == 1)
					return;
				
				((DatabaseSourceTableModel)tablelocal.getModel()).deleteRow(row);
				
//				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)table.getModel()).getDbsAtRow(row);
//				
//				String dbsdeleted = tmpcur.getCurDataBaseName (); 
//				curdbmap.remove(dbsdeleted);
//				
//				((DatabaseSourceTableModel)table.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)tablelocal.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);
			}
		});
		btmaddnewdb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				String dbnewname = JOptionPane.showInputDialog(null,"�������µ����ݿ�Դ��:","�������ݿ�Դ", JOptionPane.QUESTION_MESSAGE);
				KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0); 
				if(curdbmap.keySet().contains(dbnewname)) {
					JOptionPane.showMessageDialog(null,"���ݿ�Դ���ظ���");
					return;
				}
				
				
				CurDataBase addnewsr = new CurDataBase (dbnewname);
				int exchangeresult = JOptionPane.showConfirmDialog(null, addnewsr, "��������Դ", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				System.out.println(addnewsr.getCurDataBaseName());
				curdbmap.put( dbnewname, addnewsr );
				
				((DatabaseSourceTableModel)tablelocal.getModel()).refresh(curdbmap );
				((DatabaseSourceTableModel)tablelocal.getModel()).fireTableDataChanged();
				
				saveButton.setEnabled(true);
			}
		});
		
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(!saveButton.isEnabled()) { //���浱ǰ������Դͷ
					return;
				}
				
				if(!preSaveCheck ())
					return;
				
				Document document = DocumentFactory.getInstance().createDocument();
		        Element rootele = document.addElement("SystemSettings");//����ĵ���
		        
				Element eletdx = rootele.addElement("tdxpah");
				eletdx.setText( toUNIXpath(tfldTDXInstalledPath.getText() ) );
				
				Element eletbkparsefile = rootele.addElement("bankuaiparsefilepah");
				eletbkparsefile.setText( toUNIXpath(tfldparsefilepath.getText() ) );
				
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
				
				Element elermtsorce = rootele.addElement("serverdatabasesources");
				
				Set<String> rmtdbsnameset = rmtcurdbmap.keySet();
				Iterator<String> rmtdbsit = rmtdbsnameset.iterator();
				while(rmtdbsit.hasNext()) {
					String tmpdbsname = rmtdbsit.next();
					
					CurDataBase tmpcurbs = rmtcurdbmap.get(tmpdbsname);
					
					Element eleonedbs = elermtsorce.addElement("singledbs");
					
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
				format.setEncoding("GBK");    // ָ��XML����        
				try {
					XMLWriter writer = new XMLWriter(new FileOutputStream(systemxmlfile),format); // ���ȫ��ԭʼ���ݣ������������µ�������Ҫ��XML�ļ�  
						writer.write(document); //������ļ�  
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

	protected void setBanKuaiParseFileInstalledPath()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
		    System.out.println(chooser.getSelectedFile());
		    String linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    System.out.println(linuxpath);
		    tfldparsefilepath.setText(linuxpath);
		}
		
	}
	private boolean preSaveCheck() 
	{
		txtareacheckresult.setText("ϵͳ���ü����:" + "\n");
		boolean sucessfail = true;
		
		if(tfldTDXInstalledPath.getText().trim().isEmpty() ) {
			txtareacheckresult.append("ͨ���Ű�װ·���������ã�" + "\n");
			 sucessfail = false;
		}
		
		
		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "tdxzs.cfg").exists()  ) {
			txtareacheckresult.append("ͨ����ϵͳ�����������õ�·���У�" + "\n");
			sucessfail = false;
		}
		
		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "block_gn.dat").exists()  ) {
			txtareacheckresult.append("ͨ����ϵͳ�����������õ�·���У�" + "\n");
			sucessfail = false;
		}

		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "block_fg.dat").exists()  ) {
			txtareacheckresult.append("ͨ����ϵͳ�����������õ�·���У�" + "\n");
			sucessfail = false;
		}
		
		if( !new File(tfldTDXInstalledPath.getText().trim() + "\\" + "T0002/hq_cache/" + "tdxhy.cfg").exists()  ) {
			txtareacheckresult.append("ͨ����ϵͳ�����������õ�·���У�" + "\n");
			sucessfail = false;
		}
		
		if(   ((DatabaseSourceTableModel)tablelocal.getModel()).getCurSelectedDbs().trim().isEmpty() ) {
			txtareacheckresult.append("����Ϊ������Ϣѡ��һ�����ݿ�����Դ��" + "\n");
			sucessfail = false;
		}
		if(   ((DatabaseSourceTableModel)tablermt.getModel()).getCurSelectedDbs().trim().isEmpty() ) {
			txtareacheckresult.append("����Ϊͨ����ͬ������ѡ��һ�����ݿ�����Դ��" + "\n");
			sucessfail = false;
		}
		
		if(!new File (tfldSysInstallPath.getText() + "����checklist����.xml").exists() ) {
			txtareacheckresult.append("����checklist����.xml�ڰ�װĿ¼��û���ҵ�����������ʾ��" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "����checklist���.xml").exists() ) {
			txtareacheckresult.append("����checklist���.xml�ڰ�װĿ¼��û���ҵ�����������ʾ��" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "����checklist�ɶ�.xml" ).exists() ) {
			txtareacheckresult.append("����checklist�ɶ�.xml�ڰ�װĿ¼��û���ҵ�����������ʾ��" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "����checklist����.xml" ).exists() ) {
			txtareacheckresult.append("����checklist����.xml�ڰ�װĿ¼��û���ҵ�����������ʾ��" + "\n");
		}
		if(!new File (tfldSysInstallPath.getText() + "����checklist����.xml" ).exists() ) {
			txtareacheckresult.append("����checklist����.xml�ڰ�װĿ¼��û���ҵ�����������ʾ��" + "\n");
		}
				
		txtareacheckresult.append("Finished!");
		return sucessfail;
	}

	private final JPanel contentPanel = new JPanel();
	private JLabel lblNewLabel;
	private JTextField tfldSysInstallPath;
	private JTextField tfldTDXInstalledPath;
	private JTable table;
	private JTable tablelocal;
	private JButton btnEditDb;
	private JButton btmaddnewdb;
	private JButton btndeletedbs;
	private JButton btnChosTDXDict;
	private JScrollPane scrollPanelocal;
	private JButton saveButton;
	private JButton okButton;
	private JTextArea txtareacheckresult;
	private JTable tablermt;
	private JButton btnrmteditdb;
	private JButton btnrmtadd;
	private JButton btndelrmtdbs;
	private JTextField tfldparsefilepath;
	private JButton btnchsparsefilepath;
	private void initializeGui() 
	{
		setTitle("\u7CFB\u7EDF\u8BBE\u7F6E");
		
		setBounds(100, 100, 603, 744);
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
		
		JLabel lblNewLabel_2 = new JLabel("\u57FA\u672C\u4FE1\u606F\u6570\u636E\u6E90\u5217\u8868");
		
		btnEditDb = new JButton("");
		btnEditDb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/edit_23.851162790698px_1200630_easyicon.net.png")));
		
		btmaddnewdb = new JButton("");
		btmaddnewdb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/add_24px_1181422_easyicon.net.png")));
		
		btndeletedbs = new JButton("");
		btndeletedbs.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/minus_red.png")));
		
		scrollPanelocal = new JScrollPane();
		
		saveButton = new JButton("");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel label = new JLabel("\u901A\u8FBE\u4FE1\u540C\u6B65\u6570\u636E\u6570\u636E\u6E90\u5217\u8868");
		
		btnrmteditdb = new JButton("");
		
		btnrmteditdb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/edit_23.851162790698px_1200630_easyicon.net.png")));
		
		btnrmtadd = new JButton("");
		
		btnrmtadd.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/add_24px_1181422_easyicon.net.png")));
		
		btndelrmtdbs = new JButton("");
		
		btndelrmtdbs.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/minus_red20.png")));
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel label_1 = new JLabel("\u6BCF\u65E5\u677F\u5757\u5206\u6790\u6587\u4EF6\u5B58\u653E\u8DEF\u5F84");
		
		tfldparsefilepath = new JTextField();
		tfldparsefilepath.setEditable(false);
		tfldparsefilepath.setColumns(10);
		
		btnchsparsefilepath = new JButton("");
		
		btnchsparsefilepath.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/open24.png")));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(26)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 501, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addComponent(lblNewLabel)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addGap(424)
											.addComponent(saveButton))
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addComponent(tfldTDXInstalledPath, GroupLayout.PREFERRED_SIZE, 439, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnChosTDXDict))
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addComponent(label)
											.addGap(106)
											.addComponent(btnrmteditdb)
											.addGap(18)
											.addComponent(btnrmtadd)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btndelrmtdbs))
										.addComponent(scrollPanelocal, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addComponent(lblNewLabel_2)
											.addGap(120)
											.addComponent(btnEditDb)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btmaddnewdb)
											.addGap(18)
											.addComponent(btndeletedbs, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addComponent(label_1)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(tfldparsefilepath, GroupLayout.PREFERRED_SIZE, 292, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnchsparsefilepath))))
								.addComponent(tfldSysInstallPath, GroupLayout.PREFERRED_SIZE, 539, GroupLayout.PREFERRED_SIZE))
							.addGap(205)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(12)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfldSysInstallPath, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnChosTDXDict, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(tfldTDXInstalledPath, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(tfldparsefilepath, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnchsparsefilepath))
					.addGap(9)
					.addComponent(saveButton)
					.addPreferredGap(ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel_2)
						.addComponent(btnEditDb)
						.addComponent(btndeletedbs)
						.addComponent(btmaddnewdb))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPanelocal, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_contentPanel.createParallelGroup(Alignment.TRAILING)
							.addComponent(label)
							.addComponent(btnrmtadd)
							.addComponent(btndelrmtdbs))
						.addComponent(btnrmteditdb, Alignment.TRAILING))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		DatabaseSourceTableModel rmttablemode = new DatabaseSourceTableModel( null );
		tablermt =  new  JTable(rmttablemode){
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
		
		scrollPane.setViewportView(tablermt);
		
		txtareacheckresult = new JTextArea();
		txtareacheckresult.setEditable(false);
		scrollPane_1.setViewportView(txtareacheckresult);
		
		DatabaseSourceTableModel localtablemodel = new DatabaseSourceTableModel( null );
		tablelocal = new  JTable(localtablemodel){
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
		scrollPanelocal.setViewportView(tablelocal);
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
		saveButton.setContentAreaFilled(false); //���ذ�ť�ķ���
		
		this.setLocationRelativeTo(null);
	}
}


class DatabaseSourceTableModel extends AbstractTableModel 
{
	private HashMap<String,CurDataBase> curdbmap;
	ArrayList<String> dbsnamelist ; 
	private String selecteddbs;
	

	String[] jtableTitleStrings = {  "ѡ��","����Դ��","���ݿ�������","��¼��","����","���ݿ�����"};
	
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
	    }//���ñ������ 
		
	    
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
            	if(selecteddbs.isEmpty()) { //��û��ѡ������Դ
            		selecteddbs = dbname;
            		CurDataBase tmpcub = curdbmap.get(dbname);
        	    	boolean curselected = tmpcub.getCurrentSelectedDbs ();
        	    	tmpcub.setCurrentSelectedDbs(!curselected);
        	    	selecteddbs = dbname;

            	} else
            	if(dbname.equals(selecteddbs)) { //�Ե�ǰ����Դȥ��

            		CurDataBase tmpcub = curdbmap.get(selecteddbs);
        	    	boolean curselected = tmpcub.getCurrentSelectedDbs ();
        	    	tmpcub.setCurrentSelectedDbs(!curselected);
        	    	
        	    	selecteddbs = "";

            	} else 	{ //ѡ�����µ�����Դ,�ϵ�ȥ���µı�ture

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


