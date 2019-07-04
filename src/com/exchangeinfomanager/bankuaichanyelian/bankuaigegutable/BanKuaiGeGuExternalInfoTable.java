package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jsoup.Jsoup;

import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.commonlib.ToolTipHeader;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodesXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;

public class BanKuaiGeGuExternalInfoTable extends BanKuaiGeGuBasicTable implements BarChartHightLightFxDataValueListener
{
	

	public BanKuaiGeGuExternalInfoTable (StockInfoManager stockmanager1)
	{
		super (stockmanager1);
		
		BanKuaiGeGuExternalInfoTableModel bkgegumapmdl = new BanKuaiGeGuExternalInfoTableModel();
		this.setModel(bkgegumapmdl);

		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
		ToolTipHeader header = new ToolTipHeader(this.getColumnModel() );
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
		

	}
	
	private BanKuaiGeGuTableRenderer renderer;

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
		
		
//			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayCjeBKMaxWk( cjezbbkmax);
			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayCjeMaxWk (cjemaxwk);
			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayCjeZhanBiDPMaxMinWk (cjezbdpmax,cjezbdpmin);
			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayChenJiaoEr (cjemin,cjemax);
			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayHuanShouLv(showhsl);
			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setDisplayLiuTongShiZhi(showltszmin,showltszmax);
			((BanKuaiGeGuExternalInfoTableModel)this.getModel()).setHighLightHuiBuDownQueKou(showhuibudownquekou);
		
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
		 		StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stockofbank.getNodeTreeRelated();
	    	
		 		Boolean isin = stofbktree.isInBanKuaiFengXiResultFileForSpecificDate(requireddate);
		    	if(isin != null && isin  ) 
			    		background = Color.ORANGE;  
		    	else
			    		background = Color.white;
		    	
		    	if(stockofbank.isBkLongTou())
		    		foreground = Color.RED;
		    	else 
		    		foreground = Color.BLACK;
		    } else if( col ==3) { //{ "����", "����","Ȩ��","�߼���������","CjeMaxWk","������"};
		    	Double ltszmin = tablemodel.getDisplayLiuTongShiZhiMin() ;
			    Double ltszmax = tablemodel.getDisplayLiuTongShiZhiMax() ;
			    
			    LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
			    StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    Double curltsz = nodexdata.getSpecificTimeLiuTongShiZhi(requireddate, 0);
			    try {
				    if( curltsz >= ltszmin && curltsz <= ltszmax ) 
				    	background = Color.MAGENTA ;
				    else
				    	background = Color.white;
			    } catch (java.lang.NullPointerException e) {
			    	background = Color.white;
			    }
		    	
		    }  else  if( col == 4  && value != null ) { //ͻ����ʾCjeMAXWK>=�ĸ���
		    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 4).toString() );
		    	
		    	int fazhi = tablemodel.getDisplayCjeMaxWk();
		    	 if( dpmaxwk >=fazhi )
		    		 background = Color.CYAN ;
		    	 else 
		    		 background = Color.white ;
		    } else   if( col == 5 && value != null  ) {	    //ͻ��������
		    	double hsl = Double.parseDouble( tablemodel.getValueAt(modelRow, 5).toString() );
		    	
		    	double shouldhsl = tablemodel.getDisplayHuanShouLv();
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
