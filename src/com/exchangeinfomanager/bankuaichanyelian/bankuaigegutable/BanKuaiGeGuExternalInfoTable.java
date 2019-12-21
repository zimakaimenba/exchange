package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchConditionListener;
import com.exchangeinfomanager.commonlib.JTableToolTipHeader;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;

public class BanKuaiGeGuExternalInfoTable extends BanKuaiGeGuBasicTable implements BanKuaiGeGuMatchConditionListener
{
	

	public BanKuaiGeGuExternalInfoTable (StockInfoManager stockmanager1)
	{
		super (stockmanager1);
		
		BanKuaiGeGuExternalInfoTableModel bkgegumapmdl = new BanKuaiGeGuExternalInfoTableModel();
		this.setModel(bkgegumapmdl);

		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
		JTableToolTipHeader header = new JTableToolTipHeader(this.getColumnModel() );
	    header.setToolTipStrings(bkgegumapmdl.getTableHeader());
	    header.setToolTipText("Default ToolTip TEXT");
	    this.setTableHeader(header);
		
		//sort http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//		int columnIndexToSort = 3; //��������ռ������
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
		
		menuItemQuanZhong = new JMenuItem("���ù�Ʊ���Ȩ��");
		popupMenuGeguNews.add(menuItemQuanZhong);
		
//		this.setComponentPopupMenu(popupMenuGeguNews);
		
		createEvents ();

	}
	
	private BanKuaiGeGuTableRenderer renderer;
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
     * ���øð����ɵ�Ȩ��
     */
	private void setGeGuWeightInBanKuai()
    {
		int row = this.getSelectedRow();
		if(row < 0)
			return;
		int modelRow = this.convertRowIndexToModel(row);
		
//		BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) treechanyelian.getLastSelectedPathComponent();
		BanKuai bkcode = ((BanKuaiGeGuExternalInfoTableModel)this.getModel()).getCurDispalyBandKuai();
		String stockcode = ((BanKuaiGeGuExternalInfoTableModel)(this.getModel())).getStockCode(modelRow);
		int weight = ((BanKuaiGeGuExternalInfoTableModel)(this.getModel())).getStockCurWeight (modelRow);
		
		String weightresult = JOptionPane.showInputDialog(null,"�������Ʊ�ڸð��Ȩ�أ�\n\r"
											+ "10~1 ��ռ��ҵӪ�ձ���,\n\r 0 : Ӫ��ռ�ȼ���û�и���׶�,\n\r -1 : ���޹�ϵ"
				,weight);
		try {
			int newweight = Integer.parseInt(weightresult);
			if(newweight> 10 || newweight < -1)
				JOptionPane.showMessageDialog(null,"Ȩ��ֵ��Χ10 ~ -1��\n\r"
						,"Warning",JOptionPane.WARNING_MESSAGE);
			
			if(weight != newweight) {
				bkdbopt.setStockWeightInBanKuai (bkcode,"",stockcode,newweight);
				( (BanKuaiGeGuExternalInfoTableModel)this.getModel() ).setStockCurWeight (modelRow,newweight);
			}
		} catch (java.lang.NumberFormatException e) {
			return;
		}
	}
	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc)
	{
		((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayMatchCondition(expc);
    	this.repaint();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
	 */
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
 
	        Component comp = super.prepareRenderer(renderer, row, col);
	        BanKuaiGeGuExternalInfoTableModel tablemodel = (BanKuaiGeGuExternalInfoTableModel)this.getModel(); 
	        if(tablemodel.getRowCount() == 0) {
	        	return null;
	        }
	        
	        BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
	        
	        String value =  ((JLabel)comp).getText();

//			{ "����", "����","Ȩ��","�߼���������","CjeMaxWk","������"};
		    String valuepect = "";
//		    if (comp instanceof JLabel && (col == 5 )) { //�ðٷֱ���ʾ
//		    	try {
//	        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
//	        		 
//	        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//		    	     percentFormat.setMinimumFractionDigits(1);
//	            	 valuepect = percentFormat.format (formatevalue );
//	        	} catch (java.lang.NullPointerException e) {
//	        		valuepect = "";
//		    	}catch (java.lang.NumberFormatException e)   	{
//	        		e.printStackTrace();
//	        	} catch (ParseException e) {
//					e.printStackTrace();
//				}
//	        	((JLabel)comp).setText(valuepect);
//	        }
		    
		    //����������Ĺ�Ʊ�ر���ʾ
		    int modelRow = this.convertRowIndexToModel(row);
		    StockOfBanKuai stockofbank = ( (BanKuaiGeGuExternalInfoTableModel)this.getModel() ).getStock(modelRow);
		    Stock stock = stockofbank.getStock();
		    BanKuai bankuai = stockofbank.getBanKuai();
		    
		    if(stock.wetherHasReiewedToday()) {
	        	Font defaultFont = this.getFont();
	        	Font font = new Font(defaultFont.getName(), Font.BOLD + Font.ITALIC,defaultFont.getSize());
	        	comp.setFont(font);
	        }
		    
		    Color foreground = Color.BLACK, background = Color.white;

		    if( col == 1 ) { //��������
		    	LocalDate requireddate = tablemodel.getShowCurDate();
		 		NodesTreeRelated stofbktree = stock.getNodeTreeRelated();
	    	
		 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
		    	if(isin != null && isin  ) 
			    		background = Color.ORANGE;  
		    	else
			    		background = Color.white;
		    	
		    	if(stockofbank.isBkLongTou())
		    		foreground = Color.RED;
		    	else 
		    		foreground = Color.BLACK;
		    } else 
		    if( col ==3) { //{ "����", "����","Ȩ��","�߼���������","CjeMaxWk","������"};
		    	Double ltszmin ;
			    Double ltszmax ;
			    try {
			    	ltszmax = matchcond.getSettingLiuTongShiZhiMax().doubleValue();
			    } catch (Exception e) {
			    	ltszmax = 10000000000000.0;
			    }
			    try {
			    	ltszmin = matchcond.getSettingLiuTongShiZhiMin();
			    } catch (Exception e) {
			    	ltszmin = 10000000000000.0;
			    }
			    
			    LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
			    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    Double curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, 0);
			    try {
				    if( curltsz >= ltszmin && curltsz <= ltszmax ) 
				    	background = Color.MAGENTA ;
				    else
				    	background = Color.white;
			    } catch (java.lang.NullPointerException e) {
			    	background = Color.white;
			    }
		    	
		    }  else  
		    if( col == 4  && value != null ) { //ͻ����ʾCjeMAXWK>=�ĸ���
		    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 4).toString() );
		    	
		    	Integer cjemaxwk = matchcond.getSettingChenJiaoErMaxWk();
		    	if(cjemaxwk == null)
		    		cjemaxwk =  10000000;
		    	
		    	if( dpmaxwk >=cjemaxwk )
		    		 background = Color.CYAN ;
		    	else 
		    		 background = Color.white ;
		    } else   
		    if( col == 5 && value != null  ) {	    //ͻ��������
		    	double hsl;
		    	try{
		    		 hsl = Double.parseDouble( tablemodel.getValueAt(modelRow, 5).toString() );
		    	} catch (java.lang.NullPointerException e) {
		    		 hsl = 0.0;
		    	}
		    	
		    	double shouldhsl;
		    	try{
		    		shouldhsl = matchcond.getSettingHuanShouLv();
		    	} catch (java.lang.NullPointerException e) {
		    		shouldhsl = 1000.0;
		    	}
		    	if(hsl >= shouldhsl)
		    		 background = Color.BLUE.brighter() ;
		    	 else 
		    		 background = Color.white ;
		    }
	 
	    	comp.setBackground(background);
	    	comp.setForeground(foreground);
	    	
		    if(this.isRowSelected(row) && col == 0 ) {
		    	comp.setBackground(Color.blue);
		    }
		    
		    return comp;

	}

	 public String getToolTipText(MouseEvent e) {
	        String tip = null;
	        java.awt.Point p = e.getPoint();
	        int rowIndex = rowAtPoint(p);
	        int colIndex = columnAtPoint(p);

	        try {
	        	if(colIndex == 2) { //Ȩ��column��tipҪ����
					org.jsoup.nodes.Document doc = Jsoup.parse("");
					org.jsoup.select.Elements content = doc.select("body");
					content.append( "10~1 ��ռ��ҵӪ�ձ���<br>" );
					content.append( "0 : Ӫ��ռ�ȼ���û�и���׶�<br>" );
					content.append( "-1 : ���޹�ϵ<br>" );
					
					tip = doc.toString();
	        	} else
	        		tip = getValueAt(rowIndex, colIndex).toString();
	        } catch (RuntimeException e1) {
	            //catch null pointer exception if mouse is over an empty line
	        }

	        return tip;
    }


}
