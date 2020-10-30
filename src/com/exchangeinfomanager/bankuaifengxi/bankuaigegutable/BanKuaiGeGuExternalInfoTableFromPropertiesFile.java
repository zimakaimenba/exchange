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

import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTable;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.commonlib.JTableToolTipHeader;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;

public class BanKuaiGeGuExternalInfoTableFromPropertiesFile extends BanKuaiGeGuBasicTable //implements BanKuaiGeGuMatchConditionListener
{
	

	private String systeminstalledpath;
	private Properties prop;
	private BanKuaiGeGuExternalInfoTableRendererFromPropertiesFile renderer;

	public BanKuaiGeGuExternalInfoTableFromPropertiesFile (StockInfoManager stockmanager1)
	{
		super (stockmanager1);
		
		setPropertiesInfo ();
		BanKuaiGeGuExternalInfoTableModelFromPropertiesFile bkgegumapmdl = new BanKuaiGeGuExternalInfoTableModelFromPropertiesFile(prop);
		this.setModel(bkgegumapmdl);
		
		this.renderer =  new BanKuaiGeGuExternalInfoTableRendererFromPropertiesFile (prop);

		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
		JTableToolTipHeader header = new JTableToolTipHeader(this.getColumnModel() );
	    header.setToolTipStrings(bkgegumapmdl.getTableHeader());
	    header.setToolTipText("Default ToolTip TEXT");
	    this.setTableHeader(header);
			    
	    setColumnPreferredWidth ();
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
	
	private void setPropertiesInfo ()
	{

		File directory = new File("");//设定为当前文件夹
		try{
//		    logger.debug(directory.getCanonicalPath());//获取标准的路径
//		    logger.debug(directory.getAbsolutePath());//获取绝对路径
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		} catch(Exception e) {
			System.exit(0);
		}
		
//		FileInputStream inputStream = null;
		String propxmlFileName = null ;
		try {
//			Properties properties = System.getProperties();
//		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
//		    
//			Properties prop = new Properties();
//			String propFileName =  this.systeminstalledpath  + "/config/config.properties";
//			inputStream = new FileInputStream(propFileName);
//			if (inputStream != null) {
//				prop.load(inputStream);
//			} else {
//				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
//			}
			
			prop = new Properties();
			String propFileName = this.systeminstalledpath  + "/config/bankuaigeguexternalinfo.properties";
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
//			createTableHeaderTooltips (prop);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	private void setColumnPreferredWidth ()
	{
		String column_width0  = prop.getProperty ("0column_preferredWidth");
		if(column_width0 != null) {
			this.getColumnModel().getColumn(0).setPreferredWidth(Integer.valueOf(column_width0) );
		}
		String column_width1  = prop.getProperty ("0column_preferredWidth");
		if(column_width1 != null) {
			this.getColumnModel().getColumn(1).setPreferredWidth(Integer.valueOf(column_width1) );
		}
		String column_width2  = prop.getProperty ("2column_preferredWidth");
		if(column_width2 != null) {
			this.getColumnModel().getColumn(2).setPreferredWidth(Integer.valueOf(column_width2) );
		}
		String column_width3  = prop.getProperty ("3column_preferredWidth");
		if(column_width3 != null) {
			this.getColumnModel().getColumn(3).setPreferredWidth(Integer.valueOf(column_width3) );
		}
		String column_width4  = prop.getProperty ("4column_preferredWidth");
		if(column_width4 != null) {
			this.getColumnModel().getColumn(4).setPreferredWidth(Integer.valueOf(column_width4) );
		}
		String column_width5  = prop.getProperty ("5column_preferredWidth");
		if(column_width5 != null) {
			this.getColumnModel().getColumn(5).setPreferredWidth(Integer.valueOf(column_width5) );
		}
		String column_width6  = prop.getProperty ("6column_preferredWidth");
		if(column_width6 != null) {
			this.getColumnModel().getColumn(6).setPreferredWidth(Integer.valueOf(column_width6) );
		}
		String column_width7  = prop.getProperty ("7column_preferredWidth");
		if(column_width7 != null) {
			this.getColumnModel().getColumn(7).setPreferredWidth(Integer.valueOf(column_width7) );
		}
//		this.getColumnModel().getColumn(1).setPreferredWidth(110);
//		this.getColumnModel().getColumn(7).setPreferredWidth(30);
//		this.getColumnModel().getColumn(4).setPreferredWidth(40);
//		this.getColumnModel().getColumn(6).setPreferredWidth(40);
		
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
		String stockcode = ((BanKuaiGeGuBasicTableModel)(this.getModel())).getStockCode(modelRow);
		int weight = ((BanKuaiGeGuBasicTableModel)(this.getModel())).getStockCurWeight (modelRow);
		
		String weightresult = JOptionPane.showInputDialog(null,"请输入股票在该板块权重！\n\r"
											+ "10~1 ：占主业营收比重,\n\r 0 : 营收占比几乎没有概念阶段,\n\r -1 : 毫无关系"
				,weight);
		try {
			int newweight = Integer.parseInt(weightresult);
			if(newweight> 10 || newweight < -1)
				JOptionPane.showMessageDialog(null,"权重值范围10 ~ -1！\n\r"
						,"Warning",JOptionPane.WARNING_MESSAGE);
			
			if(weight != newweight) {
				bkdbopt.setStockWeightInBanKuai (bkcode,"",stockcode,newweight);
				( (BanKuaiGeGuBasicTableModel)this.getModel() ).setStockCurWeight (modelRow,newweight);
			}
		} catch (java.lang.NumberFormatException e) {
			return;
		}
	}
	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc)
	{
		((BanKuaiGeGuBasicTableModel)this.getModel()).setDisplayMatchCondition(expc);
    	this.repaint();
		
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
