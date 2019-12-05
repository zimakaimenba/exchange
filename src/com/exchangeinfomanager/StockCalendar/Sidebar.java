package com.exchangeinfomanager.StockCalendar;


import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsCacheListener;
import com.exchangeinfomanager.News.Labels.LabelDialog;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.guifactory.DialogFactory;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.toedter.calendar.JCalendar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

@SuppressWarnings("all")
public class Sidebar extends View implements NewsCacheListener 
{

    private ServicesForNewsLabel labelService;
    private JPanel labels;
    private JLabel colorButton;
    private JLabel createLabel;
    private JLabel createMilestoneDateForZhiShu;

    private JTextField nameField;
    private LabelDialog modifyLabelDialog;
    private LabelDialog createLabelDialog;
    private NewsCache cache;
	private JScrollPane labelsscrollpane;

    public Sidebar(Collection<ServicesForNews> newssvs, ServicesForNewsLabel labelService) 
    {
    	super (newssvs);
    	
        this.labelService = labelService;
        
        Collection<NewsCache> caches = new HashSet<> ();
        for (Iterator<ServicesForNews> lit = newssvs.iterator(); lit.hasNext(); ) {
    		ServicesForNews f = lit.next();
    		f.getCache().addCacheListener(this);
    		
    		caches.add(f.getCache());
    		
    		cache = f.getCache();
    	}
        
        
//        this.createLabelDialog = DialogFactory.createLabelDialog(labelService);
//        this.modifyLabelDialog = DialogFactory.modifyLabelDialog(labelService);
        
        this.initUI();
        this.initSidebar();
        
        this.onLabelChange(cache);
    }

    private void initUI() {
        this.labels = JPanelFactory.createPanel();
        this.createLabel = JLabelFactory.createButton("New label");
        
        this.createMilestoneDateForZhiShu = JLabelFactory.createButton("指数关键日期");
        this.createMilestoneDateForZhiShu.setToolTipText("点击右键生产指数关键日期通达信代码");
        
        this.colorButton = JLabelFactory.createLabel("", 40, 30);
        this.nameField = JTextFactory.createTextField();
    }

    private void initSidebar() {
        super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        super.setPreferredSize(new Dimension(150, Integer.MAX_VALUE));
        super.setPreferredSize(new Dimension(150, 150));
        super.setBackground(ColorScheme.BACKGROUND);

        JPanel labelPanel = JPanelFactory.createFixedSizePanel();
        labelPanel.add(new JLabel("Labels: "));
        super.add(labelPanel);

        super.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 0));
        this.labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labelsscrollpane = new JScrollPane ();
        labelsscrollpane.setViewportView(this.labels);
        super.add(labelsscrollpane);
        super.add(Box.createVerticalStrut(5));

        JPanel createLabelPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        this.createLabel.addMouseListener(new CreateNewLabelController());
        createLabelPanel.add(this.createLabel, BorderLayout.CENTER);
        super.add(createLabelPanel);
        
        JPanel createDapanDatePanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        this.createMilestoneDateForZhiShu.addMouseListener(new CreateDapanMileStoneDateController());
        createDapanDatePanel.add(this.createMilestoneDateForZhiShu, BorderLayout.CENTER);
        super.add(createDapanDatePanel);
        
        
    }

    @Override
    public void onLabelChange(NewsCache cache) 
    {
        this.labels.removeAll();
        
        for (InsertedNews.Label label : cache.produceLabels()) {
            JPanel mPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
            mPanel.setName(String.valueOf(label.getID()));
            mPanel.setPreferredSize(labelsscrollpane.getPreferredSize());

            JLabel name = JLabelFactory.createLabel("  " + label.getName());
            name.setOpaque(true);
            name.addMouseListener(new NameController());
            name.setBackground( ColorScheme.BACKGROUND );
//            name.setForeground(label.isActive()? ColorScheme.BACKGROUND: ColorScheme.BLACK_FONT);
            name.setForeground( ColorScheme.BLACK_FONT);
            name.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, ColorScheme.GREY_LINE));

            JLabel option = JLabelFactory.createButton("*", 20);
            option.addMouseListener(new ModifyLabelController());
            mPanel.add(name, BorderLayout.CENTER);
            mPanel.add(option, BorderLayout.LINE_END);
            labels.add(mPanel);
            labels.add(Box.createVerticalStrut(5));
        }
//        Dimension d = labelsscrollpane.getPreferredSize();
//        this.labels.setPreferredSize(new Dimension(d.width,cache.produceLabels().size()+1));
        super.validate();
        super.repaint();
    }

    private class NameController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int id = Integer.valueOf(((Component) e.getSource()).getParent().getName());
            Optional<InsertedNews.Label> label = cache.produceLabels().stream().filter(l -> l.getID() == id).findFirst();
            if (label.isPresent()) {
                InsertedNews.Label lbl = label.get();
                
                try {
                    labelService.updateLabel(lbl);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } 
            }
        }
    }

    private class CreateNewLabelController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            createLabelDialog.setLabel(new News.Label("New label", ColorScheme.ORANGE_LIGHT));
            getModifyDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
            createLabelDialog.setVisible(true);
        }
    }

    private class CreateDapanMileStoneDateController extends MouseAdapter {

		@Override
        public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (e.getButton() == MouseEvent.BUTTON1) {
				
				TDXNodesZhiShuGJRQPnl gjrq = new TDXNodesZhiShuGJRQPnl (null);
				gjrq.setVisible(true);
				
				cache.refresh();
		               
            } else if (e.getButton() == MouseEvent.BUTTON3) {
            	TDXFormatedOpt.parserZhiShuGuanJianRiQiToTDXCode();
            }
        }
    }

    private class ModifyLabelController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int id = Integer.valueOf(((Component) e.getSource()).getParent().getName());
            Optional<InsertedMeeting.Label> label = cache.produceLabels().stream().filter(l -> l.getID() == id).findFirst();
            if (label.isPresent()) {
                modifyLabelDialog.setLabel(label.get());
                getModifyDialog().setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                modifyLabelDialog.setVisible(true);
            }
        }
    }

	@Override
	public void onNewsChange(Collection<NewsCache> caches) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewsChange(NewsCache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLabelChange(Collection<NewsCache> cache) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onNewsAdded(News m) {
		// TODO Auto-generated method stub
		
	}
}
