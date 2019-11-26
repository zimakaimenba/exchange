package com.exchangeinfomanager.bankuaichanyelian;


import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import javax.swing.table.DefaultTableModel;

import javax.swing.tree.TreePath;


import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.labelmanagement.DBSystemTagsService;
import com.exchangeinfomanager.labelmanagement.TagCache;
import com.exchangeinfomanager.labelmanagement.LblMComponents.TagsPanel;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.google.common.collect.Lists;

import com.google.common.io.LineProcessor;

import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.EtchedBorder;
import java.awt.FlowLayout;

public class BanKuaiAndChanYeLianGUI  extends JPanel 
{
	public BanKuaiAndChanYeLianGUI  ( AllCurrentTdxBKAndStoksTree bkstk1 , CylTreeDbOperation treedb1)
	{
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		this.bkstk = bkstk1;
		
		setLayout(new BorderLayout(0, 0));
		
		bkopt = new BanKuaiDbOperation ();
		this.treedb = treedb1;
		
		treechanyelian = treedb.getBkChanYeLianTree();

		initializeGui ();
		setupSubGpcAndBanKuai ();
		createEvents ();
	}
	
	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private AllCurrentTdxBKAndStoksTree bkstk;
	private String currentselectedtdxbk = "";
	private CylTreeDbOperation treedb;
	private BanKuaiAndStockTree treechanyelian;
	private BanKuaiDbOperation bkopt;

	private DBSystemTagsService lballdbservice;

	private TagCache allsyskwcache;
 	
	private void setupSubGpcAndBanKuai() 
	{
//		Set<String> all = new HashSet<> ();
//		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbkstk.getAllBkStocksTree().getModel().getRoot();
//		all.add("treeroot");
		lballdbservice = new DBSystemTagsService (); 
		allsyskwcache = new TagCache (lballdbservice);
		lballdbservice.setCache(allsyskwcache);
		pnllblsysmanagement.initializeTagsPanel (lballdbservice,allsyskwcache);
		
		
//		//subgpc
//		List<BkChanYeLianTreeNode> subgpclist = treedb.getSubGuPiaoChi ();
//		((BkChanYeLianTreeNodeListTableModel)tablesubcyl.getModel()).refresh(subgpclist);
		
		//bankuai
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkstk.getAllBkStocksTree().getModel().getRoot();
        int bankuaicount = this.bkstk.getAllBkStocksTree().getModel().getChildCount(treeroot);
        List<BkChanYeLianTreeNode> allbklist = new ArrayList<> ();
		for(int i=0;i < bankuaicount; i++) {
			try {
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.bkstk.getAllBkStocksTree().getModel().getChild(treeroot, i);
				if(childnode.getType() == BkChanYeLianTreeNode.TDXBK) {
					BkChanYeLianTreeNode cylbk = new CylTreeNestedSetNode (childnode.getMyOwnCode(), childnode.getMyOwnName(), BkChanYeLianTreeNode.TDXBK );
					allbklist.add(cylbk);
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		} 
		((BkChanYeLianTreeNodeListTableModel)tablebankuai.getModel()).refresh(allbklist);
	}
	/*
     * 选择 
     */
    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
    {
//    	TreePath[] treePaths = treechanyelian.getSelectionPaths();
    	BkChanYeLianTreeNode node = (CylTreeNestedSetNode)treechanyelian.getLastSelectedPathComponent();
    	
    	String nodecode;
    	try{
    		 nodecode = node.getMyOwnCode();
    	} catch (java.lang.NullPointerException ex) {
    		return;
    	}
    	 
        if(!nodecode.equals(currentselectedtdxbk)) { //和当前的板块不一样，
        	String gnts = node.getNodeJiBenMian().getGainiantishi();
        	LocalDate date = node.getNodeJiBenMian().getGainiantishidate();
        	try{
        		datechooser.setLocalDate(date);
        	} catch (java.lang.NullPointerException e) {
        		
        	}
        	try{
        		txaDescripton.setText(gnts);
        	} catch (java.lang.NullPointerException e) {
        		
        	}
        	
  	       	currentselectedtdxbk = node.getMyOwnCode();
         }
    }
    private void deleteANode ()
	{
		BkChanYeLianTreeNode delnode = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();
		int action = JOptionPane.showConfirmDialog(null, "是否将该节点彻底删除？选择YES彻底删除，选择NO将保留数据","询问", JOptionPane.YES_NO_OPTION);
		if(0 == action) {
			treedb.deleteNodeFromTree ( (CylTreeNestedSetNode)delnode,true);
		} else {
			treedb.deleteNodeFromTree ( (CylTreeNestedSetNode)delnode,false);
		}
	}
	/*
	 * 
	 */
	private void createNewSubGuPiaoChi ()
	{
       String newsubbk = JOptionPane.showInputDialog(null,"请输入SUB股票池名称","输入", JOptionPane.QUESTION_MESSAGE);
 		if(newsubbk == null)
 			return;
 		String newsubbkcode = JOptionPane.showInputDialog(null,"请指定SUB股票池代码","输入", JOptionPane.QUESTION_MESSAGE);
 		if(newsubbkcode == null)
 			return;
        
 		if(	((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).findNodeByNameOrCode(newsubbkcode) != null ) {
 			JOptionPane.showMessageDialog(null,"添加失败，代码已经存在！","Warning",JOptionPane.WARNING_MESSAGE);
 			return;
 		}
 		else if(	((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).findNodeByNameOrCode(newsubbk) != null ) {
 			JOptionPane.showMessageDialog(null,"添加失败，名称已经存在！","Warning",JOptionPane.WARNING_MESSAGE);
 			return ;
 		}
 		
 		BkChanYeLianTreeNode newnode = treedb.addNewSubGuoPiaoChi (newsubbk);
 		((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).addRow (newnode);
	}
	/*
	 * 
	 */
	private void createNewGuPiaoChi ()
	{
        String newsubbk = JOptionPane.showInputDialog(null,"请输入股票池名称","输入", JOptionPane.QUESTION_MESSAGE);
 		if(newsubbk == null)
 			return;
 		String newsubbkcode = JOptionPane.showInputDialog(null,"请指定股票池代码","输入", JOptionPane.QUESTION_MESSAGE);
 		if(newsubbkcode == null)
 			return;
        
 		BkChanYeLianTreeNode newnode = treedb.addNewGuoPiaoChi (newsubbkcode,newsubbk);
	}
	
	 private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt) 
	 {
	    	int row = tablebkgegu.getSelectedRow();
	  		if(row <0) {
	  			JOptionPane.showMessageDialog(null,"请选择一个股票!","Warning",JOptionPane.WARNING_MESSAGE);
	  			return;
	  		}
	  		BkChanYeLianTreeNode addednode = ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).getNode(row);
	  		
	  		int direction =  ((SubnodeButton)evt.getSource()).getDirection();
	  		this.treedb.addNewNodeToTree (addednode,direction);
	 }
	 private void addBanKuaiButtonActionPerformed(java.awt.event.ActionEvent evt) 
	  {
			 int row = tablebankuai.getSelectedRow() ;
			 if( row <0) {
				 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
				 return;
			 }
			 
			 BkChanYeLianTreeNode subcode = ((BkChanYeLianTreeNodeListTableModel)(tablebankuai.getModel())).getNode(row);
			 
			 int direction = ((SubnodeButton)evt.getSource()).getDirection();
			 this.treedb.addNewNodeToTree (subcode, direction);
	}
	private void addSubGpcButtonActionPerformed(ActionEvent evt) 
	{
//		int row = tablesubcyl.getSelectedRow() ;
//		if( row <0) {
//			 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
//			 return;
//		}
//		BkChanYeLianTreeNode subcode = ((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).getNode(row);
		 
		Collection<Tag> selectedlbl = allsyskwcache.produceSelectedTags();
		if(selectedlbl.size() > 1)
			JOptionPane.showMessageDialog(null,"一次只能添加一个TAG!");
		
		for (Iterator<Tag> lit = selectedlbl.iterator(); lit.hasNext(); )  {
	        InsertedTag f = (InsertedTag) lit.next();
	        
	        BkChanYeLianTreeNode subcode = new CylTreeNestedSetNode (String.valueOf(f.getID() ), f.getName(), BkChanYeLianTreeNode.SUBGPC);
	        int direction = ((SubnodeButton)evt.getSource()).getDirection();
			this.treedb.addNewNodeToTree (subcode, direction);
		}
	}
	private void addSubGpcButtonActionPerformed(int direction)
	{
		Collection<Tag> selectedlbl = allsyskwcache.produceSelectedTags();
		if(selectedlbl.size() > 1)
			JOptionPane.showMessageDialog(null,"一次只能添加一个TAG!");
		
		for (Iterator<Tag> lit = selectedlbl.iterator(); lit.hasNext(); )  {
	        InsertedTag f = (InsertedTag) lit.next();
	        
	        BkChanYeLianTreeNode subcode = new CylTreeNestedSetNode (String.valueOf(f.getID() ), f.getName(), BkChanYeLianTreeNode.SUBGPC);
			this.treedb.addNewNodeToTree (subcode, direction);
		}
		
	}
	/*
	    * 产业链树上定位用户选择的板块
	    */
	private Boolean findNodeInTree(String bkinputed) 
	{
		   BkChanYeLianTreeNode findnode = treechanyelian.getSpecificNodeByHypyOrCode (bkinputed,BkChanYeLianTreeNode.TDXBK);
		   if(findnode == null)
			   return false;
		   
		   TreePath bkpath = new TreePath(findnode.getPath());
		   if(bkpath != null) {
			   treechanyelian.setSelectionPath(bkpath);
			   treechanyelian.scrollPathToVisible(bkpath);
			   treechanyelian.expandTreePathAllNode(bkpath);
			   
			   return true;
			}
		return null;
	}
	/*
	 * 
	 */
	private void findNodeInTables(String searchcode) 
	{
		int rowindex = ((BkChanYeLianTreeNodeListTableModel)tablesubcyl.getModel() ).findNodeByNameOrCode(searchcode);;
		if(rowindex >= 0) {
			tablesubcyl.setRowSelectionInterval(rowindex, rowindex);
			tablesubcyl.scrollRectToVisible( new Rectangle(tablesubcyl.getCellRect(rowindex, 0, true)) );
		}
		
		rowindex = ((BkChanYeLianTreeNodeListTableModel)tablebankuai.getModel() ).findNodeByNameOrCode(searchcode);;
		if(rowindex >= 0) {
			tablebankuai.setRowSelectionInterval(rowindex, rowindex);
			tablebankuai.scrollRectToVisible( new Rectangle(tablebankuai.getCellRect(rowindex, 0, true)) );
		}
		
		rowindex = ((BkChanYeLianTreeNodeListTableModel)tablebkgegu.getModel() ).findNodeByNameOrCode(searchcode);;
		if(rowindex >= 0) {
			tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
			tablebkgegu.scrollRectToVisible( new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)) );
		}
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		pnllblsysmanagement.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {
            		addSubGpcButtonActionPerformed (BanKuaiAndChanYeLianGUI.RIGHT );
            	}
            }
		});
		
		btndelsubgpc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tablesubcyl.getSelectedRow() ;
				if( row <0) {
					 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
					 return;
				}
			
				BkChanYeLianTreeNode node = ((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).getNode(row);
				if( findNodeInTree(node.getMyOwnCode()) ) {
					JOptionPane.showMessageDialog(null,"产业链树有该子板块，请先在树中删除该板块!");
					return;
				}
				
				treedb.deleNodeFromDatabase (node);
				
			}
		});
		
		btnfindgegu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String searchcode = tfldfindgegu.getText();
				findNodeInTables (searchcode);
			}
		});
		
		btndelnode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				deleteANode ();
			}
		});
		
		btnfindnode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				findNodeInTree (tfldfindnodeintree.getText().trim() );
			}
		});
		
		btnAddNewGPC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				createNewGuPiaoChi ();
			}
		});
		
		
		btnAddSubGPC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				createNewSubGuPiaoChi ();
			}
		});
		
		tablebankuai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tablebankuai.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				
				int modelRow = tablebankuai.convertRowIndexToModel(row);
				BkChanYeLianTreeNode selectednode =  ((BkChanYeLianTreeNodeListTableModel)tablebankuai.getModel()).getNode(modelRow);
				BanKuai selectedbankuai = (BanKuai) bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(selectednode.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
				selectedbankuai = bkopt.getTDXBanKuaiGeGuOfHyGnFg (selectedbankuai,CommonUtility.getSettingRangeDate(LocalDate.now(),"large"), LocalDate.now(),bkstk.getAllBkStocksTree());
				
				List<BkChanYeLianTreeNode> allbkgg = selectedbankuai.getAllGeGuOfBanKuaiInHistory();
				List<BkChanYeLianTreeNode> cylgg = new ArrayList<> ();
				if(allbkgg != null)
					for(BkChanYeLianTreeNode childnode : allbkgg) 
						cylgg.add( new CylTreeNestedSetNode (childnode.getMyOwnCode(), childnode.getMyOwnName(), BkChanYeLianTreeNode.TDXGG ) );
				((BkChanYeLianTreeNodeListTableModel)tablebkgegu.getModel()).refresh(cylgg);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
				SystemAudioPlayed.playSound();

			}
		});

//		tfldfindgegu.addActionListener(new AbstractAction() {
//			public void actionPerformed(ActionEvent arg0) {
//				String nodeid = tfldfindgegu.getText().trim();
//				int rowindex = ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).getNodeLineIndex(nodeid);
//				
//				if(rowindex != -1) {
//					tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
//					tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
//				} else 	{
//					JOptionPane.showMessageDialog(null,"股票/板块代码或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
//				}
//			}
//		});
//		btnfindgegu.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				String nodeid = tfldfindgegu.getText().trim();
//				int rowindex = ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).getNodeLineIndex(nodeid);
//				
//				if(rowindex != -1) {
//					tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
//					tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
//				} else 	{
//					JOptionPane.showMessageDialog(null,"股票/板块代码有误！","Warning", JOptionPane.WARNING_MESSAGE);
//				}
//
//			}
//		});
		
	
		
//		/*
//		 * 查找板块
//		 */
//		tfldfindbk.addActionListener(new AbstractAction() {
//			public void actionPerformed(ActionEvent arg0) {
//				String bkinputed = tfldfindbk.getText();
//				findBanKuaiInTree (bkinputed);
//			}
//		});
//		
//		btnfindbk.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) 
//			{
//				String bkinputed = tfldfindbk.getText();
//				findBanKuaiInTree (bkinputed);
//			}
//		});
		
		btnAddGeGutotree.addMouseMotionListener(new mousemotionadapter() );
		btnAddGeGutotree.addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mouseExited(java.awt.event.MouseEvent evt) {
//	            	addGeGuButton.setIcon(addSubnodeIcon);
//	    	    	addGeGuButton.setToolTipText("添加个股");
	            }
	        });
		btnAddGeGutotree.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                addGeGuButtonActionPerformed(evt);
	            }
	        });

		btnAddBktotree.addMouseMotionListener(new mousemotionadapter()  );
		btnAddBktotree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
//            	addSubnodeButton.setIcon(addSubnodeIcon);
//    	        addSubnodeButton.setToolTipText("Add subnode");
            }
        });
		btnAddBktotree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBanKuaiButtonActionPerformed(evt);
            }
        });
		
	   btnaddSubGPCtotree.addMouseMotionListener(new mousemotionadapter() );
	   btnaddSubGPCtotree.addMouseListener(new java.awt.event.MouseAdapter() {
           public void mouseExited(java.awt.event.MouseEvent evt) {
//           	addSubnodeButton.setIcon(addSubnodeIcon);
//   	        addSubnodeButton.setToolTipText("Add subnode");
           }
       });
	   btnaddSubGPCtotree.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
        	   int direction = ((SubnodeButton)evt.getSource()).getDirection();
               addSubGpcButtonActionPerformed(direction);
           }
       });
        
		
//		deleteButton.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
////				TreePath closestPath = cyltree.getClosestPathForLocation(arg0.getX(), arg0.getY());
////		    	cyltree.deleteNodes(closestPath);
//				
//				BkChanYeLianTreeNode delnode = (BkChanYeLianTreeNode) bkcyl.getBkChanYeLianTree().getSelectionPath().getLastPathComponent();
//				bkcyl.deleteNodes(delnode);
//				btnSaveAll.setEnabled(true);
//			}
//		});
		
		treechanyelian.addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mousePressed(java.awt.event.MouseEvent evt) {
//	            	chanYeLianTreeMousePressed(evt);
//	            	logger.debug("get action notice at bkcyl");
	    	        TreePath closestPath = treechanyelian.getClosestPathForLocation(evt.getX(), evt.getY());

	    	        if(closestPath != null) {
	    	            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
	    	            int maxY = (int) pathBounds.getMaxY();
	    	            int minX = (int) pathBounds.getMinX();
	    	            int maxX = (int) pathBounds.getMaxX();
	    	            if (evt.getY() > maxY) 
	    	            	treechanyelian.clearSelection();
	    	            else if (evt.getX() < minX || evt.getX() > maxX) 
	    	            	treechanyelian.clearSelection();
	    	        }
	    	        getReleatedInfoAndActionsForTreePathNode (closestPath);
	            }
	        });
		
	}
	
	 private class mousemotionadapter extends MouseMotionAdapter {

		 public void mouseMoved(java.awt.event.MouseEvent evt) {
           	 SubnodeButton button = (SubnodeButton) evt.getSource();
    	        String key;
    	        if (System.getProperty("os.name").startsWith("Mac OS X")) 
    	        	key = "CMD";
    	        else 
    	        	key = "CTRL";
    	        
    	        int x = evt.getX();
    	        int y = evt.getY();
    	        if (y<19 && x+y<30 && x<19) {
    	            button.setDirection(BanKuaiAndChanYeLianGUI.UP);
    	            button.setIcon(addAboveIcon);
    	            button.setToolTipText("Add above ("+key+"-UP)");
    	        }
    	        else if (y>=19 && x-y < 0 && x<19){
    	            button.setDirection(BanKuaiAndChanYeLianGUI.DOWN);
    	            button.setIcon(addBelowIcon);
    	            button.setToolTipText("Add below ("+key+"-DOWN)");
    	        }
    	        else if (x+y>30 && x-y>0){
    	            button.setDirection(BanKuaiAndChanYeLianGUI.RIGHT);
    	            button.setIcon(addChildIcon);
    	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
    	        }
    	        else {
    	            button.setDirection(BanKuaiAndChanYeLianGUI.NONE);
    	            button.setIcon(addSubnodeIcon);
    	            button.setToolTipText("Add subnode");
    	        }
           }
	    }
	
	private JUpdatedTextField tfldfindnodeintree;
	private JUpdatedTextField tfldfindgegu;
	private JButton deleteButton;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JButton addGeGuButton;
	private JButton addSubnodeButton;
	private JButton btnGenTDXCode;
	private JButton btnadddalei;
	private JButton btndeldalei;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	
	private JButton btnopencylxml;
	private JPopupMenu popupMenu;
	private JMenuItem mntmNewMenuItem;
	
	private JButton btnSaveAll;
	private JButton btnAddBktotree;
	private JScrollPane treeScrollPane;
	private JLocalDateChooser datechooser;
	private JTable tablesubcyl;
	private JTable tablebankuai;
	private JTable tablebkgegu;

	private JButton btnaddSubGPCtotree;
	private JButton btnAddSubGPC;
	private JButton btnfindnode;
	private JButton btnAddNewGPC;
	private JButton btnAddGeGutotree;
	private JButton btndelnode;
	private JButton btnrestorenode;

	private JTextArea txaDescripton;

	private JButton btndelsubgpc;

	private TagsPanel pnllblsysmanagement;

	private void initializeGui() 
	{
		JPanel paneltree = new JPanel();
		paneltree.setBorder(null);
//		paneltree.setPreferredSize(new Dimension(200, 280));
		add(paneltree, BorderLayout.WEST);
		paneltree.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
//		panel_3.setPreferredSize(new Dimension(50, 180));
		paneltree.add(panel_3, BorderLayout.EAST);
		
		btnaddSubGPCtotree = new SubnodeButton(); 
		btnaddSubGPCtotree.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		btnAddBktotree = new SubnodeButton();
		btnAddBktotree.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		btnAddGeGutotree = new SubnodeButton();
		btnAddGeGutotree.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnaddSubGPCtotree, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_panel_3.createSequentialGroup()
							.addGap(6)
							.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addComponent(btnAddBktotree, 0, 0, Short.MAX_VALUE)
								.addComponent(btnAddGeGutotree, GroupLayout.PREFERRED_SIZE, 38, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGap(223)
					.addComponent(btnaddSubGPCtotree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 224, Short.MAX_VALUE)
					.addComponent(btnAddBktotree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(94)
					.addComponent(btnAddGeGutotree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(53))
		);
		panel_3.setLayout(gl_panel_3);
		
		JPanel panel = new JPanel();
		paneltree.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		treeScrollPane = new JScrollPane(this.treechanyelian,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setPreferredSize(new Dimension(350, 250));
		treeScrollPane.grabFocus();
		treeScrollPane.getVerticalScrollBar().setValue(0);
		panel.add(treeScrollPane, BorderLayout.CENTER);
		
		
		JPanel pnlup = new JPanel();
//		pnlup.setPreferredSize(new Dimension(300, 40));
		add(pnlup, BorderLayout.NORTH);
		
		btnAddNewGPC = new JButton("\u6DFB\u52A0GPC");
		btnAddSubGPC = new JButton("\u6DFB\u52A0SUBGPC");
		pnlup.setLayout(new MigLayout("", "[75px][93px][93px]", "[23px]"));
		
		btndelsubgpc = new JButton("\u5220\u9664SUBGPC");
		pnlup.add(btndelsubgpc, "cell 2 0,alignx left,aligny top");
		pnlup.add(btnAddNewGPC, "cell 0 0,alignx left,aligny top");
		pnlup.add(btnAddSubGPC, "cell 1 0,alignx left,aligny top");
		
		JPanel pnlcenter = JPanelFactory.createPanel();
		pnlcenter.setLayout(new BoxLayout(pnlcenter, BoxLayout.PAGE_AXIS));
		add(pnlcenter, BorderLayout.CENTER);
		
		JPanel panel_1 = JPanelFactory.createPanel(new BorderLayout(0,0));
		JPanel panel_2 = JPanelFactory.createPanel(new BorderLayout(0,0));
		JPanel panel_4 = JPanelFactory.createPanel();
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));
		JPanel panel_5 = JPanelFactory.createPanel();
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		
		pnlcenter.add(panel_1);
		pnlcenter.add(panel_2);
		pnlcenter.add(panel_4);
		pnlcenter.add(panel_5);
		
		JPanel panel_16 = JPanelFactory.createPanel(new MigLayout("", "[156px][161px]", "[21px]") );
		JPanel panel_17 = JPanelFactory.createPanel(new BorderLayout(0,0) );
		panel_1.add(panel_16, BorderLayout.NORTH);
		panel_1.add(panel_17, BorderLayout.CENTER);
		
		JLabel lblgnts16 = new JLabel("\u6982\u5FF5\u63D0\u793A");
		panel_16.add(lblgnts16, "cell 0 0,grow");
		
		datechooser = new JLocalDateChooser();
		panel_16.add(datechooser, "cell 1 0,grow");
		
		JScrollPane sclpforpnl71 = new JScrollPane();
		sclpforpnl71.setPreferredSize(new Dimension(300, 80));
		panel_17.add(sclpforpnl71,BorderLayout.CENTER);
		
		txaDescripton = new JTextArea ();
		sclpforpnl71.setViewportView(txaDescripton);
		
		
		JScrollPane sclpforpnl21 = new JScrollPane();
		panel_2.add(sclpforpnl21,BorderLayout.CENTER);
		
//		TagsPanel tagspnl = new TagsPanel ();
//		panel_4.add(tagspnl);
		JScrollPane scllPnsubcyl41 = new JScrollPane();
		JScrollPane scrollPane_42 = new JScrollPane();
		panel_4.add(scllPnsubcyl41);
		panel_4.add(scrollPane_42);

		JScrollPane scrollPane_51 = new JScrollPane();
		panel_5.add(scrollPane_51);
		JScrollPane scrollPane52 = new JScrollPane();
		panel_5.add(scrollPane52);
		
		pnllblsysmanagement = new TagsPanel("所有系统关键字",null, TagsPanel.FULLCONTROLMODE);
		scllPnsubcyl41.setViewportView(pnllblsysmanagement);
		
		
	
		
		BkChanYeLianTreeNodeListTableModel bankuaimodel = new BkChanYeLianTreeNodeListTableModel ();
		tablebankuai = new JTable(bankuaimodel)
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
		scrollPane_51.setViewportView(tablebankuai);
		
		BkChanYeLianTreeNodeListTableModel bkggmodel = new BkChanYeLianTreeNodeListTableModel ();
		tablebkgegu = new JTable(bkggmodel)
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
		scrollPane52.setViewportView(tablebkgegu);
		
		
		
		

//		BkChanYeLianTreeNodeListTableModel subcylmode = new BkChanYeLianTreeNodeListTableModel ();
//		tablesubcyl = new JTable(subcylmode)
//		{
//			private static final long serialVersionUID = 1L;
//			
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch (RuntimeException e1) {
//                	e1.printStackTrace();
//                }
//                return tip;
//            } 
//		};
//		scllPnsubcyl41.setViewportView(tablesubcyl);
		
		
		
				
		
		
		
		JPanel pnldown = new JPanel();
		add(pnldown, BorderLayout.SOUTH);
		
		btnfindnode = new JButton("\u5B9A\u4F4D\u8282\u70B9");
		
		
		tfldfindnodeintree = new JUpdatedTextField();
		tfldfindnodeintree.setColumns(10);
		
		tfldfindgegu =  new JUpdatedTextField();
		tfldfindgegu.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("                             ");
		pnldown.setLayout(new MigLayout("", "[93px][66px][][][54px][54px][66px][93px][93px]", "[23px]"));
		pnldown.add(btnfindnode, "cell 0 0,alignx left,aligny top");
		pnldown.add(tfldfindnodeintree, "cell 1 0,alignx left,aligny center");
		
		btndelnode = new JButton("\u5220\u9664\u8282\u70B9");
		
		pnldown.add(btndelnode, "cell 2 0");
		
		btnrestorenode = new JButton("\u6062\u590D\u8282\u70B9");
		pnldown.add(btnrestorenode, "cell 3 0");
		pnldown.add(tfldfindgegu, "flowx,cell 5 0 2 1,alignx left,aligny center");
		pnldown.add(lblNewLabel, "cell 4 0,alignx left,aligny center");
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u8282\u70B9");
		pnldown.add(btnfindgegu, "cell 7 0,alignx left,aligny top");
		
		//
		java.util.ArrayList<java.awt.Image> imageList = new java.util.ArrayList<java.awt.Image>();
	      imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo16.png")).getImage());
	      imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo18.png")).getImage());
	      imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo20.png")).getImage());
	      imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo24.png")).getImage());
	      imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo32.png")).getImage());
	      imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo36.png")).getImage());
//	      setIconImages(imageList);
	      addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
	      addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
	      addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
	      addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));

	}
	
	
//	private void initializeGui2() 
//	{
//		
//		JPanel panelcyltree = new JPanel();
//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
//					.addGap(103)
//					.addComponent(panelcyltree, 0, 0, Short.MAX_VALUE)
//					.addGap(38))
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(panelcyltree, GroupLayout.PREFERRED_SIZE, 659, GroupLayout.PREFERRED_SIZE)
//					.addContainerGap(269, Short.MAX_VALUE))
//		);
//		
//		addSubnodeButton = new SubnodeButton();
//		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
//		
//		addGeGuButton = new SubnodeButton();
//		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
//		
//		btnAddSubBk = new JButton("\u589E\u52A0\u5B50\u677F\u5757");
//		JScrollPane scrollPanesubbk = new JScrollPane();
//		
//		scrollPanegegu = new JScrollPane();
//		
//		deleteButton = new JButton("\u5220\u9664\u8282\u70B9");
//		deleteButton.setIcon(null);
//		
//		tfldfindbk = new JUpdatedTextField();
//		tfldfindbk.setColumns(10);
//		
//		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
//		
//		tfldfindgegu = new JUpdatedTextField();
//		tfldfindgegu.setColumns(10);
//		
//		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
//		
//		btnopencylxml = new JButton("\u6253\u5F00XML");
//		
//		
//		btnSaveAll = new JButton("\u4FDD\u5B58");
//		btnSaveAll.setForeground(Color.RED);
//		
//		btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
//		btnadddalei.setEnabled(false);
//		
//		btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
//		btndeldalei.setEnabled(false);
//		
//		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
//		
//		treeScrollPane = new JScrollPane();
//		
////		treeScrollPane.setViewportView(treechanyelian);
//		treeScrollPane.grabFocus();
//		treeScrollPane.getVerticalScrollBar().setValue(0);
//		
//		
//		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
//		gl_panelcyltree.setHorizontalGroup(
//			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_panelcyltree.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING, false)
//						.addGroup(gl_panelcyltree.createSequentialGroup()
//							.addComponent(deleteButton)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(btnfindbk))
//						.addGroup(gl_panelcyltree.createSequentialGroup()
//							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING, false)
//								.addGroup(gl_panelcyltree.createSequentialGroup()
//									.addComponent(btnadddalei)
//									.addPreferredGap(ComponentPlacement.UNRELATED)
//									.addComponent(btndeldalei))
//								.addComponent(treeScrollPane))
//							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//								.addGroup(gl_panelcyltree.createSequentialGroup()
//									.addPreferredGap(ComponentPlacement.RELATED)
//									.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
//								.addGroup(gl_panelcyltree.createSequentialGroup()
//									.addGap(18)
//									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//										.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
//										.addGroup(gl_panelcyltree.createSequentialGroup()
//											.addComponent(btnSaveAll)
//											.addPreferredGap(ComponentPlacement.UNRELATED)
//											.addComponent(btnAddSubBk)))))))
//					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_panelcyltree.createSequentialGroup()
//							.addGap(137)
//							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//								.addGroup(gl_panelcyltree.createSequentialGroup()
//									.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//									.addPreferredGap(ComponentPlacement.RELATED)
//									.addComponent(btnfindgegu))
//								.addComponent(scrollPanegegu, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
//								.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
//						.addGroup(gl_panelcyltree.createSequentialGroup()
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(btnGenTDXCode)
//							.addPreferredGap(ComponentPlacement.UNRELATED)
//							.addComponent(btnopencylxml)))
//					.addContainerGap(11, Short.MAX_VALUE))
//		);
//		gl_panelcyltree.setVerticalGroup(
//			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_panelcyltree.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
//						.addComponent(btnadddalei)
//						.addComponent(btndeldalei)
//						.addComponent(btnSaveAll)
//						.addComponent(btnAddSubBk)
//						.addComponent(btnGenTDXCode)
//						.addComponent(btnopencylxml, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
//					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_panelcyltree.createSequentialGroup()
//							.addGap(29)
//							.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//							.addPreferredGap(ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
//							.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//							.addGap(91))
//						.addGroup(gl_panelcyltree.createSequentialGroup()
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//								.addComponent(treeScrollPane, GroupLayout.PREFERRED_SIZE, 413, GroupLayout.PREFERRED_SIZE)
//								.addGroup(gl_panelcyltree.createSequentialGroup()
//									.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
//									.addPreferredGap(ComponentPlacement.RELATED)
//									.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE)))
//							.addPreferredGap(ComponentPlacement.RELATED)))
//					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.TRAILING)
//						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
//							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
//								.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//								.addComponent(btnfindbk))
//							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
//								.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//								.addComponent(btnfindgegu)))
//						.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
//					.addGap(13))
//		);
//		
//		BkChanYeLianTreeNodeListTableModel subcylmode = new BkChanYeLianTreeNodeListTableModel ();
//		tablesubcyl = new JTable(subcylmode)
//		{
//			private static final long serialVersionUID = 1L;
//			
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch (RuntimeException e1) {
//                	e1.printStackTrace();
//                }
//                return tip;
//            } 
//		};
//		scrollPanesubbk.setViewportView(tablesubcyl);
//		
//		
//		BkChanYeLianTreeNodeListTableModel bkstmodel = new BkChanYeLianTreeNodeListTableModel ();
//		tablebkgegu = new  JTable(bkstmodel)
//		{
//			private static final long serialVersionUID = 1L;
//			
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch (RuntimeException e1) {
//                	e1.printStackTrace();
//                }
//                return tip;
//            } 
//		};
//		scrollPanegegu.setViewportView(tablebkgegu);
//		
//		
//		//个股table也可以加个股新闻
//		JPopupMenu popupMenuGeguNews = new JPopupMenu();
//		JMenuItem menuItemAddNews = new JMenuItem("添加个股新闻");
//		JMenuItem menuItemMakeLongTou = new JMenuItem("设置股票权重");
//		popupMenuGeguNews.add(menuItemAddNews);
//		popupMenuGeguNews.add(menuItemMakeLongTou);
//		tablebkgegu.setComponentPopupMenu(popupMenuGeguNews);
//		menuItemAddNews.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
//
//				addGeGuNews ();
//			}
//			
//		});
//		menuItemMakeLongTou.setComponentPopupMenu(popupMenuGeguNews);
//		menuItemMakeLongTou.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				setGeGuWeightInBanKuai ();
//			}
//			
//		});
//
//		panelcyltree.setLayout(gl_panelcyltree);
		
		//ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		//tableZdgzBankDetails = new  JTable(xmlaccountsmodel)
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel ();
//		int preferedwidth = 170;
//		
//		
//		
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
//		tableCurZdgzbk = new  JTable(curzdgzbkmodel) {
//		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
//		setLayout(groupLayout);
//		
//		
//		
//		java.util.ArrayList<java.awt.Image> imageList = new java.util.ArrayList<java.awt.Image>();
//        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo16.png")).getImage());
//        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo18.png")).getImage());
//        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo20.png")).getImage());
//        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo24.png")).getImage());
//        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo32.png")).getImage());
//        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo36.png")).getImage());
//        
//        //setIconImages(imageList);
//        addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
//        addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
//        addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
//        addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));
//        
//        //tree 的弹出菜单
////        popupMenu = new BanKuaiPopUpMenu(this.stockInfoManager,treechanyelian);
////		addPopup(treechanyelian, popupMenu);
//	}


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

class BkChanYeLianTreeNodeListTableModel extends DefaultTableModel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] jtableTitleStrings = { "代码", "名称"};
	private List<BkChanYeLianTreeNode> nodelist; //包含代码和名称
	
	public BkChanYeLianTreeNodeListTableModel ()
	{
//		nodelist = new ArrayList<BkChanYeLianTreeNode> ();
	}
	public void setTableTitles (String[] tabletitle)
	{
		this.jtableTitleStrings = tabletitle;
	}

	public void refresh(List<BkChanYeLianTreeNode> nodelist)  
	{
		if(nodelist != null) {
			this.nodelist = nodelist;
			this.fireTableDataChanged();
		}
		
	}
	public void addRow (BkChanYeLianTreeNode newnode) 
	{
		if(this.nodelist == null)
			nodelist = new ArrayList<BkChanYeLianTreeNode> ();
		else
			nodelist.add(newnode);
		
		this.fireTableDataChanged();
	}
	public BkChanYeLianTreeNode getNode (int row)
	{
		return this.nodelist.get(row);
	}
	public String getNodeName(int row) 
	{
		return nodelist.get(row).getMyOwnName();
	}
	public String getNodeCode(int row) 
	{
		return nodelist.get(row).getMyOwnCode();
	}
	public Integer findNodeByNameOrCode (String nameorcode)
	{
		if(nodelist == null)
			return -1;
		
		for(int i =0;i<this.nodelist.size();i++) {
			BkChanYeLianTreeNode tmpnode = this.nodelist.get(i);
			if(tmpnode.getMyOwnCode().equals(nameorcode) || tmpnode.getMyOwnName().equals(nameorcode)) 
				return i;
		}
		
		return -1;
	}
	
	
	public String getColumnName(int column) 
	{ 
    	return jtableTitleStrings[column];
    }//设置表格列名 
    @Override
    public int getColumnCount() 
    {
        return jtableTitleStrings.length;
    } 

    public boolean isCellEditable(int row,int column) {
    	return false;
	}
    
	 public int getRowCount() 
	 {
		 if(this.nodelist == null)
			 return 0;
		 else 
			 return this.nodelist.size();
	 }
	 
	 public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	switch (columnIndex) {
	    	case 0:
                value = nodelist.get(rowIndex).getMyOwnCode();
                break;
            case 1:
            	value = nodelist.get(rowIndex).getMyOwnName();
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
	 
	 public void deleteAllRows()
	 {
	    	if(this.nodelist == null)
				 return ;
			 else 
				 nodelist.clear();
	    	
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


