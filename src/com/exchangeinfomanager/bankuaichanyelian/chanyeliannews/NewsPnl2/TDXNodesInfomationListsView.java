package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.exchangeinfomanager.StockCalendar.JUpdatedLabel;
import com.exchangeinfomanager.StockCalendar.View;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;

public class TDXNodesInfomationListsView extends View
{

	private EventService meetingService;
	private String nodecode;
	
		public TDXNodesInfomationListsView(EventService meetingService,Cache cache,AbstractTableModel tablemodle)
	{
		super(meetingService, cache);
		this.meetingService = meetingService;
		this.nodecode = cache.getNodeCode();
		this.cache = cache;
	
		cache.addCacheListener(this);
		
		initializeGui (tablemodle);
		createEvents ();
		
        this.onMeetingChange(cache);
	}
		
	public static final String ANEWSADDED = "addednews";
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
	}
	
	protected  Meeting addNews() 
	{
		String newsbelogns = cache.getNodeCode();
		if(newsbelogns.toLowerCase().equals("all") )
			newsbelogns = "000000";
		
		Meeting meeting = new Meeting("新闻标题",LocalDate.now(),
                     "描述", "关键词", new HashSet<>(),"SlackURL",newsbelogns,Meeting.NODESNEWS);
        getCreateDialog().setMeeting(meeting);
        getCreateDialog().setVisible(true);
        
        return meeting;
	}
	
	protected  Meeting addZhiShuGJRQ ()
	{
		String newsbelogns = cache.getNodeCode();
		if(newsbelogns.toLowerCase().equals("all") )
			newsbelogns = "999999";
		
		Meeting meeting = new Meeting("指数日期",LocalDate.now(),
                    "描述", "关键词", new HashSet<>(),"SlackURL",newsbelogns,Meeting.ZHISHUDATE);
	    getCreateDialog().setMeeting(meeting);
	    getCreateDialog().setVisible(true);
	    
	    return meeting;
	}
	
	private void createEvents() 
	{
		btnAddNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Integer meetingtype = ((InformationTableModelBasic)tableCurCylNews.getModel()).getMainInforTypeForCreating();
				Meeting mt = null;
				if(meetingtype == Meeting.NODESNEWS)
					mt = addNews ();
				else if(meetingtype == Meeting.ZHISHUDATE)
					mt = addZhiShuGJRQ ();
				
				onMeetingChange(cache);
				
				InsertedMeeting selectednews = ((InformationTableModelBasic)tableCurCylNews.getModel()).getLastestInformation();
				if(selectednews!= null && mt.getTitle().equals(selectednews.getTitle() )  ) { //无法取得用户是否取消添加的状态，只好用这种方式来确定用户是否真的已经加了一条新新闻
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, TDXNodesInfomationListsView.ANEWSADDED, "", selectednews );
		            pcs.firePropertyChange(evtzd);
				}
			}
		});
		
		tableCurCylNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int  view_row = tableCurCylNews.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
				int  view_col = tableCurCylNews.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
				int  model_row = tableCurCylNews.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
				int  model_col = tableCurCylNews.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
				 
        		if (arg0.getClickCount() == 1) {
					 
        		}
        		 if (arg0.getClickCount() == 2) {
				 
					 int row = tableCurCylNews.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一条新闻！","Warning",JOptionPane.WARNING_MESSAGE);
							return ;
						}
						
					  InsertedMeeting stocknews = ((InformationTableModelBasic)tableCurCylNews.getModel()).getRequiredInformationOfTheRow(model_row);
					  Optional<InsertedMeeting> meeting = getCache().produceMeetings()
                              .stream()
                              .filter(m -> m.getID() == Integer.valueOf(stocknews.getID()))
                              .findFirst();
					  getModifyDialog().setMeeting(meeting.get());
		              getModifyDialog().setVisible(true);
				 }

			}
		});
		
	}
	/*
	 * 
	 */
	public InsertedMeeting getCurSelectedNews ()
	{
		int row = tableCurCylNews.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一条新闻！","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		
		int  model_row = tableCurCylNews.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
		InsertedMeeting selectednews = ((InformationTableModelBasic)tableCurCylNews.getModel()).getRequiredInformationOfTheRow(model_row);
		
		return selectednews;
	}
	
	private JTable tableCurCylNews;
	private JButton btnAddNews;
	JLabel lbltitle;
	private void initializeGui(AbstractTableModel tablemodle) 
	{
		lbltitle = new JLabel();
		
		btnAddNews = new JButton("添加");

		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 431, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lbltitle)
							.addPreferredGap(ComponentPlacement.RELATED, 296, Short.MAX_VALUE)
							.addComponent(btnAddNews)
							.addContainerGap(9, Short.MAX_VALUE))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbltitle)
						.addComponent(btnAddNews))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		tableCurCylNews = new JTable(tablemodle) {
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
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        Object value = getModel().getValueAt(row, col);
		        
		        Color foreground = Color.BLACK, background = Color.white; 
		        InformationTableModelBasic tablemodel =  (InformationTableModelBasic)this.getModel() ;
		        int modelRow = this.convertRowIndexToModel(row);
		        InsertedMeeting event = tablemodel.getRequiredInformationOfTheRow(modelRow);
		        
		        Collection<InsertedMeeting.Label> labels = cache.produceLabels();
		        for (Meeting.Label l : labels) { //有LABEL的情况
                    if (l.isActive() && event.getLabels().contains(l)) {
                        background = l.getColor();
                    }
                }
	        
		        comp.setBackground(background);
		    	comp.setForeground(foreground);

		    	return comp;
		    }

		};
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableCurCylNews.getModel());
		tableCurCylNews.setRowSorter(sorter);
		
		scrollPane.setViewportView(tableCurCylNews);
		setLayout(groupLayout);
		
	}

	@Override
	public void onMeetingChange(Cache cache) 
	{
		Collection<InsertedMeeting> meetings = cache.produceMeetings();
		((InformationTableModelBasic)tableCurCylNews.getModel()).refresh(meetings);
		
//		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)tableCurCylNews.getRowSorter();
//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//		int columnIndexToSort = 1; //优先排序
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
	}

	@Override
	public void onLabelChange(Cache cache) {
		// TODO Auto-generated method stub
		
	}

	public void setListsViewTitle(String title) 
	{
		if(this.nodecode.equals("ALL"))
			lbltitle.setText("所有:" + title);
		else
			lbltitle.setText(this.nodecode + ":" + title);
		
	}

}
