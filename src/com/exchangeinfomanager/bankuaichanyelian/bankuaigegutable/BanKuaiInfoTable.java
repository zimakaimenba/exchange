package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiInfoTable extends JTable implements BarChartHightLightFxDataValueListener
{
	private BanKuaiPopUpMenu popupMenuGeguNews;
	private static final long serialVersionUID = 1L;
	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
//	private AllCurrentTdxBKAndStoksTree allbkgg;
	private BanKuaiAndChanYeLian2 bkcyl;
	private static Logger logger = Logger.getLogger(BanKuaiInfoTable.class);

	public BanKuaiInfoTable(StockInfoManager stockmanager1) 
	{
		super ();
		
		BanKuaiInfoTableModel bkmodel = new BanKuaiInfoTableModel ();
		this.setModel(bkmodel);
		
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
		this.stockmanager = stockmanager1;
		
		this.createEvents ();
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		this.sortByZhanBiGrowthRate ();
		
//		ToolTipHeader header = new ToolTipHeader(this.getColumnModel() );
//	    header.setToolTipStrings(bkmodel.getTableHeader());
//	    header.setToolTipText("Default ToolTip TEXT");
//	    this.setTableHeader(header);
	    
		
		
//		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.getColumnModel().getColumn(0).setPreferredWidth(105);
		this.getColumnModel().getColumn(1).setPreferredWidth(115);
//		this.getColumnModel().getColumn(2).setPreferredWidth(35);
	}
	/*
	 * 
	 */
	private void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 2; //优先排序占比增长
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
	 */
    public String getToolTipText(MouseEvent e) 
    {
 
	        
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        
      	 BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
	     if(tablemodel.getRowCount() == 0) {
	        	return null;
	      }
	        
	     LocalDate curdate = tablemodel.getCurDisplayedDate();
	        
	     int modelRow = convertRowIndexToModel(rowIndex);
	     BanKuai bankuai = tablemodel.getBanKuai(modelRow);
	     Set<String> socialset = bankuai.getSocialFriendsSet();

	     if(colIndex != 1) {
	    	 try {
	             tip = getValueAt(rowIndex, colIndex).toString();
	         } catch (RuntimeException e1) {
	             //catch null pointer exception if mouse is over an empty line
	         }
	     } else { //显示所有好友的结果
	    	 if(socialset.isEmpty())
	    		 tip = getValueAt(rowIndex, colIndex).toString();
	    	 else
	    		 tip = createHtmlTipsOfSocailFriends(bankuai, socialset); 
	     }
        

        return tip;
    }
    
  //Implement table header tool tips.
  	String[] jtableTitleStringsTooltips = { "板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名(突出周线阳线阴线)"};
      protected JTableHeader createDefaultTableHeader() 
      {
          return new JTableHeader(columnModel) {
              public String getToolTipText(MouseEvent e) {
                  String tip = null;
                  java.awt.Point p = e.getPoint();
                  int index = columnModel.getColumnIndexAtX(p.x);
                  int realIndex = 
                          columnModel.getColumn(index).getModelIndex();
                  return jtableTitleStringsTooltips[realIndex];
              }
          };
      }
    
	private String createHtmlTipsOfSocailFriends(BanKuai bankuai, Set<String> socialset) 
	{
		String html = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Elements body = doc.getElementsByTag("body");
		for(Element elbody : body) {
				 org.jsoup.nodes.Element tableel = elbody.appendElement("table");
				 tableel.attr("border", "1");
				 
				 org.jsoup.nodes.Element trelheader = tableel.appendElement("tr");
				 org.jsoup.nodes.Element thel1 = trelheader.appendElement("th");
				 thel1.appendText("好友板块"); 
				 org.jsoup.nodes.Element thel2 = trelheader.appendElement("th");
				 thel2.appendText("CJE占比增长率");
				 org.jsoup.nodes.Element thel3 = trelheader.appendElement("th");
				 thel3.appendText("CJL占比增长率");
		
				for(String friend : socialset) {
					 BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
					 int rowIndex = tablemodel.getBanKuaiRowIndex (friend);
					 if(rowIndex == -1)
						 continue;
					 int modelRow = this.convertRowIndexToView(rowIndex);

					 String frbkcode = (String) this.getValueAt(modelRow, 0);
					 String frbkname = (String) this.getValueAt(modelRow, 1);
					 Double cjezbchangerate = (Double)this.getValueAt(modelRow, 2);
					 Double cjlzbchangrate = (Double)this.getValueAt(modelRow, 4);
		    		 
		    		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
		 	    	 percentFormat.setMinimumFractionDigits(1);
		        	 String cjezbchangeratepect = percentFormat.format (cjezbchangerate );
		        	 String cjlbchangeratepect = percentFormat.format (cjlzbchangrate );
		        	 
		        	 org.jsoup.nodes.Element trelline = tableel.appendElement("tr");
					 org.jsoup.nodes.Element tdel1 = trelline.appendElement("td");
					 tdel1.appendText(frbkcode + frbkname); 
					 org.jsoup.nodes.Element tdel2 = trelline.appendElement("td");
					 tdel2.appendText(cjezbchangeratepect);
					 org.jsoup.nodes.Element tdel3 = trelline.appendElement("td");
					 tdel3.appendText(cjlbchangeratepect);
		        	 
				}	 
 			
		}
		
		 
		return doc.toString();
	}
	private void createEvents ()
	{
		this.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) 
        	{
        		tableMouseClickActions (arg0);
        	}
        	
        	public void mouseEntered(MouseEvent me) {
        	    ToolTipManager.sharedInstance().setDismissDelay(60000);
        	}

        });
		
		popupMenuGeguNews = new BanKuaiPopUpMenuForTable(this.stockmanager,this);
		this.setComponentPopupMenu(popupMenuGeguNews);
	}
	/*
	 * 
	 */
	public BanKuaiPopUpMenu getPopupMenu ()
	{
		return this.popupMenuGeguNews;
	}
	protected void showGeGuInfoWin() 
	{
		 int  view_row = this.getSelectedRow();
		 int  model_row = this.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
		 
		 BanKuai bankuai = ((BanKuaiInfoTableModel)this.getModel()).getBanKuai(model_row);
		 this.stockmanager.getcBxstockcode().updateUserSelectedNode(bankuai );
		 this.stockmanager.toFront();
	}
	/*
	 * 
	 */
	private void tableMouseClickActions (MouseEvent arg0)
	{

        		 int  view_row = this.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
				 int  view_col = this.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
				 int  model_row = this.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
				 int  model_col = this.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
				 
        		if (arg0.getClickCount() == 1) {
        		}
        		 if (arg0.getClickCount() == 2) {
        			 showGeGuInfoWin ();
				 }

	}
	@Override
	public void hightLightFxValues(ExportCondition expc) 
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
	 */
	private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border inside = new EmptyBorder(0, 1, 0, 1);
	private Border highlight = new CompoundBorder(outside, inside);
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		
	        Component comp = super.prepareRenderer(renderer, row, col);
	        JComponent jc = (JComponent)comp;
	        
	        BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
	        if(tablemodel.getRowCount() == 0) {
	        	return null;
	        }
	        
	        LocalDate curdate = tablemodel.getCurDisplayedDate();
	        
	        int modelRow = convertRowIndexToModel(row);
	        BanKuai bankuai = tablemodel.getBanKuai(modelRow);
	        
	        String bktype = bankuai.getBanKuaiLeiXing();
	        if(bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
	        	Font defaultFont = this.getFont();
	        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
	        	comp.setFont(font);
	        }
	        
	       
	        
	      //为不同情况突出显示不同的颜色
	        Color foreground = super.getForeground(), background = Color.white;
	        if(!bankuai.isExportTowWlyFile() )
        		foreground = Color.GRAY;
	        
	        int rowcurselected = this.getSelectedRow();
	        if(rowcurselected != -1) {
	        	int modelRowcurselected = this.convertRowIndexToModel(rowcurselected);
				BanKuai bkcurselected = ((BanKuaiInfoTableModel)this.getModel()).getBanKuai(modelRowcurselected);
		        Set<String> socialset = bkcurselected.getSocialFriendsSet();
		        String bkcode =   bankuai.getMyOwnCode();
		        if(socialset.contains(bkcode)) 
		        	jc.setBorder( highlight );
	        }
	       
	        if (comp instanceof JLabel && col == 7) {
	        	NodeXPeriodDataBasic nodexdata = bankuai.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
	        	OHLCItem weekohlc = nodexdata.getSpecificDateOHLCData(curdate, 0);
	        	if(weekohlc != null) {
	        		double close = weekohlc.getCloseValue();
	        		double open = weekohlc.getOpenValue();
	        		if(close!= 0.0 && open >= close)
	        			background = Color.GREEN;
			        else if(close!= 0.0 && open < close)
			        	background = Color.RED;
			        else
			        	background = Color.WHITE;
	        	}
	        	
	        } 
	        if (comp instanceof JLabel && ( col == 3 ||   col == 5 ||  col == 6  )) {
	        	background = new Color(51,204,255);
	        }
	        //"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
	        if (comp instanceof JLabel && (col == 2 ||  col == 3 ||  col == 4 ||  col == 5 ||  col == 6  )) {
            	String value =  ((JLabel)comp).getText();
            	if(value == null || value.length() == 0)
            		return null;
            	
            	String valuepect = null;
            	try {
            		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value).doubleValue();
            		 
            		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
 	    	    	 percentFormat.setMinimumFractionDigits(1);
	            	 valuepect = percentFormat.format (formatevalue );
            	} catch (java.lang.NumberFormatException e)   	{
            		e.printStackTrace();
            	} catch (ParseException e) {
					e.printStackTrace();
				}
            	((JLabel)comp).setText(valuepect);
	        } 
	        if( col == 1 && !bktype.equals(BanKuai.NOGGWITHSELFCJL) ) {
	        	NodesTreeRelated tmptreerelated = this.bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(bankuai.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK).getNodeTreeRelated();
//	        	TreeRelated tmptreerelated = bankuai.getNodeTreerelated (); 
	        	Integer patchfilestocknum = ((BanKuaiTreeRelated)tmptreerelated).getStocksNumInParsedFileForSpecificDate (curdate);
	        	Boolean selfisin = ((BanKuaiTreeRelated)tmptreerelated).selfIsMatchModel (curdate);
	        	 
	        	if(patchfilestocknum != null && patchfilestocknum > 0 )
		        	background = Color.ORANGE;
		        else
		        	background = Color.white;
	        	
//	        	if(selfisin) {//板块自身满足模型,用粗体
//		        	 Font font = new Font("黑体",Font.BOLD + Font.ITALIC,14);
//		        	 ((JLabel)comp).setFont(font);
//		         } else {
//		        	 Font font=new Font("宋体",Font.PLAIN,14); 
//		        	 ((JLabel)comp).setFont(font);
//		         }
	        }
	        
	       comp.setBackground(background);
	       comp.setForeground(foreground);

	       if(this.isRowSelected(row) && col == 0 ) {
		    	comp.setBackground(Color.blue);
		    }
	        return comp;
	}


}
