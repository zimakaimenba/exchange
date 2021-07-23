package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.ToolTipManager;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.A.MainGui.StockInfoManager;
import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsCacheListener;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;

import net.coderazzi.filters.gui.IFilterEditor;
import net.coderazzi.filters.gui.TableFilterHeader;



public class BanKuaiInfoTable extends BanKuaiandGeGuTableBasic implements NewsCacheListener
{
	private BanKuaiPopUpMenu popupMenuGeguNews;
	private static final long serialVersionUID = 1L;
	private StockInfoManager stockmanager;
	
	private Logger logger = Logger.getLogger(BanKuaiInfoTable.class);
	private BanKuaiInfoTableRenderer renderer;

	public BanKuaiInfoTable(StockInfoManager stockmanager1,String propfile) 
	{
		super (propfile);
		
		BanKuaiInfoTableModel bkmodel = new BanKuaiInfoTableModel (super.prop);
		this.setModel(bkmodel);
		
		super.setColumnPreferredWidth ();
		super.createTableHeaderTooltips ();
		
		this.stockmanager = stockmanager1;
		
		this.renderer =  new BanKuaiInfoTableRenderer (prop);
		
		this.createEvents ();
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		this.sortByZhanBiGrowthRate ();
		
//		filterHeader = new TableFilterHeader(this, AutoChoices.ENABLED); //https://coderazzi.net/tablefilter/index.html#    //https://stackoverflow.com/questions/16277700/i-want-to-obtain-auto-filtering-in-jtable-as-in-ms-excel
		
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
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	/*
	 * 
	 */
	private void sortByZhanBiGrowthRate ()
	{
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)this.getRowSorter();
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		String columnIndexToSortProp = super.prop.getProperty("columnIndexToSort"); //优先排序占比增长
		int columnIndexToSort = 3;
		if(columnIndexToSortProp != null)
			columnIndexToSort = Integer.parseInt(columnIndexToSortProp);
		
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
	     BanKuai bankuai = (BanKuai) tablemodel.getNode(modelRow);
	     Set<String> socialsetneg = bankuai.getSocialFriendsSetNegtive();
	     Set<String> socialsetpos = bankuai.getSocialFriendsSetPostive();
	     Set<String>  socialset = new HashSet<> ();
	     socialset.addAll(socialsetneg);
	     socialset.addAll(socialsetpos);
	     
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
				 thel3.appendText("CJEDPMAXWK");
		
				for(String friend : socialset) {
					 BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
					 int modelRow = 0;
					 try {
						 int rowIndex = tablemodel.getNodeRowIndex (friend);
						 if(rowIndex == -1)		 continue;
						 
						 modelRow = this.convertRowIndexToView(rowIndex);
					 } catch (java.lang.NullPointerException e) {e.printStackTrace(); }
					 
					 BkChanYeLianTreeNode frbk = tablemodel.getNode(friend);
					 String frbkcode = frbk.getMyOwnCode();
					 String frbkname = frbk.getMyOwnName();
					 org.jsoup.nodes.Element trelline = tableel.appendElement("tr");
					 org.jsoup.nodes.Element tdel1 = trelline.appendElement("td");
					 tdel1.appendText(frbkcode + frbkname);
					 
					 Integer kwcolumnindex = tablemodel.getKeyWrodColumnIndex("cjezbgrowrate");
					 if(kwcolumnindex == null) {
						 org.jsoup.nodes.Element tdel2 = trelline.appendElement("td");
						 tdel2.appendText("没有数据");
					 }
					 Double cjezbchangerate = (Double)this.getValueAt(modelRow, kwcolumnindex);
					 if(cjezbchangerate != null) {
						 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
			 	    	 percentFormat.setMinimumFractionDigits(2);
			        	 String cjezbchangeratepect = percentFormat.format (cjezbchangerate );
			        	 org.jsoup.nodes.Element tdel2 = trelline.appendElement("td");
						 tdel2.appendText(cjezbchangeratepect);
					 } else {
			        	 org.jsoup.nodes.Element tdel2 = trelline.appendElement("td");
						 tdel2.appendText("没有数据");
					 }
					 
//					 Integer kwcolumnindex2 = tablemodel.getKeyWrodColumnIndex("cjlzbgrowrate");
//					 Integer cjedpmaxwk;
//					if(kwcolumnindex2 != null) {
//						cjedpmaxwk = (Integer)this.getValueAt(modelRow, kwcolumnindex2);
//						org.jsoup.nodes.Element tdel3 = trelline.appendElement("td");
//						tdel3.appendText(cjedpmaxwk.toString());
//					} else {
//						org.jsoup.nodes.Element tdel3 = trelline.appendElement("td");
//						tdel3.appendText("没有相关数据");
//					}
						
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
		 
		 BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel)this.getModel()).getNode(model_row);
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
	public void onNewsChange(Collection<NewsCache> caches) {
		System.out.print("test");
		
	}
	@Override
	public void onNewsChange(NewsCache cache)
	{
		System.out.print("test");
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
		System.out.print("test");
		
	}

}
