package com.exchangeinfomanager.systemconfigration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.exchangeinfomanager.database.ConnectDataBase;

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
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

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
//		rmtcurdbmap = new HashMap<String,CurDataBase> ();
		parseSystemSettingXML ();
	}

	private String systemxmlfile;
	private HashMap<String, CurDataBase> curdbmap;
//	private HashMap<String, CurDataBase> rmtcurdbmap;
	private boolean  newsystemsetting = false;
	private static Logger logger = Logger.getLogger(SystemSetting.class);
	
	private void parseSystemSettingXML()  
	{
		File directory = new File(this.systemxmlfile);//设定为当前文件夹
		try{
		    logger.debug(directory.getCanonicalPath());//获取标准的路径
		    logger.debug(directory.getAbsolutePath());//获取绝对路径
		    logger.debug(directory.getParent() );

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
			
			Element elenameofguanzhuzdybk = xmlroot.element("nameofguanzhubankuai");
			if(elenameofguanzhuzdybk != null ) {
				String text = elenameofguanzhuzdybk.getText();
				tfldzdyguanzhu.setText(text);
			}
			
			Element elezbfxzq = xmlroot.element("zhanbifengxizhouqi");
			if(elezbfxzq != null ) {
				String text = elezbfxzq.getText();
				tfldzhanbizhouqi.setText(text);
			}
			
			
			Element eletdxzdybkpath = xmlroot.element("tdxzdybkpath");
			if(eletdxzdybkpath != null) {
				String text = eletdxzdybkpath.getText();
				tfldzdyfilepath.setText(text);
			}
			
			Element eleprivate = xmlroot.element("privatemode");
			if(eleprivate != null ) {
				String text = eleprivate.getText();
				cbxprivatemode.setSelected(Boolean.parseBoolean(text));
			}
			
			Element elepythoninterper = xmlroot.element("pythoninterper");
			if(elepythoninterper != null ) {
				String text = elepythoninterper.getText();
				tfldpythonptah.setText(text);
			}
			
			
			
			Element elesorce = xmlroot.element("databasesources");
			Iterator it = elesorce.elementIterator();
			 while (it.hasNext()) 
			 {
				 Element elementdbs = (Element) it.next();

				 logger.debug( elementdbs.attributeValue("dbsname") ) ;
				 CurDataBase tmpdb = new CurDataBase (elementdbs.attributeValue("dbsname"));
				 logger.debug( elementdbs.attributeValue("user") ) ;
				 tmpdb.setUser(elementdbs.attributeValue("user"));
				 logger.debug( elementdbs.attributeValue("password") ) ;
				 tmpdb.setPassWord(elementdbs.attributeValue("password"));
				 logger.debug( elementdbs.getText() ) ;
				 tmpdb.setDataBaseConStr(elementdbs.getText());
				 tmpdb.setCurDatabaseType( elementdbs.attributeValue("databasetype") );
				 logger.debug( elementdbs.attributeValue("curselecteddbs") ) ;
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
//			Element elermtsorce = xmlroot.element("serverdatabasesources");
//			Iterator itrmt = elermtsorce.elementIterator();
//			 while (itrmt.hasNext()) 
//			 {
//				 Element elementdbs = (Element) itrmt.next();
//
//				 logger.debug( elementdbs.attributeValue("dbsname") ) ;
//				 CurDataBase tmpdb = new CurDataBase (elementdbs.attributeValue("dbsname"));
//				 logger.debug( elementdbs.attributeValue("user") ) ;
//				 tmpdb.setUser(elementdbs.attributeValue("user"));
//				 logger.debug( elementdbs.attributeValue("password") ) ;
//				 tmpdb.setPassWord(elementdbs.attributeValue("password"));
//				 logger.debug( elementdbs.getText() ) ;
//				 tmpdb.setDataBaseConStr(elementdbs.getText());
//				 tmpdb.setCurDatabaseType( elementdbs.attributeValue("databasetype") );
//				 logger.debug( elementdbs.attributeValue("curselecteddbs") ) ;
//				 if(elementdbs.attributeValue("curselecteddbs").equals("yes")){
//					 tmpdb.setCurrentSelectedDbs(true);
//					 //curselecteddbs = elementdbs.attributeValue("dbsname");
//				 }
//				 else
//					 tmpdb.setCurrentSelectedDbs(false);
//				 
//				 rmtcurdbmap.put( elementdbs.attributeValue("dbsname"), tmpdb);
//			 }
			
			 
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.debug("SystemSetting2 parse xml error");
		}
		
		try {
			xmlfileinput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (java.lang.NullPointerException e) {
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
		    logger.debug(chooser.getSelectedFile());
		    String linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    logger.debug(linuxpath);
		    tfldTDXInstalledPath.setText(linuxpath);
		}
	}
	private  String toUNIXpath(String filePath) 
	{
		    return filePath.replace('\\', '/');
	 }
	private void createEvents() 
	{
		ckbxzdy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(ckbxzdy.isSelected()) {
					btnzdyselect.setEnabled(true);
				} else {
					btnzdyselect.setEnabled(false);
					tfldzdyfilepath.setText("通达信默认路径。");
				}
			}
		});
		
		btnchoosepython.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				choosePythonInterper ();
				
			}
		});
		
		btnzdyselect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				setBanKuaiParseFileInstalledPath ();
			}
		});
		
		
		
		btnEditDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tablelocal.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择数据源","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				CurDataBase tmpcur = (CurDataBase) ((DatabaseSourceTableModel)tablelocal.getModel()).getDbsAtRow(row);
				String dbnewname = tmpcur.getCurDataBaseName();
				
				int exchangeresult = JOptionPane.showConfirmDialog(null, tmpcur, "加入数据源", JOptionPane.OK_CANCEL_OPTION);
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
		
		tablelocal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tablelocal.getSelectedRow();
				int column = tablelocal.getSelectedColumn();
				
				
				if(column !=0)
					return;
				
				Object tmp = ((DatabaseSourceTableModel)tablelocal.getModel()).getValueAt(row, column);
				logger.debug(tmp);
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
					JOptionPane.showMessageDialog(null,"请选择数据源","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int n = JOptionPane.showConfirmDialog(null, "确认删除该数据源?", "删除数据源",JOptionPane.YES_NO_OPTION);//i=0/1
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
				
				logger.debug(addnewsr.getCurDataBaseName());
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
				if(!saveButton.isEnabled()) { //保存当前的数据源头
					return;
				}
				
				if(!preSaveCheck ())
					return;
				
				Document document = DocumentFactory.getInstance().createDocument();
		        Element rootele = document.addElement("SystemSettings");//添加文档根
		        
				Element eletdx = rootele.addElement("tdxpah");
				eletdx.setText( toUNIXpath(tfldTDXInstalledPath.getText() ) );
				
				Element eletbknameofguanzhu = rootele.addElement("nameofguanzhubankuai");
				eletbknameofguanzhu.setText( toUNIXpath(tfldzdyguanzhu.getText() ) );
				
				if(ckbxzdy.isSelected() ) {
					Element eletdxzdybkpath = rootele.addElement("tdxzdybkpath");
					eletdxzdybkpath.setText(toUNIXpath(tfldzdyfilepath.getText()));
				}
				
				Element zhanbifengxizhouqi = rootele.addElement("zhanbifengxizhouqi");
				zhanbifengxizhouqi.setText(tfldzhanbizhouqi.getText());
				
				Element priavtemodesetting = rootele.addElement("privatemode");
				priavtemodesetting.setText( String.valueOf(cbxprivatemode.isSelected() ) );
				
				Element elebpythoninterper = rootele.addElement("pythoninterper");
				elebpythoninterper.setText(tfldpythonptah.getText().trim());

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
				
//				Element elermtsorce = rootele.addElement("serverdatabasesources");
				
//				Set<String> rmtdbsnameset = rmtcurdbmap.keySet();
//				Iterator<String> rmtdbsit = rmtdbsnameset.iterator();
//				while(rmtdbsit.hasNext()) {
//					String tmpdbsname = rmtdbsit.next();
//					
//					CurDataBase tmpcurbs = rmtcurdbmap.get(tmpdbsname);
//					
//					Element eleonedbs = elermtsorce.addElement("singledbs");
//					
//					eleonedbs.addAttribute("dbsname", tmpcurbs.getCurDataBaseName());
//					eleonedbs.addAttribute("user", tmpcurbs.getUser() );
//					eleonedbs.addAttribute("password", tmpcurbs.getPassWord() );
//					eleonedbs.addAttribute("databasetype", tmpcurbs.getCurDatabaseType() );
//					eleonedbs.setText(tmpcurbs.getDataBaseConStr());
//					if(tmpcurbs.getCurrentSelectedDbs())
//						eleonedbs.addAttribute("curselecteddbs", "yes" );
//					else
//						eleonedbs.addAttribute("curselecteddbs", "no" );
//				}
				
				
				
				
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
				
				try {
					restartApplication (null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
//				dispose ();
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
	
	protected void choosePythonInterper() 
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
		    logger.debug(chooser.getSelectedFile());
		    String linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    logger.debug(linuxpath);
		    tfldpythonptah.setText(linuxpath);
		}
	}

	//	https://dzone.com/articles/programmatically-restart-java
	/** 
	 * Sun property pointing the main class and its arguments. 
	 * Might not be defined on non Hotspot VM implementations.
	 */
	public static final String SUN_JAVA_COMMAND = "sun.java.command";

	/**
	 * Restart the current Java application
	 * @param runBeforeRestart some custom code to be run before restarting
	 * @throws IOException
	 */
	public static void restartApplication(Runnable runBeforeRestart) throws IOException 
	{
		try {
				// java binary
				String java = System.getProperty("java.home") + "/bin/java";
				// vm arguments
				List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
				StringBuffer vmArgsOneLine = new StringBuffer();
				for (String arg : vmArguments) {
					// if it's the agent argument : we ignore it otherwise the
					// address of the old application and the new one will be in conflict
					if (!arg.contains("-agentlib")) {
						vmArgsOneLine.append(arg);
						vmArgsOneLine.append(" ");
					}
				}
			// init the command to execute, add the vm args
			final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);
		
			// program main and program arguments
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
			// program main is a jar
			if (mainCommand[0].endsWith(".jar")) {
				// if it's a jar, add -jar mainJar
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else {
				// else it's a .class, add the classpath and mainClass
				cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
			// finally add program arguments
			for (int i = 1; i < mainCommand.length; i++) {
				cmd.append(" ");
				cmd.append(mainCommand[i]);
			}
			// execute the command in a shutdown hook, to be sure that all the
			// resources have been disposed before restarting the application
			Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			// execute some custom code before restarting
			if (runBeforeRestart!= null) {
				runBeforeRestart.run();
			}
			// exit
			System.exit(0);
		} catch (Exception e) {
		// something went wrong
			throw new IOException("Error while trying to restart the application", e);
		}
	}

	protected void setBanKuaiParseFileInstalledPath()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
		    logger.debug(chooser.getSelectedFile());
		    String linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    logger.debug(linuxpath);
		    tfldzdyfilepath.setText(linuxpath);
		}
		
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
		
		if(   ((DatabaseSourceTableModel)tablelocal.getModel()).getCurSelectedDbs().trim().isEmpty() ) {
			txtareacheckresult.append("必须为基本信息选择一个数据库连接源！" + "\n");
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
//	private JTable table;
	private JTable tablelocal;
	private JButton btnEditDb;
	private JButton btmaddnewdb;
	private JButton btndeletedbs;
	private JButton btnChosTDXDict;
	private JScrollPane scrollPanelocal;
	private JButton okButton;
	private JTextArea txtareacheckresult;
	private JTextField tfldzdyfilepath;
	private JButton btnzdyselect;
	private JTextField tfldzhanbizhouqi;
	private JCheckBox cbxprivatemode;
	private JTextField tfldpythonptah;
	private JButton btnchoosepython;
	private JCheckBox ckbxzdy;
	private JButton saveButton;
	private JTextField tfldzdyguanzhu;
	private JLabel lblblocknewcfg;
	private void initializeGui() 
	{
		saveButton = new JButton ();
		setTitle("\u7CFB\u7EDF\u8BBE\u7F6E");
		
		setBounds(100, 100, 603, 854);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel label_1 = new JLabel("");
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
						.addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label_1)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap(16, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 419, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))))
		);
		
		JPanel syspnl = new JPanel();
		tabbedPane.addTab("\u7CFB\u7EDF\u4FE1\u606F", null, syspnl, null);
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
		
		cbxprivatemode = new JCheckBox("\u9690\u79C1\u6A21\u5F0F");
		syspnl.setLayout(new MigLayout("", "[73px][3px][8px][4px][289px][6px][57px]", "[42px][38px][23px]"));
		syspnl.add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		syspnl.add(tfldSysInstallPath, "cell 2 0 5 1,grow");
		syspnl.add(lblNewLabel_1, "cell 0 1 3 1,alignx left,aligny center");
		syspnl.add(tfldTDXInstalledPath, "cell 4 1,grow");
		syspnl.add(btnChosTDXDict, "cell 6 1,alignx left,aligny bottom");
		syspnl.add(cbxprivatemode, "cell 0 2,alignx left,aligny top");
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("\u6570\u636E\u5E93\u8BBE\u7F6E", null, panel_1, null);
		
		scrollPanelocal = new JScrollPane();
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
		
		btnEditDb = new JButton("");
		btnEditDb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/edit_23.851162790698px_1200630_easyicon.net.png")));
		
		btmaddnewdb = new JButton("");
		btmaddnewdb.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/add_24px_1181422_easyicon.net.png")));
		
		btndeletedbs = new JButton("");
		btndeletedbs.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/minus_red.png")));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPanelocal, GroupLayout.PREFERRED_SIZE, 494, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(btnEditDb)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btmaddnewdb)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btndeletedbs, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPanelocal, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnEditDb)
						.addComponent(btmaddnewdb)
						.addComponent(btndeletedbs))
					.addContainerGap(85, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JPanel zdybkpnl = new JPanel();
		tabbedPane.addTab("\u81EA\u5B9A\u4E49\u677F\u5757\u8BBE\u7F6E", null, zdybkpnl, null);
		
		ckbxzdy = new JCheckBox("\u81EA\u5B9A\u4E49\u677F\u5757\u8DEF\u5F84");
		
		ckbxzdy.setSelected(true);
		
		tfldzdyfilepath = new JTextField();
		tfldzdyfilepath.setText("\\\\jeffauas\\\\blocknew");
		tfldzdyfilepath.setEditable(false);
		tfldzdyfilepath.setColumns(10);
		
		btnzdyselect = new JButton("");
		
		btnzdyselect.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/open24.png")));
		
		JLabel lblNewLabel_3 = new JLabel("\u81EA\u5B9A\u4E49\u5173\u6CE8\u677F\u5757\u540D\u79F0");
		
		tfldzdyguanzhu = new JTextField();
		tfldzdyguanzhu.setText("\u6A21\u578B\u9A8C\u8BC1");
		tfldzdyguanzhu.setColumns(10);
		zdybkpnl.setLayout(new MigLayout("", "[109px][3px][77px][4px][292px][57px]", "[28px][2px][15px][29px]"));
		
		lblblocknewcfg = new JLabel("\uFF08\u6307\u5B9A\u5230blocknew.cfg\u4E0A\u4E00\u7EA7\uFF09");
		zdybkpnl.add(lblblocknewcfg, "cell 0 2 3 1,alignx right,aligny top");
		zdybkpnl.add(ckbxzdy, "cell 0 0,alignx left,aligny bottom");
		zdybkpnl.add(tfldzdyfilepath, "cell 2 0 3 2,growx,aligny center");
		zdybkpnl.add(lblNewLabel_3, "cell 0 3,alignx left,aligny center");
		zdybkpnl.add(tfldzdyguanzhu, "cell 2 3 3 1,grow");
		zdybkpnl.add(btnzdyselect, "cell 5 0 1 3,alignx left,aligny top");
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("\u5916\u90E8\u89E3\u91CA\u5668", null, panel, null);
		
		JLabel lblPythonInterpreter = new JLabel(" Python Interpreter\u8DEF\u5F84");
		
		tfldpythonptah = new JTextField();
		tfldpythonptah.setEnabled(false);
		tfldpythonptah.setEditable(false);
		tfldpythonptah.setColumns(10);
		
		btnchoosepython = new JButton("");
		
		btnchoosepython.setIcon(new ImageIcon(SystemSetting.class.getResource("/images/open24.png")));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPythonInterpreter)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfldpythonptah, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnchoosepython)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(32)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnchoosepython)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblPythonInterpreter)
							.addComponent(tfldpythonptah, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(160, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("\u677F\u5757\u5206\u6790\u8BBE\u7F6E", null, panel_2, null);
		
		JLabel label_2 = new JLabel("\u6210\u4EA4\u5360\u6BD4\u5206\u6790\u5468\u671F\u8DE8\u5EA6(\u6708)");
		
		tfldzhanbizhouqi = new JTextField();
		tfldzhanbizhouqi.setText("8");
		tfldzhanbizhouqi.setColumns(10);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(label_2)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(tfldzhanbizhouqi, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(291, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(tfldzhanbizhouqi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(182, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
//		DatabaseSourceTableModel rmttablemode = new DatabaseSourceTableModel( null );
		
		txtareacheckresult = new JTextArea();
		txtareacheckresult.setEditable(false);
		scrollPane_1.setViewportView(txtareacheckresult);
		
		
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
//            		 logger.debug("cur dbs = " + dbname);
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


