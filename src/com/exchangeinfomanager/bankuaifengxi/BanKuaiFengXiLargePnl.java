package com.exchangeinfomanager.bankuaifengxi;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenu;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenuForTable;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarRenderer;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

/*
 * ������ʾĳ��node�����ڵ�ռ������
 */
public  class BanKuaiFengXiLargePnl extends JPanel implements BarChartPanelHightLightColumnListener
{
	private TDXNodes displaynode;
	private SystemConfigration sysconfig;
	private TDXNodes nodebankuai;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private Boolean exportuserselectedinfotocsv;
	private String globeperiod;

	public BanKuaiFengXiLargePnl (TDXNodes nodebkbelonged, TDXNodes node, LocalDate displayedstartdate1,LocalDate displayedenddate1,String period)
	{
		this.nodebankuai = nodebkbelonged;
		this.displaynode = node;
		this.globeperiod = period;
		initialzieSysconf ();
//		this.displaystartdate = displayedstartdate1;
//		this.displayenddate = displayedenddate1;
//		this.displayperiod = period;
//		this.bkdbopt = new BanKuaiDbOperation ();
		createGui (nodebkbelonged);
		createEvents ();

//		this.displayedstartdate = displayedstartdate1;
//		this.displayedenddate = displayedenddate1;
		updateData (nodebkbelonged, node,displayedstartdate1,displayedenddate1,period);
		
		
	}
	
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
	}
	/*
	 * ���͸��ɵ�K�ߣ�����ͬʱ��ʾ�����ԶԱ��о�
	 */
	private void refreshTDXGeGuAndBanKuaiKXian (TDXNodes selectnode,TDXNodes superbankuai)
	{
		LocalDate requireend = nodekpnl.getDispalyEndDate();
		LocalDate requirestart = nodekpnl.getDispalyStartDate();
		
		TDXNodes tmpnode = null;
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXGG) {
//			selectnode = allbksks.getStock((Stock)selectnode,requirestart,requireend,NodeGivenPeriodDataItem.WEEK);
//			//����K�����ƣ�ĿǰK�����ƺͳɽ��������ߺ��������������Ƿֿ��ģ����Ե���ʱ��Ҫ�ر�С�ģ��Ժ��ϲ�
//			this.allbksks.syncStockData((Stock)selectnode);
			
			tmpnode = selectnode;
		} else if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK) {
//			selectnode = allbksks.getBanKuai( (BanKuai)selectnode, requirestart,requireend, NodeGivenPeriodDataItem.WEEK);
//			this.allbksks.syncBanKuaiData( (BanKuai)selectnode);
			
			tmpnode = selectnode;
		} else if (selectnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
//			Stock stock = allbksks.getStock( ((StockOfBanKuai)selectnode).getStock(),requirestart,requireend,NodeGivenPeriodDataItem.WEEK);
//			this.allbksks.syncStockData( ((StockOfBanKuai)selectnode).getStock() );
			
			tmpnode = ((StockOfBanKuai)selectnode).getStock() ;
		}
		
		 if(superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 superbankuai = allbksks.getBanKuai( (BanKuai)superbankuai, requirestart,requireend, NodeGivenPeriodDataItem.WEEK,true);
			 this.allbksks.syncBanKuaiData( (BanKuai)superbankuai);
		 }
		
		
		this.allbksks.getDaPanKXian (requirestart,requireend,NodeGivenPeriodDataItem.DAY); 

		nodekpnl.updatedDate(superbankuai,tmpnode,requirestart,requireend,NodeGivenPeriodDataItem.DAY);
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		tfldselectedmsg.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiListEditorPane.EXPORTCSV_PROPERTY)) {
        			//����Ǵ�tfldselectedmsg����CSV����ǰ���ܺʹӰ��͸��ɵ���CSVһ���ã�ʱ���ߵ����⻹û�н������
//                	int extraresult = JOptionPane.showConfirmDialog(null,"Ҫ������CSV����ǰ����CSV�����ö�������գ��Ƿ������" , "Warning", JOptionPane.OK_CANCEL_OPTION);
//            		if(extraresult == JOptionPane.CANCEL_OPTION)
//            			return;
            	
            		exportuserselectedinfotocsv = true;
                }
                	
            }
		});

		nodekpnl.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiCandlestickPnl.ZHISHU_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String zhishuinfo = evt.getNewValue().toString();
                    
                    if(zhishuinfo.toLowerCase().equals("bankuaizhisu") ) {
                		
                		nodekpnl.displayQueKou(true);
                    	refreshTDXGeGuAndBanKuaiKXian ( displaynode, nodebankuai );
                		
                		
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"��������ӵĴ���ָ��", "999999");
                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
                    	if(zhishubk == null)  {
        					JOptionPane.showMessageDialog(null,"ָ����������","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}

                    	if(nodekpnl.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.TDXGG || nodekpnl.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU )
                    		nodekpnl.displayQueKou(true);
                    	else
                    		nodekpnl.displayQueKou(false);
                    	
                    	refreshTDXGeGuAndBanKuaiKXian ( displaynode, zhishubk );
//                    	nodekpnl.updatedDate(zhishubk,displaynode,displayedstartdate,displayedenddate,NodeGivenPeriodDataItem.DAY);
                		
                    	
                    }

                } 
             }
        });
		/*
		 * 
		 */
		mntmsaveimage.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0)
			{
				String image = sysconfig.getSavedImageStoredPath () + displaynode.getMyOwnCode()+ displaynode.getMyOwnName() + LocalDateTime.now().toString();
				image = image.replace('.', ' ');
				image = image.replace(":", "");
				image = image.replace(" ", "");
				image = image + ".bmp";
				File filefmxx = new File( image );
				if(!filefmxx.getParentFile().exists()) {  
		            //���Ŀ���ļ����ڵ�Ŀ¼�����ڣ��򴴽���Ŀ¼  
//		            logger.debug("Ŀ���ļ�����Ŀ¼�����ڣ�׼����������");  
		            if(!filefmxx.getParentFile().mkdirs()) {  
		                System.out.println("����Ŀ���ļ�����Ŀ¼ʧ�ܣ�");  
		                return ;  
		            }  
		        } 
				try {
					if (filefmxx.exists()) {
						filefmxx.delete();
						filefmxx.createNewFile();
					} else
						filefmxx.createNewFile();
				} catch (Exception e) {
						e.printStackTrace();
						return ;
				}

				Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
				BufferedImage capture;
				try {
					capture = new Robot().createScreenCapture(screenRect);
					ImageIO.write(capture, "bmp", filefmxx);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});

		nodebkcjezblargepnl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
            		LocalDate datekey = LocalDate.parse(selectedinfo);

    				nodebkcjezblargepnl.highLightSpecificBarColumn(datekey);
    				nodecombinedpnl.highLightSpecificBarColumn(datekey);
    				nodekpnl.highLightSpecificBarColumn(datekey);
    				
    				setUserSelectedColumnMessage(nodebankuai,selectedinfo);
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.AVERAGEDAILYCJE ) ) {
                	nodebkcjezblargepnl.resetLineDate ();
                	NodeXPeriodData dpnodexdata = nodebankuai.getNodeXPeroidData (NodeGivenPeriodDataItem.WEEK);
        			((BanKuaiFengXiCategoryBarChartCjePnl)nodebkcjezblargepnl).displayAverageDailyCjeOfWeekLineDataToGui(dpnodexdata,NodeGivenPeriodDataItem.WEEK);
        		}
            }
        });
		
		nodecombinedpnl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
            		LocalDate datekey = LocalDate.parse(selectedinfo);
            		
            		nodebkcjezblargepnl.highLightSpecificBarColumn(datekey);
    				nodekpnl.highLightSpecificBarColumn(datekey);
    				
    				setUserSelectedColumnMessage(displaynode,selectedinfo);
    				
    				
                }
            }
        });
		
	}
	/*
	 * ��ʾ�û����bar column��Ӧ����ʾ����Ϣ
	 */
	private void setUserSelectedColumnMessage(TDXNodes node, String selttooltips) 
	{
		String htmlstring = node.getNodeXPeroidDataInHtml(LocalDate.parse(selttooltips),this.globeperiod);
		tfldselectedmsg.displayNodeSelectedInfo (htmlstring);
	}
	/*
	 * �û�ѡ���˵���CSV������Ϣ���ݵ���һ���������壬�������NULL 
	 */
	public String getUserSelectedColumnMessage( )
	{
		if(exportuserselectedinfotocsv != null && exportuserselectedinfotocsv == true) {
			org.jsoup.nodes.Document doc = Jsoup.parse(tfldselectedmsg.getText());
//			org.jsoup.select.Elements content = doc.select("body");
//			String text = content.text();
//			return text;
			
//			return doc.toString();
			exportuserselectedinfotocsv = false;
			return tfldselectedmsg.getText();
		}
		
		return null;
	}

	private void updateData(TDXNodes nodebkbelogned, TDXNodes node, LocalDate displayedstartdate1, LocalDate displayedenddate1,
			String period) 
	{
		if(nodebkbelogned != null) {
			this.nodebkcjezblargepnl.setDrawAverageDailyCjeOfWeekLine(true);
			this.nodebkcjezblargepnl.updatedDate(nodebkbelogned, displayedstartdate1, displayedenddate1, period);
		}
		
		this.nodecombinedpnl.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		this.nodekpnl.updatedDate(node, displayedstartdate1,displayedenddate1,  NodeGivenPeriodDataItem.DAY);
		
		Integer[] wantedzhishutype = {Integer.valueOf(Meeting.ZHISHUDATE)};
		EventService allDbmeetingService = new DBMeetingService ();
	    Cache cacheAll = new Cache("ALL",allDbmeetingService, null,displayedstartdate1,displayedenddate1,wantedzhishutype);
	    
	    Integer[] wantednewstype = {Integer.valueOf(Meeting.NODESNEWS)};
	    Cache cacheNode = new Cache(node.getMyOwnCode(),allDbmeetingService, null,displayedstartdate1,displayedenddate1,wantednewstype);
	    
		Collection<InsertedMeeting> zhishukeylists = cacheAll.produceMeetings();
		Collection<InsertedMeeting> newslist = cacheNode.produceMeetings();
		
		zhishukeylists.addAll(newslist);
		nodekpnl.updateNewAndZhiShuKeyDates (zhishukeylists); //ָ���ؼ�����
	}
	
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
//		centerPanel.highLightSpecificBarColumn(selecteddate);
		this.nodebkcjezblargepnl.highLightSpecificBarColumn(selecteddate);
		this.nodecombinedpnl.highLightSpecificBarColumn(selecteddate);
		this.nodekpnl.highLightSpecificBarColumn(selecteddate);
	}

	@Override
	public void highLightSpecificBarColumn(Integer columnindex) {
		// TODO Auto-generated method stub
		
	}


//	private BanKuaiFengXiNodeCombinedCategoryPnl centerPanel;
	private JPanel centerPanel;
	private BanKuaiFengXiNodeCombinedCategoryPnl nodecombinedpnl;
	private BanKuaiFengXiCategoryBarChartPnl nodebkcjezblargepnl;
	private BanKuaiListEditorPane tfldselectedmsg;
	private BanKuaiFengXiCandlestickPnl nodekpnl;
	private JPopupMenu saveImage;
	private JMenuItem mntmsaveimage; 
	private void createGui(BkChanYeLianTreeNode nodebkbelonged) 
	{
		this.setLayout(new BorderLayout());

		this.nodekpnl = new BanKuaiFengXiCandlestickPnl ();
		this.nodekpnl.displayQueKou(true);
//		this.centerPanel = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		this.centerPanel = new JPanel ();
		this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.Y_AXIS));
		this.centerPanel.setPreferredSize(new Dimension(1640, 705)); //������ʾ��Ĵ�С
		
		this.nodecombinedpnl = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical","CJE");
		
		if(nodebkbelonged.getType() == BkChanYeLianTreeNode.TDXBK) {//����ϼ�node�ǰ�飬��ʾ�ǰ��ĳɽ���ռ��
			this.nodebkcjezblargepnl = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
			this.nodebkcjezblargepnl.setBarDisplayedColor(Color.RED.brighter());
		} else if(nodebkbelonged.getType() == BkChanYeLianTreeNode.DAPAN) {//����ϼ�node�Ǵ��̣���ʾ�Ǵ��̵ĳɽ���
			this.nodebkcjezblargepnl = new BanKuaiFengXiCategoryBarChartCjePnl ();
			this.nodebkcjezblargepnl.setBarDisplayedColor(Color.orange);
		}
		this.nodecombinedpnl.setDisplayZhanBiInLine(true);
		
		this.centerPanel.add(this.nodecombinedpnl);
		this.centerPanel.add(this.nodebkcjezblargepnl);
		
//		JPanel eastpanel;
		tfldselectedmsg = new BanKuaiListEditorPane();
//		tfldselectedmsg.setLineWrap(true);
		JScrollPane scrollPaneuserselctmsg = new JScrollPane (); 
//		JScrollBar bar = scrollPaneuserselctmsg.getHorizontalScrollBar();
		
//		tfldselectedmsg.setPreferredSize(new Dimension(150, 500));
		scrollPaneuserselctmsg.setPreferredSize(new Dimension(150, 200));
		
		scrollPaneuserselctmsg.setViewportView(tfldselectedmsg);

		this.add(this.nodekpnl,BorderLayout.NORTH);
		this.add(scrollPaneuserselctmsg,BorderLayout.EAST);
		this.add(centerPanel,BorderLayout.CENTER);
		
//		saveImage = new BanKuaiPopUpMenuForTable(this.stockmanager,this);
		saveImage = new JPopupMenu ();
		addPopup(this, saveImage);
		
		mntmsaveimage = new JMenuItem("���浽ϵͳ");
		saveImage.add(mntmsaveimage);

		this.setComponentPopupMenu(saveImage);
		
		

	}

	private  void addPopup(Component component, final JPopupMenu popup) 
	{
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
}
