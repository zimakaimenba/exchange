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

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenu;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenuForTable;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

/*
 * 可以显示某个node大周期的占比数据
 */
public  class BanKuaiFengXiLargePnl extends JPanel implements BarChartPanelHightLightColumnListener
{
	private BkChanYeLianTreeNode displaynode;
	private SystemConfigration sysconfig;
	private BkChanYeLianTreeNode nodebankuai;
	private LocalDate displayedstartdate;
//	private LocalDate displaystartdate;
//	private LocalDate displayenddate;
//	private String displayperiod;
//	private BanKuaiDbOperation bkdbopt;
	private LocalDate displayedenddate;
	private AllCurrentTdxBKAndStoksTree allbksks;

	public BanKuaiFengXiLargePnl (BkChanYeLianTreeNode nodebkbelonged, BkChanYeLianTreeNode node, LocalDate displayedstartdate1,LocalDate displayedenddate1,String period)
	{
		this.nodebankuai = nodebkbelonged;
		this.displaynode = node;
		initialzieSysconf ();
//		this.displaystartdate = displayedstartdate1;
//		this.displayenddate = displayedenddate1;
//		this.displayperiod = period;
//		this.bkdbopt = new BanKuaiDbOperation ();
		createGui ();
		createEvents ();

		this.displayedstartdate = displayedstartdate1;
		this.displayedenddate = displayedenddate1;
		updateData (nodebkbelonged, node,displayedstartdate1,displayedenddate1,period);
	}
	
	private void initialzieSysconf ()
	{
		sysconfig = SystemConfigration.getInstance();
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
	}
	
	private void createEvents() 
	{
		nodekpnl.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiCandlestickPnl.ZHISHU_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String zhishuinfo = evt.getNewValue().toString();
                    
                    if(displaynode.getType() != BanKuaiAndStockBasic.BKGEGU && displaynode.getType() != BanKuaiAndStockBasic.TDXGG) {
                    	return;
                    }
                    
                    if(zhishuinfo.toLowerCase().equals("bankuaizhisu") ) {
      
                		nodekpnl.updatedDate(nodebankuai,displaynode,displayedstartdate,displayedenddate,StockGivenPeriodDataItem.DAY);
                		nodekpnl.displayRangeHighLowValue(true);
                		
                		
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	BanKuai zhishubk = null;
                    	if(displaynode.getMyOwnCode().startsWith("6") ) {
                    		BanKuai shdpbankuai = (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("999999",BanKuaiAndStockBasic.TDXBK);
                    		zhishubk = shdpbankuai;
                    	} else if(displaynode.getMyOwnCode().startsWith("3")) {
                    		BanKuai szdpbankuai = (BanKuai)  allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("399001",BanKuaiAndStockBasic.TDXBK);
                    		zhishubk = szdpbankuai;
                    	} else{
                    		
                    		BanKuai cybdpbankuai = (BanKuai)  allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("399006",BanKuaiAndStockBasic.TDXBK);     
                    		zhishubk = cybdpbankuai;
                    	}
                    	
                    	nodekpnl.updatedDate(zhishubk,displaynode,displayedstartdate,displayedenddate,StockGivenPeriodDataItem.DAY);
                		nodekpnl.displayRangeHighLowValue(true);
                    	
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

		this.nodebkcjezblargepnl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setUserSelectedColumnMessage(tooltip);

    				nodebkcjezblargepnl.highLightSpecificBarColumn(datekey);
    				nodecombinedpnl.highLightSpecificBarColumn(datekey);
    				nodekpnl.highLightSpecificBarColumn(datekey);
                }
            }
        });
		
		nodecombinedpnl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setUserSelectedColumnMessage(tooltip);
    				
    				nodebkcjezblargepnl.highLightSpecificBarColumn(datekey);
    				nodekpnl.highLightSpecificBarColumn(datekey);
                }
            }
        });
		
	}
	/*
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(String selttooltips) 
	{
		String allstring = selttooltips + "\n" + "*----------------*" + "\n";
		tfldselectedmsg.setText( allstring + tfldselectedmsg.getText() + "\n");
		tfldselectedmsg.setCaretPosition(0);
	}

	private void updateData(BkChanYeLianTreeNode nodebkbelogned, BkChanYeLianTreeNode node, LocalDate displayedstartdate1, LocalDate displayedenddate1,
			String period) 
	{
//		centerPanel.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		if(nodebkbelogned != null)
			this.nodebkcjezblargepnl.updatedDate(nodebkbelogned, displayedstartdate1, displayedenddate1, period);
		
		this.nodecombinedpnl.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		this.nodekpnl.updatedDate(node, displayedstartdate1,displayedenddate1,  StockGivenPeriodDataItem.DAY);
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
	private  BanKuaiFengXiCategoryBarChartPnl nodebkcjezblargepnl;
	private JTextArea tfldselectedmsg;
	private BanKuaiFengXiCandlestickPnl nodekpnl;
	private JPopupMenu saveImage;
	private JMenuItem mntmsaveimage; 
	private void createGui() 
	{
		this.setLayout(new BorderLayout());

		this.nodekpnl = new BanKuaiFengXiCandlestickPnl ();
//		this.centerPanel = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		this.centerPanel = new JPanel ();
		this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.Y_AXIS));
		this.centerPanel.setPreferredSize(new Dimension(1640, 705)); //设置显示框的大小
		
		this.nodecombinedpnl = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		this.nodebkcjezblargepnl = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
		this.nodebkcjezblargepnl.setBarDisplayedColor(Color.RED.brighter());
		
		this.centerPanel.add(this.nodecombinedpnl);
		this.centerPanel.add(this.nodebkcjezblargepnl);
		
		JPanel eastpanel;
		tfldselectedmsg = new JTextArea();
		tfldselectedmsg.setLineWrap(true);
		JScrollPane scrollPaneuserselctmsg = new JScrollPane (); 
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

	private static void addPopup(Component component, final JPopupMenu popup) 
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
