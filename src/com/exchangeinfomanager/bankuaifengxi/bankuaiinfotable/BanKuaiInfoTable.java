package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

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
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ta4j.core.Bar;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsCacheListener;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchConditionListener;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuZhangFu;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.collect.Range;


public class BanKuaiInfoTable extends JTable implements  BanKuaiGeGuMatchConditionListener , NewsCacheListener
{
	private BanKuaiPopUpMenu popupMenuGeguNews;
	private static final long serialVersionUID = 1L;
	private StockInfoManager stockmanager;
	private BanKuaiAndStockTree allbkskstree;
	
	
	private static Logger logger = Logger.getLogger(BanKuaiInfoTable.class);

	public BanKuaiInfoTable(StockInfoManager stockmanager1) 
	{
		super ();
		
		BanKuaiInfoTableModel bkmodel = new BanKuaiInfoTableModel ();
		this.setModel(bkmodel);
		
		this.allbkskstree = AllCurrentTdxBKAndStoksTree.getInstance().getAllBkStocksTree();
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
    
  //Implement table header tool tips.
  	String[] jtableTitleStringsTooltips = { "板块代码", "名称","CJE占比增长率","CJE占比","CJLZBMAXWK","大盘成交额增长贡献率","周日平均成交额MAXWK","周日平均成交额连续(成交额上周变化升降)"};
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
				 thel3.appendText("CJEDPMAXWK");
		
				for(String friend : socialset) {
					 BanKuaiInfoTableModel tablemodel = (BanKuaiInfoTableModel)this.getModel(); 
					 int modelRow = 0;
					 try {
						 int rowIndex = tablemodel.getBanKuaiRowIndex (friend);
						 if(rowIndex == -1)
							 continue;
						 modelRow = this.convertRowIndexToView(rowIndex);
					 } catch (java.lang.NullPointerException e) {
//						 e.printStackTrace();
					 }
					 
					 String frbkcode = (String) this.getValueAt(modelRow, 0);
					 String frbkname = (String) this.getValueAt(modelRow, 1);
					 Double cjezbchangerate = (Double)this.getValueAt(modelRow, 2);
					 Integer cjedpmaxwk = (Integer)this.getValueAt(modelRow, 4);
		    		 
		    		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
		 	    	 percentFormat.setMinimumFractionDigits(1);
		        	 String cjezbchangeratepect = percentFormat.format (cjezbchangerate );
		        	 
		        	 org.jsoup.nodes.Element trelline = tableel.appendElement("tr");
					 org.jsoup.nodes.Element tdel1 = trelline.appendElement("td");
					 tdel1.appendText(frbkcode + frbkname); 
					 org.jsoup.nodes.Element tdel2 = trelline.appendElement("td");
					 tdel2.appendText(cjezbchangeratepect);
					 org.jsoup.nodes.Element tdel3 = trelline.appendElement("td");
					 tdel3.appendText(cjedpmaxwk.toString());
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
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
	 */
	private Border outsidepos = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border insidepos = new EmptyBorder(0, 1, 0, 1);
	private Border highlightpos = new CompoundBorder(outsidepos, insidepos);
	
	private Border outsideneg = new MatteBorder(1, 0, 1, 0, Color.GREEN);
	private Border insideneg = new EmptyBorder(0, 1, 0, 1);
	private Border highlightneg = new CompoundBorder(outsideneg, insideneg);
	
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
	        	String bkcode =   bankuai.getMyOwnCode();
				BanKuai bkcurselected = ((BanKuaiInfoTableModel)this.getModel()).getBanKuai(modelRowcurselected);  
		        Set<String> socialsetpos = bkcurselected.getSocialFriendsSetPostive();
		        if(socialsetpos.contains(bkcode)) {
		        		jc.setBorder( highlightpos );
	        	}
		        Set<String> socialsetneg = bkcurselected.getSocialFriendsSetNegtive();
		        if(socialsetneg.contains(bkcode)) {
		        		jc.setBorder( highlightneg );
	        	}	
	        }
	       
	        if (comp instanceof JLabel && col == 0) {
	        	TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC) bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
	        	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (curdate,0);
			    if(zhangdiefu != null  && zhangdiefu > 0 )
			    	background = Color.RED;
			    else if(zhangdiefu != null  &&  zhangdiefu < 0 )
			    	background = Color.GREEN;
			    else
			    	background = Color.WHITE;
	        	
	        } 
	        
	        BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
	        RuleOfCjeZbDpMaxWk dpmaxwkRule = null;
	        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = null;
	        if(matchcond != null) {
	        	Facts facts = new Facts();
		        facts.put("evanode", bankuai);
		        facts.put("evadate", curdate);
		        facts.put("evadatedifference", 0);
		        facts.put("evaperiod", NodeGivenPeriodDataItem.WEEK);
		        facts.put("evacond", matchcond);
		        
		        Rules rules = new Rules();
		        
		        dpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
		        rules.register(dpmaxwkRule);
		        
		        averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
		        rules.register(averagecjemaxwkRule);
		        
		     // fire rules on known facts
		        RulesEngine rulesEngine = new DefaultRulesEngine();
		        rulesEngine.fire(rules, facts);
	        }

//	        if (comp instanceof JLabel && ( col == 3 ||   col == 4  )) {
//	        	try {
//	        		background = new Color(51,204,255);
//	        	} catch (java.lang.NullPointerException e) {
//	        		background = Color.WHITE;
//	        	}
//	        }
	        if (comp instanceof JLabel &&   col == 4  ) {
	        	try {
	        		background = dpmaxwkRule.getBackGround();
	        	} catch (java.lang.NullPointerException e) {
	        		background = Color.WHITE;
	        	}
	        }
	        if (comp instanceof JLabel && ( col == 6  ) ) {
	        	try {
	        		background = averagecjemaxwkRule.getBackGround();
		        } catch (java.lang.NullPointerException e) {
	        		background = Color.WHITE;
	        	}
	        }
//	        if (comp instanceof JLabel && ( col == 7  ) ) {
//	        	NodeXPeriodData nodexdata = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
//	        	Integer avgdailycjemaxwk = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(curdate,0);
//	        	if(avgdailycjemaxwk != null && avgdailycjemaxwk > 0) 
//	        		background = Color.RED;
//			    else if ( avgdailycjemaxwk != null && avgdailycjemaxwk <= 0 )
//			       	background = Color.GREEN;
//			    else
//			       	background = Color.WHITE;
//	        }
	        //"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
	        if (comp instanceof JLabel && (col == 2 ||  col == 3 ||    col == 5  )) {
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
	        
	        if( col == 1  ) { //关注/在板块文件中 这2个操作互斥，优先关注板块的颜色
	        	
	        	try {
	        		NodesTreeRelated tmptreerelated = bankuai.getNodeTreeRelated();
	        		Boolean matchmodel = tmptreerelated.selfIsMatchModel(curdate);
	        		if(matchmodel )
			        	background = Color.ORANGE;
			        else
			        	background = Color.white;
	        		
	        		Range<LocalDate> indqgz = bankuai.isInDuanQiGuanZhuRange (curdate);
	        		if(indqgz != null)
	        			background = new Color(102,178,255);
	        		
	        		if(this.isRowSelected(row))
	    	    		background = new Color(102,102,255);
	        		
	        		Range<LocalDate> inqsgz = bankuai.isInQiangShiBanKuaiRange (curdate);
	        		if(inqsgz != null)
	        			foreground = Color.RED;
	        		
	        		Range<LocalDate> inrsgz = bankuai.isInRuoShiBanKuaiRange (curdate);
	        		if(inrsgz != null)
	        			foreground = Color.GREEN;
		        	
	        	} catch (java.lang.NullPointerException e) {
	        		background = Color.white;
	        		foreground = Color.BLACK;
	        	}

	        }
	        
	       comp.setBackground(background);
	       comp.setForeground(foreground);

	       return comp;
	}
	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc)
	{
		((BanKuaiInfoTableModel)this.getModel()).setDisplayMatchCondition(expc);
    	this.repaint();
		
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
