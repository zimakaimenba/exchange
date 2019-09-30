package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ZhiShuGuanJianRiQi;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.StockCalendar.View;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianGeGuNews;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;

public class ZhiShuGJRQView extends View 
{
	private JTable tableCurCylNews;
	private JButton btnAddNews;
	private EventService meetingService;
	private String title;
	private String nodecode;
	private static Logger logger = Logger.getLogger(ChanYeLianGeGuNews.class);

	public ZhiShuGJRQView(EventService meetingService, Cache cache,String title)
	{
		super(meetingService, cache);
		this.meetingService = meetingService;
		this.title = title;
		this.nodecode = cache.getNodeCode();
		
		
		cache.addCacheListener(this);
		
		initializeGui ();
		createEvents ();
		
        this.onMeetingChange(cache);
	}

	private void createEvents() 
	{
		btnAddNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				addNews ();
				
			}
		});
		
		tableCurCylNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tableMouseClickActions (arg0);
			}
		});
		
	}
	
	protected void addNews() 
	{
		String newsbelogns = cache.getNodeCode();
		if(newsbelogns.toLowerCase().equals("all") )
			newsbelogns = "999999";
		
		Meeting meeting = new Meeting("指数日期",LocalDate.now(),
                     "描述", "关键词", new HashSet<>(),"SlackURL",newsbelogns,Meeting.ZHISHUDATE);
        getCreateDialog().setMeeting(meeting);
        getCreateDialog().setVisible(true);
		
	}

	public void updateNewsToABkGeGu (InsertedMeeting news,String bkggcode)
	{
		Boolean addresult = news.addMeetingToSpecificOwner(bkggcode);
		if(addresult) {
			try {
				meetingService.updateMeeting(news);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public InsertedMeeting getCurSelectedNews ()
	{
		int row = tableCurCylNews.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一条新闻！","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		
		int  model_row = tableCurCylNews.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引
		InsertedMeeting selectednews = ((ZhiShuGJRQModel)tableCurCylNews.getModel()).getZhiShuGJRQ(model_row);
		
//		InsertedMeeting selectednews = ((NewsTableModel)tableCurCylNews.getModel()).getNews(row);
		return selectednews;
		
	}
	
	private void tableMouseClickActions (MouseEvent arg0)
	{
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
						
//						int model_row = tableCurCylNews.convertRowIndexToModel(row);//将视图中的行索引转化为数据模型中的行索引

					  InsertedMeeting stocknews = ((ZhiShuGJRQModel)tableCurCylNews.getModel()).getZhiShuGJRQ(model_row);
					  Optional<InsertedMeeting> meeting = getCache().produceMeetings()
                              .stream()
                              .filter(m -> m.getID() == Integer.valueOf(stocknews.getID()))
                              .findFirst();
					  getModifyDialog().setMeeting(meeting.get());
		              getModifyDialog().setVisible(true);
				 }

	}

	private void initializeGui() 
	{
		JLabel lbltitle = new JLabel(title);
		
		btnAddNews = new JButton("添加关键日期");

		
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
		
		ZhiShuGJRQModel newsmodel = new ZhiShuGJRQModel ();
		tableCurCylNews = new JTable(newsmodel) {
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
		((ZhiShuGJRQModel)tableCurCylNews.getModel()).refresh(meetings);
		

		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)tableCurCylNews.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 1; //优先排序占比增长
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	
	}

	@Override
	public void onLabelChange(Cache cache) 
	{
		
		
	}

}
