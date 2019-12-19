package com.exchangeinfomanager.News.ExternalNewsType;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForURLAndFile;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.lc.nlp.keyword.algorithm.TextRank;

public class ExternalNewsDialog <T extends ExternalNewsType> extends JDialog 
{
	  protected static final int WIDTH = 400;
	    protected static final int HEIGHT = 600;
	    protected static final int PADDING = 20;
	    protected static final int TITLE_SIZE = 40;
	    protected static final int TITLE_FONT_SIZE = 20;

	    protected static final DateTimeFormatter LABEL_DATE = DateTimeFormatter.ofPattern("dd MMM uuuu");
	    protected JUpdatedTextField newsownersField;
	    protected JUpdatedTextField newstitleField;
	    protected JUpdatedTextField keywordsField; 
	    protected JUpdatedTextField newsurlField;
	    protected JTextArea descriptionArea;
	    protected JLocalDateChooser startTimeChooser;
	    protected JLocalDateChooser endTimeChooser;
	    
	    protected JPanel centerPanel;
	    protected JLabel labelButton;
	    protected NewsCache cache;
	    protected ServicesForNews NewsService;
	    protected LabelListDialog labelListDialog;
	    private BanKuaiDbOperation bkdbopt;

	    private T event;
		private JLabel kwbutton;

	    public ExternalNewsDialog(ServicesForNews NewsService) 
	    {
	        this.NewsService = NewsService;
	        this.cache = NewsService.getCache();
	        this.bkdbopt = new BanKuaiDbOperation ();
	        
	        this.createUIComponents();
	        this.createCenterPanel();
	        
	        this.createEvent ();
	        
	    }

	    private void createEvent() 
	    {
	    	kwbutton.addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mouseClicked(MouseEvent arg0) {
	        		String urlcontent = "";
	        		if( ! newsurlField.getText().toLowerCase ().equals("slackurl")) {
	        			
	        			Collection<String> urlset = new HashSet<> ();
	        			urlset.add(newsurlField.getText().trim() );
	        			TagsServiceForURLAndFile internet = new TagsServiceForURLAndFile (urlset);
	        			try {
							Collection<Tag> tags = internet.getTags();
							
							String keywords = "";
							for (Iterator<Tag> lit = tags.iterator(); lit.hasNext(); ) {
						        Tag f = lit.next();
						        
						        keywords = keywords + f.getName() + " ";
							}
							keywordsField.setText(keywords );
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	        		}
	        	}
	        });
	    	
	    	descriptionArea.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent event) {
					
					if (SwingUtilities.isRightMouseButton(event)) {
						Toolkit toolkit = Toolkit.getDefaultToolkit();
						Clipboard clipboard = toolkit.getSystemClipboard();
						try	{
							String result = (String) clipboard.getData(DataFlavor.stringFlavor);
							descriptionArea.setText(descriptionArea.getText() + result);
							
						}catch(Exception ex)	{
							
						}
					}
				}
			});
			
		}

		public JPanel getNewsPanel ()
	    {
	    	return this.centerPanel;
	    }
	    
	    private void createUIComponents() 
	    {
	    	this.newsownersField = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
	    	this.newsownersField.setMouseLeftClearEnabled(false);
	        this.newstitleField = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
	        this.newstitleField.setMouseLeftClearEnabled(false);
	        this.keywordsField = (JUpdatedTextField) JTextFactory.createTextField();
	        this.keywordsField.setMouseLeftClearEnabled(false);
	        this.newsurlField = (JUpdatedTextField) JTextFactory.createTextField();
	        this.newsurlField.setMouseLeftClearEnabled(false);
	        this.newsurlField.setMouseRightPasteEnabled(true, JUpdatedTextField.PASTEWITHCLEAR);
	        this.descriptionArea = JTextFactory.createTextArea();
	        this.descriptionArea.setLineWrap(true);
	        this.startTimeChooser = new JLocalDateChooser();
	        this.endTimeChooser = new JLocalDateChooser ();

	        this.centerPanel = new JPanel();
	        this.labelListDialog = new LabelListDialog();
	    }


	    private void createCenterPanel()
	    {
	        this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
	        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.PAGE_AXIS));

	        JPanel p = JPanelFactory.createPanel(new FlowLayout(FlowLayout.CENTER));
	        this.centerPanel.add(p);
	        this.centerPanel.add(Box.createVerticalStrut(PADDING));
	        this.centerPanel.add(this.newsownersField);
	        this.centerPanel.add(this.newstitleField);
	        this.centerPanel.add(Box.createVerticalStrut(30));
	                this.centerPanel.add(this.getTimeChooserPanel());
	        this.centerPanel.add(Box.createVerticalStrut(10));

	        JPanel labelPanel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 2));
	        labelPanel.add(new JLabel("设置 labels: "));
	        labelButton = JLabelFactory.createButton("Labels");
	        labelButton.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	                super.mouseClicked(e);
	                labelListDialog.display();
	            }
	        });

	        labelPanel.add(labelButton);
	        this.centerPanel.add(labelPanel);

	        this.centerPanel.add(Box.createVerticalStrut(PADDING));
	        JScrollPane despane = new JScrollPane ();
	        despane.setAutoscrolls(true);
	        despane.setViewportView(this.descriptionArea);
	        this.centerPanel.add(despane);
	        this.centerPanel.add(Box.createVerticalStrut(10));
	        this.centerPanel.add(this.newsurlField);
	        this.centerPanel.add(Box.createVerticalStrut(10));
	        
	        JPanel kwPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
	        kwbutton = JLabelFactory.createButton("关键词");
	        kwPanel.add(kwbutton,BorderLayout.WEST);
//	        kwPanel.add(new JLabel(" "));
	        kwPanel.add(this.keywordsField,BorderLayout.CENTER);
	        this.centerPanel.add(kwPanel);
	        
	        this.centerPanel.add(Box.createVerticalStrut(PADDING));

	        // add main panel to the dialog
	        getContentPane().setLayout(new BorderLayout());
	        super.add(this.centerPanel, BorderLayout.CENTER);
	        super.setModalityType(ModalityType.APPLICATION_MODAL);
	        super.setSize(new Dimension(WIDTH, HEIGHT));
	        super.setResizable(false);
	    }

	    private JPanel getTimeChooserPanel() 
	    {
	        JPanel panel = JPanelFactory.createPanel(new GridLayout(1, 3) );
	        panel.add(new JLabel("日期:"));
	        panel.add(this.startTimeChooser);
	        panel.add(this.endTimeChooser);
	        return panel;
	    }

	    public Boolean setNews(T event)  
	    {
	        this.event = event;

	        newstitleField.setText(event.getTitle());
	        newsownersField.setText(event.getNewsOwnerCodes());
	        keywordsField.setText(event.getKeyWords());
	        descriptionArea.setText(event.getDescription());
	        startTimeChooser.setLocalDate( event.getStart() );
	        try {
	        	endTimeChooser.setLocalDate( event.getEnd() );
	        } catch (java.lang.NullPointerException e) {}
	        newsurlField.setText(event.getNewsUrl());
	        
	        return true;
	    }

	    public T getNews() 
	    {
	    	event.setNewsOwnerCodes(newsownersField.getText());
	    	event.setTitle(newstitleField.getText());
	    	event.setStart(startTimeChooser.getLocalDate() );
	    	event.setEnd(endTimeChooser.getLocalDate());
	    	event.setKeyWords(keywordsField.getText());
	    	event.setDescription(descriptionArea.getText());
	    	event.setNewsUrl(newsurlField.getText());
	        return event;
	    }

	    private LocalTime toLocalTime(Instant instant) {
	        return LocalTime.from(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
	    }

	    private Instant adjustTime(Instant instant, LocalTime time) {
	        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute())
	                            .toInstant(ZoneOffset.UTC);
	    }

	    private class LabelListDialog extends JDialog 
	    {
	        JPanel centerPanel;

	        public LabelListDialog()
	        {
	            this.centerPanel = JPanelFactory.createPanel();
	            this.centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
	            this.centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	            super.add(this.centerPanel);
	            super.setModalityType(ModalityType.APPLICATION_MODAL);
	            super.setTitle("Labels");
	        }

	        void display() 
	        {
	            this.centerPanel.removeAll();
	            Collection<InsertedNews.Label> labels = cache.produceLabels();
	            for (InsertedNews.Label label : labels) {
	                JPanel mPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
	                mPanel.setName(event.getLabels().contains(label) ? "selected" : "");

	                JLabel name = JLabelFactory.createLabel("  " + label.getName());
	                name.setName(String.valueOf(label.getID()));
	                name.setOpaque(true);
	                name.setBackground(event.getLabels().contains(label) ? label.getColor() : ColorScheme.BACKGROUND);
	                name.setForeground(
	                		event.getLabels().contains(label) ? ColorScheme.BACKGROUND : ColorScheme.BLACK_FONT);
	                name.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.GREY_LINE));

	                mPanel.addMouseListener(new MouseAdapter() {
	                    @Override
	                    public void mouseClicked(MouseEvent e) {
	                        super.mouseClicked(e);
	                        Component l = ((Component) e.getSource());
	                        if (l.getName().equals("selected")) {
	                            l.setName("");
	                            name.setBackground(ColorScheme.BACKGROUND);
	                            name.setForeground(ColorScheme.BLACK_FONT);
	                        } else {
	                            l.setName("selected");
	                            name.setBackground(label.getColor());
	                            name.setForeground(ColorScheme.BACKGROUND);
	                        }
	                    }
	                });

	                mPanel.add(name, BorderLayout.CENTER);

	                this.centerPanel.add(mPanel);
	                this.centerPanel.add(Box.createVerticalStrut(5));
	            }

	            super.addWindowListener(new WindowAdapter() {
	                @Override
	                public void windowClosing(WindowEvent e) {
	                    super.windowClosing(e);
	                    event.getLabels().clear();

	                    for (Component c : centerPanel.getComponents()) {
	                        if (c instanceof JPanel && c.getName().equals("selected")) {
	                            int id = Integer.valueOf(((JPanel) c).getComponent(0).getName());
	                            Optional<InsertedNews.Label> label = cache.produceLabels()
	                                                                 .stream()
	                                                                 .filter(l -> l.getID() == id)
	                                                                 .findFirst();
	                            if (label.isPresent())
	                            	event.getLabels().add(label.get());
	                        }
	                    }

	                }
	            });

	            super.setMinimumSize(new Dimension(200, (int) this.centerPanel.getPreferredSize().getHeight() + 40));
	            super.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	            super.setVisible(true);
	        }
	    }



}
