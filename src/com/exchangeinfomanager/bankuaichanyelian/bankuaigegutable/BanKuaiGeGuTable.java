package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
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
		this.getColumnModel().getColumn(7).setPreferredWidth(30);
		this.getColumnModel().getColumn(4).setPreferredWidth(40);
		this.getColumnModel().getColumn(6).setPreferredWidth(40);
		
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
	String[] jtableTitleStringsTooltips = { "代码(周阳线阴线)", "名称(是否包含在分析文件)",
			"板块成交额贡献(跳空缺口)","大盘CJEZB增长率(突出涨幅和股价)","CJEDpMaxWk(突出DPMAXWK)","大盘CJLZB增长率(大于指定均线)","周平均成交额MAXWK","高级排序排名(流通市值)"};
//	{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","周平均成交额MAXWK"};
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
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc)
    {
    	((BanKuaiGeGuTableModel)this.getModel()).setDisplayMatchCondition(expc);
    	this.repaint();
	}
	
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        if(colIndex == 1) {
        	StockOfBanKuai stkofbk = ((BanKuaiGeGuTableModel)this.getModel()).getStock(rowIndex);
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
	



}




