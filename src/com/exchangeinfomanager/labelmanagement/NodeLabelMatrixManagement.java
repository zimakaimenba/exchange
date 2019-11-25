package com.exchangeinfomanager.labelmanagement;

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

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JTextFactory;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.CylTreeDbOperation;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.labelmanagement.LblMComponents.LabelsManagement;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.lc.nlp.keyword.algorithm.TextRank;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class NodeLabelMatrixManagement extends JDialog 
{
	private BkChanYeLianTreeNode node;
	private DBSystemTagsService lballdbservice;
	private LabelCache allsyskwcache;
	private DBNodesTagsService lbnodedbservice;
	private LabelCache bkstkkwcache;
	private DBNewsTagsService lbnewsdbservice;
	private LabelCache newskwcache;
	private AllCurrentTdxBKAndStoksTree allbkstk;
	private BanKuaiDbOperation nodedbopt;
	private DBNodesTagsService lbbkdbservice;
	private LabelCache bankuaikwcache;
	private CylTreeDbOperation cydbopt;
	private DBNodesTagsService lbcyldbservice;
	private LabelCache cylkwcache;
	private DBUrlTagsService lburldbservice;
	private LabelCache urlcache;
	private HashSet<TagService> tagdeltedlisteners;
	private TagServicesForTreeChanYeLian tagserviceforcyl;


	/**
	 * Create the dialog.
	 */
	public NodeLabelMatrixManagement(BkChanYeLianTreeNode node) 
	{
		initializeGui ();
		
		this.node = node;
		this.nodedbopt = new BanKuaiDbOperation ();
		this.cydbopt = new CylTreeDbOperation ();
		this.allbkstk = AllCurrentTdxBKAndStoksTree.getInstance();
		
		//所有KW
		Set<String> all = new HashSet<> ();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbkstk.getAllBkStocksTree().getModel().getRoot();
		all.add("treeroot");
		lballdbservice = new DBSystemTagsService (); 
		allsyskwcache = new LabelCache (lballdbservice);
		lballdbservice.setCache(allsyskwcache);
		pnldisplayallsyskw.initializeLabelsManagement (lballdbservice,allsyskwcache);
		//当前KW
		Set<BkChanYeLianTreeNode> bkstk = new HashSet<> ();
		bkstk.add(this.node);
		lbnodedbservice = new DBNodesTagsService (bkstk);
		bkstkkwcache = new LabelCache (lbnodedbservice);
		lbnodedbservice.setCache(bkstkkwcache);
		tfldcurkeywords.initializeLabelsManagement (lbnodedbservice,bkstkkwcache);
		//所属板块KW
		Set<BkChanYeLianTreeNode> suosusysbankuai = null;
		if(node.getType () == BkChanYeLianTreeNode.TDXGG) {
			node = this.nodedbopt.getTDXBanKuaiForAStock ((Stock) node);
			suosusysbankuai = ((Stock) node).getGeGuCurSuoShuTDXSysBanKuaiList();
			
			lbbkdbservice = new DBNodesTagsService (suosusysbankuai);
			bankuaikwcache = new LabelCache (lbbkdbservice);
			lbbkdbservice.setCache(bankuaikwcache);
			pnldisplayallbkskw.initializeLabelsManagement (lbbkdbservice,bankuaikwcache);
		}
		//新闻KW
		lbnewsdbservice = new DBNewsTagsService (suosusysbankuai);
		newskwcache = new LabelCache (lbnewsdbservice);
		lbnewsdbservice.setCache(newskwcache);
		pnldisplayallnewskw.initializeLabelsManagement (lbnewsdbservice,newskwcache);
		//产业链
		tagserviceforcyl = new TagServicesForTreeChanYeLian (bkstk);
		cylkwcache = new LabelCache (tagserviceforcyl);
		tagserviceforcyl.setCache(cylkwcache);
		pnldisplaycylskw.initializeLabelsManagement (tagserviceforcyl,cylkwcache);
		
		
		createEvent ();
		
		//系统有删除信息时候，其他都要删除
		tagdeltedlisteners = new HashSet<> ();
		tagdeltedlisteners.add(lballdbservice);
		tagdeltedlisteners.add(lbnodedbservice);
		tagdeltedlisteners.add(lbnewsdbservice);
		tagdeltedlisteners.add(lbbkdbservice);
		tagdeltedlisteners.add(lbcyldbservice);
	}
	
	private void createKeyWorkdsFromURL() 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		Set<String> urlset = new HashSet< > ();
		urlset.add(tfldurl.getText());
	
		lburldbservice = new DBUrlTagsService (urlset);
		urlcache = new LabelCache (lburldbservice);
		lburldbservice.setCache(urlcache);
		pnldisplayurlskw.initializeLabelsManagement (lburldbservice,urlcache);
		pnldisplayurlskw.revalidate();
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
		
	}
	

	
	private void createEvent() 
	{
		pnldisplayallsyskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelsManagement.ADDNEWTAGSTONODE)) {
            		Collection<Tag> sltlbs = allsyskwcache.produceSelectedLabels();
    						try {
    							lbnodedbservice.createLabels(sltlbs); //个股
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				
            	}   else
                	if (evt.getPropertyName().equals(LabelsManagement.NODESBEENDELETED)) {
                		Collection<Tag> tagslist = (Collection<Tag>) evt.getNewValue();
						tagdeltedlisteners.forEach(l -> {
							try {
								l.deleteLabels(tagslist);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}  );
                	}
            }
		});
		
		pnldisplayurlskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelsManagement.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = urlcache.produceSelectedLabels();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if( !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createLabel(tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
    			
            	}
            }
		});


		
		pnldisplayallnewskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelsManagement.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = newskwcache.produceSelectedLabels();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if(  !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createLabel(tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
            	}
            }
		});
		
		pnldisplayallbkskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelsManagement.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = bankuaikwcache.produceSelectedLabels();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if( !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createLabel( tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
            	}
            }
		});
		
		pnldisplaycylskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelsManagement.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = cylkwcache.produceSelectedLabels();
    				for(Tag tmpsltlbs : sltlbs) {
    					
    					if( !bkstkkwcache.hasBeenInCache (tmpsltlbs.getName())   )
    						try {
    							lbnodedbservice.createLabel(tmpsltlbs);
    						} catch (SQLException e) {
    							e.printStackTrace();
    						}
    				}
            	}
            }
		});
		
		tfldcurkeywords.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelsManagement.NODEADDNEWTAGSNEEDSYSUPDAT)) {
            		Tag newtag = (Tag) evt.getNewValue();
					try {
						allsyskwcache.addLabel(newtag);  //同时也要加入到系统关键词里面
					} catch (Exception e) {
    					e.printStackTrace();
    				}
            	} else
            	if( evt.getPropertyName().equals(LabelsManagement.NODESBEENEDIT ) ) {
            		Tag newtag = (Tag) evt.getNewValue();
            		tagdeltedlisteners.forEach(l -> {
						try {
							l.updateLabel(newtag);
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
	private LabelsManagement tfldcurkeywords;
	private LabelsManagement pnldisplayallsyskw;
	private LabelsManagement pnldisplayallnewskw;
	private JTextField tfldsearchkw;
	private JButton btnsyskwaddtocur;
	private JPanel pnlnewskw;
	private JTextField tfldsearchsyskw;
	private JTextField tfldsearchnewskw;
	private JButton btnnewskwaddtocur;
	private JTextField tfldsearchbkkw;
	private JButton btnkbkwaddtocur;
	private LabelsManagement pnldisplayallbkskw;
	private LabelsManagement pnldisplaycylskw;
	private JTextField tfldurl;
	private LabelsManagement pnldisplayurlskw;

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
			tfldcurkeywords = new LabelsManagement("当前关键字",LabelsManagement.HIDEHEADERMODE, LabelsManagement.PARTCONTROLMODE);
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
		//系统关键字
//		JPanel pnlsyskwup = new JPanel ();
//		pnlsyskwup.setLayout (new FlowLayout(FlowLayout.LEFT) );
//		JLabel lblsyskw = new JLabel("所有系统关键字");
//		JLabel lblfengge = new JLabel("  ");
//		tfldsearchsyskw = new JTextField ("                 ");
//		btnsyskwaddtocur = new JButton ("加入到当前");
//		pnlsyskwup.add(lblsyskw);
//		pnlsyskwup.add(lblfengge);
//		pnlsyskwup.add(tfldsearchsyskw);
//		pnlsyskwup.add(btnsyskwaddtocur);
		
		pnldisplayallsyskw = new LabelsManagement("所有系统关键字",null, LabelsManagement.FULLCONTROLMODE);
		
//		pnlsyskw.add(pnlsyskwup,BorderLayout.NORTH);
		pnlsyskw.add(pnldisplayallsyskw,BorderLayout.CENTER);
		
		//新闻关键字
//		JPanel pnlnewskwup = new JPanel ();
//		pnlnewskwup.setLayout (new FlowLayout(FlowLayout.LEFT) );
//		JLabel lblnewskw = new JLabel("所属新闻关键字");
//		JLabel lblnewsfengge = new JLabel("  ");
//		tfldsearchnewskw = new JTextField ("                 ");
//		btnnewskwaddtocur = new JButton ("加入到当前");
//		pnlnewskwup.add(lblnewskw);
//		pnlnewskwup.add(lblnewsfengge);
//		pnlnewskwup.add(tfldsearchnewskw);
//		pnlnewskwup.add(btnnewskwaddtocur);
		pnldisplayallnewskw = new LabelsManagement("所属新闻关键字",null, LabelsManagement.ONLYIPLAYMODE);
		
//		pnlnewskw.add(pnlnewskwup,BorderLayout.NORTH);
		pnlnewskw.add(pnldisplayallnewskw,BorderLayout.CENTER);

		//所属板块关键词
//		JPanel pnlbkskwup = new JPanel ();
//		pnlbkskwup.setLayout (new FlowLayout(FlowLayout.LEFT) );
//		JLabel lblbkkw = new JLabel("所属板块关键字");
//		JLabel lblbkfengge = new JLabel("  ");
//		tfldsearchbkkw = new JTextField ("                 ");
//		btnkbkwaddtocur = new JButton ("加入到当前");
//		pnlbkskwup.add(lblbkkw);
//		pnlbkskwup.add(lblbkfengge);
//		pnlbkskwup.add(tfldsearchbkkw);
//		pnlbkskwup.add(btnkbkwaddtocur);
		pnldisplayallbkskw = new LabelsManagement("所属板块关键字",null, LabelsManagement.ONLYIPLAYMODE);
		
//		pnlbkkw.add(pnlbkskwup,BorderLayout.NORTH);
		pnlbkkw.add(pnldisplayallbkskw,BorderLayout.CENTER);
		
		//所属产业链关键词
		pnldisplaycylskw = new LabelsManagement("所在产业链关键字",null, LabelsManagement.ONLYIPLAYMODE);
		pnlcylkw.add(pnldisplaycylskw,BorderLayout.CENTER);
		
		//URL关键词
		JPanel pnlurlup = new JPanel ();
		pnlurlup.setLayout (new BorderLayout(0, 0) );
		JLabel lblurl = new JLabel ("URL: ");
		tfldurl = new JTextField ();
		pnlurlup.add (lblurl,BorderLayout.WEST);
		pnlurlup.add (tfldurl,BorderLayout.CENTER);

		pnldisplayurlskw = new LabelsManagement("",LabelsManagement.HIDEHEADERMODE, LabelsManagement.ONLYIPLAYMODE);

		pnlurlkw.add(pnlurlup,BorderLayout.NORTH);
		pnlurlkw.add(pnldisplayurlskw,BorderLayout.CENTER);
		
		
		panel.setLayout(gl_panel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			NodeLabelMatrixManagement dialog = new NodeLabelMatrixManagement();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}

