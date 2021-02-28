package com.exchangeinfomanager.News;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.LabelTag;
import com.exchangeinfomanager.TagServices.TagsServiceForURLAndFile;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.lc.nlp.keyword.algorithm.TextRank;

public class NewsDialog <T extends News> extends JDialog
{
	protected static final int WHOLESIZEWIDTH = 1200;
	protected static final int WHOLESIZEHIGHT = 700;
    protected static final int NEWSWIDTH = 400;
    protected static final int NEWSHEIGHT = 500;
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
    
    protected JPanel newCenterPanel;
    protected JLabel labelButton;
    protected NewsCache cache;
    protected ServicesForNews newsService;
    protected LabelListDialog labelListDialog;

    protected T event;
	private JLabel kwbutton;
	private JUpdatedTextField attachmentField;
	private JLabel attachbutton;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
	}
    public NewsDialog(ServicesForNews meetingService) 
    {
        this.newsService = meetingService;
        this.cache = meetingService.getCache();
        
        this.createUIComponents();
        this.createCenterPanel();
        
        this.createEvent ();
    }

    private void createEvent() 
    {
    	 labelButton.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 super.mouseClicked(e);
                 labelListDialog.display();
             }
         });
    	 
    	attachbutton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		selectAttachmets ();
        	}
    	});
    	
    	kwbutton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		
        		if( ! newsurlField.getText().toLowerCase ().equals("slackurl")) 
        			createKeyWords ();
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
						descriptionArea.setText( descriptionArea.getText() + result);
						
					}catch(Exception ex)	{
						
					}
				}
			}
		});
		
	}

	protected void createKeyWords() 
	{
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
			
			PropertyChangeEvent evtzd = new PropertyChangeEvent(this, News.KEYWORDSADD , "", tags  );
		    pcs.firePropertyChange(evtzd);
		    
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}

	protected void selectAttachmets() 
	{
		String parsedpath = "";//sysconfig.getTDXModelMatchExportFile ();
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(parsedpath) );
		
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		    String filename;
			if(chooser.getSelectedFile().isDirectory())
		    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    else
		    	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
		} else
			return;
	}

	public JPanel getNewsPanel ()
    {
    	return this.newCenterPanel;
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
        
        this.attachmentField = (JUpdatedTextField) JTextFactory.createTextField();
        this.attachbutton = JLabelFactory.createButton("Attachments:");
        this.newCenterPanel = new JPanel();
        this.labelListDialog = new LabelListDialog();
    }


    private void createCenterPanel()
    {
        this.newCenterPanel = JPanelFactory.createFixedSizePanel(NEWSWIDTH, NEWSHEIGHT, PADDING);
        this.newCenterPanel.setLayout(new BoxLayout(this.newCenterPanel, BoxLayout.PAGE_AXIS));

//        JPanel p = JPanelFactory.createPanel(new FlowLayout(FlowLayout.CENTER));
//        this.newCenterPanel.add(p);
//        this.newCenterPanel.add(Box.createVerticalStrut(PADDING));
        this.newCenterPanel.add(this.newsownersField);
        
        this.newCenterPanel.add(this.newstitleField);
        
        this.newCenterPanel.add(Box.createVerticalStrut(30));
        
        this.newCenterPanel.add(this.getTimeChooserPanel());
        
        this.newCenterPanel.add(Box.createVerticalStrut(10));
        
        JPanel labelPanel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 2));
        labelPanel.add(new JLabel("设置 labels: "));
        labelButton = JLabelFactory.createButton("Labels");
        labelPanel.add(labelButton);
        this.newCenterPanel.add(labelPanel);

        this.newCenterPanel.add(Box.createVerticalStrut(PADDING));
        JScrollPane despane = new JScrollPane ();
        despane.setAutoscrolls(true);
        despane.setViewportView(this.descriptionArea);
        this.newCenterPanel.add(despane);
        this.newCenterPanel.add(Box.createVerticalStrut(10));
        this.newCenterPanel.add(this.newsurlField);
        
        this.newCenterPanel.add(Box.createVerticalStrut(10));
        
        JPanel attachpanel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 3) );
        attachpanel.add(new JLabel("Attachment:"));
        attachpanel.add(this.attachmentField);
        attachpanel.add(this.attachbutton);
        this.newCenterPanel.add(attachpanel);
        
        this.newCenterPanel.add(Box.createVerticalStrut(10));
        
        JPanel kwPanel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        kwbutton = JLabelFactory.createButton("关键词");
        kwPanel.add(kwbutton,BorderLayout.WEST);
//        kwPanel.add(new JLabel(" "));
        kwPanel.add(this.keywordsField,BorderLayout.CENTER);
        this.newCenterPanel.add(kwPanel);
        
        this.newCenterPanel.add(Box.createVerticalStrut(PADDING));

        // add main panel to the dialog
        getContentPane().setLayout(new BorderLayout());
        super.add(this.newCenterPanel, BorderLayout.CENTER);
        
        super.setModalityType(ModalityType.APPLICATION_MODAL);
//        super.setSize(new Dimension(WHOLESIZEWIDTH, WHOLESIZEHIGHT));
        super.setSize(new Dimension(NEWSWIDTH, NEWSHEIGHT));
        super.setResizable(true);
    }

    private JPanel getTimeChooserPanel() 
    {
        JPanel panel = JPanelFactory.createFixedSizePanel(new GridLayout(1, 3) );
        panel.add(new JLabel("日期:"));
        panel.add(this.startTimeChooser);
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
        newsurlField.setText(event.getNewsUrl());
        attachmentField.setText(event.getAttachment());
        
        newCenterPanel.revalidate();
    	newCenterPanel.repaint();
        
        return true;
    }

    public T getNews() 
    {
    	event.setNewsOwnerCodes(newsownersField.getText());
    	event.setTitle(newstitleField.getText());
    	event.setStart(startTimeChooser.getLocalDate() );
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
