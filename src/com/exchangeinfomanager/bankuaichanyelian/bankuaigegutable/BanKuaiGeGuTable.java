package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;   
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;


import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTable extends BanKuaiGeGuBasicTable 
{ 
	public BanKuaiGeGuTable (StockInfoManager stockmanager1)
	{
		super (stockmanager1);
		
		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel();
		this.setModel(bkgegumapmdl);
		this.renderer =  new BanKuaiGeGuTableRenderer (); 
//		this.setDefaultRenderer(Object.class, this.renderer );
		
		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
//		ToolTipHeader header = new ToolTipHeader(this.getColumnModel() );
//	    header.setToolTipStrings(bkgegumapmdl.getTableHeader());
//	    header.setToolTipText("Default ToolTip TEXT");
//	    this.setTableHeader(header);
		
		//sort http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//		int columnIndexToSort = 3; //优先排序占比增长
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
		
		this.getColumnModel().getColumn(1).setPreferredWidth(110);
		this.getColumnModel().getColumn(2).setPreferredWidth(30);
		this.getColumnModel().getColumn(5).setPreferredWidth(40);
		this.getColumnModel().getColumn(7).setPreferredWidth(40);
		
//		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
//		headerRenderer.setBackground(new Color(239, 198, 46));
//
//		for (int i = 0; i < myJTable.getModel().getColumnCount(); i++) {
//		        this.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
//		}
		
		
		
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTable.class);
	
	private BanKuaiGeGuTableRenderer renderer;
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	
	//Implement table header tool tips.
	String[] jtableTitleStringsTooltips = { "代码(周阳线阴线)", "名称(是否包含在分析文件)","高级排序排名(流通市值)",
			"板块成交额贡献(跳空缺口)","大盘CJEZB增长率(成交额区间)","CJEDpMaxWk(突出DPMAXWK)","大盘CJLZB增长率(大于指定均线)","CJLDpMaxWk(突出DPMAXWK)"};
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

	@Override
	public void hightLightFxValues(ExportCondition expc) 
	{
//		Integer cjezbbkmax = expc.getSettinBkmaxwk();
		Integer cjemaxwk = expc.getSettingCjemaxwk();
		Integer cjezbdpmax = expc.getSettinDpmaxwk();
		Integer cjezbdpmin = expc.getSettingDpminwk();
		Double cjemin = expc.getSettingCjemin();
		Double cjemax = expc.getSettingCjeMax();
		Double showhsl = expc.getSettingHsl();
		Double showltszmax = expc.getLiuTongShiZhiMax();
		Double showltszmin = expc.getLiuTongShiZhiMin();
		Boolean showhuibudownquekou = expc.shouldHighLightHuiBuDownQueKou();
		String displayma = expc.getSettingMAFormula();
		
		
//			((BanKuaiGeGuTableModel)this.getModel()).setDisplayCjeBKMaxWk( cjezbbkmax);
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayCjeMaxWk (cjemaxwk);
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayCjeZhanBiDPMaxMinWk (cjezbdpmax,cjezbdpmin);
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayChenJiaoEr (cjemin,cjemax);
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayHuanShouLv(showhsl);
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayLiuTongShiZhi(showltszmin,showltszmax);
			((BanKuaiGeGuTableModel)this.getModel()).setHighLightHuiBuDownQueKou(showhuibudownquekou);
			((BanKuaiGeGuTableModel)this.getModel()).setDisplayMAFormula(displayma);
		
		this.repaint();
		
	}
	
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        tip =  getValueAt(rowIndex, colIndex).toString();
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
	



}




