package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.toedter.calendar.JDateChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.event.MouseAdapter;

public class ChanYeLianNewsPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public ChanYeLianNewsPanel(String bankuaiorstockcode) {
		this.myowncode = bankuaiorstockcode;
		bkopt = new BanKuaiDbOperation(); 
		
		initializeGui ();
		createEvents ();
		
		initializeNews ();
		
		
	}

	private String myowncode;
	private BanKuaiDbOperation bkopt;
	private ArrayList<ChanYeLianNews> allnewlist;
	private ArrayList<ChanYeLianNews> curnewlist;
//	private static Logger logger = Logger.getLogger(ChanYeLianNewsPanel.class);
	
	private void initializeNews() 
	{
		allnewlist = bkopt.getBanKuaiRelatedNews ("All");
		((NewsTableModel)tableAllNews.getModel()).refresh(allnewlist);

		curnewlist = bkopt.getBanKuaiRelatedNews (this.myowncode);
		((NewsTableModel)tableCurCylNews.getModel()).refresh(curnewlist);
	}
	public ChanYeLianNews getInputedNews ()
	{
		return this.panelNewNews;
	}

	private void createEvents() 
	{
		tableAllNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				int row = tableAllNews.getSelectedRow();
				if(row <0)
					return;
				ChanYeLianNews selectnews = ((NewsTableModel)tableAllNews.getModel()).getChanYeLianNews(row);
				panelNewNews.resetInput ();
				panelNewNews.setNewsId(selectnews.getNewsId()); 
				panelNewNews.setNewsTitle(selectnews.getNewsTitle());
				panelNewNews.setGenerateDate(selectnews.getGenerateDate());
				panelNewNews.setNewsSlackUrl(selectnews.getNewsSlackUrl());
				panelNewNews.setKeyWords(selectnews.getKeyWords() );
			}
		});
		
		tableCurCylNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				int row = tableCurCylNews.getSelectedRow();
				if(row <0)
					return;
				ChanYeLianNews selectnews = ((NewsTableModel)tableCurCylNews.getModel()).getChanYeLianNews(row);
				panelNewNews.resetInput ();
				panelNewNews.setNewsId(selectnews.getNewsId()); 
				panelNewNews.setNewsTitle(selectnews.getNewsTitle());
				panelNewNews.setGenerateDate(selectnews.getGenerateDate());
				panelNewNews.setNewsSlackUrl(selectnews.getNewsSlackUrl());
				panelNewNews.setKeyWords(selectnews.getKeyWords() );
				
			}
		});
		
		btnDeleteNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				ArrayList<ChanYeLianNews> selectednews = ((NewsTableModel)tableAllNews.getModel()).getSelectedNews();
				for(ChanYeLianNews aseltnews:selectednews) {
					bkopt.deleteBanKuaiNews("ALL", aseltnews);
					
					int newid = aseltnews.getNewsId();
					int indexofnewid = ((NewsTableModel)tableCurCylNews.getModel()).getIndexOfChanYeLianNews(newid);
					if(indexofnewid >0)
						curnewlist.remove(indexofnewid);
				}
				((NewsTableModel)tableCurCylNews.getModel()).refresh(curnewlist);
				
				allnewlist.removeAll(selectednews);
				((NewsTableModel)tableAllNews.getModel()).refresh(allnewlist);
				
			}
		});
		
		btnRemoveNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ArrayList<ChanYeLianNews> selectednews = ((NewsTableModel)tableCurCylNews.getModel()).getSelectedNews();
				for(ChanYeLianNews aseltnews:selectednews) {
					bkopt.deleteBanKuaiNews(myowncode, aseltnews);
					
					
				}
				curnewlist.removeAll(selectednews);
				((NewsTableModel)tableCurCylNews.getModel()).refresh(curnewlist);
			}
		});
		
		
		btnAddToOwnNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ChanYeLianNews tmpcylnews = new ChanYeLianNews();
				tmpcylnews.setNewsId(panelNewNews.getNewsId()); 
				tmpcylnews.setNewsTitle(panelNewNews.getNewsTitle());
				tmpcylnews.setGenerateDate(panelNewNews.getGenerateDate());
				tmpcylnews.setNewsSlackUrl(panelNewNews.getNewsSlackUrl());
				tmpcylnews.setKeyWords(panelNewNews.getKeyWords() );
				panelNewNews.resetInput ();
				
				bkopt.addBanKuaiNews(myowncode, tmpcylnews); 
				
				curnewlist.add(tmpcylnews);
				((NewsTableModel)tableCurCylNews.getModel()).refresh(curnewlist);
				
				allnewlist.add(tmpcylnews);
				((NewsTableModel)tableAllNews.getModel()).refresh(allnewlist);
				
			}
		});
		
		addNewToOwnNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				ArrayList<ChanYeLianNews> selectednews = ((NewsTableModel)tableAllNews.getModel()).getSelectedNews();
				for(ChanYeLianNews aseltnews:selectednews) {
					if(!aseltnews.getNewsRelatedBanKuai().contains(myowncode) ) {
						bkopt.addBanKuaiNews(myowncode, aseltnews);
						curnewlist.add(aseltnews);
						
					}
				}

				((NewsTableModel)tableCurCylNews.getModel()).refresh(curnewlist);
			}
		});
		
	}
	private JTable tableAllNews;
	private JTable tableCurCylNews;
	private JButton btnRemoveNews;
	private JButton addNewToOwnNews;
	private JButton btnDeleteNews;
	private JButton btnAddToOwnNews;
	private ChanYeLianNews panelNewNews;
	private JLabel labelcylcode;
	private JLabel label_1;
	private void initializeGui() 
	{
		
		JScrollPane scrollPane = new JScrollPane();
		
		NewsTableModel allcylnewsmodel = new NewsTableModel ();
		tableAllNews = new JTable(allcylnewsmodel){
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
		
		tableAllNews.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(40);
		tableAllNews.getTableHeader().getColumnModel().getColumn(0).setMinWidth(40);
		tableAllNews.getTableHeader().getColumnModel().getColumn(0).setWidth(40);
		tableAllNews.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(40);
		tableAllNews.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(80);
		tableAllNews.getTableHeader().getColumnModel().getColumn(2).setMinWidth(80);
		tableAllNews.getTableHeader().getColumnModel().getColumn(2).setWidth(80);
		tableAllNews.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(80);


		scrollPane.setViewportView(tableAllNews);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		addNewToOwnNews = new JButton("\u52A0\u5165\u5230\u5F53\u524D\u65B0\u95FB");
		
		
		btnRemoveNews = new JButton("\u79FB\u9664\u51FA\u5F53\u524D\u65B0\u95FB");
		
		
		btnDeleteNews = new JButton("\u5220\u9664\u65B0\u95FB");
		
		
		btnAddToOwnNews = new JButton("\u6DFB\u52A0\u65B0\u95FB");
		
		
		NewsTableModel curcylnewsmodel = new NewsTableModel ();
		tableCurCylNews = new JTable(curcylnewsmodel){
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
		
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(40);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(0).setMinWidth(40);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(0).setWidth(40);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(40);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(80);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(2).setMinWidth(80);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(2).setWidth(80);
		tableCurCylNews.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(80);
		scrollPane_1.setViewportView(tableCurCylNews);
		
		 panelNewNews = new ChanYeLianNews();
		
		labelcylcode = new JLabel("板块产业链"+this.myowncode+"当前新闻");
		
		label_1 = new JLabel("5\u65E5\u6240\u6709\u65B0\u95FB");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panelNewNews, GroupLayout.PREFERRED_SIZE, 432, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(labelcylcode)
							.addPreferredGap(ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
							.addComponent(btnAddToOwnNews)
							.addGap(42))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnDeleteNews)
							.addContainerGap(359, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(label_1)
									.addGap(94)
									.addComponent(addNewToOwnNews)
									.addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
									.addComponent(btnRemoveNews))
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
								.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
							.addGap(23))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelNewNews, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(labelcylcode)
							.addGap(12))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnAddToOwnNews)
							.addGap(18)))
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
					.addGap(25)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_1)
						.addComponent(addNewToOwnNews)
						.addComponent(btnRemoveNews))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDeleteNews)
					.addGap(23))
		);
		setLayout(groupLayout);

		
	}
}


class NewsTableModel extends AbstractTableModel 
{
	private ArrayList<ChanYeLianNews> cylnewslists; 
	
	String[] jtableTitleStrings = { "选择", "新闻标题", "录入日期"};
	
	NewsTableModel ()
	{
		
	}
	
	public void refresh  ( ArrayList<ChanYeLianNews> cylnewslists2)
	{
		this.cylnewslists = cylnewslists2;
		
		this.fireTableDataChanged();
	}

	public void addRow(ChanYeLianNews newnews)
	{
		this.cylnewslists.add(newnews);
		this.fireTableDataChanged();
	}

//	public boolean isSelected (int row) 
//	{
//		boolean isselected = (Boolean)getValueAt(row,0);
//		return isselected;
//	}
	
	public ArrayList<ChanYeLianNews> getSelectedNews ()
	{
		ArrayList<ChanYeLianNews> tmpnewslist = new ArrayList<ChanYeLianNews> ();
		for(int i=0;i<this.getRowCount();i++) {
			if((Boolean)getValueAt(i,0))
				tmpnewslist.add(this.cylnewslists.get(i));
		}
		
		return tmpnewslist;
	}
	public int getIndexOfChanYeLianNews (int cylnewsid) 
	{
		for(int i=0;i<this.cylnewslists.size();i++) {
			int tmpid = this.cylnewslists.get(i).getNewsId();
			if(tmpid == cylnewsid)
				return i;
		}
		
		return -1;
	}
	
	 public int getRowCount() 
	 {
		 if(this.cylnewslists == null)
			 return 0;
		 else 
			 return this.cylnewslists.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(this.cylnewslists.isEmpty())
	    		return null;
	    	
	    	ChanYeLianNews tmpcylnews = this.cylnewslists.get(rowIndex);
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = tmpcylnews.isSelected();
                break;
            case 1:
            	value = tmpcylnews.getNewsTitle();
                break;
            case 2:
            	value = tmpcylnews.getGenerateDate();
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
			          clazz = LocalDate.class;
			          break;
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	if(column == 0) {
	    		ChanYeLianNews tmpcylnews = this.cylnewslists.get(row);
	    		tmpcylnews.setIsSelected (!tmpcylnews.isSelected());
	    		this.fireTableDataChanged();
	    		return tmpcylnews.isSelected();
	    	}
	    	else 
	    		return false;
		}
	    public ChanYeLianNews getChanYeLianNews (int row) 
	    {
	    	return cylnewslists.get(row) ;
	    }
 
	    
	    public void deleteAllRows()
	    {
	    	if(this.cylnewslists == null)
				 return ;
			 else 
				 cylnewslists.clear();
	    }
}