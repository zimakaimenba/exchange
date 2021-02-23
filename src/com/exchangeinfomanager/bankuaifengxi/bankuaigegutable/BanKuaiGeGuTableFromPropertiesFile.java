package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;


public class BanKuaiGeGuTableFromPropertiesFile extends BanKuaiGeGuBasicTable 
{ 
	public BanKuaiGeGuTableFromPropertiesFile (StockInfoManager stockmanager1,String propfile)
	{
		super (stockmanager1,propfile);
		
		BanKuaiGeGuTableModelFromPropertiesFile bkgegumapmdl = new BanKuaiGeGuTableModelFromPropertiesFile(prop);
		this.setModel(bkgegumapmdl);
		
		this.renderer =  new BanKuaiGeGuBasicTableRenderer (prop); 
		
		super.setColumnPreferredWidth ();
		super.createTableHeaderTooltips ();
		super.setColumnPredefinedFilter();
		
		//sort http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableFromPropertiesFile.class);
	
	private BanKuaiGeGuBasicTableRenderer renderer;
	private String systeminstalledpath;
	String[] jtableTitleStringsTooltips = new String[8];

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
        int modelRow = this.convertRowIndexToModel(rowIndex);
        int colIndex = columnAtPoint(p);
        
        StockOfBanKuai stkofbk = (StockOfBanKuai) ((BanKuaiGeGuTableModelFromPropertiesFile)this.getModel()).getNode(modelRow);
        if(colIndex == 0) { // highlight the zhangfu of the week
        	String period = ((BanKuaiGeGuBasicTableModel)this.getModel()).getCurDisplayPeriod();
        	LocalDate requireddate = ((BanKuaiGeGuTableModelFromPropertiesFile)this.getModel()).getCurDisplayedDate ();
        	NodeXPeriodData nodexdata = stkofbk.getStock().getNodeXPeroidData(period);
        	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
        	if(zhangdiefu != null) {
        		zhangdiefu = zhangdiefu * 100;
        		tip = "周涨幅" + zhangdiefu .toString() + "%";
        	} 
        } else if(colIndex == 1) { //highlight when the stock was added to the bankuai
        	Set<Interval> timeinbk = stkofbk.getInAndOutBanKuaiInterval ();
        	String timett = "";
        	for(Interval tmpinterval : timeinbk ) {
        		DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
        		timett = "[" + requiredstartday + "~" + requiredendday + "]";
        	}
        	tip = timett;
        } else {
        	try {
        		tip =  getValueAt(rowIndex, colIndex).toString();
        	} catch ( java.lang.NullPointerException ex) {}
        }
//        try {
//        	if(colIndex == 2) { //权重column的tip要具体
//				org.jsoup.nodes.Document doc = Jsoup.parse("");
//				org.jsoup.select.Elements content = doc.select("body");
//				content.append( "10~1 ：占主业营收比重<br>" );
//				content.append( "0 : 营收占比几乎没有概念阶段<br>" );
//				content.append( "-1 : 毫无关系<br>" );
//				
//				tip = doc.toString();
//        	} else
//        		tip = getValueAt(rowIndex, colIndex).toString();
//        } catch (RuntimeException e1) {
//            //catch null pointer exception if mouse is over an empty line
//        }

        return tip;
    }
	
    
    private  String toUNIXpath(String filePath) 
   	{
   		    return filePath.replace('\\', '/');
   	}	
}