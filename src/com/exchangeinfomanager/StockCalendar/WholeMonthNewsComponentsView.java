package com.exchangeinfomanager.StockCalendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.ExternalNewsType.ChangQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.CreateExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.ExternalNewsType;
import com.exchangeinfomanager.News.ExternalNewsType.GuanZhu;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.News.ExternalNewsType.ModifyExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShi;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShi;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLang;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLangServices;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class WholeMonthNewsComponentsView extends View 
{

	protected JPanel cqjlpnl = new JPanel ();
	private ServicesForNews svsofexternalnews;
	protected JPanel contentpnl;
	private String viewtitle;
	protected JLabel titleLable;
	protected JPanel pnlmonthnews;
	protected JPanel pnmmonthnresnorth;

	public WholeMonthNewsComponentsView(ServicesForNews meetingServices,String title)
	{
		super(meetingServices);
		
		this.viewtitle = title;
		
		this.svsofexternalnews = meetingServices;
		
		this.svsofexternalnews.getCache().addCacheListener(this);
		
		this.onNewsChange(this.svsofexternalnews.getCache()); //获取本月的信息
        
        this.initCompView();
	}
	
	 private void initCompView() 
	 {
//	        super.setBackground(ColorScheme.BACKGROUND);
	        super.setLayout(new BorderLayout());
	        super.add(this.cqjlpnl,BorderLayout.CENTER);
	 }

	public void onNewsChange(NewsCache cache) 
	{
		LocalDate firstdayofmonth = this.initView();
		
		for (Iterator<NewsCache> lit = super.caches.iterator(); lit.hasNext(); ) {
    		NewsCache f = lit.next();
    		
    		updateView (f, firstdayofmonth );
		}
		
		this.cqjlpnl.validate();
        this.cqjlpnl.repaint();
		
	}

	protected LocalDate initView() 
	{
		this.cqjlpnl.removeAll();
		this.cqjlpnl.setLayout(new BorderLayout() );
        
        LocalDate firstDayInMonth = super.getDate().withDayOfMonth(1);
        //每月新闻
       	JScrollPane sclpmonthnews = new JScrollPane ();
       	pnlmonthnews = new JPanel();
       	pnlmonthnews.setName(firstDayInMonth + "CHANGQIGUANZHU");
        pnlmonthnews.setLayout(new BorderLayout());
        pnlmonthnews.setBackground(ColorScheme.BACKGROUND);
        pnlmonthnews.addMouseListener(new CreateController(firstDayInMonth));
        
        pnmmonthnresnorth = new JPanel();
                
        titleLable = new JLabel(this.viewtitle);
        titleLable.setForeground(ColorScheme.GREY_LINE_DARKER);
        titleLable.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 5));
        titleLable.setBackground(ColorScheme.BACKGROUND);
        titleLable.setOpaque(true);
        
        pnmmonthnresnorth.add(titleLable);
        
        contentpnl = JPanelFactory.createPanel(new WrapLayout(WrapLayout.LEFT, 5, 5));
        contentpnl.setName("CHANGQIGUANZHU" + "CONTENT");
        
        pnlmonthnews.add(pnmmonthnresnorth, BorderLayout.PAGE_START);
        pnlmonthnews.add(contentpnl, BorderLayout.CENTER); //中间用来显示信息的panel

        sclpmonthnews.setViewportView (pnlmonthnews);
        
        this.cqjlpnl.add(sclpmonthnews, BorderLayout.CENTER);

        return firstDayInMonth;
	}

	private void updateView (NewsCache cache,LocalDate firstdayofmonth) 
	{
		Collection<News> meetings = cache.produceNews(firstdayofmonth);
        Collection<InsertedNews.Label> labels = cache.produceLabels();

        for (News m : meetings) {
            LocalDate mDate = m.getStart();
            LocalDate superdate = super.getDate();
            String actioncode = m.getNewsOwnerCodes();
            LocalDate firstDayInMonth = mDate.withDayOfMonth(1);
            
            if( !( cache.getServicesForNews() instanceof ChangQiGuanZhuServices) ) //除长期记录外，都要关心年是否一样
            	if ( mDate.getYear() != super.getDate().getYear()    )  
            		continue;
            
            if( ! mDate.getMonth().equals(super.getDate().getMonth()) )
            	continue;
        	
            if (m.getLabels().isEmpty()) {
                	JUpdatedLabel label = getFormatedLabelForNoneLabel (m);
                   	this.contentpnl.add(label);
                    continue;
            }
                
            for (News.Label l : labels) {
                    if (m.getLabels().contains(l)) {
                    	JUpdatedLabel label = getFormatedLabelForWithLabels (m,l);
                       	this.contentpnl.add(label);
                        continue;
                    }
            }
        }
	}
	 /*
     * 
     */
//    private JPanel getRequiredPanel(String pnlname) 
//    {
//        for (Component day1 : this.cqjlpnl.getComponents() ) {
//        	Component comp = ((JScrollPane)day1).getViewport().getView();
//        	String compname = comp.getName();
//        	if(compname != null && compname.equals(pnlname)) {
//        		return (JPanel) ((JPanel) comp).getComponent(1);
//        	}
//        }
//        return null;
//    }
	/*
	 * 
	 */
	protected JUpdatedLabel getFormatedLabelForWithLabels(News m, News.Label l) 
    {
    	Integer dbid = null ;
		if( m instanceof InsertedNews ) 
			dbid = ((InsertedNews)m).getID();
		else if (m instanceof InsertedExternalNews )
			dbid = ((InsertedExternalNews)m).getID ();
		
    	JUpdatedLabel label = new JUpdatedLabel(m.getNewsOwnerCodes() + m.getTitle());
        label.setToolTipText( getLabelToolTipText(m) );
        label.setOpaque(true);
        label.setName( String.valueOf(  dbid ) );
        label.addMouseListener(new ModifyCQJLController());
        label.setForeground(Color.BLACK);
        label.setBackground(l.getColor());
        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
		return label;
	}

	protected JUpdatedLabel getFormatedLabelForNoneLabel(News m) 
    {
		Integer dbid = null ;
		if( m instanceof InsertedNews ) 
			dbid = ((InsertedNews)m).getID();
		else if (m instanceof InsertedExternalNews )
			dbid = ((InsertedExternalNews)m).getID ();
		
		JUpdatedLabel label = new JUpdatedLabel(m.getNewsOwnerCodes() + m.getTitle());
        label.setToolTipText( getLabelToolTipText(m) );
        label.setOpaque(true);
        label.setName( String.valueOf( dbid ) );
        label.addMouseListener(new ModifyCQJLController());
        label.setForeground(ColorScheme.BLACK_FONT);
        label.setBackground(ColorScheme.GREY_WHITER_DARKER);
        label.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        
		return label;
	}

	@Override
	public void onNewsChange(Collection<NewsCache> caches)
	{
		this.onNewsChange(this.svsofexternalnews.getCache()); //获取本月的信息
	}

	@Override
	public void onLabelChange(Collection<NewsCache> cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsAdded(News m) {
		// TODO Auto-generated method stub
		
	}
	
	private class CreateController extends MouseAdapter
	{
		private LocalDate firstDayInMonth;

		public CreateController(LocalDate firstDayInMonth) {
			this.firstDayInMonth = firstDayInMonth;
		}

		@Override
        public void mouseClicked(MouseEvent e) {
			
			super.mouseClicked(e);
            JPanel panel = (JPanel) e.getSource();
            
            if (e.getClickCount() == 1) { 
            	
            }
            if (e.getClickCount() == 2) { 
            	ExternalNewsType gz = getRequiredObject ();
                
                CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsofexternalnews);
                createnewDialog.setNews(gz);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
            }
		}

		public ExternalNewsType getRequiredObject ()
		{
			LocalDate createDate;
			if ( firstDayInMonth.getMonth().equals(LocalDate.now().getMonth() ) )
            	createDate = LocalDate.now();
            else
            	createDate = firstDayInMonth;
			
			BanKuaiAndStockTree treebkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks(); 
            BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treebkstk.getModel().getRoot();
            
            if( svsofexternalnews instanceof ChangQiGuanZhuServices) {
            	return new GuanZhu(treeroot, "描述", createDate, createDate, "详细描述", viewtitle,  new HashSet<>(),"URL",true);
    		}
    		if( svsofexternalnews instanceof QiangShiServices) {
    			return new 	QiangShi(treeroot, "描述", createDate, createDate, "详细描述", viewtitle,new HashSet<>(),"URL");
    		}
    		if( svsofexternalnews instanceof RuoShiServices) {
    			return new 	RuoShi(treeroot, "描述", createDate, createDate, "详细描述", viewtitle,new HashSet<>(),"URL");
    		}
    		if( svsofexternalnews instanceof DuanQiGuanZhuServices) {
    			return new GuanZhu(treeroot, "描述", createDate, createDate, "详细描述", viewtitle,  new HashSet<>(),"URL",false);
    		}
    		if( svsofexternalnews instanceof ZhiShuBoLangServices) {
    			return new ZhiShuBoLang(treeroot, "描述", createDate, createDate, "详细描述", viewtitle,  new HashSet<>(),"URL","A");
    		}
    		
			return null;
		}
	}
	private class ModifyCQJLController extends MouseAdapter
	{
		@Override
        public void mouseClicked(MouseEvent e) {
			 super.mouseClicked(e);
	            JLabel label = (JLabel) e.getSource();
	            String labelname = label.getName();
	            
	            InsertedExternalNews n = null ;
	            Collection<News> cqjlist = svsofexternalnews.getCache().produceNews();
	            for (Iterator<News> it = cqjlist.iterator(); it.hasNext(); ) {
	            	News f = it.next();
	            	Integer dbid  = ((InsertedExternalNews)f).getID ();
	            	
	            	if(dbid == Integer.parseInt(labelname)) {
	            		n = (InsertedExternalNews) f;
	            		break;
	            	}
	            	
	            }
	            
	            ModifyExternalNewsDialog mod = new ModifyExternalNewsDialog (svsofexternalnews);
	            mod.setNews(n);
	            mod.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	            mod.setVisible(true);
	            
		}
	}
	
}
