package com.exchangeinfomanager.TagManagment;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.LabelTag;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagCache;
import com.exchangeinfomanager.TagServices.TagServicesForTreeChanYeLian;
import com.exchangeinfomanager.TagServices.TagsServiceForNews;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.TagServices.TagsServiceForSystemTags;
import com.exchangeinfomanager.TagServices.TagsServiceForURLAndFile;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.lc.nlp.keyword.algorithm.TextRank;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NodeLabelMatrixManagement extends JDialog 
{
	private BkChanYeLianTreeNode node;
	private TagsServiceForSystemTags lballdbservice;
	private TagCache allsyskwcache;
	private TagsServiceForNodes lbnodedbservice;
	private TagCache bkstkkwcache;
	private TagsServiceForNews lbnewsdbservice;
	private TagCache newskwcache;
	private AllCurrentTdxBKAndStoksTree allbkstk;
	private BanKuaiDbOperation nodedbopt;
	private TagsServiceForNodes lbbkdbservice;
	private TagCache bankuaikwcache;
//	private CylTreeDbOperation cydbopt;
	private TagCache cylkwcache;
	private TagsServiceForURLAndFile lburldbservice;
	private TagCache urlcache;
	private HashSet<TagService> tagdeltedlisteners;
	private HashSet<TagCache> tagsystemcachechangessteners;
	private TagServicesForTreeChanYeLian tagserviceforcyl;


	/**
	 * Create the dialog.
	 */
	public NodeLabelMatrixManagement(BkChanYeLianTreeNode node) 
	{
		initializeGui ();
		
		this.node = node;
		this.nodedbopt = new BanKuaiDbOperation ();
//		this.cydbopt = new CylTreeDbOperation ();
		this.allbkstk = AllCurrentTdxBKAndStoksTree.getInstance();
		
		//所有KW
		lballdbservice = new TagsServiceForSystemTags (); 
		allsyskwcache = new CacheForInsertedTag (lballdbservice);
		lballdbservice.setCache(allsyskwcache);
		pnldisplayallsyskw.initializeTagsPanel (lballdbservice,allsyskwcache);
		//当前KW
		Set<BkChanYeLianTreeNode> bkstk = Set.of(this.node);
		lbnodedbservice = new TagsServiceForNodes (bkstk);
		bkstkkwcache = new CacheForInsertedTag (lbnodedbservice);
		lbnodedbservice.setCache(bkstkkwcache);
		tfldcurkeywords.initializeTagsPanel (lbnodedbservice,bkstkkwcache);
		//所属板块KW
		Set<BkChanYeLianTreeNode> suosusysbankuai = new HashSet<> ();
		if(node.getType () == BkChanYeLianTreeNode.TDXGG) {
			node = this.nodedbopt.getTDXBanKuaiForAStock ((Stock) node);
			suosusysbankuai = ((Stock) node).getGeGuCurSuoShuTDXSysBanKuaiList();
			
			lbbkdbservice = new TagsServiceForNodes (suosusysbankuai);
			bankuaikwcache = new TagCache (lbbkdbservice);
			lbbkdbservice.setCache(bankuaikwcache);
			pnldisplayallbkskw.initializeTagsPanel (lbbkdbservice,bankuaikwcache);
		} if(node.getType () == BkChanYeLianTreeNode.TDXBK) {
			node = this.nodedbopt.getBanKuaiBasicInfo ((BanKuai) node);
			Set<String> friends = ((BanKuai)node).getSocialFriendsSet();
			for(String tmpfri : friends) 
				suosusysbankuai.add( this.allbkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(tmpfri, BkChanYeLianTreeNode.TDXBK) );
			
			lbbkdbservice = new TagsServiceForNodes (suosusysbankuai);
			bankuaikwcache = new TagCache (lbbkdbservice);
			lbbkdbservice.setCache(bankuaikwcache);
			pnldisplayallbkskw.initializeTagsPanel (lbbkdbservice,bankuaikwcache);
		}
		//新闻KW
		Set<BkChanYeLianTreeNode> nodewithbankuais = new HashSet<> () ;
		nodewithbankuais.add(this.node);
		nodewithbankuais.addAll(suosusysbankuai);
		lbnewsdbservice = new TagsServiceForNews (nodewithbankuais);
		newskwcache = new TagCache (lbnewsdbservice);
		lbnewsdbservice.setCache(newskwcache);
		pnldisplayallnewskw.initializeTagsPanel (lbnewsdbservice,newskwcache);
		//产业链
		tagserviceforcyl = new TagServicesForTreeChanYeLian (bkstk);
		cylkwcache = new TagCache (tagserviceforcyl);
		tagserviceforcyl.setCache(cylkwcache);
		pnldisplaycylskw.initializeTagsPanel (tagserviceforcyl,cylkwcache);
		
		
		createEvent ();
		
		//系统有删除信息时候，其他都要删除
		tagdeltedlisteners = new HashSet<> ();
		tagdeltedlisteners.add(lballdbservice);
		tagdeltedlisteners.add(lbnodedbservice);
		tagdeltedlisteners.add(lbnewsdbservice);
		tagdeltedlisteners.add(lbbkdbservice);
		tagdeltedlisteners.add(tagserviceforcyl);
		
		tagsystemcachechangessteners = new HashSet<> ();
		tagsystemcachechangessteners.add (bkstkkwcache);
		tagsystemcachechangessteners.add (newskwcache);
		tagsystemcachechangessteners.add (cylkwcache);
		tagsystemcachechangessteners.add (bankuaikwcache);
	}
	
	private void createKeyWorkdsFromURL() 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		Set<String> urlset = new HashSet< > ();
		urlset.add(tfldurl.getText());
	
		lburldbservice = new TagsServiceForURLAndFile (urlset);
		urlcache = new TagCache (lburldbservice);
		lburldbservice.setCache(urlcache);
		pnldisplayurlskw.initializeTagsPanel (lburldbservice,urlcache);
		pnldisplayurlskw.revalidate();
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
		
	}
	

	
	private void createEvent() 
	{
		pnldisplayallsyskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {
            		Collection<Tag> sltlbs = allsyskwcache.produceSelectedTags();
    				try {
    					lbnodedbservice.createTags(sltlbs); //个股
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    				
            	}   else
                if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASDELETE)) {
                	Collection<Tag> tagslist = (Collection<Tag>) evt.getNewValue();
					tagdeltedlisteners.forEach(l -> {
					try {
						l.deleteTags(tagslist);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					}  );
                } else
                if( evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASEDIT ) ) {
//                	Tag newtag = (Tag) evt.getNewValue();
                	tagsystemcachechangessteners.forEach(l -> {
    				try {
    					l.remindListenersToRefresh();;
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
                	}  );
                } else
                if( evt.getPropertyName().equals( LabelTag.PROPERTYCHANGEDBUNCHADD ) ) {
                	
//                	Tag newtag = (Tag) evt.getNewValue();
                	tagsystemcachechangessteners.forEach(l -> {
    				try {
    					l.remindListenersToRefresh();;
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
                	}  );
                }
            }
		});
		
		pnldisplayurlskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = urlcache.produceSelectedTags();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if( !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createTag (tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
    			
            	}
            }
		});


		
		pnldisplayallnewskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = newskwcache.produceSelectedTags();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if(  !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createTag(tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
            	}
            }
		});
		
		pnldisplayallbkskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = bankuaikwcache.produceSelectedTags();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if( !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createTag( tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
            	}
            }
		});
		
		pnldisplaycylskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = cylkwcache.produceSelectedTags();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if( !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createTag(tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
            	}
            }
		});
		
		tfldcurkeywords.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {
            		Tag newtag = (Tag) evt.getNewValue();
					try {
						allsyskwcache.addTag(newtag);  //同时也要加入到系统关键词里面
					} catch (Exception e) {
    					e.printStackTrace();
    				}
            	} else
            	if( evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASEDIT ) ) {
            		Tag newtag = (Tag) evt.getNewValue();
            		tagdeltedlisteners.forEach(l -> {
						try {
							l.updateTag(newtag);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}  );
            	}
            }
		});

		
		tfldurl.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent  evt)
			{
				if(evt.getKeyCode() == KeyEvent.VK_ENTER)	{
					createKeyWorkdsFromURL ();
			}
			}
		});
	}
	
	

	protected void refreshPanels() 
	{
		tfldcurkeywords.revalidate();
		pnldisplayallsyskw.revalidate();
		pnldisplayallnewskw.revalidate();
		pnldisplayallbkskw.revalidate();
		pnldisplaycylskw.revalidate();	
		pnldisplayurlskw.revalidate();
		
	}



	private JPanel contentPanel ;
	private TagsPanel tfldcurkeywords;
	private TagsPanel pnldisplayallsyskw;
	private TagsPanel pnldisplayallnewskw;
	private JTextField tfldsearchkw;
	private JButton btnsyskwaddtocur;
	private JPanel pnlnewskw;
	private JTextField tfldsearchsyskw;
	private JTextField tfldsearchnewskw;
	private JButton btnnewskwaddtocur;
	private JTextField tfldsearchbkkw;
	private JButton btnkbkwaddtocur;
	private TagsPanel pnldisplayallbkskw;
	private TagsPanel pnldisplaycylskw;
	private JTextField tfldurl;
	private TagsPanel pnldisplayurlskw;

	protected static final int WIDTH = 400;
    protected static final int HEIGHT = 200;
    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;

	
	private void initializeGui ()
	{
		setBounds(100, 100, 886, 722);
		getContentPane().setLayout(new BorderLayout());
		contentPanel = new JPanel(); //JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel pnlup = JPanelFactory.createFixedSizePanel(200, 80, 5);
			pnlup.setLayout(new BorderLayout());
			JLabel lblcurkeywords = new JLabel ("当前关键字");
			tfldcurkeywords = new TagsPanel("当前关键字",TagsPanel.HIDEHEADERMODE, TagsPanel.SELFFULLCONTROLMODE);
			pnlup.add(lblcurkeywords,BorderLayout.WEST);
			pnlup.add(tfldcurkeywords,BorderLayout.CENTER);
			
			
			contentPanel.add(pnlup, BorderLayout.NORTH);
		}
		
		JPanel panel = new JPanel ();//
		contentPanel.add(panel, BorderLayout.CENTER);
		
		JPanel pnlsyskw = JPanelFactory.createFixedSizePanel(200, 100, 5);
		pnlsyskw.setLayout(new BorderLayout(0, 0));
		
		
		JPanel pnlbkkw = new JPanel();
		pnlbkkw.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlcylkw = new JPanel();
		pnlcylkw.setLayout(new BorderLayout(0, 0));
		
		pnlnewskw = JPanelFactory.createFixedSizePanel(200, 100, 5);
		pnlnewskw.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlurlkw = new JPanel();
		pnlurlkw.setLayout(new BorderLayout(0, 0));
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(pnlnewskw, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
						.addComponent(pnlsyskw, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(pnlbkkw, GroupLayout.PREFERRED_SIZE, 421, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlcylkw, GroupLayout.PREFERRED_SIZE, 418, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnlurlkw, GroupLayout.PREFERRED_SIZE, 415, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(pnlbkkw, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(pnlcylkw, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pnlurlkw, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
							.addGap(48))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(pnlsyskw, GroupLayout.PREFERRED_SIZE, 310, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pnlnewskw, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		
		pnldisplayallsyskw = new TagsPanel("所有系统关键字",null, TagsPanel.FULLCONTROLMODE);
		pnlsyskw.add(pnldisplayallsyskw,BorderLayout.CENTER);
		
		pnldisplayallnewskw = new TagsPanel("所属新闻关键字",null, TagsPanel.ONLYIPLAYMODE);
		pnlnewskw.add(pnldisplayallnewskw,BorderLayout.CENTER);


		pnldisplayallbkskw = new TagsPanel("所属板块关键字",null, TagsPanel.ONLYIPLAYMODE);
		pnlbkkw.add(pnldisplayallbkskw,BorderLayout.CENTER);
		
		//所属产业链关键词
		pnldisplaycylskw = new TagsPanel("所在产业链关键字",null, TagsPanel.ONLYIPLAYMODE);
		pnlcylkw.add(pnldisplaycylskw,BorderLayout.CENTER);
		
		//URL关键词
		JPanel pnlurlup = new JPanel ();
		pnlurlup.setLayout (new BorderLayout(0, 0) );
		JLabel lblurl = new JLabel ("URL: ");
		tfldurl = new JTextField ();
		pnlurlup.add (lblurl,BorderLayout.WEST);
		pnlurlup.add (tfldurl,BorderLayout.CENTER);

		pnldisplayurlskw = new TagsPanel("",TagsPanel.HIDEHEADERMODE, TagsPanel.ONLYIPLAYMODE);

		pnlurlkw.add(pnlurlup,BorderLayout.NORTH);
		pnlurlkw.add(pnldisplayurlskw,BorderLayout.CENTER);
		
		
		panel.setLayout(gl_panel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
}

