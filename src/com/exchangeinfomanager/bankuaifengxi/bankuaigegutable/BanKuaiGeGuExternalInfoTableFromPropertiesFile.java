package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;

import java.util.Properties;


import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.commonlib.JTableToolTipHeader;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;

public class BanKuaiGeGuExternalInfoTableFromPropertiesFile extends BanKuaiGeGuBasicTable //implements BanKuaiGeGuMatchConditionListener
{
	

	private String systeminstalledpath;
//	private Properties prop;
	private BanKuaiGeGuBasicTableRenderer renderer;

	public BanKuaiGeGuExternalInfoTableFromPropertiesFile (StockInfoManager stockmanager1, String propfile)
	{
		super (stockmanager1,propfile);
		
//		setPropertiesInfo ();
		BanKuaiGeGuExternalInfoTableModelFromPropertiesFile bkgegumapmdl = new BanKuaiGeGuExternalInfoTableModelFromPropertiesFile(prop);
		this.setModel(bkgegumapmdl);
		
		this.renderer =  new BanKuaiGeGuBasicTableRenderer (prop);
		
		super.setColumnPreferredWidth ();
		super.createTableHeaderTooltips ();
		super.setColumnPredefinedFilter ();
		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
//		JTableToolTipHeader header = new JTableToolTipHeader(this.getColumnModel() );
//	    header.setToolTipStrings(bkgegumapmdl.getTableHeader());
//	    header.setToolTipText("Default ToolTip TEXT");
//	    this.setTableHeader(header);
			    
//	    setColumnPreferredWidth ();
		//sort http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//		int columnIndexToSort = 3; //优先排序占比增长
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
		
		menuItemQuanZhong = new JMenuItem("设置股票板块权重");
		popupMenuGeguNews.add(menuItemQuanZhong);
		
//		this.setComponentPopupMenu(popupMenuGeguNews);
		
		createEvents ();

	}
	
	private JMenuItem menuItemQuanZhong;

	private void createEvents ()
	{
		menuItemQuanZhong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setGeGuWeightInBanKuai ();
			}
			
		});
		
	}
	/*
     * 设置该板块个股的权重
     */
	private void setGeGuWeightInBanKuai()
    {
		int row = this.getSelectedRow();
		if(row < 0)
			return;
		int modelRow = this.convertRowIndexToModel(row);
		
//		BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) treechanyelian.getLastSelectedPathComponent();
		BanKuai bkcode = ((BanKuaiGeGuBasicTableModel)this.getModel()).getCurDispalyBandKuai();
		String stockcode = ((BanKuaiGeGuBasicTableModel)(this.getModel())).getNodeCode(modelRow);
		int weight = ((BanKuaiGeGuExternalInfoTableModelFromPropertiesFile)(this.getModel())).getStockCurWeight (modelRow);
		
		String weightresult = JOptionPane.showInputDialog(null,"请输入股票在该板块权重！\n\r"
											+ "10~1 ：占主业营收比重,\n\r 0 : 营收占比几乎没有概念阶段,\n\r -1 : 毫无关系"
				,weight);
		try {
			int newweight = Integer.parseInt(weightresult);
			if(newweight> 10 || newweight < -1)
				JOptionPane.showMessageDialog(null,"权重值范围10 ~ -1！\n\r"
						,"Warning",JOptionPane.WARNING_MESSAGE);
			
			if(weight != newweight) {
				bkdbopt.setStockWeightInBanKuai (bkcode,stockcode,newweight);
				( (BanKuaiGeGuExternalInfoTableModelFromPropertiesFile)this.getModel() ).setStockCurWeight (modelRow,newweight);
			}
		} catch (java.lang.NumberFormatException e) {
			return;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	
	
	 public String getToolTipText(MouseEvent e) {
	        String tip = null;
	        java.awt.Point p = e.getPoint();
	        int rowIndex = rowAtPoint(p);
	        int colIndex = columnAtPoint(p);

	        try {
	        	if(colIndex == 2) { //权重column的tip要具体
					org.jsoup.nodes.Document doc = Jsoup.parse("");
					org.jsoup.select.Elements content = doc.select("body");
					content.append( "10~1 ：占主业营收比重<br>" );
					content.append( "0 : 营收占比几乎没有概念阶段<br>" );
					content.append( "-1 : 毫无关系<br>" );
					
					tip = doc.toString();
	        	} else
	        		tip = getValueAt(rowIndex, colIndex).toString();
	        } catch (RuntimeException e1) {
	            //catch null pointer exception if mouse is over an empty line
	        }

	        return tip;
    }
	 
	 private  String toUNIXpath(String filePath) 
	 {
	   		    return filePath.replace('\\', '/');
	 }
}
