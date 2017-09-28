package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.SubnodeButton;
import com.exchangeinfomanager.bankuai.gui.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiFengXiBarChartPnl;
import com.exchangeinfomanager.gui.subgui.BanKuaiFengXiPieChartPnl;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.sun.rowset.CachedRowSetImpl;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import com.toedter.calendar.JDateChooser;

public class BanKuaiAndChanYeLian extends JPanel 
{
	private static final long serialVersionUID = 1L;

	public BanKuaiAndChanYeLian (StockInfoManager stockInfoManager2) 
	{
		this.stockInfoManager = stockInfoManager2;
		this.bkdbopt = new BanKuaiDbOperation ();

		this.cylxmhandler = new ChanYeLianXMLHandler ();
		this.zdgzbkxmlhandler = new TwelveZhongDianGuanZhuXmlHandler ();
		treechanyelian = initializeBkChanYeLianXMLTree();
		initializeSysConfig ();
		zdgzbkmap = zdgzbkxmlhandler.getZdgzBanKuaiFromXmlAndUpatedToCylTree(treechanyelian);
		startGui ();
	}
	public void startGui ()
	{
		initializeGui ();
		initializeAllDaLeiZdgzTableFromXml ();
		initializeBanKuaiParsedFile ();
		createEvents ();
		if(cylxmhandler.hasXmlRevised())
			btnSaveAll.setEnabled(true);
	}

	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private SystemConfigration sysconfig;
	HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap;
	private ChanYeLianXMLHandler cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    private BkChanYeLianTree treechanyelian;
    private BanKuaiDbOperation bkdbopt;
	private StockInfoManager stockInfoManager;
//	private StockDbOperations stockdbopt;
//	private boolean cylneedsave; //��ǲ�ҵ�����и���
//	private boolean zdgzxmlneedsave; //����ص��ע�и���

    
    boolean editingNodeText = false;
	private String currentselectedtdxbk = "";
	
	private  BkChanYeLianTree initializeBkChanYeLianXMLTree()
	{
		BkChanYeLianTreeNode topNode = cylxmhandler.getBkChanYeLianXMLTree();
		BkChanYeLianTree tmptreechanyelian = new BkChanYeLianTree(topNode);
		return tmptreechanyelian;
	}
	public BkChanYeLianTree getBkChanYeLianTree () {
		return treechanyelian;
	}
	/*
	 * �ж��Ƿ������ص��ע���
	 */
	public Multimap<String,String> checkBanKuaiSuoSuTwelveDaLei (Set<String> union)
	{
		return zdgzbkxmlhandler.subBkSuoSuTwelveDaLei (union);
	}
	/*
	 * ��Ʊ������ҵ��
	 */
	public Stock getStockChanYeLianInfo (Stock stockbasicinfo2)
	{
		cylxmhandler.getStockChanYeLianInfo(stockbasicinfo2);
		return stockbasicinfo2;
	}
	/*
	 * ��ȡ���ɲ�ҵ��
	 */
	public ArrayList<String> getGeGuChanYeLian(String stockcode)
	{
		return cylxmhandler.getGeGuChanYeLian(stockcode);
	}
	
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
//	 * ����Ϊ��������λ��
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
	 * ͳһ�����ϸ��������������Ķ�������סһ�¡�
	 */
	private void unifyDisplaysInDifferentCompOnGui (String selecteddalei,int selectrowfordaleiinsubcyl) 
	{
		//for �ص��ע
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
		int selecteddaleirow = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiIndex (selecteddalei);
		tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
		
		//for sub�ص��ע
		((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selecteddalei);
		if( ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getRowCount() != 0)
			tableZdgzBankDetails.setRowSelectionInterval(selectrowfordaleiinsubcyl,selectrowfordaleiinsubcyl);
		
		BkChanYeLianTreeNode curselectnode = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(selectrowfordaleiinsubcyl);
//		if(curselectnode != null) {
//
//			((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//			tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
//		}
		//System.out.println("current node is  " + currentselectedtdxbk );

		//for tree
		if(curselectnode != null) {
			TreePath nodepath = new TreePath(curselectnode.getPath());
			treechanyelian.expandTreePathAllNode( new TreePath(curselectnode.getPath()) );
		   		
		    //for ����Talble
			getReleatedInfoAndActionsForTreePathNode (new TreePath(curselectnode.getPath()) );
		}
	}


	
	/*
	 * ���ӽڵ�
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
//		  cylneedsave = true;
			 
			 int direction = ((SubnodeButton)addSubnodeButton).getDirection();
			 int row = tablesubcyl.getSelectedRow() ;
			 if( row <0) {
				 JOptionPane.showMessageDialog(null,"��ѡ��һ���Ӱ��!");
				 return;
			 }
			 
			 String subcode = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianCode(row);
			 String subname = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianName(row);

			 treechanyelian.addNewNode (BkChanYeLianTreeNode.SUBBK,subcode,subname,direction);
			 btnSaveAll.setEnabled(true);
			 
		}//GEN-LAST:event_addSubnodeButtonActionPerformed

		    
	  /*
	     * �����ĳ����������������ͨ���Ű�飬�ڶ����ð��Ĳ�ҵ���Ӱ�� ��ͨ���԰�����ظ���
	     */
	    private void chanYeLianTreeMousePressed(java.awt.event.MouseEvent evt) //GEN-FIRST:event_treeMousePressed 
	    {System.out.println("get action notice at bkcyl");
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
	        bkfxpnl.resetDate ();
	        pnlGeGuZhanBi.resetDate();
	               
	    }//GEN-LAST:event_treeMousePressed
	    
	    /*
	     * ��ѡ������ص��Ӳ�ҵ�������� 
	     */
	    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
	    {
	    	 BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
	    	 String tdxbk = bknode.getMyOwnName(); 
	    	 String tdxbkcode = bknode.getMyOwnCode();
	    	 
	    	 HashSet<String> stockinparsefile = bknode.getParseFileStockSet ();
	         if(!tdxbk.equals(currentselectedtdxbk)) { //�͵�ǰ�İ�鲻һ����
	        	 
	  	       	//�����ĳ����������������ͨ���Ű�飬�ڶ����ð��Ĳ�ҵ���Ӱ�� 
	  	       	HashMap<String, String> tmpsubbk = bkdbopt.getSubBanKuai (tdxbkcode);
	  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk);
	  	       
	  	       	//�����ð�鵱ǰ���еĸ���
	  	       	HashMap<String, Stock> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbk,tdxbkcode,new Date(),new Date(),false );
	  	      
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).deleteAllRows();
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(tdxbk,tdxbkcode,tmpallbkge,stockinparsefile);
	  	       	
	  	       	int row = tableCurZdgzbk.getSelectedRow();
	  	       	((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
	  	       	if(row >= 0)
	  	       		tableCurZdgzbk.setRowSelectionInterval(row,row);
	  	       	else
	  	       		tableCurZdgzbk.setRowSelectionInterval(0,0);
	  	       	
	  	       	currentselectedtdxbk = tdxbk;
	  	       	
////	  	       	 �����ð���һ���ռ������
//	  	       	displayBanKuaiZhanBi (tdxbkcode,tdxbk);
//	  	       	displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk);
	         }
	         
	       //�����ð����ص�����
  	       	 BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
  	       	 String curselectedbknodename = curselectedbknode.getMyOwnName();
  	       	 String curselectedbknodecode = curselectedbknode.getMyOwnCode();
  	       	 System.out.println("my name:" + curselectedbknodename + "mycode" +  curselectedbknodecode);
  	       	 ArrayList<ChanYeLianNews> curnewlist = bkdbopt.getBanKuaiRelatedNews (curselectedbknodecode);
  	       	 createChanYeLianNewsHtml (curselectedbknodecode,curnewlist);
	    }
	    /*
	     * ��ʾ����ڹ�Ʊ����ռ��, �ж����ܼ��������������������ʾ���ܵģ�������ʾ���ܵ�
	     */
	    private void displayBanKuaiGeGuZhanBi (String tdxbkcode, String tdxbkname)
	    {
	    	pnlGeGuZhanBi.setBanKuaiNeededDisplay(tdxbkname, tdxbkcode, new Date());
	    }
    

	      /*
	     * ��ʾ������������г���ռ��
	     */
	    private void displayBanKuaiZhanBi(String tdxbkcode, String tdxbkname) 
	    {
	    	Date date=new Date();//ȡʱ��
//	    	Calendar calendar =  Calendar.getInstance();
//	    	calendar.setTime(date);
//	    	calendar.add(calendar.MONTH,-6);//��������������һ��.����������,������ǰ�ƶ�
//	    	date = calendar.getTime();
	    	
	    	HashMap<String,String> displaybk = new HashMap<String,String> ();
	    	displaybk.put(tdxbkcode, tdxbkname);
	    	bkfxpnl.setBanKuaiNeededDisplay(displaybk, tdxbkname,date,6);
	    	
	    	//��ʾ���ܺ����ܳɽ���ռ�ȵı仯
//	    	try {
//		    	rs.absolute(row);
//		    	Double zhanbiweek = rs.getDouble("ռ��");
//		    	Double zhanbilastweek;
//		    	Double growthrate;
//		    	if(row >1) {
//		    		rs.absolute(row-1);
//			    	zhanbilastweek =rs.getDouble("ռ��");
//			    	growthrate = (zhanbiweek-zhanbilastweek)/zhanbilastweek;
//		    	} else {
//		    		zhanbilastweek = 0.0;
//		    		growthrate = 10000.0;
//		    	}
//		    	lblcjlzbinfo.setText("����ռ��: " + new DecimalFormat("%#0.000").format(zhanbiweek) + " ����ռ��: " + new DecimalFormat("%#0.000").format(zhanbilastweek) + " ռ��������: " + new DecimalFormat("%#0.000").format((growthrate)) );
//	    	} catch (SQLException e) {
//	    		// TODO Auto-generated catch block
//	    		e.printStackTrace();
//	    	} finally {
//	    		if(rs != null) {
//	    			try {
//						rs.close();
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	    			rs = null;
//	    		}
//	    	}
		}
	   
	    /*
	     * ���øð����ɵ�Ȩ��
	     */
		private void setGeGuWeightInBanKuai()
	    {
			int row = tablebkgegu.getSelectedRow();
			if(row < 0)
				return;
			
//			BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) treechanyelian.getLastSelectedPathComponent();
			String bkcode = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getTdxBkCode();
			String bkname = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getTdxBkName();
			String stockcode = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCode(row);
			int weight = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCurWeight (row);
			
			String weightresult = JOptionPane.showInputDialog(null,"�ֹ������꣬������ӯ�����:",weight);
			int newweight = Integer.parseInt(weightresult);
			if(newweight>10)
				JOptionPane.showMessageDialog(null,"Ȩ��ֵ���ܳ���10��","Warning",JOptionPane.WARNING_MESSAGE);
			
			if(weight != newweight) {
				bkdbopt.setStockWeightInBanKuai (bkcode,bkname,stockcode,newweight);
				((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).setStockCurWeight (row,newweight);
			}
		}
	    
	    /*
	     * ��ʾ�����������
	     */
	    private void createChanYeLianNewsHtml(String curselectedbknodecode, ArrayList<ChanYeLianNews> curnewlist)
	    {
	    	String htmlstring = "";
	    	htmlstring  += "<h3>���"+ curselectedbknodecode + "�������</h3>";
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
	    	addGeGuButton.setToolTipText("��Ӹ���");
	    }
	    
	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt) 
	    {
//	    	cylneedsave = true;
	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
	    	btnSaveAll.setEnabled(true);
	    }
	    
	    public void addGeGunode(int direction)
	    {
	   	int row = tablebkgegu.getSelectedRow();
	  		if(row <0) {
	  			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ!","Warning",JOptionPane.WARNING_MESSAGE);
	  			return;
	  		}
	  		
	  		String gegucode = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCode(row);
	  		String geguname = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockName(row);
	  		
	  		 treechanyelian.addNewNode (BkChanYeLianTreeNode.BKGEGU,gegucode,geguname,direction);
	     }

	    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
			  if(treechanyelian.deleteNodes () )
//				  cylneedsave = true;
				  btnSaveAll.setEnabled(true);
		        
		    }//GEN-LAST:event_deleteButtonActionPerformed

	   
		   /*
		    * ��ҵ�����϶�λ�û�ѡ��İ�飬ֻѡ�񵽰��һ�����Ӱ�鲻��
		    */
		   protected TreePath findBanKuaiInTree(String bkinputed) 
		   {
		    	@SuppressWarnings("unchecked")
		    	TreePath bkpath = treechanyelian.locateNodeByNameOrHypyOrBkCode (bkinputed);
		    	if(bkpath != null)
		    		getReleatedInfoAndActionsForTreePathNode (bkpath); //��ʾ�Ͱ����ص��Ӳ�ҵ���͸���
		    	
			    return bkpath;
			}
		    public boolean isXmlEdited ()
		    {
//		    	if(cylneedsave == true || zdgzxmlneedsave == true )
//		    		return true;
//		    	else 
//		    		return false ;
		    	if(btnSaveAll.isEnabled() )
		    		return true;
		    	else
		    		return false;
		    }
		    
		    private void initializeBanKuaiParsedFile() 
			{
				String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
				if(parsedpath == null || parsedpath == "")
					return;
				
				Date date=new Date();//ȡʱ��
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(calendar.DATE,-1);//��������������һ��.����������,������ǰ�ƶ�
				date=calendar.getTime(); //���ʱ���������������һ��Ľ�� 
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String dateString = formatter.format(date);
				System.out.println(dateString);
				
				String parsefilename = parsedpath + dateString + ".ebk";
//				tfldparsefilename.setText(parsefilename);
				
				parseSelectedBanKuaiFile (parsefilename);
			}
		    
		    private void parseSelectedBanKuaiFile (String filename)
		    {
		    	File parsefile = new File(filename);
		    	if(!parsefile.exists() )
		    		return;
		    	
		    	tfldparsefilename.setText(filename);
				List<String> readparsefileLines = null;
				try {
					readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				HashSet<String> stockinfile = new HashSet<String> (readparsefileLines);
				
				treechanyelian.updateTreeParseFileInfo(stockinfile);
				
				((BanKuaiGeGuTableModel)tablebkgegu.getModel()).deleteAllRows(); //�����б�ɾ����
				
				((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
		    	
		    }

		    /*
		     * ѡ��Ҫ�����İ���ļ�
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
				}
				
			}   


//		    private void notesPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesPaneFocusLost
//		        if(editingNodeText){
//		            notesNode.setNoteText(notesPane.getText());
//		            editingNodeText = false;
//		            notesPane.setEditable(false);
//		        }
//		    }//GEN-LAST:event_notesPaneFocusLost

//		    private void notesPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesPaneFocusGained
//		        
//		        if (treechanyelian.getSelectionCount()==1 && notesNode != null){
//		            treechanyelian.stopEditing();
//		            editingNodeText = true;
//		            notesPane.setEditable(true);
//		            notesPane.getCaret().setVisible(true);
//		        } else {
//		            treeScrollPane.requestFocusInWindow();
//		            editingNodeText = false;
//		            notesPane.setEditable(false);
//		        }
//		    }//GEN-LAST:event_notesPaneFocusGained

		    /*
		     * ����2��XML 
		     */
	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
	{
		if(btnSaveAll.isEnabled() ) {
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
			if(!cylxmhandler.saveTreeToChanYeLianXML(treeroot) ) {
				JOptionPane.showMessageDialog(null, "�����ҵ��XMLʧ�ܣ������ԭ��","Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () ) {
				JOptionPane.showMessageDialog(null, "�����ص��ע��Ʊ��XMLʧ�ܣ������ԭ��","Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		btnSaveAll.setEnabled(false);
		return true;
	}	    
	private void createEvents() 
	{
		btndisplaybkfx.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
//				 �����ð���һ���ռ������
				TreePath closestPath = treechanyelian.getSelectionPath();
				BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
		    	 String tdxbk = bknode.getMyOwnName(); 
		    	 String tdxbkcode = bknode.getMyOwnCode();
		    	 
		  	     displayBanKuaiZhanBi (tdxbkcode,tdxbk);
		  	     displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk);
			}
		});
		
		dchgeguwkzhanbi.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	
		    	TreePath closestPath = treechanyelian.getSelectionPath();
		    	if(closestPath != null ) {
		    		 BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
			    	 String tdxbk = bknode.getMyOwnName(); 
			    	 String tdxbkcode = bknode.getMyOwnCode();
			    	 
			    	
			    	if("date".equals(e.getPropertyName())) {
			    		displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk);
			    		displayBanKuaiZhanBi (tdxbkcode,tdxbk);
			    	}
		    	}
		    }
		});
		
		buttonCjlFx.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				chengJiaoLiangFengXi ();
			}
		});
		
		
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
				String newdaleiname = JOptionPane.showInputDialog(null,"�������µĹ�Ʊ������:","��ӹ�Ʊ��", JOptionPane.QUESTION_MESSAGE);
				if( !newdaleiname.isEmpty() && !zdgzbkmap.keySet().contains(newdaleiname)) {
					ArrayList<BkChanYeLianTreeNode> tmpgzbklist = new ArrayList<BkChanYeLianTreeNode> (); 
					zdgzbkmap.put(newdaleiname, tmpgzbklist);
					
					unifyDisplaysInDifferentCompOnGui (newdaleiname,0);
					
//					zdgzxmlneedsave = true;
					btnSaveAll.setEnabled(true);
				} else
					JOptionPane.showMessageDialog(null,"��Ʊ�������Ѿ����ڣ�","Warning",JOptionPane.WARNING_MESSAGE);
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
							 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //�����ͼ�е�������
							 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //�����ͼ�е�������
							 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//����ͼ�е�������ת��Ϊ����ģ���е�������
							 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//����ͼ�е�������ת��Ϊ����ģ���е�������
							 
							 
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
							JOptionPane.showMessageDialog(null,"��ѡ��һ����ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
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
							JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
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
//						//���table��ѡ��Ӧ�Ĳ�ҵ��
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
						if(rowindex != -1) {
							tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
							tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
						}
						
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
						String newsubbk = JOptionPane.showInputDialog(null,"�������Ӱ������:","�����Ӱ��", JOptionPane.QUESTION_MESSAGE);
						if(newsubbk == null)
							return;
						
//						if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
//							JOptionPane.showMessageDialog(null,"�����Ӱ��������ͨ���Ű�����Ƴ�ͻ,����������!");
//							return ;
//						}
						
						TreePath closestPath = treechanyelian.getSelectionPath();
//				        System.out.println(closestPath);
				         BkChanYeLianTreeNode tdxbk = (BkChanYeLianTreeNode)closestPath.getPathComponent(1);
				         String tdxbkcode = tdxbk.getMyOwnCode();
				        
						String newsubcylcode = bkdbopt.addNewSubBanKuai (tdxbkcode,newsubbk.trim() ); 
						if(newsubcylcode != null)
							((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode,newsubbk);
						else
							JOptionPane.showMessageDialog(null,"���ʧ�ܣ���������Ϊ������","Warning",JOptionPane.WARNING_MESSAGE);
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
	    
	
	protected void chengJiaoLiangFengXi() 
	{
		
//		if(bkfx == null ) {
			BanKuaiFengXi bkfx = new BanKuaiFengXi (treechanyelian,tfldparsefilename.getText());
			bkfx.setModal(false);
			bkfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			bkfx.setVisible(true);
//		} 
		
//		if(!bkfx.isVisible() ) {
//			bkfx.setVisible(true);
//		 } 
		bkfx.toFront();
		
		
	}

	protected void addGeGuNews() 
	{
		int row = tablebkgegu.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String stockcode = ((BanKuaiGeGuTableModel) tablebkgegu.getModel()).getStockCode (row);
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӹ�������", JOptionPane.OK_CANCEL_OPTION);
		System.out.print(exchangeresult);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
	}

	protected void addChanYeLianNews() 
	{
		try {
			TreePath closestPath = treechanyelian.getSelectionPath();
			BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
			String selectnodecode = selectednode.getMyOwnCode();
			ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (selectnodecode);
			int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӳ�ҵ������", JOptionPane.OK_CANCEL_OPTION);
			System.out.print(exchangeresult);
			if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		} catch (java.lang.NullPointerException ex) {
			JOptionPane.showMessageDialog(null,"��ѡ���ҵ��飡","Warning",JOptionPane.WARNING_MESSAGE);
		}
		
		
		
	}

	private void deleteDaLeiGuPiaoChi () 
	{
		int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String daleiname = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(row);
		int n = JOptionPane.showConfirmDialog(null, "ȷ��ɾ��" + daleiname + "?", "ɾ����Ʊ��",JOptionPane.YES_NO_OPTION);//i=0/1  
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
			
//			zdgzxmlneedsave = true;
			btnSaveAll.setEnabled(true);
		}
		
	}
	
    
	private void removeTreeChanYeLianNodeFromZdgz(MouseEvent arg0) 
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ������ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}

		String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			  BkChanYeLianTreeNode gzcyl = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 
			 treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzcyl,false);
			 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzcyl);
			 unifyDisplaysInDifferentCompOnGui (daleiname,0);
			 
//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
			 
		 }
		 else
			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
		
	}

	private void addChanYeLianToOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 gzbk.setOfficallySelected(true);
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(gzbk,true);
			 unifyDisplaysInDifferentCompOnGui (selectedalei,row);

//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
		} else
			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
	}
	private void removeChanYeLianFromOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 if(!gzbk.isOfficallySelected()) {
				 JOptionPane.showMessageDialog(null,"ѡ��Ĳ�ҵ��ֻ�Ǻ򲹹�ע�����ڵ�ǰ�ص��ע��Ʊ����,�����Ƴ���","Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }
				 
			 if(JOptionPane.showConfirmDialog(null, "�Ƿ�ֱ�ӴӺ򲹲�ҵ����ɾ����","Warning", JOptionPane.YES_NO_OPTION) == 0) {
					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
					unifyDisplaysInDifferentCompOnGui (selectedalei,0);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,false);
				} else {
					unifyDisplaysInDifferentCompOnGui (selectedalei,row);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,true);
				}

//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
			 
		} else
			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
		
	}
		
	
	private void adddTreeChanYeLianNodeToZdgz(MouseEvent arg0) 
	{
		 String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			 TreePath path = treechanyelian.getSelectionPath();
			 BkChanYeLianTreeNode nodewilladded = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();

			 boolean isofficallyselected = false;
			 if(JOptionPane.showConfirmDialog(null, "�Ƿ�ֱ�Ӽ����ص��ע��","Warning", JOptionPane.YES_NO_OPTION) == 1) {
				 nodewilladded.setOfficallySelected(false);
			 } else 
				 nodewilladded.setOfficallySelected(true);
			 
			 nodewilladded.setSelectedToZdgzTime( CommonUtility.formatDateYYMMDD(new Date() ) );
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(nodewilladded,false);
			 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, nodewilladded);
			 int addedrow = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfoIndex(nodewilladded);
			 unifyDisplaysInDifferentCompOnGui (daleiname,addedrow);
			
//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
		 }
		 else
			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
		 
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
	private JButton buttonCjlFx;
	private ChartPanel barchartPanel; //chart 
	private ChartPanel piechartPanel; //chart
	private JScrollPane sclpBanKuaiZhanBi;
	private JLabel lblcjlzbinfo;
	private JScrollPane sclpGeGuZhanBi;
	private JDateChooser dchgeguwkzhanbi;
	private JButton btndisplaybkfx;
	private BanKuaiFengXiBarChartPnl bkfxpnl ;
	private BanKuaiFengXiPieChartPnl pnlGeGuZhanBi;

	private void initializeGui() 
	{
		JPanel panel = new JPanel();
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		
		JPanel panel_1 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelzdgz, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 893, Short.MAX_VALUE)
						.addComponent(panelcyltree, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 610, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addGap(2)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyltree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
					.addGap(10))
		);
		
		sclpBanKuaiZhanBi = new JScrollPane();
		
		buttonCjlFx = new JButton("\u5468\u6210\u4EA4\u91CF\u5206\u6790");
		
		lblcjlzbinfo = new JLabel("New label");
		
		sclpGeGuZhanBi = new JScrollPane();
		
		dchgeguwkzhanbi = new JDateChooser();
		dchgeguwkzhanbi.setDate(new Date());
		
		btndisplaybkfx = new JButton("\u663E\u793A\u677F\u5757\u5206\u6790\u4FE1\u606F");
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_panel_1.createSequentialGroup()
								.addContainerGap()
								.addComponent(sclpBanKuaiZhanBi, GroupLayout.PREFERRED_SIZE, 597, GroupLayout.PREFERRED_SIZE))
							.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
								.addGap(22)
								.addComponent(lblcjlzbinfo, GroupLayout.PREFERRED_SIZE, 460, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(sclpGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 594, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(btndisplaybkfx)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(dchgeguwkzhanbi, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
							.addComponent(buttonCjlFx)))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(btndisplaybkfx)
						.addComponent(dchgeguwkzhanbi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(buttonCjlFx))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sclpGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 323, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblcjlzbinfo, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sclpBanKuaiZhanBi, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
		);
		
		pnlGeGuZhanBi = new BanKuaiFengXiPieChartPnl();
		//������ʾ��ͼƬ���ұ�
	    Rectangle bounds = sclpGeGuZhanBi.getViewport().getViewRect();
	    Dimension size = sclpGeGuZhanBi.getViewport().getViewSize();
	    int x = (size.width - bounds.width) ;
	    int y = (size.height - bounds.height) ;
	    sclpGeGuZhanBi.getViewport().setViewPosition(new Point(x, 0));
		sclpGeGuZhanBi.setViewportView(pnlGeGuZhanBi);
		
      
      
      
		
//		chartPanel = new ChartPanel(barchart);
//		panel_2 = new JPanel();
//		sclpBanKuaiZhanBi.setViewportView(panel_2);
		panel_1.setLayout(gl_panel_1);
		
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
									.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnfindbk)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnopencylxml))
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
										.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnAddSubBk)))))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnCylAddToZdgz)
							.addGap(10)
							.addComponent(btnCylRemoveFromZdgz)))
					.addContainerGap(11, Short.MAX_VALUE))
		);
		gl_panelcyltree.setVerticalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCylRemoveFromZdgz)
						.addComponent(btnCylAddToZdgz)
						.addComponent(btnAddSubBk))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addGap(69)
							.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(186)
							.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(jSplitPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 415, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(btnfindbk)
									.addComponent(btnopencylxml, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(btnfindgegu))))
						.addGroup(Alignment.TRAILING, gl_panelcyltree.createSequentialGroup()
							.addGap(10)
							.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
					.addGap(13))
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
		
		//����tableҲ���ԼӸ�������
		JPopupMenu popupMenuGeguNews = new JPopupMenu();
		JMenuItem menuItemAddNews = new JMenuItem("��Ӹ�������");
		JMenuItem menuItemMakeLongTou = new JMenuItem("���ù�ƱȨ��");
		popupMenuGeguNews.add(menuItemAddNews);
		popupMenuGeguNews.add(menuItemMakeLongTou);
		tablebkgegu.setComponentPopupMenu(popupMenuGeguNews);
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {

				addGeGuNews ();
			}
			
		});
		menuItemMakeLongTou.setComponentPopupMenu(popupMenuGeguNews);
		menuItemMakeLongTou.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setGeGuWeightInBanKuai ();
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
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnGenTDXCode))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(buttonremoveoffical)
								.addComponent(buttonaddofficial))
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addGap(22)
									.addComponent(btnadddalei)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btndeldalei)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnopenzdgzxml)
									.addGap(20))
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(scrollPaneDaLei, GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE))))
						.addComponent(separator_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 884, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelzdgz.setVerticalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnadddalei)
								.addComponent(btndeldalei)
								.addComponent(btnGenTDXCode)
								.addComponent(btnopenzdgzxml))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPaneDaLei, 0, 0, Short.MAX_VALUE)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(Alignment.TRAILING, gl_panelzdgz.createSequentialGroup()
							.addComponent(buttonaddofficial)
							.addGap(77)
							.addComponent(buttonremoveoffical)
							.addGap(76)))
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
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				
				Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
				Border inside = new EmptyBorder(0, 1, 0, 1);
				Border highlight = new CompoundBorder(outside, inside);
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        CurZdgzBanKuaiTableModel tablemodel = (CurZdgzBanKuaiTableModel)this.getModel(); 
		        Object value = tablemodel.getValueAt(row, col);
		        
		        if(tablemodel.shouldRemovedNodeWhenSaveXml(row) && col == 0) {
		       	 	Font font=new Font("����",Font.BOLD + Font.ITALIC,14); 
		        	comp.setFont(font);
		        }
		        
		        return comp;
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
		        
		        if(tablemodel.hasShouldRemovedNodeWhenSaveXml(row) && col == 0) {
		       	 	Font font=new Font("����",Font.BOLD + Font.ITALIC,14); 
		        	comp.setFont(font);
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
		
		
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMinWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(80);
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
		btnSaveAll.setEnabled(false);
		
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
        
        //tree �ĵ����˵�
        popupMenu = new JPopupMenu();
		addPopup(treechanyelian, popupMenu);
		mntmNewMenuItem = new JMenuItem("\u6DFB\u52A0\u4EA7\u4E1A\u94FE\u65B0\u95FB");
		popupMenu.add(mntmNewMenuItem);
		

		bkfxpnl = new BanKuaiFengXiBarChartPnl ();
		sclpBanKuaiZhanBi.setViewportView(bkfxpnl);
        //������ʾ��ͼƬ���ұ�
        Rectangle bounds2 = sclpBanKuaiZhanBi.getViewport().getViewRect();
        Dimension size2 = sclpBanKuaiZhanBi.getViewport().getViewSize();
        int x2 = (size.width - bounds.width) ;
        int y2 = (size.height - bounds.height) ;
        sclpBanKuaiZhanBi.getViewport().setViewPosition(new Point(x, 0));	
        

	
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
 * 12������ĳ���������Ĺ�ע���ݱ�
 */
class CurZdgzBanKuaiTableModel extends AbstractTableModel 
{
	private HashMap<String,ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	private String cbxDale;

	String[] jtableTitleStrings = {  "����ҵ��","����ʱ��","��ѡ","����"};
	
	CurZdgzBanKuaiTableModel ()
	{

	}

	public boolean shouldRemovedNodeWhenSaveXml(int row){
		BkChanYeLianTreeNode bkcyltn = this.getGuanZhuBanKuaiInfo(row);
		if(bkcyltn.shouldBeRemovedWhenSaveXml())
			return true;
		return false;
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
		
		int findsimilarnode = -1;
		
		for(int i=0;i<tmpgzbkinfo.size();i++) {
			String cylinmapnode = tmpgzbkinfo.get(i).getNodeCurLocatedChanYeLian().trim();
			String cylinverfiednode = parent.getNodeCurLocatedChanYeLian().trim();

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
		try {
			return tmpgzbkinfo.get(rowindex);
		} catch (java.lang.IndexOutOfBoundsException ex) {
			return null;
		}
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
                value = tmpgzbk.getNodeCurLocatedChanYeLianByName();
                break;
            case 1:
            	try {
            		value = tmpgzbk.getSelectedToZdgzTime();
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
	    }//���ñ������ 

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
 * �ص��ע����
 */
class ZdgzBanKuaiDetailXmlTableModel extends AbstractTableModel 
{
	private HashMap<String, ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	
	private String[] jtableTitleStrings = { "��Ʊ��", "��ע���","����"};
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
	 public boolean hasShouldRemovedNodeWhenSaveXml (int rowindex)
	 {
		 boolean hasnode = false;
		String dalei = (String)gzdalei.get(rowindex);
		ArrayList<BkChanYeLianTreeNode> daleidetail = gzbkmap.get(dalei) ;
		for(BkChanYeLianTreeNode tmpbkcyltn : daleidetail)
			if(tmpbkcyltn.shouldBeRemovedWhenSaveXml())
				hasnode = true;
		
		return hasnode;
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
            				String chanyelian = gznode.getNodeCurLocatedChanYeLianByName(); 
            				
            				String seltime = "";
            				if(gznode.getSelectedToZdgzTime() != null)
            					seltime = gznode.getSelectedToZdgzTime();
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
	    }//���ñ������ 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}

}


class BanKuaiGeGuTableModel extends AbstractTableModel 
{
	private HashMap<String, Stock> bkgegumap; //������Ʊ����͹�Ʊ����
	String[] jtableTitleStrings = { "��Ʊ����", "��Ʊ����", "Ȩ��"};
	private ArrayList<String> bkgeguname;
	private HashSet<String> stockcodeinparsefile;
	private String tdxbkname;
	private String tdxbkcode;
	
	BanKuaiGeGuTableModel ()
	{
	}
	
	public void refresh  (String tdxbkname2, String tdxbkcode2, HashMap<String, Stock> tmpallbkge,HashSet<String> stockcodeinparsefile2)
	{
		this.bkgegumap = tmpallbkge;
		this.tdxbkname = tdxbkname2;
		this.tdxbkcode = tdxbkcode2;
 		this.stockcodeinparsefile = stockcodeinparsefile2;
 		
 		if(stockcodeinparsefile.size() >0 ) { //���Ȱ�parsefile��ĸ�����ʾ��ǰ��
 			Set<String> bkgegucodelist = tmpallbkge.keySet() ;
 	 		SetView<String> commonbkcode = Sets.intersection(bkgegucodelist, this.stockcodeinparsefile);
 	 		SetView<String> diffbkcode = Sets.difference(bkgegucodelist, this.stockcodeinparsefile);
 	 		
 	 		ArrayList<String> tmpbkgeguname = new ArrayList<String> (commonbkcode);
 	 		tmpbkgeguname.addAll(diffbkcode);
 	 		
 	 		bkgeguname = tmpbkgeguname;
 		} else
 			bkgeguname = new ArrayList<String> (tmpallbkge.keySet() );
 		
 		this.fireTableDataChanged();
	}

	public String getTdxBkName ()
	{
		return this.tdxbkname;
	}
	public String getTdxBkCode ()
	{
		return this.tdxbkcode;
	}
	public int getStockCurWeight(int rowIndex) 
	{
		int weight = (Integer)this.getValueAt(rowIndex, 2);
		return weight;
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
	    public void setStockCurWeight(int row, int newweight) 
		{
	    	String bkcode = bkgeguname.get(row);
	    	Stock tmpstock = bkgegumap.get( bkcode ); 
	    	HashMap<String, Integer> stockweightmap = tmpstock.getGeGuSuoShuBanKuaiWeight();
	    	stockweightmap.put(this.tdxbkcode,newweight);
	    	tmpstock.setGeGuSuoShuBanKuaiWeight(stockweightmap);
	    	this.fireTableDataChanged();
		}
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bkgegumap.isEmpty())
	    		return null;
	    	String bkcode = bkgeguname.get(rowIndex);
	    	Stock tmpstock = bkgegumap.get( bkcode ); 
	    	HashMap<String, Integer> stockweightmap = tmpstock.getGeGuSuoShuBanKuaiWeight();
	    	Object[] stockweight = stockweightmap.values().toArray();
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkcode;
                break;
            case 1:
            	value = tmpstock.getMyOwnName();
                break;
            case 2:
            	value = (Integer)stockweight[0];
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
			          clazz = Integer.class;
			          break;
		      }
		      
		      return clazz;
		}
    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//���ñ������ 
	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getStockCode (int row) 
	    {
	    	return bkgeguname.get(row) ;
	    }
	    public String getStockName (int row) 
	    {
	    	return  (String) this.getValueAt(row, 1);
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
	    	
	    	if(index == -1) {
	    		HanYuPinYing hypy = new HanYuPinYing ();
	    		for(int i=0;i<this.getRowCount();i++) {
	    			String codename = this.getStockName(i); 
	    			if(codename == null)
	    				continue;
	    			
			   		String namehypy = hypy.getBanKuaiNameOfPinYin(codename );
			   		if(namehypy.toLowerCase().equals(stockcode.toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	    	}
	   		
	   		return index;
	    }
	    
}

class BanKuaiSubChanYeLianTableModel extends AbstractTableModel 
{
	private HashMap<String,String> bksubcylmap; //�������������
	private ArrayList<String> bksubcylcodelist;
	String[] jtableTitleStrings = { "�Ӳ�ҵ������", "�Ӳ�ҵ������"};
	
	
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
	    }//���ñ������ 
		

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
 * google guava files �࣬����ֱ�Ӵ��������line
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

//It��s a little trick to make a row automatically selected when the user right clicks on the table. Create a handler class for mouse-clicking events as follows:
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


//http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=29571#
class CustomToolTipGenerator implements CategoryToolTipGenerator  
{
  public String generateToolTip(CategoryDataset dataset, int row, int column)   
  {
  	NumberFormat numberFormat = NumberFormat.getNumberInstance();
  	numberFormat.setMinimumFractionDigits(3);
  	String zhanbiperct = new DecimalFormat("%#0.000").format(dataset.getValue(row, column));
  	return "��" + column + "ռ��:" + zhanbiperct;
  }
}
