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
import java.util.Properties;

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

import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeExchangeDataServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeExchangeDataServicesPanel;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjlPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;

import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;


/*
 * 可以显示某个node大周期的占比数据
 */
public  class BanKuaiFengXiLargePnl extends JPanel implements BarChartPanelHightLightColumnListener
{
	private TDXNodes displaynode;
//	private SystemConfigration sysconfig;
	private TDXNodes nodebankuai;
//	private AllCurrentTdxBKAndStoksTree allbksks;
//	private Boolean exportuserselectedinfotocsv;
	private String globeperiod;

	public BanKuaiFengXiLargePnl (TDXNodes nodebkbelonged, TDXNodes node, LocalDate displayedstartdate1,LocalDate displayedenddate1,String period,String guitype, Properties prop)
	{
		this.nodebankuai = nodebkbelonged;
		this.displaynode = node;
		this.globeperiod = period;
//		initialzieSysconf ();

		createGui (nodebkbelonged, guitype);
		this.nodecombinedpnl.setProperties(prop);
		
		createEvents ();


		updateData (nodebkbelonged, node,displayedstartdate1,displayedenddate1,period);
	}
	
//	private void initialzieSysconf ()
//	{
////		sysconfig = SystemConfigration.getInstance();
//		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
//	}
	/*
	 * 板块和个股的K线，可以同时显示，用以对比研究
	 */
	private void refreshTDXGeGuAndBanKuaiKXian (TDXNodes selectnode,TDXNodes superbankuai)
	{
		LocalDate requireend = nodekpnl.getDispalyEndDate();
		LocalDate requirestart = nodekpnl.getDispalyStartDate();
		
		TDXNodes tmpnode = null;
		if(selectnode.getType() == BkChanYeLianTreeNode.DAPAN) {
			tmpnode = selectnode;
		} else
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXGG) {
//			selectnode = allbksks.getStock((Stock)selectnode,requirestart,requireend,NodeGivenPeriodDataItem.WEEK);
//			//日线K线走势，目前K线走势和成交量在日线和日线以上周期是分开的，所以调用时候要特别小心，以后会合并
//			this.allbksks.syncStockData((Stock)selectnode);
			
			tmpnode = selectnode;
		} else 
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK) {
//			selectnode = allbksks.getBanKuai( (BanKuai)selectnode, requirestart,requireend, NodeGivenPeriodDataItem.WEEK);
//			this.allbksks.syncBanKuaiData( (BanKuai)selectnode);
			
			tmpnode = selectnode;
		} else
		if (selectnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
//			Stock stock = allbksks.getStock( ((StockOfBanKuai)selectnode).getStock(),requirestart,requireend,NodeGivenPeriodDataItem.WEEK);
//			this.allbksks.syncStockData( ((StockOfBanKuai)selectnode).getStock() );
			
			tmpnode = ((StockOfBanKuai)selectnode).getStock() ;
		}
		
		 if(superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
			 superbankuai = (TDXNodes) svsbk.getNodeData(superbankuai,requirestart,requireend, NodeGivenPeriodDataItem.WEEK,true);
			 svsbk.syncNodeData(superbankuai);
			 
			 svsbk = null;
		 }
		
		 SvsForNodeOfDaPan svsdp = new SvsForNodeOfDaPan ();
		 svsdp.getNodeData ("", requirestart,requireend,NodeGivenPeriodDataItem.DAY,true);
		 
		 nodekpnl.updatedDate(superbankuai,tmpnode,requirestart,requireend,NodeGivenPeriodDataItem.DAY);
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		tfldselectedmsg.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(DisplayNodeExchangeDataServicesPanel.EXPORTCSV_PROPERTY)) {
//            		exportuserselectedinfotocsv = true;
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
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"请输入叠加的大盘指数", "999999");
                    	BanKuai zhishubk =  (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
//                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
                    	if(zhishubk == null)  {
        					JOptionPane.showMessageDialog(null,"指数代码有误！","Warning",JOptionPane.WARNING_MESSAGE);
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
				String image = (new SetupSystemConfiguration()).getSavedImageStoredPath () + displaynode.getMyOwnCode()+ displaynode.getMyOwnName() + LocalDateTime.now().toString();
				image = image.replace('.', ' ');
				image = image.replace(":", "");
				image = image.replace(" ", "");
				image = image + ".bmp";
				File filefmxx = new File( image );
				if(!filefmxx.getParentFile().exists()) {  
		            //如果目标文件所在的目录不存在，则创建父目录  
//		            logger.debug("目标文件所在目录不存在，准备创建它！");  
		            if(!filefmxx.getParentFile().mkdirs()) {  
		                System.out.println("创建目标文件所在目录失败！");  
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
//                	nodebkcjezblargepnl.resetLineDate ();
//                	NodeXPeriodData dpnodexdata = nodebankuai.getNodeXPeroidData (NodeGivenPeriodDataItem.WEEK);
//        			((BanKuaiFengXiCategoryBarChartCjePnl)nodebkcjezblargepnl).displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis(dpnodexdata,NodeGivenPeriodDataItem.WEEK);
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
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(TDXNodes node, String selttooltips) 
	{
		DisplayNodeExchangeDataServices svsnodedata = new DisplayNodeExchangeDataServices (node, LocalDate.parse(selttooltips), this.globeperiod);
		DisplayNodeExchangeDataServicesPanel nodedatapnl = new DisplayNodeExchangeDataServicesPanel (svsnodedata) ;
		tfldselectedmsg.add(nodedatapnl);
		tfldselectedmsg.revalidate();
		tfldselectedmsg.repaint();
	}
	/*
	 * 用户选择了导出CSV，把信息传递到上一级才有意义，否则就是NULL 
	 */
	public String getUserSelectedColumnMessage( )
	{
//		if(exportuserselectedinfotocsv != null && exportuserselectedinfotocsv == true) {
//			org.jsoup.nodes.Document doc = Jsoup.parse(tfldselectedmsg.getText());
////			org.jsoup.select.Elements content = doc.select("body");
////			String text = content.text();
////			return text;
//			
////			return doc.toString();
//			exportuserselectedinfotocsv = false;
//			return tfldselectedmsg.getText();
//		}
		
		return null;
	}

	private void updateData(TDXNodes nodebkbelogned, TDXNodes node, LocalDate displayedstartdate1, LocalDate displayedenddate1,
			String period) 
	{
		if(nodebkbelogned != null) {
//			this.nodebkcjezblargepnl.setDrawAverageDailyCjeOfWeekLine(true);
			//实践后决定永远显示大盘的成交量。
			BanKuaiAndStockTree treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
			DaPan dapan = (DaPan) treeofbkstk.getModel().getRoot();
			nodecombinedpnl.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (dapan);
			this.nodebkcjezblargepnl.updatedDate(dapan, displayedstartdate1, displayedenddate1, period);
		}
		
		this.nodecombinedpnl.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		this.nodekpnl.updatedDate(node, displayedstartdate1,displayedenddate1,  NodeGivenPeriodDataItem.DAY);
		
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
    	ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache (node.getMyOwnCode(),svsnews,svslabel,displayedstartdate1,displayedenddate1);
    	svsnews.setCache(newcache);
    	nodekpnl.displayNodeNewsToGui (newcache.produceNews() );
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
	public BanKuaiFengXiNodeCombinedCategoryPnl nodecombinedpnl;
	private BanKuaiFengXiCategoryBarChartPnl nodebkcjezblargepnl;
	private JPanel tfldselectedmsg;
	private BanKuaiFengXiCandlestickPnl nodekpnl;
	private JPopupMenu saveImage;
	private JMenuItem mntmsaveimage; 
	private void createGui(BkChanYeLianTreeNode nodebkbelonged, String guitype) 
	{
		this.setLayout(new BorderLayout());

		this.nodekpnl = new BanKuaiFengXiCandlestickPnl ();
		this.nodekpnl.displayQueKou(true);
//		this.centerPanel = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		this.centerPanel = new JPanel ();
		this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.Y_AXIS));
		this.centerPanel.setPreferredSize(new Dimension(1800, 705)); //设置显示框的大小
		
		this.nodecombinedpnl = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical",guitype);
		this.nodecombinedpnl.setDrawAverageDailyCjeOfWeekLine(true); //保证个股显示是上日均成交额，下占比线
		nodecombinedpnl.setDisplayZhanBiInLine(true);
		
		if(guitype.toUpperCase().equals("CJE"))
			this.nodebkcjezblargepnl = new BanKuaiFengXiCategoryBarChartCjePnl ();
		else
			this.nodebkcjezblargepnl = new BanKuaiFengXiCategoryBarChartCjlPnl ();
		
		this.centerPanel.add(this.nodecombinedpnl);
		this.centerPanel.add(this.nodebkcjezblargepnl);
		
//		JPanel eastpanel;
		tfldselectedmsg = new JPanel();
		tfldselectedmsg.setLayout(new BoxLayout(tfldselectedmsg, BoxLayout.Y_AXIS));
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
		
		mntmsaveimage = new JMenuItem("保存到系统");
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
