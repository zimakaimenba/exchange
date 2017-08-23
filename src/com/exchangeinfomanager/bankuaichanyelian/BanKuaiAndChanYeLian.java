package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import net.ginkgo.dom4jcopy.SubnodeButton;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

public class BanKuaiAndChanYeLian extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param stockInfoManager 
	 * @param bkdbopt2 
	 * @param zdgzbkxmlhandler 
	 * @param cylxmlhandler 
	 */
	public BanKuaiAndChanYeLian (StockInfoManager stockInfoManager2) 
	{
		this.stockInfoManager = stockInfoManager2;
		
		this.bkdbopt = new BanKuaiDbOperation ();
		this.cylxmhandler = new ChanYeLianXMLHandler2 ();
		this.zdgzbkxmlhandler = new TwelveZhongDianGuanZhuXmlHandler ();
		
		initializeGui ();
		initializeSysConfig ();
		zdgzbkmap = zdgzbkxmlhandler.getZdgzBanKuaiFromXmlAndUpatedToCylTree(treechanyelian);
		initializeAllDaLeiZdgzTableFromXml ();
		initializeBanKuaiParsedFile ();
		
		createEvents ();
	}

	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private SystemConfigration sysconfig;
	HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap;
	private ChanYeLianXMLHandler2 cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    private BkChanYeLianTree treechanyelian;
    private BanKuaiDbOperation bkdbopt;
	private StockInfoManager stockInfoManager;
	private boolean cylneedsave; //标记产业链树有更改
	private boolean zdgzxmlneedsave; //标记重点关注有更改

    
    boolean editingNodeText = false;
	private String currentselectedtdxbk = "";

	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}

	/*
	 * 
	 */
	private void initializeAllDaLeiZdgzTableFromXml ()
	{
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
				
		tableCurZdgzbk.setRowSelectionInterval(0,0);
		int row = tableCurZdgzbk.getSelectedRow();
		String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
		
		unifyDisplaysInDifferentCompOnGui (selecteddalei,0);
		
	}
//	
//	/*
//	 * 参数为操作后光标位置
//	 */
//	private void initializeSingleDaLeiZdgzTableFromXml (int row )
//	{
//		String selectedalei = getCurSelectedDaLei ();
//		if( selectedalei != null) {
//			((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selectedalei);
//			((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
//		}
//		
//		if(((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getRowCount() != 0)
//			tableZdgzBankDetails.setRowSelectionInterval(row,row);
//	}
	private String getCurSelectedDaLei ()
    {
    	int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			return null;
		} 
		
		 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
		 tableCurZdgzbk.setRowSelectionInterval(row, row);
		 return  selecteddalei;
    }
	/*
	 * 统一界面上各个部件鼠标点击后的动作，保住一致。
	 */
	private void unifyDisplaysInDifferentCompOnGui (String selecteddalei,int selectrowfordaleiinsubcyl) 
	{
		//for 重点关注
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
		int selecteddaleirow = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiIndex (selecteddalei);
		tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
		
		
		//for sub重点关注
		((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selecteddalei);
		tableZdgzBankDetails.setRowSelectionInterval(selectrowfordaleiinsubcyl,selectrowfordaleiinsubcyl);
		BkChanYeLianTreeNode curselectnode = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(selectrowfordaleiinsubcyl);
		if(curselectnode != null) {
//			currentselectedtdxbk = curselectnode.getTdxBk();
			
			((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
			tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
		}
		System.out.println("current node is  " + currentselectedtdxbk );

		//for tree
		TreePath nodepath = new TreePath(curselectnode.getPath());
		treechanyelian.expandTreePathAllNode( new TreePath(curselectnode.getPath()) );
	   		
	    //for 个股Talble
		getReleatedInfoAndActionsForTreePathNode (new TreePath(curselectnode.getPath()) );
	}


	
	/*
	 * 加子节点
	 */
	  private void addSubnodeButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	  {
	        SubnodeButton button = (SubnodeButton) addSubnodeButton;
	        String key;
	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
	        else key = "CTRL";
	        int x = evt.getX();
	        //System.out.print("x=" + x + " ");
	        int y = evt.getY();
	        //System.out.print(y);
	        //System.out.print("y=" + y + " ");
	        if (y<19 && x+y<30 && x<19) {
	            button.setDirection(BanKuaiAndChanYeLian.UP);
	            button.setIcon(addAboveIcon);
	            button.setToolTipText("Add above ("+key+"-UP)");
	        }
	        else if (y>=19 && x-y < 0 && x<19){
	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
	            button.setIcon(addBelowIcon);
	            button.setToolTipText("Add below ("+key+"-DOWN)");
	        }
	        else if (x+y>30 && x-y>0){
	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
	            button.setIcon(addChildIcon);
	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	        }
	        else {
	            button.setDirection(BanKuaiAndChanYeLian.NONE);
	            button.setIcon(addSubnodeIcon);
	            button.setToolTipText("Add subnode");
	        }
	   }
	
	  private void addSubnodeButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
	  {
	        addSubnodeButton.setIcon(addSubnodeIcon);
	        addSubnodeButton.setToolTipText("Add subnode");
	  }
	  
	  private void addSubnodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
	  {
		  cylneedsave = true;
			 
			 int direction = ((SubnodeButton)addSubnodeButton).getDirection();
			 int row = tablesubcyl.getSelectedRow() ;
			 if( row <0) {
				 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
				 return;
			 }
			 
			 String subcode = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianCode(row);
			 String subname = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianName(row);

			 treechanyelian.addNewNode (BkChanYeLianTreeNode.SUBBK,subcode,subname,direction);
			 
		}//GEN-LAST:event_addSubnodeButtonActionPerformed

		    
	  /*
	     * 鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 和通信性板块的相关个股
	     */
	    private void chanYeLianTreeMousePressed(java.awt.event.MouseEvent evt) //GEN-FIRST:event_treeMousePressed 
	    {
	        TreePath closestPath = treechanyelian.getClosestPathForLocation(evt.getX(), evt.getY());

	        if(closestPath != null) {
	            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
	            int maxY = (int) pathBounds.getMaxY();
	            int minX = (int) pathBounds.getMinX();
	            int maxX = (int) pathBounds.getMaxX();
	            if (evt.getY() > maxY) treechanyelian.clearSelection();
	            else if (evt.getX() < minX || evt.getX() > maxX) treechanyelian.clearSelection();
	        }
	        getReleatedInfoAndActionsForTreePathNode ( closestPath);
	               
	    }//GEN-LAST:event_treeMousePressed
	    
	    /*
	     * 和选择板块相关的子产业链，个股 
	     */
	    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
	    {
	    	 BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
	    	 String tdxbk = bknode.getUserObject().toString(); 
	    	 String tdxbkcode = bknode.getTongDaXingBanKuaiCode();
	    	 HashSet<String> stockinparsefile = bknode.getParseFileStockSet ();
	         if(!tdxbk.equals(currentselectedtdxbk)) { //和当前的板块不一样，
	        	 
	  	       	//鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 
	  	       	HashMap<String, String> tmpsubbk = bkdbopt.getSubBanKuai (tdxbkcode);
	  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk);
//	  	       	Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
//	  	   		Collections.sort(tmpsubbk,collator);
	  	       
	  	       	//读出该板块所有的个股
	  	       	HashMap<String,String> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbk);
	  	      
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).deleteAllRows();
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(tmpallbkge,stockinparsefile);
	  	       	
	  	       	int row = tableCurZdgzbk.getSelectedRow();
	  	       	((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
	  	       	if(row >= 0)
	  	       		tableCurZdgzbk.setRowSelectionInterval(row,row);
	  	       	else
	  	       		tableCurZdgzbk.setRowSelectionInterval(0,0);
	  	       	
	  	       	currentselectedtdxbk = tdxbk;
	         }
	         
	       //读出该板块相关的新闻
  	       	 BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
  	       	 String curselectedbknodecode = curselectedbknode.getNodeOwnCode();
  	       	 ArrayList<ChanYeLianNews> curnewlist = bkdbopt.getBanKuaiRelatedNews (curselectedbknodecode);
  	       	 createChanYeLianNewsHtml (curselectedbknodecode,curnewlist);
	    }
	    
	    /*
	     * 显示板块新闻连接
	     */
	    private void createChanYeLianNewsHtml(String curselectedbknodecode, ArrayList<ChanYeLianNews> curnewlist)
	    {
	    	String htmlstring = "";
	    	htmlstring  += "<h3>板块"+ curselectedbknodecode + "相关新闻</h3>";
	    	for(ChanYeLianNews cylnew : curnewlist ) {
	    		String title = cylnew.getNewsTitle();
	    		String newdate = sysconfig.formatDate(cylnew.getGenerateDate() ).substring(0,11); 
	    		String slackurl = cylnew.getNewsSlackUrl();
	    		String keywords = cylnew.getKeyWords ();
	    		if(slackurl != null && !slackurl.isEmpty() )	    		
	    			htmlstring  += "<p>" + newdate + "<a href=\" " +   slackurl + "\"> " + title + "</a></p> ";
	    		else
	    			htmlstring  += "<p>" + newdate  + title + "</p> ";
	    		//notesPane.setText("<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>");
	    	}
	    	notesPane.setText(htmlstring);
	    	notesPane.setCaretPosition(0);
	    	
			
		}

		private void addGeGuButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	    {
	  	        SubnodeButton button = (SubnodeButton) addGeGuButton;
	  	        String key;
	  	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
	  	        else key = "CTRL";
	  	        int x = evt.getX();
	  	        //System.out.print("x=" + x + " ");
	  	        int y = evt.getY();
	  	        //System.out.print(y);
	  	        //System.out.print("y=" + y + " ");
	  	        if (y<19 && x+y<30 && x<19) {
	  	            button.setDirection(BanKuaiAndChanYeLian.UP);
	  	            button.setIcon(addAboveIcon);
	  	            button.setToolTipText("Add above ("+key+"-UP)");
	  	        }
	  	        else if (y>=19 && x-y < 0 && x<19){
	  	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
	  	            button.setIcon(addBelowIcon);
	  	            button.setToolTipText("Add below ("+key+"-DOWN)");
	  	        }
	  	        else if (x+y>30 && x-y>0){
	  	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
	  	            button.setIcon(addChildIcon);
	  	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	  	        }
	  	        else {
	  	            button.setDirection(BanKuaiAndChanYeLian.NONE);
	  	            button.setIcon(addSubnodeIcon);
	  	            button.setToolTipText("Add subnode");
	  	        }
	  	    }
	    
	    
	    private void addGeGuButtonMouseExited(java.awt.event.MouseEvent evt)  
	    {
	    	addGeGuButton.setIcon(addSubnodeIcon);
	    	addGeGuButton.setToolTipText("添加个股");
	    }
	    
	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt) 
	    {
	    	cylneedsave = true;
	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
	    }
	    
	    public void addGeGunode(int direction)
	    {
	   	int row = tablebkgegu.getSelectedRow();
	  		if(row <0) {
	  			JOptionPane.showMessageDialog(null,"请选择一个股票!","Warning",JOptionPane.WARNING_MESSAGE);
	  			return;
	  		}
	  		
	  		String gegucode = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCode(row);
	  		String geguname = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockName(row);
	  		
	  		 treechanyelian.addNewNode (BkChanYeLianTreeNode.BKGEGU,gegucode,geguname,direction);
	     }

	    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
			  if(treechanyelian.deleteNodes () )
				  cylneedsave = true;
		        
		    }//GEN-LAST:event_deleteButtonActionPerformed

	   
		   /*
		    * 产业链树上定位用户选择的板块，只选择到板块一级，子板块不找
		    */
		   protected TreePath findBanKuaiInTree(String bkinputed) 
		   {
		    	@SuppressWarnings("unchecked")
		    	TreePath bkpath = treechanyelian.locateNodeByNameOrHypyOrBkCode (bkinputed);
		    	getReleatedInfoAndActionsForTreePathNode (bkpath); //显示和板块相关的子产业链和个股
		    	
			    return bkpath;
			}
		    public boolean isXmlEdited ()
		    {
		    	return cylneedsave ;
		    }
		    
		    private void initializeBanKuaiParsedFile() 
			{
				String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
				if(parsedpath == null || parsedpath == "")
					return;
				
				Date date=new Date();//取时间
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
				date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String dateString = formatter.format(date);
				System.out.println(dateString);
				
				String parsefilename = parsedpath + dateString + ".ebk";
				tfldparsefilename.setText(parsefilename);
				
				parseSelectedBanKuaiFile (parsefilename);
				
			}
		    
		    private void parseSelectedBanKuaiFile (String filename)
		    {
		    	File parsefile = new File(filename);
		    	if(!parsefile.exists() )
		    		return;
		    	
				List<String> readparsefileLines = null;
				try {
					readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				HashSet<String> stockinfile = new HashSet<String> (readparsefileLines);
				
				treechanyelian.updateTreeParseFileInfo(stockinfile);
				
				((BanKuaiGeGuTableModel)tablebkgegu.getModel()).deleteAllRows(); //个股列表删除光
				
				((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
		    	
		    }

		    /*
		     * 选择要分析的板块文件
		     */
		    private void selectBanKuaiParseFile ()
			{
		    	String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setCurrentDirectory(new File(parsedpath) );
				
				if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				    
				    String linuxpath;
				    if(chooser.getSelectedFile().isDirectory())
				    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
				    else
				    	linuxpath = (chooser.getSelectedFile()).toString().replace('\\', '/');
				    
				    System.out.println(linuxpath);
				    tfldparsefilename.setText(linuxpath);
				    
				    parseSelectedBanKuaiFile (linuxpath);
				    
//				    File recordspath = chooser.getSelectedFile();
//				    File[] filesList = recordspath.listFiles();
//			        for(File f : filesList){
//
//			            if(f.isFile() && f.getName().endsWith("txt")){
//			                ((DefaultListModel)listLeft.getModel()).addElement(f.getName());
//			            }
//			        }
//				    FileInputStream xmlfileinput = null;
//					try {
//						xmlfileinput = new FileInputStream(linuxpath);
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}
					
					
				}
				
			}   


		    private void notesPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesPaneFocusLost
		        if(editingNodeText){
		            notesNode.setNoteText(notesPane.getText());
		            editingNodeText = false;
		            notesPane.setEditable(false);
		        }
		    }//GEN-LAST:event_notesPaneFocusLost

		    private void notesPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesPaneFocusGained
		        
		        if (treechanyelian.getSelectionCount()==1 && notesNode != null){
		            treechanyelian.stopEditing();
		            editingNodeText = true;
		            notesPane.setEditable(true);
		            notesPane.getCaret().setVisible(true);
		        } else {
		            treeScrollPane.requestFocusInWindow();
		            editingNodeText = false;
		            notesPane.setEditable(false);
		        }
		    }//GEN-LAST:event_notesPaneFocusGained

		    /*
		     * 保存2个XML 
		     */
	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
	{
		if(cylneedsave == true) {
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
			if(!cylxmhandler.saveTreeToChanYeLianXML(treeroot) )
				JOptionPane.showMessageDialog(null, "保存产业链XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
			
		}
		 
		if(zdgzxmlneedsave == true) {
			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () )
				JOptionPane.showMessageDialog(null, "保存重点关注股票池XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
		}

		return true;
	}	    
	private void createEvents() 
	{
		notesPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if(Desktop.isDesktopSupported()) {
					    try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
			        }
			}
		});
		
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addChanYeLianNews ();
			}
			
		});
		
		btnopencylxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cylxmhandler.openChanYeLianXmlInWinSystem ();
			}
		});
		
		btnopenzdgzxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				zdgzbkxmlhandler.openZdgzXmlInWinSystem ();
			}
		});
		
		btnSaveAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				saveCylXmlAndZdgzXml ();
			}
		});
		
		btnGenTDXCode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				TDXFormatedOpt.parseZdgzBkToTDXCode(zdgzbkxmlhandler.getZdgzBkDetail ());
			}
		});
		
		btndeldalei.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				deleteDaLeiGuPiaoChi ();
			}
		});
		
		btnadddalei.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) 
			{
				String newdaleiname = JOptionPane.showInputDialog(null,"请输入新的股票池名称:","添加股票池", JOptionPane.QUESTION_MESSAGE);
				if( !newdaleiname.isEmpty() && !zdgzbkmap.keySet().contains(newdaleiname)) {
					ArrayList<BkChanYeLianTreeNode> tmpgzbklist = new ArrayList<BkChanYeLianTreeNode> (); 
					zdgzbkmap.put(newdaleiname, tmpgzbklist);
					
					unifyDisplaysInDifferentCompOnGui (newdaleiname,0);
					
					zdgzxmlneedsave = true;
				} else
					JOptionPane.showMessageDialog(null,"股票池名称已经存在！","Warning",JOptionPane.WARNING_MESSAGE);
			}
		});

		
				btnChsParseFile.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						selectBanKuaiParseFile ();
						
					}
				});
				
				tablebkgegu.addMouseListener(new TableMouseListener(tablebkgegu));
				tablebkgegu.addMouseListener(new MouseAdapter() {
		        	@Override
		        	public void mouseClicked(MouseEvent arg0) 
		        	{
		        		 if (arg0.getClickCount() == 2) {
							 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
							 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
							 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
							 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
							 
							 
							 int row = tablebkgegu.getSelectedRow();
							 //int column = tblSearchResult.getSelectedColumn();
							 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
							 String stockcode = tablebkgegu.getModel().getValueAt(model_row, 0).toString().trim();
							 System.out.println(stockcode);
							 stockInfoManager.getcBxstockcode().setSelectedItem(stockcode);
							 stockInfoManager.preUpdateSearchResultToGui(stockcode);
						 }
		        	}
		        });
				
				tableZdgzBankDetails.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						int row = tableZdgzBankDetails.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						int rowInZdgz = tableCurZdgzbk.getSelectedRow();
						String zdgzdalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(rowInZdgz);
						
						BkChanYeLianTreeNode selectedgzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
						
						unifyDisplaysInDifferentCompOnGui (zdgzdalei,row);
					}


				});
				
				btnCylRemoveFromZdgz.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						removeTreeChanYeLianNodeFromZdgz (arg0);
					}
				});

				
			
				btnCylAddToZdgz.addMouseListener(new MouseAdapter() {
					 @Override
					public void mouseClicked(MouseEvent arg0) 
					{
						 adddTreeChanYeLianNodeToZdgz ( arg0);
		
					}
				});

				
				
				buttonremoveoffical.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						removeChanYeLianFromOffical ();
					}
				});
				
				
				buttonaddofficial.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						addChanYeLianToOffical ();
					}
					

				});
				
				tableCurZdgzbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						//initializeSingleDaLeiZdgzTableFromXml (0);
						String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
						ArrayList<BkChanYeLianTreeNode> selectedgzbklist = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiChanYeLianList(selecteddalei);
						BkChanYeLianTreeNode selectedgzbk = null ;
						try {
							 selectedgzbk = selectedgzbklist.get(0);
						} catch ( java.lang.IndexOutOfBoundsException e) {
							
						}
						
						unifyDisplaysInDifferentCompOnGui(selecteddalei,0);
						

					}

//					private void unifyDisplaysInDifferentCompOnGui(BkChanYeLianTreeNode selectedgzbk,int row) 
//					{
//						treechanyelian.expandTreePathAllNode( new TreePath(selectedgzbk.getPath()) );
//						
//						//相关table高选对应的产业链
//						currentselectedtdxbk = selectedgzbk.getUserObject().toString();
//						
//						((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//						tableCurZdgzbk.setRowSelectionInterval(row,row);
//						
//						String tdxbk = selectedgzbk.getTdxBk();
//						findBanKuaiInTree(tdxbk);
//						
//					}
				});
				
				tfldfindgegu.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						tfldfindgegu.setText("");
					}
				});
				
				tfldfindgegu.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						String bkinputed = tfldfindgegu.getText();
						int rowindex = ((BanKuaiGeGuTableModel)tablebkgegu.getModel()).getStockRowIndex(bkinputed);
						tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
						tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
						
					}
				});
				
				tfldfindbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						tfldfindbk.setText("");
					}
				});
				
				tfldfindbk.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						String bkinputed = tfldfindbk.getText();
						findBanKuaiInTree (bkinputed);
					}
				});
				
				btnfindbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						String bkinputed = tfldfindbk.getText();
						findBanKuaiInTree (bkinputed);
					}
				});
				
				addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) 
					{
						 int id = e.getID();
						 String keyString;
					        if (id == KeyEvent.KEY_TYPED) {
					            char c = e.getKeyChar();
					            keyString = "key character = '" + c + "'";
					            System.out.println(keyString);
					        } else {
					        	int keyCode = e.getKeyCode();
					            keyString = "key code = " + keyCode
					                    + " ("
					                    + KeyEvent.getKeyText(keyCode)
					                    + ")";
					        }
					        
					}
				});
				

		        treechanyelian.addMouseListener(new java.awt.event.MouseAdapter() {
		            public void mousePressed(java.awt.event.MouseEvent evt) {
		            	chanYeLianTreeMousePressed(evt);
		            }
		        });

		        
				btnAddSubBk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						String newsubbk = JOptionPane.showInputDialog(null,"请输入子板块名称:","增加子版块", JOptionPane.QUESTION_MESSAGE);
						if(newsubbk == null)
							return;
						
//						if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
//							JOptionPane.showMessageDialog(null,"输入子版块名称与通达信板块名称冲突,请重新输入!");
//							return ;
//						}
						
						TreePath closestPath = treechanyelian.getSelectionPath();
//				        System.out.println(closestPath);
				         BkChanYeLianTreeNode tdxbk = (BkChanYeLianTreeNode)closestPath.getPathComponent(1);
				         String tdxbkcode = tdxbk.getTongDaXingBanKuaiCode();
				        
						String newsubcylcode = bkdbopt.addNewSubBanKuai (tdxbkcode,newsubbk.trim() ); 
						if(newsubcylcode != null)
							((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode,newsubbk);
						else
							JOptionPane.showMessageDialog(null,"添加失败，可能是因为重名！","Warning",JOptionPane.WARNING_MESSAGE);
//			  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).fireTableDataChanged ();
						
					}
				});
				


				addGeGuButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			            public void mouseMoved(java.awt.event.MouseEvent evt) {
			            	addGeGuButtonMouseMoved(evt);
			            }
			        });

				addGeGuButton.addMouseListener(new java.awt.event.MouseAdapter() {
			            public void mouseExited(java.awt.event.MouseEvent evt) {
			            	addGeGuButtonMouseExited(evt);
			            }
			        });
				addGeGuButton.addActionListener(new java.awt.event.ActionListener() {
			            public void actionPerformed(java.awt.event.ActionEvent evt) {
			                addGeGuButtonActionPerformed(evt);
			            }
			        });
			

				
		        addSubnodeButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
		            public void mouseMoved(java.awt.event.MouseEvent evt) {
		                addSubnodeButtonMouseMoved(evt);
		            }
		        });
		        addSubnodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
		            public void mouseExited(java.awt.event.MouseEvent evt) {
		                addSubnodeButtonMouseExited(evt);
		            }
		        });
		        addSubnodeButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                addSubnodeButtonActionPerformed(evt);
		            }
		        });
		        

		        
		        deleteButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                deleteButtonActionPerformed(evt);
		            }
		        });
			}
	    
	
	protected void addGeGuNews() 
	{
		int row = tablebkgegu.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String stockcode = ((BanKuaiGeGuTableModel) tablebkgegu.getModel()).getStockCode (row);
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加个股新闻", JOptionPane.OK_CANCEL_OPTION);
		System.out.print(exchangeresult);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
	}

	protected void addChanYeLianNews() 
	{
		try {
			TreePath closestPath = treechanyelian.getSelectionPath();
			BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
			String selectnodecode = selectednode.getNodeOwnCode();
			ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (selectnodecode);
			int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加产业链新闻", JOptionPane.OK_CANCEL_OPTION);
			System.out.print(exchangeresult);
			if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		} catch (java.lang.NullPointerException ex) {
			JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
		}
		
		
		
	}

	private void deleteDaLeiGuPiaoChi () 
	{
		int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票池","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String daleiname = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(row);
		int n = JOptionPane.showConfirmDialog(null, "确定删除" + daleiname + "?", "删除股票池",JOptionPane.YES_NO_OPTION);//i=0/1  
		if(n == 0) {
			ArrayList<BkChanYeLianTreeNode> daleicyl = zdgzbkxmlhandler.getASubDaiLeiDetail(daleiname);
			for(BkChanYeLianTreeNode tmpgzbkinfo : daleicyl ) {
				 treechanyelian.removeZdgzBkCylInfoFromTreeNode (tmpgzbkinfo,false);
			}
			
			zdgzbkxmlhandler.deleteDaLei(daleiname);
			
			String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (0);
			unifyDisplaysInDifferentCompOnGui (selecteddalei,0);
//			initializeAllDaLeiZdgzTableFromXml (null);
//			initializeSingleDaLeiZdgzTableFromXml (0);
			
			zdgzxmlneedsave = true;
		}
		
	}
	
    
	private void removeTreeChanYeLianNodeFromZdgz(MouseEvent arg0) 
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个板块产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}

		String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			  BkChanYeLianTreeNode gzcyl = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 
			 treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzcyl,false);
			 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzcyl);
			 unifyDisplaysInDifferentCompOnGui (daleiname,0);
			 
			 zdgzxmlneedsave = true;
			 
		 }
		 else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		
	}

	private void addChanYeLianToOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 gzbk.setOfficallySelected(true);
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(gzbk,true);
			 unifyDisplaysInDifferentCompOnGui (selectedalei,row);

			 zdgzxmlneedsave = true;
		} else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
	}
	private void removeChanYeLianFromOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 if(!gzbk.isOfficallySelected()) {
				 JOptionPane.showMessageDialog(null,"选择的产业链只是候补关注，不在当前重点关注股票池中,不用移出！","Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }
				 
			 if(JOptionPane.showConfirmDialog(null, "是否直接从候补产业链中删除？","Warning", JOptionPane.YES_NO_OPTION) == 0) {
					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
					unifyDisplaysInDifferentCompOnGui (selectedalei,0);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,false);
				} else {
					unifyDisplaysInDifferentCompOnGui (selectedalei,row);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,true);
				}

			 zdgzxmlneedsave = true;
			 
		} else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		
	}
		
	
	private void adddTreeChanYeLianNodeToZdgz(MouseEvent arg0) 
	{
		 String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			 TreePath path = treechanyelian.getSelectionPath();
			 BkChanYeLianTreeNode nodewilladded = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();

			 boolean isofficallyselected = false;
			 if(JOptionPane.showConfirmDialog(null, "是否直接加入重点关注？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
				 nodewilladded.setOfficallySelected(false);
			 } else 
				 nodewilladded.setOfficallySelected(true);
			 
			 nodewilladded.setSelectedtime( formatDate(new Date() ) );
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(nodewilladded,false);
			 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, nodewilladded);
			 int addedrow = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfoIndex(nodewilladded);
			 unifyDisplaysInDifferentCompOnGui (daleiname,addedrow);
			
			 zdgzxmlneedsave = true;
		 }
		 else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		 
	}
	
	private String formatDate(Date tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
		//return formatterhwy;
	}


	private JTextField tfldparsefilename;
	private JTable tableCurZdgzbk;
	private JTable tableZdgzBankDetails;
	private JTable tablebkgegu;
	private JTextField tfldfindbk;
	private JTextField tfldfindgegu;
	private JButton btnCylAddToZdgz;
	private JButton btnCylRemoveFromZdgz;
	private JButton deleteButton;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JButton btnAddSubBk;
	private JButton buttonaddofficial;
	private JButton buttonremoveoffical;
	private JButton btnChsParseFile;
	private JButton addGeGuButton;
	private JButton addSubnodeButton;
	private JButton btnGenTDXCode;
	private JButton btnadddalei;
	private JButton btndeldalei;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	private JScrollPane notesScrollPane;
	private JScrollPane treeScrollPane;
	private JSplitPane jSplitPane;
	private JButton btnSaveAll;
	private JButton btnopenzdgzxml;
	private JButton btnopencylxml;
	private JPopupMenu popupMenu;
	private JMenuItem mntmNewMenuItem;
	private JTable tablesubcyl;
	private JEditorPane notesPane;

	private void initializeGui() 
	{
		JPanel panel = new JPanel();
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 893, GroupLayout.PREFERRED_SIZE)
							.addComponent(panelcyltree, 0, 0, Short.MAX_VALUE))
						.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 912, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addGap(2)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(17))
		);
		
		btnCylAddToZdgz = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		btnCylRemoveFromZdgz = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		addSubnodeButton = new SubnodeButton();
		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian.class.getResource("/images/subnode24.png")));
		
		addGeGuButton = new SubnodeButton();
		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian.class.getResource("/images/subnode24.png")));
		
		btnAddSubBk = new JButton("\u589E\u52A0\u5B50\u677F\u5757");
		
		JScrollPane scrollPanesubbk = new JScrollPane();
		
		scrollPanegegu = new JScrollPane();
		
		jSplitPane = new JSplitPane();
		jSplitPane.setResizeWeight(0.36);
		
		deleteButton = new JButton("\u5220\u9664\u8282\u70B9");
		deleteButton.setIcon(null);
		
		tfldfindbk = new JTextField();
		tfldfindbk.setColumns(10);
		
		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
		
		tfldfindgegu = new JTextField();
		tfldfindgegu.setColumns(10);
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
		
		btnopencylxml = new JButton("\u6253\u5F00XML");
		
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(deleteButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnopencylxml)
									.addGap(64)
									.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnfindbk))
								.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 504, GroupLayout.PREFERRED_SIZE))
							.addGap(10)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelcyltree.createSequentialGroup()
											.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnfindgegu))
										.addComponent(scrollPanegegu, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
										.addComponent(btnAddSubBk)
										.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE))))
							.addGap(122))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnCylAddToZdgz)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnCylRemoveFromZdgz)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panelcyltree.setVerticalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnCylAddToZdgz)
									.addComponent(btnCylRemoveFromZdgz))
								.addComponent(btnAddSubBk))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addGap(4)
									.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE))
								.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addGap(69)
							.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(186)
							.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnopencylxml, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnfindbk))
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnfindgegu)))
					.addGap(12))
		);
		
		BanKuaiSubChanYeLianTableModel subcylmode = new BanKuaiSubChanYeLianTableModel ();
		tablesubcyl = new JTable(subcylmode)
		{
			private static final long serialVersionUID = 1L;
			
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		scrollPanesubbk.setViewportView(tablesubcyl);
		
		treeScrollPane = new JScrollPane();
		jSplitPane.setLeftComponent(treeScrollPane);
		
		treechanyelian = initializeBkChanYeLianXMLTree();
		treeScrollPane.setViewportView(treechanyelian);
		treeScrollPane.grabFocus();
		treeScrollPane.getVerticalScrollBar().setValue(0);
		
		
		notesScrollPane = new JScrollPane();
		jSplitPane.setRightComponent(notesScrollPane);
		
		notesPane = new JEditorPane();
		
		notesPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		notesPane.setEditable(false);
		notesScrollPane.setViewportView(notesPane);
		
		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel();
		tablebkgegu = new  JTable(bkgegumapmdl)
		{
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
					 
			        Component comp = super.prepareRenderer(renderer, row, col);
			        BanKuaiGeGuTableModel tablemodel = (BanKuaiGeGuTableModel)this.getModel(); 
			        HashSet<String> stockinparsefile = tablemodel.getStockInParseFile();
			        Object value = tablemodel.getValueAt(row, col);
			        
			        if (!isRowSelected(row)) {
			        	comp.setBackground(getBackground());
			        	comp.setForeground(getForeground());
			        	int modelRow = convertRowIndexToModel(row);
			        	String stockcode = (String)getModel().getValueAt(modelRow, 0);
						if(stockinparsefile.contains(stockcode)) {
							//comp.setBackground(Color.YELLOW);
							comp.setForeground(Color.BLUE);
						}
			        }
			        
			        return comp;
			    }
				    

			
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		
		//个股table也可以加个股新闻
		JPopupMenu popupMenuGeguNews = new JPopupMenu();
		JMenuItem menuItemAddNews = new JMenuItem("添加个股新闻");
		popupMenuGeguNews.add(menuItemAddNews);
		tablebkgegu.setComponentPopupMenu(popupMenuGeguNews);
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addGeGuNews ();
			}
			
		});
		scrollPanegegu.setViewportView(tablebkgegu);
		panelcyltree.setLayout(gl_panelcyltree);
		
		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		JScrollPane scrollPaneDaLeiDetail = new JScrollPane();
		
		buttonaddofficial = new JButton("->");
		
		buttonremoveoffical = new JButton("<-");
		
		JScrollPane scrollPaneDaLei = new JScrollPane();
		
		btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
				
		btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
		
		
		JSeparator separator_1 = new JSeparator();
		
		btnopenzdgzxml = new JButton("\u6253\u5F00XML");
		
		GroupLayout gl_panelzdgz = new GroupLayout(panelzdgz);
		gl_panelzdgz.setHorizontalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
										.addComponent(buttonremoveoffical)
										.addComponent(buttonaddofficial)))
								.addComponent(btnGenTDXCode))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addComponent(btnadddalei)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btndeldalei)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnopenzdgzxml))
								.addComponent(scrollPaneDaLei, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE)))
						.addComponent(separator_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 906, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelzdgz.setVerticalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGap(89)
							.addComponent(buttonaddofficial)
							.addGap(77)
							.addComponent(buttonremoveoffical))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnadddalei)
								.addComponent(btndeldalei)
								.addComponent(btnGenTDXCode)
								.addComponent(btnopenzdgzxml))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING, false)
								.addComponent(scrollPaneDaLei, 0, 0, Short.MAX_VALUE)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		//ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		//tableZdgzBankDetails = new  JTable(xmlaccountsmodel)
		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel ();
		tableZdgzBankDetails = new  JTable(curzdgzbkmodel)
		{
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		tableZdgzBankDetails.setToolTipText("tableZdgzBankDetails");
		int preferedwidth = 170;
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMinWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(preferedwidth);

		scrollPaneDaLeiDetail.setViewportView(tableZdgzBankDetails);
		
		
		
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
//		tableCurZdgzbk = new  JTable(curzdgzbkmodel) {
		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		tableCurZdgzbk = new  JTable(xmlaccountsmodel) {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				
				Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
				Border inside = new EmptyBorder(0, 1, 0, 1);
				Border highlight = new CompoundBorder(outside, inside);
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        ZdgzBanKuaiDetailXmlTableModel tablemodel = (ZdgzBanKuaiDetailXmlTableModel)this.getModel(); 
		        Object value = tablemodel.getValueAt(row, col);
		        
		        if (!isRowSelected(row)) {
		        	comp.setBackground(this.getBackground()); 
		        	int modelRow = convertRowIndexToModel(row);
		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
						jc.setBorder( highlight );
		        }
		        
		        if (isRowSelected(row)) {
		        	comp.setBackground(this.getSelectionBackground());
		        	
		        	int modelRow = convertRowIndexToModel(row);
		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
						jc.setBorder( highlight );
		        	
		        }
		        
		        return comp;
		    }

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
		tableCurZdgzbk.setToolTipText("tableZdgzBankDetails");
		
		
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMinWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setMinWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(390);
//		tableZdgzBankDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		tableZdgzBankDetails.setPreferredScrollableViewportSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		scrollPaneDaLei.setViewportView(tableCurZdgzbk);
		panelzdgz.setLayout(gl_panelzdgz);
		
		JLabel label = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsefilename = new JTextField();
		tfldparsefilename.setColumns(10);
		
		btnChsParseFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		
		JSeparator separator = new JSeparator();
		
		btnSaveAll = new JButton("\u4FDD\u5B58\u4FEE\u6539");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 565, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnChsParseFile)
							.addGap(33)
							.addComponent(btnSaveAll))
						.addComponent(separator, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChsParseFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSaveAll))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
		
		
		
		java.util.ArrayList<java.awt.Image> imageList = new java.util.ArrayList<java.awt.Image>();
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo16.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo18.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo20.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo24.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo32.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo36.png")).getImage());
        
        //setIconImages(imageList);
        addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
        addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
        addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
        addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));
		

		
	}
	

	private  BkChanYeLianTree initializeBkChanYeLianXMLTree()
	{
        
		BkChanYeLianTreeNode topNode = cylxmhandler.getBkChanYeLianXMLTree();
		
		BkChanYeLianTree tmptreechanyelian = new BkChanYeLianTree(topNode);
		
		popupMenu = new JPopupMenu();
		addPopup(tmptreechanyelian, popupMenu);
		
		mntmNewMenuItem = new JMenuItem("\u6DFB\u52A0\u4EA7\u4E1A\u94FE\u65B0\u95FB");
		
		popupMenu.add(mntmNewMenuItem);
		
		return tmptreechanyelian;
		

	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}


/*
 * 12个大类某个具体大类的关注内容表
 */
class CurZdgzBanKuaiTableModel extends AbstractTableModel 
{
	private HashMap<String,ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	private String cbxDale;

	String[] jtableTitleStrings = {  "板块产业链","创建时间","入选","发现"};
	
	CurZdgzBanKuaiTableModel ()
	{

	}

	public void refresh (HashMap<String,ArrayList<BkChanYeLianTreeNode>> zdgzbkmap2,String cbxDale2) 
	{
		this.gzbkmap =  zdgzbkmap2;
		this.cbxDale = cbxDale2;
		this.fireTableDataChanged();
	}
	public int getGuanZhuBanKuaiInfoIndex (BkChanYeLianTreeNode parent)
	{
		String currentdalei = this.cbxDale;
		ArrayList<BkChanYeLianTreeNode> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		
//		boolean findexsitsamenode = false;
		int findsimilarnode = -1;
		
		//for(BkChanYeLianTreeNode bkcylnode : tmpgzbkinfo) {
		for(int i=0;i<tmpgzbkinfo.size();i++) {
			String cylinmapnode = tmpgzbkinfo.get(i).getChanYeLian().trim();
			String cylinverfiednode = parent.getChanYeLian().trim();

			if(cylinmapnode.equals(cylinverfiednode) ) 
				return i;
			else if(cylinmapnode.contains(cylinverfiednode) )
				findsimilarnode = i;
		}

		return findsimilarnode;
	}
	public BkChanYeLianTreeNode getGuanZhuBanKuaiInfo (int rowindex)
	{
		String currentdalei = this.cbxDale;
		ArrayList<BkChanYeLianTreeNode> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		return tmpgzbkinfo.get(rowindex);
	}

	 public int getRowCount() 
	 {
		 try {
			 String currentdalei = this.cbxDale;  
			 ArrayList<BkChanYeLianTreeNode> tmpgzbklist = gzbkmap.get(currentdalei);
			 return tmpgzbklist.size();
		 } catch (java.lang.NullPointerException e) {
			 return 0;
		 }
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(gzbkmap == null)
	    		return null;
	    	
	    	String currentdalei = this.cbxDale;  
			ArrayList<BkChanYeLianTreeNode> tmpgzbklist = gzbkmap.get(currentdalei);
			if(tmpgzbklist == null)
				return null;
			BkChanYeLianTreeNode tmpgzbk = tmpgzbklist.get(rowIndex);
	    	
	    	Object value = "??";

	    	switch (columnIndex) {
            case 0:
                value = tmpgzbk.getChanYeLian();
                break;
            case 1:
            	try {
            		value = tmpgzbk.getSelectedtime();
            	} catch (java.lang.NullPointerException e) {
            		value = "";
            	}
            	
                break;
            case 2:
                value = new Boolean(tmpgzbk.isOfficallySelected() );
                break;
            case 3:
            	if(tmpgzbk.getParseFileStockSet() != null && tmpgzbk.getParseFileStockSet().size() >0)
            		value = new Boolean(true );
            	else 
            		value = new Boolean(false );
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
			          clazz = Boolean.class;
			          break;
		        case 3:
			          clazz = Boolean.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public void deleteAllRows()
	    {
			this.cbxDale = "";
	    }
//	    public void deleteRow(int row)
//	    {
//	    	String currentdalei = cbxDale.getSelectedItem().toString();  
//			ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
//			tmpgzbklist.remove(row);
//	    }
	    
}


/*
 * 重点关注板块表
 */
class ZdgzBanKuaiDetailXmlTableModel extends AbstractTableModel 
{
	private HashMap<String, ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	
	private String[] jtableTitleStrings = { "股票池", "关注板块","发现"};
	private ArrayList<String> gzdalei;
	private boolean foundstockinparsefile = false;
	
	ZdgzBanKuaiDetailXmlTableModel ()
	{
	}

	public void refresh (HashMap<String,ArrayList<BkChanYeLianTreeNode>> zdgzbkmap) 
	{
		this.gzbkmap =  zdgzbkmap;
		this.gzdalei = new ArrayList<String>(zdgzbkmap.keySet() );
		this.fireTableDataChanged();
	}

	 public int getRowCount() 
	 {
		return gzdalei.size();
	 }
	 
	 public String getZdgzDaLei (int row)
	 {
		 return (String)gzdalei.get(row);
	 }
	 public int getDaLeiIndex (String dalei)
	 {
		 return this.gzdalei.indexOf(dalei);
	 }
	 public ArrayList<BkChanYeLianTreeNode>  getDaLeiChanYeLianList (String dalei)
	 {
		 return gzbkmap.get(dalei) ;
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(gzbkmap == null)
	    		return null;
	    	
	    	
	    	Object value = "??";
	    	
	    	switch (columnIndex) {
            case 0:
                value = gzdalei.get(rowIndex);
                break;
            case 1:
            	String result = "";
            	try {
            		ArrayList<BkChanYeLianTreeNode> zdgzsub = this.getDaLeiChanYeLianList(this.getZdgzDaLei(rowIndex));
            		if(zdgzsub.size() == 0)
            			foundstockinparsefile = false;
            		
            		for(BkChanYeLianTreeNode gznode : zdgzsub) {
            			if(gznode.isOfficallySelected()) {
            				String chanyelian = gznode.getChanYeLian(); 
            				
            				String seltime = "";
            				if(gznode.getSelectedtime() != null)
            					seltime = gznode.getSelectedtime();
            				result = result + chanyelian + "(" + seltime +")" + " | " + " ";
            				
            				if(gznode.getParseFileStockSet() != null && gznode.getParseFileStockSet().size() > 0)
            					foundstockinparsefile = true;
            				else
            					foundstockinparsefile = false;
            			}
            		}

            	 } catch (java.lang.NullPointerException e) {
            		 e.printStackTrace();
            		 
            	 }
            	value = result;
                break;
            case 2:
                value = new Boolean (foundstockinparsefile);
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
			          clazz = Boolean.class;
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

//		public String getCylNodesDaLei(BkChanYeLianTreeNode bknode) 
//		{
//			for(String daleiname : this.gzdalei) {
//				ArrayList<BkChanYeLianTreeNode> zdgzsub = gzbkmap.get(daleiname );
//        		if(zdgzsub.size() == 0)
//        			continue;
//        		
//        		for(BkChanYeLianTreeNode gznode : zdgzsub) {
//        			String cylinmapnode = gznode.getChanYeLian().trim();
//        			String cylinverfiednode = bknode.getChanYeLian().trim();
//        			
//        			if(cylinmapnode.contains(cylinverfiednode) )
//        				return daleiname;
//        			
//        		}
//			}
//			return null;
//		}
	    
}


class BanKuaiGeGuTableModel extends AbstractTableModel 
{
	private HashMap<String,String> bkgegumap; //包含股票代码和股票名称
	String[] jtableTitleStrings = { "股票代码", "股票名称"};
	private ArrayList<String> bkgeguname;
	private HashSet<String> stockcodeinparsefile;
	
	BanKuaiGeGuTableModel ()//HashMap<String,String> bkgegu,HashSet<String> stockcodeinparsefile2)
	{
//		if(bkgegu != null) {
//			this.bkgegumap = bkgegu;
//			this.bkgeguname = new ArrayList<String> (bkgegu.keySet() );
//		}
//		if(stockcodeinparsefile2 != null)
//			this.stockcodeinparsefile = stockcodeinparsefile2;
//		else 
//			this.stockcodeinparsefile = new HashSet<String> ();
	}

	public void refresh  (HashMap<String,String> bkgegu,HashSet<String> stockcodeinparsefile2)
	{
		this.bkgegumap = bkgegu;
		
 		this.stockcodeinparsefile = stockcodeinparsefile2;

 		
 		if(stockcodeinparsefile.size() >0 ) { //优先把parsefile里的个股显示在前面
 			Set<String> bkgegucodelist = bkgegu.keySet() ;
 	 		SetView<String> commonbkcode = Sets.intersection(bkgegucodelist, this.stockcodeinparsefile);
 	 		SetView<String> diffbkcode = Sets.difference(bkgegucodelist, this.stockcodeinparsefile);
 	 		
 	 		ArrayList<String> tmpbkgeguname = new ArrayList<String> (commonbkcode);
 	 		tmpbkgeguname.addAll(diffbkcode);
 	 		
 	 		bkgeguname = tmpbkgeguname;
 		} else
 			bkgeguname = new ArrayList<String> (bkgegu.keySet() );
 		
 		this.fireTableDataChanged();
	}
	public HashSet<String> getStockInParseFile ()
	{
		return this.stockcodeinparsefile;
	}
	 public int getRowCount() 
	 {
		 if(this.bkgegumap == null)
			 return 0;
		 else if(this.bkgegumap.isEmpty()  )
			 return 0;
		 else
			 return this.bkgegumap.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bkgegumap.isEmpty())
	    		return null;
	    	String bkcode = bkgeguname.get(rowIndex);
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkcode;
                break;
            case 1:
            	value = bkgegumap.get( bkcode );
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
	    public String getStockCode (int row) 
	    {
	    	return bkgeguname.get(row) ;
	    }
	    public String getStockName (int row) 
	    {
	    	return  bkgegumap.get( bkgeguname.get(row) );
	    }

	    
	    public void deleteAllRows()
	    {
	    	if(this.bkgegumap == null)
				 return ;
			 else 
				 bkgegumap.clear();
	    	this.fireTableDataChanged();
	    }
	    
	    public int getStockRowIndex (String stockcode) 
	    {
	    	int index = bkgeguname.indexOf(stockcode);
	    	return index;
	    }
	    
}

class BanKuaiSubChanYeLianTableModel extends AbstractTableModel 
{
	private HashMap<String,String> bksubcylmap; //包含代码和名称
	private ArrayList<String> bksubcylcodelist;
	String[] jtableTitleStrings = { "子产业链代码", "子产业链名称"};
	
	
	BanKuaiSubChanYeLianTableModel ()
	{
		
	}

	public void addRow(String newsubcylcode, String newsubbk)
	{
		this.bksubcylmap.put(newsubcylcode, newsubbk);
		this.bksubcylcodelist = new ArrayList<String> (this.bksubcylmap.keySet());
		this.fireTableDataChanged();
	}

	public void refresh  (HashMap<String,String> bksubcylmap2)
	{
		this.bksubcylmap = bksubcylmap2;
		bksubcylcodelist = new ArrayList<String> (this.bksubcylmap.keySet());
		this.fireTableDataChanged();
	}
	
	 public int getRowCount() 
	 {
		 if(this.bksubcylmap == null)
			 return 0;
		 else 
			 return this.bksubcylmap.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bksubcylmap.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bksubcylcodelist.get(rowIndex);
                break;
            case 1:
            	value = bksubcylmap.get( bksubcylcodelist.get(rowIndex) );
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
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getSubChanYeLianCode (int row) 
	    {
	    	return bksubcylcodelist.get(row) ;
	    }
	    public String getSubChanYeLianName (int row) 
	    {
	    	return bksubcylmap.get( bksubcylcodelist.get(row) );
	    } 
	    
	    public void deleteAllRows()
	    {
	    	if(this.bksubcylmap == null)
				 return ;
			 else 
				 bksubcylmap.clear();
	    	
	    	this.fireTableDataChanged();
	    }
}

/*
 * google guava files 类，可以直接处理读出的line
 */
class ParseBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7)
    		stocklists.add(line.substring(1));
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

//It’s a little trick to make a row automatically selected when the user right clicks on the table. Create a handler class for mouse-clicking events as follows:
class TableMouseListener extends MouseAdapter {
    
    private JTable table;
     
    public TableMouseListener(JTable table) {
        this.table = table;
    }
     
    @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint(point);
        table.setRowSelectionInterval(currentRow, currentRow);
    }
}