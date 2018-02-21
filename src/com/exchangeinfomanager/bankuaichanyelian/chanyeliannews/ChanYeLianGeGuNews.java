package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import com.exchangeinfomanager.StockCalendar.View;
import com.exchangeinfomanager.StockCalendar.view.Cache;
import com.exchangeinfomanager.StockCalendar.view.InsertedMeeting;
import com.exchangeinfomanager.StockCalendar.view.Meeting;
import com.exchangeinfomanager.StockCalendar.view.MeetingDialog;
import com.exchangeinfomanager.StockCalendar.view.MeetingService;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;

public class ChanYeLianGeGuNews extends View
{
	private JTable tableCurCylNews;
	private JButton btnAddNews;
	private MeetingService meetingService;
	private String title;
	private String nodecode;
	private static Logger logger = Logger.getLogger(BanKuaiAndChanYeLian.class);

	public ChanYeLianGeGuNews(MeetingService meetingService, Cache cache,String title)
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
					Meeting meeting = new Meeting("新闻标题",LocalDate.now(),
	                     "描述", "关键词", new HashSet<>(),"SlackURL",nodecode);
	                getCreateDialog().setMeeting(meeting);
	                getCreateDialog().setVisible(true);
			}
		});
		
		tableCurCylNews.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tableMouseClickActions (arg0);
			}
		});
		
	}
	
	public void updateNewsToABkGeGu (InsertedMeeting news,String bkggcode)
	{
		news.addMeetingToSpecificOwner(bkggcode);
		
		try {
			meetingService.updateMeeting(news);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public InsertedMeeting getCurSelectedNews ()
	{
		int row = tableCurCylNews.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一条新闻！","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		
		InsertedMeeting selectednews = ((NewsTableModel)tableCurCylNews.getModel()).getNews(row);
		
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

					  InsertedMeeting stocknews = ((NewsTableModel)tableCurCylNews.getModel()).getNews(row);
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
		
		btnAddNews = new JButton("\u6DFB\u52A0\u65B0\u95FB");

		
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
		
		NewsTableModel newsmodel = new NewsTableModel ();
		tableCurCylNews = new JTable(newsmodel);
		
		scrollPane.setViewportView(tableCurCylNews);
		setLayout(groupLayout);
		
	}

	@Override
	public void onMeetingChange(Cache cache) 
	{
		Collection<InsertedMeeting> meetings = cache.produceMeetings();
		logger.debug(meetings.size());
		((NewsTableModel)tableCurCylNews.getModel()).refresh(meetings);
	}

	@Override
	public void onLabelChange(Cache cache) 
	{
		
		
	}
}
